package net.anweisen.cloud.modules.cloudflare.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.modules.cloudflare.api.dns.DnsRecord;
import net.anweisen.cloud.modules.cloudflare.api.dns.DnsRecordDetail;
import net.anweisen.cloud.modules.cloudflare.config.CloudflareConfigEntry;
import net.anweisen.cloud.modules.cloudflare.config.CloudflareConfigEntry.AuthenticationMethod;
import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CloudflareAPI implements LoggingApiUser {

	protected static final String CLOUDFLARE_ENDPOINT = "https://api.cloudflare.com/client/v4/";
	protected static final String ZONE_RECORDS_ENDPOINT = CLOUDFLARE_ENDPOINT + "zones/%s/dns_records";
	protected static final String ZONE_RECORDS_MANAGEMENT_ENDPOINT = ZONE_RECORDS_ENDPOINT + "/%s";

	protected final Multimap<UUID, DnsRecordDetail> createdRecords = Multimaps.newSetMultimap(new ConcurrentHashMap<>(), CopyOnWriteArraySet::new);

	@Nullable
	public DnsRecordDetail createRecord(@Nonnull UUID serviceUniqueId, @Nonnull CloudflareConfigEntry config, @Nonnull DnsRecord record) {
		Preconditions.checkNotNull(serviceUniqueId, "serviceUniqueId");
		Preconditions.checkNotNull(config, "configuration");
		Preconditions.checkNotNull(record, "record");

		try {
			HttpURLConnection connection = prepareConnection(String.format(ZONE_RECORDS_ENDPOINT, config.getZoneId()), "POST", config);
			Document result = sendRequestAndReadResponse(connection, record);

			Document content = result.getDocument("result");
			if (result.getBoolean("success") && content != null) {
				String id = content.getString("id");
				if (id != null) {
					debug("Successfully created record with id {} based on {} (configuration: {})", id, record, config);

					DnsRecordDetail detail = new DnsRecordDetail(id, record, config);
					createdRecords.put(serviceUniqueId, detail);
					return detail;
				}
			} else {
				debug("Unable to create cloudflare record, response was: {}", result);
			}
		} catch (IOException ex) {
			error("Error while creating cloudflare record for configuration {} (record: {})", config, record, ex);
		}

		return null;
	}

	public boolean deleteRecord(@Nonnull DnsRecordDetail recordDetail) {
		Preconditions.checkNotNull(recordDetail, "recordDetail");
		return deleteRecord(recordDetail.getConfigEntry(), recordDetail.getId());
	}

	@Nonnull
	public Collection<DnsRecordDetail> deleteAllRecords(@Nonnull UUID serviceUniqueId) {
		Preconditions.checkNotNull(serviceUniqueId, "serviceUniqueId");

		return createdRecords.removeAll(serviceUniqueId).stream()
			.filter(this::deleteRecord)
			.collect(Collectors.toSet());
	}

	public boolean deleteRecord(@Nonnull CloudflareConfigEntry configuration, @Nonnull String id) {
		Preconditions.checkNotNull(configuration, "configuration");
		Preconditions.checkNotNull(id, "id");

		try {
			HttpURLConnection connection = prepareConnection(String.format(ZONE_RECORDS_MANAGEMENT_ENDPOINT, configuration.getZoneId(), id), "DELETE", configuration);
			Document result = sendRequestAndReadResponse(connection);

			Document content = result.getDocument("result");
			if (content != null && content.contains("id")) {
				debug("Successfully deleted record " + id + " for configuration " + configuration);
				return true;
			}

			debug("Unable to delete record " + id + ", response: " + result);
		} catch (IOException exception) {
			error("Error while deleting dns record for configuration " + configuration, exception);
		}

		return false;
	}

	@Nonnull
	protected HttpURLConnection prepareConnection(@Nonnull String endpoint, @Nonnull String method, @Nonnull CloudflareConfigEntry entry) throws IOException {
		Preconditions.checkNotNull(endpoint, "endpoint");
		Preconditions.checkNotNull(method, "method");
		Preconditions.checkNotNull(entry, "entry");

		HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		connection.setUseCaches(false);
		connection.setDoOutput(true);
		connection.setRequestMethod(method);

		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Content-Type", "application/json");

		if (entry.getAuthenticationMethod() == AuthenticationMethod.GLOBAL_KEY) {
			connection.setRequestProperty("X-Auth-Email", entry.getEmail());
			connection.setRequestProperty("X-Auth-Key", entry.getApiToken());
		} else {
			connection.setRequestProperty("Authorization", "Bearer " + entry.getApiToken());
		}

		return connection;
	}

	@Nonnull
	protected Document sendRequestAndReadResponse(@Nonnull HttpURLConnection connection) throws IOException {
		Preconditions.checkNotNull(connection, "connection");
		return sendRequestAndReadResponse(connection, (String) null);
	}

	@Nonnull
	protected Document sendRequestAndReadResponse(@Nonnull HttpURLConnection connection, @Nonnull DnsRecord record) throws IOException {
		Preconditions.checkNotNull(connection, "connection");
		Preconditions.checkNotNull(record, "record");

		return sendRequestAndReadResponse(connection, Documents.newJsonDocument(record).toJson());
	}

	@Nonnull
	protected Document sendRequestAndReadResponse(@Nonnull HttpURLConnection connection, @Nullable String data) throws IOException {
		Preconditions.checkNotNull(connection, "connection");

		connection.connect();

		if (data != null) {
			try (OutputStream outputStream = connection.getOutputStream()) {
				outputStream.write(data.getBytes(StandardCharsets.UTF_8));
				outputStream.flush();
			}
		}

		if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
			return Documents.newJsonDocument(connection.getInputStream());
		} else {
			return Documents.newJsonDocument(connection.getErrorStream());
		}
	}

	public void close() {
		for (Entry<UUID, DnsRecordDetail> entry : this.createdRecords.entries()) {
			deleteRecord(entry.getValue());
		}
	}

	@Nonnull
	public Collection<DnsRecordDetail> getCreatedRecords(@Nonnull UUID serviceUniqueId) {
		Preconditions.checkNotNull(serviceUniqueId, "serviceUniqueId");
		return createdRecords.get(serviceUniqueId);
	}

	@Nonnull
	public Collection<DnsRecordDetail> getCreatedRecords() {
		return createdRecords.values();
	}

	@Nonnull
	public Collection<UUID> getServiceUniqueIds() {
		return createdRecords.keys();
	}

}

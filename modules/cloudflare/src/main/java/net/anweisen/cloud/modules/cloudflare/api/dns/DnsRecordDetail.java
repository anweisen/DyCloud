/*
 * Copyright 2019-2021 CloudNetService team & contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.anweisen.cloud.modules.cloudflare.api.dns;

import net.anweisen.cloud.modules.cloudflare.config.CloudflareConfigEntry;

public class DnsRecordDetail {

	private final String id;
	private final DnsRecord dnsRecord;
	private final CloudflareConfigEntry configurationEntry;

	public DnsRecordDetail(String id, DnsRecord dnsRecord, CloudflareConfigEntry configurationEntry) {
		this.id = id;
		this.dnsRecord = dnsRecord;
		this.configurationEntry = configurationEntry;
	}

	public String getId() {
		return this.id;
	}

	public DnsRecord getDnsRecord() {
		return this.dnsRecord;
	}

	public CloudflareConfigEntry getConfigEntry() {
		return configurationEntry;
	}
}

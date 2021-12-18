package net.anweisen.cloud.driver.network.http;

import net.anweisen.utility.common.misc.StringUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class HttpCodes {

	// Information
	public static final int CONTINUE = 100;
	public static final int SWITCHING_PROTOCOL = 101;
	public static final int PROCESSING = 102;
	public static final int EARLY_HINTS = 103;

	// Successful
	public static final int OK = 200;
	public static final int CREATED = 201;
	public static final int ACCEPTED = 202;
	public static final int NON_AUTHORITATIVE_INFORMATION = 203;
	public static final int NO_CONTENT = 204;
	public static final int RESET_CONTENT = 205;
	public static final int PARTIAL_CONTENT = 206;
	public static final int MULTI_STATUS = 207;
	public static final int ALREADY_REPORTED = 208;
	public static final int IM_USED = 226;

	// Redirection
	public static final int MULTIPLE_CHOICE = 300;
	public static final int MOVED_PERMANENTLY = 301;
	public static final int FOUND = 302;
	public static final int SEE_OTHER = 303;
	public static final int NOT_MODIFIED = 304;
	public static final int USE_PROXY = 305;
	public static final int TEMPORARY_REDIRECT = 307;
	public static final int PERMANENT_REDIRECT = 308;

	// Client Error
	public static final int BAD_REQUEST = 400;
	public static final int UNAUTHORIZED = 401;
	public static final int PAYMENT_REQUIRED = 402;
	public static final int FORBIDDEN = 403;
	public static final int NOT_FOUND = 404;
	public static final int METHOD_NOT_ALLOWED = 405;
	public static final int NOT_ACCEPTABLE = 406;
	public static final int PROXY_AUTHENTICATION_REQUIRED = 407;
	public static final int REQUEST_TIMEOUT = 408;
	public static final int CONFLICT = 409;
	public static final int GONE = 410;
	public static final int LENGTH_REQUIRED = 411;
	public static final int PRECONDITION_FAILED = 412;
	public static final int PAYLOAD_TOO_LARGE = 413;
	public static final int URI_TOO_LONG = 414;
	public static final int UNSUPPORTED_MEDIA_TYPE = 415;
	public static final int RANGE_NOT_SATISFIABLE = 416;
	public static final int EXPECTATION_FAILED = 417;
	public static final int MISDIRECTED_REQUEST = 421;
	public static final int UNPROCESSABLE_ENTITY = 422;
	public static final int LOCKED = 423;
	public static final int FAILED_DEPENDENCY = 424;
	public static final int TOO_EARLY = 425;
	public static final int UPGRADE_REQUIRED = 426;
	public static final int PRECONDITION_REQUIRED = 428;
	public static final int TOO_MANY_REQUEST = 429;
	public static final int REQUEST_HEADER_FIELD_TOO_LARGE = 431;
	public static final int UNAVAILABLE_FOR_LEGAL_REASONS = 451;

	// Server Error
	public static final int INTERNAL_SERVER_ERROR = 500;
	public static final int NOT_IMPLEMENTED = 501;
	public static final int BAD_GATEWAY = 502;
	public static final int SERVICE_UNAVAILABLE = 503;
	public static final int GATEWAY_TIMEOUT = 504;
	public static final int HTTP_VERSION_NOT_SUPPORTED = 505;
	public static final int VARIANT_ALSO_NEGOTIATES = 506;
	public static final int INSUFFICIENT_STORAGE = 507;
	public static final int LOOP_DETECTED = 508;
	public static final int NOT_EXTENDED = 510;
	public static final int NETWORK_AUTHENTICATION_REQUIRED = 511;

	private static final Map<Integer, String> messages = new LinkedHashMap<>();
	static {
		for (Field field : HttpCodes.class.getFields()) {
			try {
				String fieldName = field.getName();
				int value = (int) field.get(null);

				String name = StringUtils.getEnumName(fieldName);
				if (messages.containsKey(value))
					System.err.println("HttpCodes: '" + messages.get(value) + "' and '" + name + "' have the same value of " + value);

				messages.put(value, name);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Nonnull
	public static String getStatusMessage(int code) {
		return messages.getOrDefault(code, "");
	}

	public static boolean isInformation(int code) {
		return code >= 100 && code < 200;
	}

	public static boolean isSuccessful(int code) {
		return code >= 200 && code < 300;
	}

	public static boolean isRedirection(int code) {
		return code >= 300 && code < 400;
	}

	public static boolean isClientError(int code) {
		return code >= 400 && code < 500 || code >= 600;
	}

	public static boolean isServerError(int code) {
		return code >= 500 && code < 600;
	}

	private HttpCodes() {}

}

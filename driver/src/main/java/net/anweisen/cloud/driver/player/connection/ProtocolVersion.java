package net.anweisen.cloud.driver.player.connection;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see PlayerConnection#getVersion()
 */
public final class ProtocolVersion {

	private static final Map<Integer, ProtocolVersion> versions = new ConcurrentHashMap<>();

	public static final ProtocolVersion UNKNOWN = create(-1, "Unknown");
	public static final ProtocolVersion v1_7    = create(4, "1.7", "1.7.1", "1.7.2", "1.7.3", "1.7.4", "1.7.5");
	public static final ProtocolVersion v1_7_6  = create(5, "1.7.6", "1.7.7", "1.7.8", "1.7.9", "1.7.10");
	public static final ProtocolVersion v1_8    = create(47, "1.8");
	public static final ProtocolVersion v1_9    = create(107, "1.9");
	public static final ProtocolVersion v1_9_1  = create(108, "1.9.1");
	public static final ProtocolVersion v1_9_2  = create(109, "1.9.2");
	public static final ProtocolVersion v1_9_3  = create(110, "1.9.3", "1.9.4");
	public static final ProtocolVersion v1_10   = create(210, "1.10");
	public static final ProtocolVersion v1_11   = create(315, "1.11");
	public static final ProtocolVersion v1_11_1 = create(316, "1.11.1", "1.11.2");
	public static final ProtocolVersion v1_12   = create(335, "1.12");
	public static final ProtocolVersion v1_12_1 = create(338, "1.12.1");
	public static final ProtocolVersion v1_12_2 = create(340, "1.12.2");
	public static final ProtocolVersion v1_13   = create(393, "1.13");
	public static final ProtocolVersion v1_13_1 = create(401, "1.13.1");
	public static final ProtocolVersion v1_13_2 = create(404, "1.13.2");
	public static final ProtocolVersion v1_14   = create(477, "1.14");
	public static final ProtocolVersion v1_14_1 = create(477, "1.14.1");
	public static final ProtocolVersion v1_14_2 = create(485, "1.14.2");
	public static final ProtocolVersion v1_14_3 = create(490, "1.14.3");
	public static final ProtocolVersion v1_14_4 = create(498, "1.14.4");
	public static final ProtocolVersion v1_15   = create(573, "1.15");
	public static final ProtocolVersion v1_15_1 = create(575, "1.15.1");
	public static final ProtocolVersion v1_15_2 = create(578, "1.15.2");
	public static final ProtocolVersion v1_16   = create(735, "1.16");
	public static final ProtocolVersion v1_16_1 = create(736, "1.16.1");
	public static final ProtocolVersion v1_16_2 = create(751, "1.16.2");
	public static final ProtocolVersion v1_16_3 = create(753, "1.16.3");
	public static final ProtocolVersion v1_16_4 = create(754, "1.16.4", "1.16.5");
	public static final ProtocolVersion v1_17   = create(755, "1.17");
	public static final ProtocolVersion v1_17_1 = create(756, "1.17.1");

	@Nonnull
	private static ProtocolVersion create(int versionId, @Nonnull String name, @Nonnull String... otherNames) {
		ProtocolVersion version = new ProtocolVersion(versionId, name, otherNames);
		versions.put(versionId, version);
		return version;
	}

	@Nonnull
	public static ProtocolVersion getVersion(int protocolId) {
		return versions.getOrDefault(protocolId, UNKNOWN);
	}

	@Nonnull
	public static Collection<ProtocolVersion> getVersions() {
		return Collections.unmodifiableCollection(versions.values());
	}

	private final int versionId;
	private final String name;
	private final String[] otherNames;

	private ProtocolVersion(int versionId, @Nonnull String name, @Nonnull String... otherNames) {
		this.versionId = versionId;
		this.name = name;
		this.otherNames = otherNames;
	}

	public int getVersionId() {
		return versionId;
	}

	@Nonnull
	public String getName() {
		String nameString = name;
		for (String otherName : otherNames) {
			nameString += ", " + otherName;
		}
		return nameString;
	}

	@Nonnull
	public String getMainName() {
		return name;
	}

	@Nonnull
	public String[] getOtherNames() {
		return otherNames;
	}

	@Override
	public String toString() {
		return "ProtocolVersion[name=" + getName() + " id=" + versionId + "]";
	}
}

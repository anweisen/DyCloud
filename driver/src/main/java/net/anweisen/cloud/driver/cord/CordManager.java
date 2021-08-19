package net.anweisen.cloud.driver.cord;

import net.anweisen.cloud.driver.CloudDriver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudDriver#getCordManager()
 */
public interface CordManager {

	@Nonnull
	List<CordInfo> getCordInfos();

	@Nullable
	default CordInfo getCordInfo(@Nonnull String name) {
		return getCordInfos().stream().filter(info -> info.getName().equals(name)).findFirst().orElse(null);
	}

	@Nonnull
	default Collection<String> getCordNames() {
		return getCordInfos().stream().map(CordInfo::getName).collect(Collectors.toList());
	}

}

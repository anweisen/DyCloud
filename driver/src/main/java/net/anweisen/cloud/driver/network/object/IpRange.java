package net.anweisen.cloud.driver.network.object;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Takes a specific IP address or a range specified using the IP/Netmask (e.g. 192.168.1.0/24 or 202.24.0.0/14).
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class IpRange {

	private final int maskBits;
	private final InetAddress requiredAddress;

	/**
	 * @param ipAddress the address or range of addresses from which the request must come.
	 */
	public IpRange(@Nonnull String ipAddress) {
		if (ipAddress.indexOf('/') > 0) {
			String[] addressAndMask = ipAddress.split("/");
			ipAddress = addressAndMask[0];
			maskBits = Integer.parseInt(addressAndMask[1]);
		} else {
			maskBits = -1;
		}
		requiredAddress = parseAddress(ipAddress);
		Preconditions.checkArgument(requiredAddress.getAddress().length * 8 >= maskBits, String.format("IP address %s is too short for bitmask of length %d", ipAddress, maskBits));
	}

	public IpRange(@Nonnull InetAddress requiredAddress, int maskBits) {
		this.requiredAddress = requiredAddress;
		this.maskBits = maskBits;
		Preconditions.checkArgument(requiredAddress.getAddress().length * 8 >= maskBits, String.format("IP address %s is too short for bitmask of length %d", requiredAddress.getHostAddress(), maskBits));
	}

	public boolean matches(@Nonnull String address) {
		InetAddress remoteAddress = parseAddress(address);

		if (!requiredAddress.getClass().equals(remoteAddress.getClass())) {
			return false;
		}

		if (maskBits < 0) {
			return remoteAddress.equals(requiredAddress);
		}

		byte[] remote = remoteAddress.getAddress();
		byte[] required = requiredAddress.getAddress();

		int maskFullBytes = maskBits / 8;
		byte finalByte = (byte) (0xFF00 >> (maskBits & 0x07));

		for (int i = 0; i < maskFullBytes; i++) {
			if (remote[i] != required[i]) {
				return false;
			}
		}

		if (finalByte != 0) {
			return (remote[maskFullBytes] & finalByte) == (required[maskFullBytes] & finalByte);
		}

		return true;
	}

	@Nonnull
	private InetAddress parseAddress(@Nonnull String address) {
		try {
			return InetAddress.getByName(address);
		} catch (UnknownHostException ex) {
			throw new IllegalArgumentException("Failed to parse address " + address, ex);
		}
	}

	public int getMaskBits() {
		return maskBits;
	}

	@Nonnull
	public InetAddress getRequiredAddress() {
		return requiredAddress;
	}

	@Override
	public String toString() {
		return requiredAddress.getHostAddress() + "/" + maskBits;
	}
}

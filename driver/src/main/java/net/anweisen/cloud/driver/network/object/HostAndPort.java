package net.anweisen.cloud.driver.network.object;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class HostAndPort implements SerializableObject {

	private String host;
	private int port;

	private HostAndPort() {
	}

	public HostAndPort(@Nonnull String host, int port) {
		Preconditions.checkNotNull(host, "Host cannot be null");
		this.host = host;
		this.port = port;
	}

	public HostAndPort(@Nonnull InetSocketAddress socketAddress) {
		Preconditions.checkNotNull(socketAddress, "socketAddress cannot be null");
		this.host = socketAddress.getAddress().getHostAddress();
		this.port = socketAddress.getPort();
	}

	@Nonnull
	@CheckReturnValue
	public static HostAndPort parse(@Nonnull String input) {
		String[] args = input.split(":");
		if (args.length == 2)
			return new HostAndPort(args[0], Integer.parseInt(args[1]));

		throw new IllegalStateException("Unable to parse '" + input + "'");
	}

	@Nullable
	public static HostAndPort parseOrNull(@Nullable String input) {
		try {
			return parse(input);
		} catch (Exception ex) {
			return null;
		}
	}

	@Nonnull
	@CheckReturnValue
	public static HostAndPort fromSocketAddress(@Nonnull SocketAddress address) {
		Preconditions.checkArgument(address instanceof InetSocketAddress, "Cannot convert " + address.getClass().getSimpleName() + " to InetSocketAddress");
		return new HostAndPort((InetSocketAddress) address);
	}

	@Nonnull
	@CheckReturnValue
	public static HostAndPort localhost(int port) {
		return new HostAndPort(localhost(), port);
	}

	@Nonnull
	@CheckReturnValue
	public static String localhost() {
		try {
			return new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		}

		return "127.0.0.1";
	}

	@Nonnull
	@CheckReturnValue
	public InetSocketAddress toSocketAddress() {
		return new InetSocketAddress(host, port);
	}

	@Nonnull
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer) {
		buffer.writeString(host);
		buffer.writeInt(port);
	}

	@Override
	public void read(@Nonnull PacketBuffer buffer) {
		host = buffer.readString();
		port = buffer.readInt();
	}

	@Override
	public String toString() {
		return host + ":" + port;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HostAndPort that = (HostAndPort) o;
		return port == that.port && Objects.equals(host, that.host);
	}

	@Override
	public int hashCode() {
		return Objects.hash(host, port);
	}

	@Nonnull
	@Override
	public HostAndPort clone() {
		return new HostAndPort(host, port);
	}

	@Nonnull
	public HostAndPort withPort(int port) {
		return new HostAndPort(host, port);
	}

	@Nonnull
	public HostAndPort withHost(@Nonnull String host) {
		return new HostAndPort(host, port);
	}
}

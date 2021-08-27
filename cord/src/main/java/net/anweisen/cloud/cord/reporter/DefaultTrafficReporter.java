package net.anweisen.cloud.cord.reporter;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.utilities.common.collection.NumberFormatter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultTrafficReporter implements CordTrafficReporter, LoggingApiUser {

	private static final Duration period = Duration.ofMinutes(1);

	private final AtomicInteger newConnections = new AtomicInteger();
	private final AtomicInteger upstreamPacketCount = new AtomicInteger();
	private final AtomicLong upstreamPacketBytes = new AtomicLong();
	private final AtomicInteger downstreamPacketCount = new AtomicInteger();
	private final AtomicLong downstreamPacketBytes = new AtomicLong();

	public void start() {
		if (period.getSeconds() <= 0) throw new IllegalStateException();
		CloudDriver.getInstance().getExecutor().scheduleAtFixedRate(this::reportCycle, period.getSeconds(), period.getSeconds(), TimeUnit.SECONDS);
	}

	protected void reportCycle() {
		long seconds = period.getSeconds();
		double downstreamPacketMegabytes = downstreamPacketBytes.doubleValue() / 1024 / 1024;
		double upstreamPacketMegabytes = upstreamPacketBytes.doubleValue() / 1024 / 1024;

		info("============== average cord activity report of last {} ==============", (seconds > 60 ? seconds / 60 + " minutes" : seconds + " seconds"));
		info("New Connections: {}/s, total {}", NumberFormatter.DEFAULT.format(newConnections.doubleValue() / seconds), newConnections);
		info("Downstream Packets: {}/s {}mb/s, total {} {}mb", NumberFormatter.DEFAULT.format(downstreamPacketCount.doubleValue() / seconds), NumberFormatter.BIG_FLOATING_POINT.format(downstreamPacketMegabytes / seconds), downstreamPacketCount, NumberFormatter.BIG_FLOATING_POINT.format(downstreamPacketMegabytes));
		info("Upstream Packets: {}/s {}mb/s, total {} {}mb", NumberFormatter.DEFAULT.format(upstreamPacketCount.doubleValue() / seconds), NumberFormatter.BIG_FLOATING_POINT.format(upstreamPacketMegabytes / seconds), upstreamPacketCount, NumberFormatter.BIG_FLOATING_POINT.format(upstreamPacketMegabytes));

		newConnections.set(0);
		upstreamPacketCount.set(0);
		upstreamPacketBytes.set(0);
		downstreamPacketCount.set(0);
		downstreamPacketBytes.set(0);
	}

	@Override
	public void reportDownstreamPacket(int readablePacketBytes) {
		downstreamPacketCount.incrementAndGet();
		downstreamPacketBytes.addAndGet(readablePacketBytes);
	}

	@Override
	public void reportUpstreamPacket(int readablePacketBytes) {
		upstreamPacketCount.incrementAndGet();
		upstreamPacketBytes.addAndGet(readablePacketBytes);
	}

	@Override
	public void reportNewConnection() {
		newConnections.incrementAndGet();
	}

}

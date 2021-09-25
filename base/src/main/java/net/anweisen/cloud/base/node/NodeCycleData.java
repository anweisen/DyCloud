package net.anweisen.cloud.base.node;

import com.sun.management.OperatingSystemMXBean;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;
import java.lang.management.ManagementFactory;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class NodeCycleData implements SerializableObject {

	public static final int PUBLISH_INTERVAL = 5_000; // publish all 5 seconds
	public static final int CYCLE_TIMEOUT = 5; // node time-outs after 25 seconds

	static {
		current(); // init management
	}

	@Nonnull
	public static NodeCycleData current() {
		OperatingSystemMXBean system = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

		float cpuUsage = (float) system.getSystemCpuLoad() * 100f;
		int cores = system.getAvailableProcessors();
		long maxMemory = system.getTotalPhysicalMemorySize() / 1024 / 1024; // bytes -> kilobytes -> megabytes
		long freeRam = system.getFreePhysicalMemorySize() / 1024 / 1024; // bytes -> kilobytes -> megabytes

		return new NodeCycleData(cpuUsage, cores, maxMemory, freeRam);
	}

	private float cpuUsage; // cpu usage in percent
	private int cores; // the amount of cores the machine of the node has
	private long maxRam; // the ram the machine of the node has in megabytes
	private long freeRam; // the ram the machine of the node has left in megabytes
	private int latency; // the amount of time it takes to send a packet from the node to the server in ms
	private long timestamp;

	private NodeCycleData() {
	}

	public NodeCycleData(float cpuUsage, int cores, long maxRam, long freeRam) {
		this.cpuUsage = cpuUsage;
		this.cores = cores;
		this.maxRam = maxRam;
		this.freeRam = freeRam;
		this.latency = -1;
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer) {
		buffer.writeFloat(cpuUsage);
		buffer.writeVarInt(cores);
		buffer.writeVarLong(maxRam);
		buffer.writeVarLong(freeRam);
		buffer.writeLong(System.currentTimeMillis());
	}

	@Override
	public void read(@Nonnull PacketBuffer buffer) {
		cpuUsage = buffer.readFloat();
		cores = buffer.readVarInt();
		maxRam = buffer.readVarInt();
		freeRam = buffer.readVarInt();
		timestamp = buffer.readLong();
		latency = (int) (System.currentTimeMillis() - timestamp);
	}

	public float getCpuUsage() {
		return cpuUsage;
	}

	public int getCores() {
		return cores;
	}

	public long getMaxRam() {
		return maxRam;
	}

	public long getFreeRam() {
		return freeRam;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getLatency() {
		return latency;
	}

	public boolean hasTimeouted() {
		long lastCycleDelay = System.currentTimeMillis() - timestamp - 30; // we allow 30ms delay
		int lostCycles = (int) lastCycleDelay / PUBLISH_INTERVAL;
		if (lostCycles > 0) CloudDriver.getInstance().getLogger().trace("Node timeout: lost {} cycles ({}ms)", lostCycles, lastCycleDelay);
		return lostCycles >= CYCLE_TIMEOUT;
	}

	@Override
	public String toString() {
		return "NodeCycleData[" + "cpuUsage=" + cpuUsage + " cores=" + cores + " maxRam=" + maxRam + " freeRam=" + freeRam + " latency=" + latency + "ms]";
	}
}

package net.anweisen.cloud.base.node;

import com.sun.management.OperatingSystemMXBean;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;
import java.lang.management.ManagementFactory;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class NodeCycleData implements SerializableObject {

	static {
		current();
	}

	@Nonnull
	public static NodeCycleData current() {
		OperatingSystemMXBean system = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

		float cpuUsage = (float) (system.getSystemCpuLoad() * 100f);
		int cores = system.getAvailableProcessors();
		long maxMemory = system.getTotalPhysicalMemorySize() / 1024 / 1024; // bytes -> kilobytes -> megabytes
		long freeRam = system.getFreePhysicalMemorySize() / 1024 / 1024; // bytes -> kilobytes -> megabytes

		return new NodeCycleData(cpuUsage, cores, maxMemory, freeRam);
	}

	private float cpuUsage; // cpu usage in percent
	private int cores; // the amount of cores the machine of the node has
	private long maxRam; // the ram the machine of the node has in megabytes
	private long freeRam; //the ram the machine of the node has left in megabytes

	private NodeCycleData() {
	}

	public NodeCycleData(float cpuUsage, int cores, long maxRam, long freeRam) {
		this.cpuUsage = cpuUsage;
		this.cores = cores;
		this.maxRam = maxRam;
		this.freeRam = freeRam;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeFloat(cpuUsage);
		buffer.writeVarInt(cores);
		buffer.writeVarLong(maxRam);
		buffer.writeVarLong(freeRam);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		cpuUsage = buffer.readFloat();
		cores = buffer.readVarInt();
		maxRam = buffer.readVarInt();
		freeRam = buffer.readVarInt();
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

	@Override
	public String toString() {
		return "NodeCycleData[" + "cpuUsage=" + cpuUsage + " cores=" + cores + " maxRam=" + maxRam + " freeRam=" + freeRam + "]";
	}
}

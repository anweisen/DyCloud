package net.anweisen.cloud.cord.reporter;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface CordTrafficReporter {

	void start();

	void reportDownstreamPacket(int readablePacketBytes);

	void reportUpstreamPacket(int readablePacketBytes);

	void reportNewConnection();

}

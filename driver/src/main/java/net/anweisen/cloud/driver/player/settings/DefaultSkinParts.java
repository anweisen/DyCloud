package net.anweisen.cloud.driver.player.settings;

import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultSkinParts implements SkinParts, SerializableObject {

	private boolean cape, jacket, leftSleeve, rightSleeve, leftPants, rightPants, hat;

	private DefaultSkinParts() {
	}

	public DefaultSkinParts(boolean cape, boolean jacket, boolean leftSleeve, boolean rightSleeve, boolean leftPants, boolean rightPants, boolean hat) {
		this.cape = cape;
		this.jacket = jacket;
		this.leftSleeve = leftSleeve;
		this.rightSleeve = rightSleeve;
		this.leftPants = leftPants;
		this.rightPants = rightPants;
		this.hat = hat;
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer) {
		buffer.writeBoolean(cape);
		buffer.writeBoolean(jacket);
		buffer.writeBoolean(leftSleeve);
		buffer.writeBoolean(rightSleeve);
		buffer.writeBoolean(leftPants);
		buffer.writeBoolean(rightPants);
		buffer.writeBoolean(hat);
	}

	@Override
	public void read(@Nonnull PacketBuffer buffer) {
		cape = buffer.readBoolean();
		jacket = buffer.readBoolean();
		leftSleeve = buffer.readBoolean();
		rightSleeve = buffer.readBoolean();
		leftPants = buffer.readBoolean();
		rightPants = buffer.readBoolean();
		hat = buffer.readBoolean();
	}

	@Override
	public boolean hasCape() {
		return cape;
	}

	@Override
	public boolean hasJacket() {
		return jacket;
	}

	@Override
	public boolean hasLeftSleeve() {
		return leftSleeve;
	}

	@Override
	public boolean hasRightSleeve() {
		return rightSleeve;
	}

	@Override
	public boolean hasLeftPants() {
		return leftPants;
	}

	@Override
	public boolean hasRightPants() {
		return rightPants;
	}

	@Override
	public boolean hasHat() {
		return hat;
	}

	@Override
	public String toString() {
		return "SkinParts[cape=" + cape + " jacket=" + jacket + " leftSleeve=" + leftSleeve + " rightSleeve=" + rightSleeve + " leftPants=" + leftPants + " rightPants=" + rightPants + " hat=" + hat + "]";
	}
}

package alpvax.mc.goprone;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Set prone packet.
 * @author Alpvax
 * @author VidTu
 */
public class SetPronePacket {
	private final boolean pressed;

	public SetPronePacket(boolean isPressed) {
		this.pressed = isPressed;
	}

	public static SetPronePacket decode(PacketByteBuf buffer) {
		return new SetPronePacket(buffer.readBoolean());
	}

	public static void encode(SetPronePacket msg, PacketByteBuf buffer) {
		buffer.writeBoolean(msg.pressed);
	}

	public static void handle(SetPronePacket msg, ServerPlayerEntity sender) {
		GoProne.entityProneStates.put(sender.getUuid(), msg.pressed);
	}
}

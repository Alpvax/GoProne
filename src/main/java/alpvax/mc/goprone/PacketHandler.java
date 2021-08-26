package alpvax.mc.goprone;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Handler for packets.
 * @author Alpvax
 * @author VidTu
 * @implNote Not compatible with Forge impl. (e.g. Fabric client is not guaranteed to work on Forge server)
 */
public final class PacketHandler {
	/**ID of packet. Current value: <code>goprone:main_channel</code>*/
	public static final Identifier ID = new Identifier(GoProne.MODID, "main_channel");

	/**
	 * Register handlers for packet.
	 * @see {@link SetPronePacket#decode(PacketByteBuf)}
	 */
	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(ID, (srv, ep, handle, buf, sender) -> SetPronePacket.handle(SetPronePacket.decode(buf), ep));
		//SetPronePacket.handle only accepts serverplayer as arg. So no client impl.
	}

	/**
	 * Send a packet to the server.<br>
	 * Must be called Client side.]
	 * @param msg Packet to send
	 * @see {@link SetPronePacket#encode(SetPronePacket, PacketByteBuf)}
	 */
	public static void sendToServer(SetPronePacket msg) {
		PacketByteBuf pbb = new PacketByteBuf(Unpooled.buffer());
		SetPronePacket.encode(msg, pbb);
		ClientPlayNetworking.send(ID, pbb);
	}

	/**
	 * Send a packet to a specific player.<br>
	 * Must be called Server side.
	 * @param msg Packet to send
	 * @param player Player for packet to send
	 * @apiNote Looks like this method is unused. It can be removed soon.
	 * @see {@link SetPronePacket#encode(SetPronePacket, PacketByteBuf)}
	 */
	public static void sendTo(SetPronePacket msg, ServerPlayerEntity player) {
		PacketByteBuf pbb = new PacketByteBuf(Unpooled.buffer());
		SetPronePacket.encode(msg, pbb);
		ServerPlayNetworking.send(player, ID, pbb);
	}
}

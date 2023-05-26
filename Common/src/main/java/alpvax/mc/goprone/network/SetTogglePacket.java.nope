package alpvax.mc.goprone.network;

import alpvax.mc.goprone.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetTogglePacket {
    private final boolean toggleState;

    SetTogglePacket(boolean proneState) {
        this.toggleState = proneState;
    }

    public static SetTogglePacket decode(FriendlyByteBuf buffer) {
        return new SetTogglePacket(buffer.readBoolean());
    }

    public static void encode(SetTogglePacket msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.toggleState);
    }

    public static void handle(SetTogglePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> ClientProxy.setToggleState(msg.toggleState));
    }
}

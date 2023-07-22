package dev.turtywurty.shulkercolours.network;

import dev.turtywurty.shulkercolours.capability.ShulkerColour;
import dev.turtywurty.shulkercolours.capability.ShulkerColourCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CSyncShulkerColourPacket {
    private final byte colourId;
    private final int entityId;

    public CSyncShulkerColourPacket(byte colourId, int entityId) {
        this.colourId = colourId;
        this.entityId = entityId;
    }

    public CSyncShulkerColourPacket(FriendlyByteBuf friendlyByteBuf) {
        this(friendlyByteBuf.readByte(), friendlyByteBuf.readInt());
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeByte(this.colourId);
        friendlyByteBuf.writeInt(this.entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            context.setPacketHandled(true);

            Level level = Minecraft.getInstance().level;
            if (level == null)
                return;

            Entity entity = level.getEntity(this.entityId);
            if (!(entity instanceof Shulker shulker))
                return;

            LazyOptional<ShulkerColour> capability = shulker.getCapability(ShulkerColourCapability.CAPABILITY);
            capability.ifPresent(shulkerColour -> {
                if(this.colourId == -1) {
                    shulkerColour.setColour(null);
                    return;
                }

                var dyeColour = DyeColor.byId(this.colourId);
                shulkerColour.setColour(dyeColour);
            });
        });
    }
}

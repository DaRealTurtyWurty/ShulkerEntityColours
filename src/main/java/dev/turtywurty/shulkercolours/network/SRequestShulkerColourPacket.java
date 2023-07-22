package dev.turtywurty.shulkercolours.network;

import dev.turtywurty.shulkercolours.ShulkerColours;
import dev.turtywurty.shulkercolours.capability.ShulkerColour;
import dev.turtywurty.shulkercolours.capability.ShulkerColourCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SRequestShulkerColourPacket {
    private final int entityId;

    public SRequestShulkerColourPacket(int entityId) {
        this.entityId = entityId;
    }

    public SRequestShulkerColourPacket(FriendlyByteBuf friendlyByteBuf) {
        this(friendlyByteBuf.readInt());
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(this.entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            context.setPacketHandled(true);

            Level level = context.getSender().level();
            Entity entity = level.getEntity(this.entityId);
            if(!(entity instanceof Shulker shulker)) return;

            LazyOptional<ShulkerColour> capability = shulker.getCapability(ShulkerColourCapability.CAPABILITY);
            capability.ifPresent(shulkerColour -> {
                DyeColor dyeColour = shulkerColour.getColour();

                PacketManager.INSTANCE.send(PacketDistributor.PLAYER.with(context::getSender),
                        new CSyncShulkerColourPacket(dyeColour == null ? -1 : (byte) dyeColour.getId(), shulker.getId()));
            });
        });
    }
}

package dev.turtywurty.shulkercolours.capability;

import dev.turtywurty.shulkercolours.ShulkerColours;
import dev.turtywurty.shulkercolours.network.CSyncShulkerColourPacket;
import dev.turtywurty.shulkercolours.network.PacketManager;
import dev.turtywurty.shulkercolours.network.SRequestShulkerColourPacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class ShulkerColourProvider implements ICapabilitySerializable<ByteTag> {
    private static final ResourceLocation ID = new ResourceLocation(ShulkerColours.MODID, "shulker_colour");

    private final ShulkerColour backend = new ShulkerColourCapability();
    private final LazyOptional<ShulkerColour> optional = LazyOptional.of(() -> this.backend);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        return ShulkerColourCapability.CAPABILITY.orEmpty(cap, this.optional);
    }

    @Override
    public ByteTag serializeNBT() {
        return this.backend.serializeNBT();
    }

    @Override
    public void deserializeNBT(ByteTag nbt) {
        this.backend.deserializeNBT(nbt);
    }

    @Mod.EventBusSubscriber(modid = ShulkerColours.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void attach(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Shulker) {
                var provider = new ShulkerColourProvider();
                event.addCapability(ID, provider);
            }
        }

        @SubscribeEvent
        public static void entityJoin(EntityJoinLevelEvent event) {
            if(!(event.getEntity() instanceof Shulker shulker) || !event.getLevel().isClientSide()) return;

            PacketManager.INSTANCE.sendToServer(new SRequestShulkerColourPacket(shulker.getId()));
        }

        @SubscribeEvent
        public static void interactEntity(PlayerInteractEvent.EntityInteract event) {
            if(!(event.getTarget() instanceof Shulker shulker))
                return;

            if (event.getLevel().isClientSide())
                return;

            LazyOptional<ShulkerColour> capability = shulker.getCapability(ShulkerColourCapability.CAPABILITY);

            ItemStack stack = event.getItemStack();
            if (!(stack.getItem() instanceof DyeItem dye)) {
                if(stack.is(Items.POTION) && PotionUtils.getPotion(stack) == Potions.WATER) {
                    capability.ifPresent(shulkerColour -> {
                        if(shulkerColour.getColour() == null)
                            return;

                        shulkerColour.setColour(null);
                        if(!event.getEntity().isCreative()) {
                            stack.shrink(1);
                            event.getEntity().setItemInHand(event.getHand(), stack);
                        }

                        PacketManager.INSTANCE.send(PacketDistributor.ALL.noArg(),
                                new CSyncShulkerColourPacket((byte) -1, shulker.getId()));
                    });
                }

                return;
            }

            DyeColor fromItem = dye.getDyeColor();
            capability.resolve().ifPresentOrElse(
                shulkerColour -> {
                    shulkerColour.setColour(fromItem);

                    if(!event.getEntity().isCreative()) {
                        stack.shrink(1);
                        event.getEntity().setItemInHand(event.getHand(), stack);
                    }

                    PacketManager.INSTANCE.send(PacketDistributor.ALL.noArg(),
                            new CSyncShulkerColourPacket((byte) fromItem.getId(), shulker.getId()));
                },
                () -> ShulkerColours.LOGGER.warn("Shulker has no colour!"));
        }
    }

    @Mod.EventBusSubscriber(modid = ShulkerColours.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void register(RegisterCapabilitiesEvent event) {
            event.register(ShulkerColour.class);
        }
    }
}

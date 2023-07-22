package dev.turtywurty.shulkercolours.capability;

import net.minecraft.nbt.ByteTag;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.jetbrains.annotations.Nullable;

public class ShulkerColourCapability implements ShulkerColour {
    public static final Capability<ShulkerColour> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private DyeColor colour;

    @Override
    @Nullable
    public DyeColor getColour() {
        return this.colour;
    }

    @Override
    public void setColour(@Nullable DyeColor colour) {
        this.colour = colour;
    }

    @Override
    public ByteTag serializeNBT() {
        return ByteTag.valueOf(this.colour == null ? -1 : (byte) this.colour.getId());
    }

    @Override
    public void deserializeNBT(ByteTag nbt) {
        byte colourId = nbt.getAsByte();
        this.colour = colourId == -1 ? null : DyeColor.byId(colourId);
    }
}

package dev.turtywurty.shulkercolours.capability;

import net.minecraft.nbt.ByteTag;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.concurrent.ThreadLocalRandom;

public interface ShulkerColour extends INBTSerializable<ByteTag> {
    DyeColor getColour();

    void setColour(DyeColor colour);

    static DyeColor getRandomColor() {
        DyeColor[] colors = DyeColor.values();
        return colors[ThreadLocalRandom.current().nextInt(colors.length)];
    }
}

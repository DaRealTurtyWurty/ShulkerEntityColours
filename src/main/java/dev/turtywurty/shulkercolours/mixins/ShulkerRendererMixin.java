package dev.turtywurty.shulkercolours.mixins;

import dev.turtywurty.shulkercolours.capability.ShulkerColour;
import dev.turtywurty.shulkercolours.capability.ShulkerColourCapability;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerRenderer.class)
public abstract class ShulkerRendererMixin extends MobRenderer<Shulker, ShulkerModel<Shulker>>  {
    public ShulkerRendererMixin(EntityRendererProvider.Context pContext, ShulkerModel<Shulker> pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Inject(
            method = "getTextureLocation(Lnet/minecraft/world/entity/monster/Shulker;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void shulkercolours$getTextureLocation(Shulker pEntity, CallbackInfoReturnable<ResourceLocation> callback) {
        if (pEntity.hasCustomName() && pEntity.getCustomName().getString().equals("jeb_")) {
            callback.setReturnValue(ShulkerRenderer.getTextureLocation(DyeColor.WHITE));
            return;
        }

        LazyOptional<ShulkerColour> capability = pEntity.getCapability(ShulkerColourCapability.CAPABILITY);
        if(capability.resolve().isPresent()) {
            ShulkerColour shulkerColour = capability.orElse(null);
            if (shulkerColour == null)
                return;

            callback.setReturnValue(ShulkerRenderer.getTextureLocation(shulkerColour.getColour()));
        }
    }
}

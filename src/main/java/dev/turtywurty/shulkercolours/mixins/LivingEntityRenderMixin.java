package dev.turtywurty.shulkercolours.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRenderMixin {
    private float red, green, blue;

    @Inject(
        method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("HEAD")
    )
    private <T extends LivingEntity> void shulkercolours$render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
        if(pEntity instanceof Shulker shulker) {
            if(shulker.hasCustomName() && shulker.getCustomName().getString().equals("jeb_")) {
                int ticker = shulker.tickCount / 25 + shulker.getId();
                int colourSize = DyeColor.values().length;

                int currentColourIndex = ticker % colourSize;
                int nextColourIndex = (ticker + 1) % colourSize;

                float timer = ((float)(shulker.tickCount % 25) + pPartialTicks) / 25.0F;

                float[] currentColours = Sheep.getColorArray(DyeColor.byId(currentColourIndex));
                float[] nextColours = Sheep.getColorArray(DyeColor.byId(nextColourIndex));

                this.red = currentColours[0] * (1.0F - timer) + nextColours[0] * timer;
                this.green = currentColours[1] * (1.0F - timer) + nextColours[1] * timer;
                this.blue = currentColours[2] * (1.0F - timer) + nextColours[2] * timer;
            }
        }
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
            ),
            index = 4
    )
    private float shulkercolours$renderRed(float red) {
        float value = this.red != -1 ? this.red : red;
        this.red = -1;
        return value;
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
            ),
            index = 5
    )
    private float shulkercolours$renderGreen(float green) {
        float value = this.green != -1 ? this.green : green;
        this.green = -1;
        return value;
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
            ),
            index = 6
    )
    private float shulkercolours$renderBlue(float blue) {
        float value = this.blue != -1 ? this.blue : blue;
        this.blue = -1;
        return value;
    }
}

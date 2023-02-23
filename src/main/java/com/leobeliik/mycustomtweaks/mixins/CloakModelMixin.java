package com.leobeliik.mycustomtweaks.mixins;


import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.client.model.CloakModel;

@Mixin(CloakModel.class)
public class CloakModelMixin {

    @Inject(method = "Lvazkii/botania/client/model/CloakModel;createMesh()Lnet/minecraft/client/model/geom/builders/MeshDefinition;",
            at = @At("RETURN"), remap = false)
    private static MeshDefinition cloakModelMixin(CallbackInfoReturnable cir) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        CubeDeformation cubedeformation = new CubeDeformation(1.0F);

        partdefinition.addOrReplaceChild("collar", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("sideL", CubeListBuilder.create()
                .texOffs(1, 15).addBox(-10.0F, 0.0F, 1.0F, 9.99F, 18.0F, 2.5F, cubedeformation),
                PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0F));

        partdefinition.addOrReplaceChild("sideR", CubeListBuilder.create()
                .texOffs(1, 15).mirror().addBox(0.0F, 0.0F, 1.0F, 10.0F, 18.0F, 2.5F, cubedeformation),
                PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0F));
        return meshdefinition;
    }
}

package com.leobeliik.mycustomtweaks.mixins;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Arrays;

import static java.lang.Math.abs;

@Mixin(ChunkGenerator.class)
public class StructureDisabler {

    @Shadow
    @Final
    protected BiomeSource biomeSource;
    @Unique
    private static final String[] ignore = {"fort", "shaft", "fossil", "stronghold", "buried", "ship", "ocean"};

    @Inject(method = "tryGenerateStructure", at = @At("HEAD"), cancellable = true)
    private void disableStructuresAroundSpawn(StructureSet.StructureSelectionEntry structureSelectionEntry, StructureManager structureManager, RegistryAccess registryAccess, RandomState randomState, StructureTemplateManager structureTemplateManager, long seed, ChunkAccess chunkAccess, ChunkPos chunkPos, SectionPos sectionPos, CallbackInfoReturnable<Boolean> cir) {
        if (abs(chunkPos.x) < 8 && abs(chunkPos.z) < 8) {
            boolean result = Arrays.stream(ignore).anyMatch(s -> structureSelectionEntry.structure().value().toString().toLowerCase().contains(s));
            if (!result) {
                cir.setReturnValue(false);
            }
        }
    }
}
package com.refinedmods.refinedstorage.forge.mixin;

import com.refinedmods.refinedstorage.forge.support.model.DynamicModelDependencyResolver;

import java.util.Map;

import net.minecraft.client.resources.model.ModelDiscovery;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelDiscovery.class)
public abstract class ModelDiscoveryMixin {
    @Shadow
    public abstract void addRoot(ResolvableModel model);

    @Inject(method = "resolve", at = @At("HEAD"))
    private void registerDynamicRSModels(final CallbackInfoReturnable<Map<Identifier, ResolvedModel>> cir) {
        DynamicModelDependencyResolver.registerDependencies(this::addRoot);
    }
}

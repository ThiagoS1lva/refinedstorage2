package com.refinedmods.refinedstorage.forge.mixin;

import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

/**
 * Captures the {@link ModelBaker} created during model baking so the mod can bake its dynamic
 * block and item models when handling {@code ModifyBakingResult}.
 *
 * <p>Forge 26.1 exposes no registry for custom unbaked model types, so the dynamic models are
 * swapped into the baking result instead (see {@code ModelSwapHandler}). Baking them needs a
 * {@link ModelBaker}, which is only available while {@link ModelBakery#bakeModels} runs.
 */
@Mixin(ModelBakery.class)
public abstract class ModelBakerCaptureMixin {
    @WrapOperation(
        method = "bakeModels",
        at = @At(
            value = "NEW",
            target = "net/minecraft/client/resources/model/ModelBakery$ModelBakerImpl"
                + ".<init>(Lnet/minecraft/client/resources/model/ModelBakery;"
                + "Lnet/minecraft/client/resources/model/sprite/MaterialBaker;"
                + "Lnet/minecraft/client/resources/model/ModelBaker$Interner;"
                + "Lnet/minecraft/client/resources/model/ModelBakery$MissingModels;)V"
        )
    )
    private Object captureBaker(final Object bakery, final Object materials, final Object interner,
                                final Object missing, final Operation<Object> original) throws Throwable {
        final Object baker = original.call(bakery, materials, interner, missing);
        ModelBakerHolder.set((ModelBaker) baker);
        return baker;
    }
}

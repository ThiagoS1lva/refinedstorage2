package com.refinedmods.refinedstorage.forge.support.model;

import com.refinedmods.refinedstorage.common.autocrafting.PatternTypeItemModelProperty;
import com.refinedmods.refinedstorage.common.configurationcard.ActiveConfigurationCardItemModelProperty;
import com.refinedmods.refinedstorage.common.controller.ControllerEnergyLevelItemModelProperty;
import com.refinedmods.refinedstorage.common.networking.ActiveNetworkCardItemModelProperty;
import com.refinedmods.refinedstorage.common.security.ActiveSecurityCardItemModelProperty;
import com.refinedmods.refinedstorage.common.support.network.item.NetworkBoundItemModelProperty;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import net.minecraft.resources.Identifier;

/**
 * Forge 26.1 exposes no event to register custom item model properties, so the vanilla property
 * bootstraps are extended directly through their (private) id mappers. The properties of the mod
 * have to be registered both when the client loads item models and when the data generators
 * serialize them.
 */
public final class ItemModelPropertySupport {
    private static final String CONDITIONAL_PROPERTIES =
        "net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties";
    private static final String RANGE_PROPERTIES =
        "net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties";
    private static final String SELECT_PROPERTIES =
        "net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties";

    private static boolean registered;

    private ItemModelPropertySupport() {
    }

    public static synchronized void registerAll() {
        if (registered) {
            return;
        }
        registered = true;
        put(CONDITIONAL_PROPERTIES, Map.of(
            ActiveConfigurationCardItemModelProperty.NAME,
            ActiveConfigurationCardItemModelProperty.MAP_CODEC,
            ActiveNetworkCardItemModelProperty.NAME,
            ActiveNetworkCardItemModelProperty.MAP_CODEC,
            ActiveSecurityCardItemModelProperty.NAME,
            ActiveSecurityCardItemModelProperty.MAP_CODEC,
            NetworkBoundItemModelProperty.NAME,
            NetworkBoundItemModelProperty.MAP_CODEC
        ));
        put(RANGE_PROPERTIES, Map.of(
            ControllerEnergyLevelItemModelProperty.NAME,
            ControllerEnergyLevelItemModelProperty.MAP_CODEC
        ));
        put(SELECT_PROPERTIES, Map.of(
            PatternTypeItemModelProperty.NAME,
            PatternTypeItemModelProperty.PROPERTY_TYPE
        ));
    }

    private static void put(final String className, final Map<Identifier, Object> properties) {
        try {
            final Field field = Class.forName(className).getDeclaredField("ID_MAPPER");
            field.setAccessible(true);
            final Object mapper = field.get(null);
            final Method put = mapper.getClass().getMethod("put", Object.class, Object.class);
            for (final Map.Entry<Identifier, Object> entry : properties.entrySet()) {
                put.invoke(mapper, entry.getKey(), entry.getValue());
            }
        } catch (final NoSuchFieldException | ClassNotFoundException | NoSuchMethodException
                       | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to register item model properties in " + className, e);
        }
    }
}

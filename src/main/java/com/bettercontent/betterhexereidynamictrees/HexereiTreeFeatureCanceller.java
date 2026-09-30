package com.bettercontent.betterhexereidynamictrees;

import com.ferreusveritas.dynamictrees.api.registry.RegistryEvent;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors;
import com.ferreusveritas.dynamictrees.api.worldgen.FeatureCanceller;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Dthexerei.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class HexereiTreeFeatureCanceller {
    private static final ResourceLocation HEXEREI_MAHOGANY_TREE_FEATURE = new ResourceLocation("hexerei", "mahogany_tree");
    private static final ResourceLocation HEXEREI_WILLOW_TREE_FEATURE = new ResourceLocation("hexerei", "willow_tree");
    private static final ResourceLocation HEXEREI_WITCH_HAZEL_TREE_FEATURE = new ResourceLocation("hexerei", "witch_hazel_tree");

    private HexereiTreeFeatureCanceller() {
    }

    @SubscribeEvent
    public static void registerFeatureCancellers(final RegistryEvent<FeatureCanceller> event) {
        event.getRegistry().register(createCanceller("mahogany_tree", HEXEREI_MAHOGANY_TREE_FEATURE));
        event.getRegistry().register(createCanceller("willow_tree", HEXEREI_WILLOW_TREE_FEATURE));
        event.getRegistry().register(createCanceller("witch_hazel_tree", HEXEREI_WITCH_HAZEL_TREE_FEATURE));
    }

    private static FeatureCanceller createCanceller(final String name, final ResourceLocation hexereiFeature) {
        return new FeatureCanceller(Dthexerei.location(name)) {
            @Override
            public boolean shouldCancel(final ConfiguredFeature<?, ?> configuredFeature,
                                        final BiomePropertySelectors.NormalFeatureCancellation cancellation) {
                final ResourceLocation featureName = ForgeRegistries.FEATURES.getKey(configuredFeature.feature());
                return hexereiFeature.equals(featureName)
                        && cancellation.shouldCancelNamespace(hexereiFeature.getNamespace());
            }
        };
    }
}

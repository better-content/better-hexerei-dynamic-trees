package com.bettercontent.betterhexereidynamictrees.gametest;

import com.bettercontent.betterhexereidynamictrees.Dthexerei;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors;
import com.ferreusveritas.dynamictrees.api.worldgen.FeatureCanceller;
import com.ferreusveritas.dynamictrees.tree.species.Species;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder(Dthexerei.MODID)
@PrefixGameTestTemplate(false)
@Mod.EventBusSubscriber(modid = Dthexerei.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DthexereiGameTests {
    private DthexereiGameTests() {
    }

    @SubscribeEvent
    public static void register(final RegisterGameTestsEvent event) {
        event.register(DthexereiGameTests.class);
    }

    @GameTest(templateNamespace = "minecraft", template = "empty", batch = "better_hexerei_dynamic_trees_resources", timeoutTicks = 80)
    public static void dynamicHexereiTreeResourcesLoad(final GameTestHelper helper) {
        assertDynamicTree(helper, "mahogany", "hexerei:mahogany_sapling");
        assertDynamicTree(helper, "willow", "hexerei:willow_sapling");
        assertDynamicTree(helper, "witch_hazel", "hexerei:witch_hazel_sapling");
        helper.succeed();
    }

    @GameTest(templateNamespace = "minecraft", template = "empty", batch = "better_hexerei_dynamic_trees_replacement", timeoutTicks = 80)
    public static void hexereiStaticTreeFeaturesAreCancelledForReplacement(final GameTestHelper helper) {
        assertCancels(helper, "mahogany_tree", "hexerei:mahogany");
        assertCancels(helper, "willow_tree", "hexerei:willow");
        assertCancels(helper, "witch_hazel_tree", "hexerei:witch_hazel");
        assertDoesNotCancel(helper, "mahogany_tree", "hexerei:selenite_geode");
        helper.succeed();
    }

    private static void assertDynamicTree(final GameTestHelper helper, final String tree, final String primitiveSapling) {
        Species species = Species.REGISTRY.get(Dthexerei.location(tree));
        helper.assertTrue(species.isValid(), "species should load: " + tree);
        helper.assertTrue(species.getFamily().isValid(), "family should load for " + tree);
        helper.assertTrue(species.getLeavesProperties().isValid(), "leaves properties should load for " + tree);
        helper.assertTrue(species.getPrimitiveLeaves().isPresent(), "primitive leaves should resolve for " + tree);
        helper.assertTrue(species.hasSeed(), "generated seed should be registered for " + tree);
        helper.assertTrue(species.getSapling().isPresent(), "generated dynamic sapling should be registered for " + tree);
        helper.assertTrue(
                ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(primitiveSapling)),
                "primitive sapling should exist: " + primitiveSapling
        );
    }

    private static void assertCancels(final GameTestHelper helper, final String cancellerName, final String configuredFeatureId) {
        FeatureCanceller canceller = FeatureCanceller.REGISTRY.get(Dthexerei.location(cancellerName));
        helper.assertTrue(canceller != FeatureCanceller.NULL_CANCELLER, "canceller should be registered: " + cancellerName);
        helper.assertTrue(
                canceller.shouldCancel(configuredFeature(helper, configuredFeatureId), cancellationForHexereiNamespace(canceller)),
                "canceller " + cancellerName + " should remove static feature " + configuredFeatureId
        );
    }

    private static void assertDoesNotCancel(final GameTestHelper helper, final String cancellerName, final String configuredFeatureId) {
        FeatureCanceller canceller = FeatureCanceller.REGISTRY.get(Dthexerei.location(cancellerName));
        helper.assertFalse(
                canceller.shouldCancel(configuredFeature(helper, configuredFeatureId), cancellationForHexereiNamespace(canceller)),
                "canceller " + cancellerName + " should not remove unrelated feature " + configuredFeatureId
        );
    }

    private static ConfiguredFeature<?, ?> configuredFeature(final GameTestHelper helper, final String id) {
        ResourceLocation location = new ResourceLocation(id);
        return helper.getLevel()
                .registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolderOrThrow(ResourceKey.create(Registries.CONFIGURED_FEATURE, location))
                .value();
    }

    private static BiomePropertySelectors.NormalFeatureCancellation cancellationForHexereiNamespace(final FeatureCanceller canceller) {
        BiomePropertySelectors.NormalFeatureCancellation cancellation = new BiomePropertySelectors.NormalFeatureCancellation();
        cancellation.cancelUsing(canceller);
        cancellation.cancelWithNamespace("hexerei");
        return cancellation;
    }

}

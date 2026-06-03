package com.dthexerei.dthexerei.gametest;

import com.dthexerei.dthexerei.Dthexerei;
import com.ferreusveritas.dynamictrees.tree.species.Species;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
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

    @GameTest(templateNamespace = "minecraft", template = "empty", batch = "dthexerei_resources", timeoutTicks = 80)
    public static void dynamicHexereiTreeResourcesLoad(final GameTestHelper helper) {
        assertDynamicTree(helper, "mahogany", "hexerei:mahogany_sapling");
        assertDynamicTree(helper, "willow", "hexerei:willow_sapling");
        assertDynamicTree(helper, "witch_hazel", "hexerei:witch_hazel_sapling");
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

}

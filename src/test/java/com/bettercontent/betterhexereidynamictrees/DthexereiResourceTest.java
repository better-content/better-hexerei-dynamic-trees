package com.bettercontent.betterhexereidynamictrees;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

final class DthexereiResourceTest {
    private static final Path TREE_ROOT = Path.of("src/main/resources/trees/better_hexerei_dynamic_trees");
    private static final Path ASSET_ROOT = Path.of("src/main/resources/assets/better_hexerei_dynamic_trees");
    private static final Path GENERATED_ROOT = Path.of("src/generated/resources");
    private static final Set<String> EXPECTED_SPECIES = Set.of(
            "better_hexerei_dynamic_trees:mahogany",
            "better_hexerei_dynamic_trees:willow",
            "better_hexerei_dynamic_trees:witch_hazel"
    );
    private static final Map<String, String> PRIMITIVE_SAPLINGS = Map.of(
            "better_hexerei_dynamic_trees:mahogany", "hexerei:mahogany_sapling",
            "better_hexerei_dynamic_trees:willow", "hexerei:willow_sapling",
            "better_hexerei_dynamic_trees:witch_hazel", "hexerei:witch_hazel_sapling"
    );

    @Test
    void speciesReferenceExistingFamiliesLeavesPropertiesAndHexereiSaplings() throws IOException {
        Set<String> families = resourceIds(TREE_ROOT.resolve("families"));
        Set<String> leavesProperties = resourceIds(TREE_ROOT.resolve("leaves_properties"));
        Set<String> packagedSpecies = resourceIds(TREE_ROOT.resolve("species"));

        assertEquals(EXPECTED_SPECIES, packagedSpecies);

        try (var paths = Files.list(TREE_ROOT.resolve("species"))) {
            for (Path path : paths.filter(DthexereiResourceTest::isJson).toList()) {
                JsonObject species = readObject(path);
                String speciesId = "better_hexerei_dynamic_trees:" + path.getFileName().toString().replaceFirst("\\.json$", "");
                assertTrue(families.contains(species.get("family").getAsString()), "unknown family in " + path);
                assertTrue(leavesProperties.contains(species.get("leaves_properties").getAsString()),
                        "unknown leaves_properties in " + path);
                assertEquals(PRIMITIVE_SAPLINGS.get(speciesId), species.get("primitive_sapling").getAsString());
                assertTrue(species.get("signal_energy").getAsDouble() > 0.0, "signal_energy must be positive in " + path);
                assertTrue(species.get("growth_rate").getAsDouble() > 0.0, "growth_rate must be positive in " + path);
                assertTrue(species.get("up_probability").getAsInt() > 0, "up_probability must be positive in " + path);
                if (species.has("texture_overrides")) {
                    assertFalse(species.getAsJsonObject("texture_overrides").has("seed"),
                            "seed models should use addon-owned item textures in " + path);
                }
                String tree = path.getFileName().toString().replaceFirst("\\.json$", "");
                assertExists(ASSET_ROOT.resolve("textures/item/" + tree + "_seed.png"));
            }
        }
    }

    @Test
    void familiesAndLeavesPointAtHexereiPrimitiveBlocks() throws IOException {
        for (String tree : Set.of("mahogany", "willow", "witch_hazel")) {
            JsonObject family = readObject(TREE_ROOT.resolve("families/" + tree + ".json"));
            assertEquals("hexerei:" + tree + "_log", family.get("primitive_log").getAsString());
            assertEquals("hexerei:stripped_" + tree + "_log", family.get("primitive_stripped_log").getAsString());
            assertTrue(family.get("max_branch_radius").getAsInt() <= 8,
                    "families using Hexerei log-top textures must not trigger thick branch ring textures");
            assertTrue(family.getAsJsonObject("texture_overrides").has("branch"));
            assertTrue(family.getAsJsonObject("texture_overrides").has("stripped_branch_top"));

            JsonObject leaves = readObject(TREE_ROOT.resolve("leaves_properties/" + tree + ".json"));
            assertEquals("hexerei:" + tree + "_leaves", leaves.get("primitive_leaves").getAsString());
            assertEquals("dynamictrees:deciduous", leaves.get("cell_kit").getAsString());
        }
    }

    @Test
    void worldGenTargetsPackagedSpeciesAndHexereiFeatureCancellers() throws IOException {
        JsonElement defaultWorldGen = JsonParser.parseReader(Files.newBufferedReader(TREE_ROOT.resolve("world_gen/default.json")));
        defaultWorldGen.getAsJsonArray().forEach(element -> {
            JsonObject apply = element.getAsJsonObject().getAsJsonObject("apply");
            JsonObject random = apply.getAsJsonObject("species").getAsJsonObject("random");
            assertTrue(random.keySet().stream().anyMatch(EXPECTED_SPECIES::contains), "worldgen entry must inject a Hexerei species");
            assertTrue(random.has("..."), "worldgen splice must preserve existing Dynamic Trees species choices");
            assertFalse(apply.has("density"), "worldgen should not override biome tree density");
            assertFalse(apply.has("chance"), "worldgen should not override biome tree chance");
        });

        JsonArray cancellers = JsonParser.parseReader(
                Files.newBufferedReader(TREE_ROOT.resolve("world_gen/feature_cancellers.json"))
        ).getAsJsonArray();
        assertEquals(3, cancellers.size(), "each Hexerei tree feature should have a scoped canceller");

        JsonObject mahogany = findCanceller(cancellers, "better_hexerei_dynamic_trees:mahogany_tree");
        Set<String> mahoganyBiomes = mahogany.getAsJsonObject("select")
                .getAsJsonArray("names")
                .asList()
                .stream()
                .map(JsonElement::getAsString)
                .collect(Collectors.toUnmodifiableSet());
        assertEquals(Set.of("minecraft:jungle", "minecraft:bamboo_jungle"), mahoganyBiomes);

        JsonObject willow = findCanceller(cancellers, "better_hexerei_dynamic_trees:willow_tree");
        assertEquals("#forge:is_swamp", willow.getAsJsonObject("select").get("tag").getAsString());

        JsonObject witchHazel = findCanceller(cancellers, "better_hexerei_dynamic_trees:witch_hazel_tree");
        assertEquals("minecraft:birch_forest", witchHazel.getAsJsonObject("select").get("name").getAsString());

        cancellers.forEach(element -> {
            JsonArray namespaces = element.getAsJsonObject()
                    .getAsJsonObject("cancellers")
                    .getAsJsonArray("namespaces");
            assertTrue(namespaces.asList().stream().anyMatch(namespace -> "hexerei".equals(namespace.getAsString())),
                    "Hexerei cancellers must be scoped to the Hexerei namespace");
        });
    }

    @Test
    void willowKeepsHexereiHangingVineIdentity() throws IOException {
        JsonObject willow = readObject(TREE_ROOT.resolve("species/willow.json"));
        boolean hasWillowVines = willow.getAsJsonArray("features").asList().stream()
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .filter(feature -> "vines".equals(feature.get("name").getAsString()))
                .map(feature -> feature.getAsJsonObject("properties"))
                .anyMatch(properties ->
                        "hexerei:willow_vines_plant".equals(properties.get("block").getAsString())
                                && "hexerei:willow_vines".equals(properties.get("tip_block").getAsString())
                );

        assertTrue(hasWillowVines, "dynamic willow should preserve Hexerei's hanging willow vines");
    }

    @Test
    void generatedClientDataAndLootResourcesCoverEverySpecies() {
        for (String tree : Set.of("mahogany", "willow", "witch_hazel")) {
            assertExists(GENERATED_ROOT.resolve("assets/better_hexerei_dynamic_trees/blockstates/" + tree + "_branch.json"));
            assertExists(GENERATED_ROOT.resolve("assets/better_hexerei_dynamic_trees/blockstates/" + tree + "_leaves.json"));
            assertExists(GENERATED_ROOT.resolve("assets/better_hexerei_dynamic_trees/blockstates/" + tree + "_sapling.json"));
            assertExists(GENERATED_ROOT.resolve("assets/better_hexerei_dynamic_trees/blockstates/stripped_" + tree + "_branch.json"));
            assertExists(GENERATED_ROOT.resolve("assets/better_hexerei_dynamic_trees/models/block/" + tree + "_branch.json"));
            assertExists(GENERATED_ROOT.resolve("assets/better_hexerei_dynamic_trees/models/block/stripped_" + tree + "_branch.json"));
            assertExists(GENERATED_ROOT.resolve("assets/better_hexerei_dynamic_trees/models/block/saplings/" + tree + ".json"));
            assertExists(GENERATED_ROOT.resolve("assets/better_hexerei_dynamic_trees/models/item/" + tree + "_branch.json"));
            assertExists(GENERATED_ROOT.resolve("assets/better_hexerei_dynamic_trees/models/item/" + tree + "_seed.json"));
            assertExists(GENERATED_ROOT.resolve("data/better_hexerei_dynamic_trees/loot_tables/blocks/" + tree + "_leaves.json"));
            assertExists(GENERATED_ROOT.resolve("data/better_hexerei_dynamic_trees/loot_tables/trees/branches/" + tree + ".json"));
            assertExists(GENERATED_ROOT.resolve("data/better_hexerei_dynamic_trees/loot_tables/trees/branches/stripped_" + tree + ".json"));
            assertExists(GENERATED_ROOT.resolve("data/better_hexerei_dynamic_trees/loot_tables/trees/leaves/" + tree + ".json"));
            assertExists(GENERATED_ROOT.resolve("data/better_hexerei_dynamic_trees/loot_tables/trees/voluntary/" + tree + ".json"));
        }

        assertExists(GENERATED_ROOT.resolve("assets/better_hexerei_dynamic_trees/lang/en_us.json"));
        assertExists(GENERATED_ROOT.resolve("data/dynamictrees/tags/blocks/saplings.json"));
        assertExists(GENERATED_ROOT.resolve("data/dynamictrees/tags/items/seeds.json"));
    }

    private static Set<String> resourceIds(Path directory) throws IOException {
        try (var paths = Files.list(directory)) {
            Set<String> ids = paths.filter(DthexereiResourceTest::isJson)
                    .map(path -> "better_hexerei_dynamic_trees:" + path.getFileName().toString().replaceFirst("\\.json$", ""))
                    .collect(Collectors.toUnmodifiableSet());
            assertFalse(ids.isEmpty(), "expected resources in " + directory);
            return ids;
        }
    }

    private static boolean isJson(Path path) {
        return path.getFileName().toString().endsWith(".json");
    }

    private static JsonObject readObject(Path path) throws IOException {
        return JsonParser.parseReader(Files.newBufferedReader(path)).getAsJsonObject();
    }

    private static JsonObject findCanceller(JsonArray cancellers, String type) {
        for (JsonElement element : cancellers) {
            JsonObject entry = element.getAsJsonObject();
            if (type.equals(entry.getAsJsonObject("cancellers").get("type").getAsString())) {
                return entry;
            }
        }
        throw new AssertionError("missing feature canceller for " + type);
    }

    private static void assertExists(Path path) {
        assertTrue(Files.isRegularFile(path), "expected generated resource " + path);
    }
}

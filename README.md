# Dynamic Trees for Hexerei (`dynamic_trees_hexerei`)

Forge 1.20.1 addon that provides Dynamic Trees integration for Hexerei mahogany, willow, and witch hazel trees.

## Versions

- Minecraft: `1.20.1`
- Forge: `47.4.13`
- Dynamic Trees build dependency: `1.4.9`
- Hexerei runtime dependency: `hexerei-0.4.2.3.jar`

## Trees Included

- `dynamic_trees_hexerei:mahogany`
- `dynamic_trees_hexerei:willow`
- `dynamic_trees_hexerei:witch_hazel`

Each species maps back to Hexerei primitive logs, stripped logs, leaves, and saplings, while Dynamic Trees generates the dynamic branches, seeds, and saplings.

## Worldgen Strategy

The addon inserts Hexerei species into existing Dynamic Trees biome selections with splice rules so vanilla/other DT species and biome density remain available:

- Mahogany: `minecraft:jungle`, `minecraft:bamboo_jungle`
- Willow: `#forge:is_swamp`
- Witch hazel: `minecraft:birch_forest`

It also cancels the matching Hexerei static configured tree features in those same biome selections so the dynamic trees replace Hexerei's originals instead of appearing beside duplicate static trees.

## Validation

- Unit tests validate the tree-pack JSON graph, primitive Hexerei block IDs, worldgen splice entries, feature-canceller JSON, and willow vine identity.
- Forge game tests validate loaded Dynamic Trees registries, generated seeds/saplings, primitive sapling blocks, and custom feature canceller registration.

Run:

```sh
./gradlew verifyFast
./gradlew verifyFull
```

`verifyFast` runs the unit-test lane. `verifyFull` adds the headless Forge GameTest pass.

If CurseMaven cannot serve Hexerei, place `hexerei-0.4.2.3.jar` in `libs/`; the Gradle build prefers that local jar when present.

## Community and support

For modpack and mod discussion, playtest feedback, and bug reports, join the [Better Content Discord](https://discord.gg/EkRnZbzqS9).

## Canonical identity

- Repository and Gradle project: `dynamic-trees-hexerei`
- Mod ID and resource namespace: `dynamic_trees_hexerei`
- Maven group: `com.bettercontent`
- Runtime artifact: `build/libs/dynamic-trees-hexerei-<version>.jar`

The canonical identity is a clean break. Legacy mod IDs, resource namespaces, configuration paths, commands, network channels, and saved-data keys are not migrated or aliased.

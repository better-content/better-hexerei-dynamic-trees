# Bounded playtest observations

## Authority and feature scope

This repository owns the observations at `HexereiTreeFeatureCanceller`. Authoritative source remains the
behavior owner; the local full gate tests native behavior. Logging source/privacy contracts
are not pack runtime, performance or visual acceptance.

Each existing exact feature/namespace decision is canceled or delegated, with different-feature and namespace-not-selected reasons. Authored binding IDs and the actual already-read registered feature ID distinguish the selected rule from a different incoming feature; an unregistered feature is explicitly UNKNOWN. This native configuration-cancellation API provides no level/tick/position, so none is invented or looked up for logging. Only existing public registry identifiers are projected; configured-feature payloads remain private. Cancellation is not tree-generation success; registry/resource/native generation tests remain separate. No biome/world scan, new RNG or tree persistence is introduced.

## Controls, bounds and lifecycle

The existing loader SLF4J backend owns `bc.playtest.better_hexerei_dynamic_trees.*`; no runtime provider
is bundled. Startup reports real mod version, effective loaded instrumentation and own-artifact
source metadata when available, otherwise UNKNOWN. Metadata is bounded and read only from this
mod's exact Forge ModFile at initialization. Normal standalone logging stays INFO; an explicitly
selected DEBUG profile enables causal records. One process-local instance shares 120 detail
records/minute, burst20, bounded256 aggregation keys and 2048-character single-line records.
Native END-server-tick boundaries coalesce monotonic30-second summaries; server stop clears
keys without replenishing the bucket or rearming TRACE. Actual failures preserve existing
normal severity/stack traces independently of detail suppression.

Focused TRACE remains default off and requires `bc.playtest.trace.better_hexerei_dynamic_trees.dimension`
and exactly one `.subject` UUID or `.pos=x,y,z`; the once-per-process window begins on the first
matching detail and expires at60seconds/1000records, also constrained by the common bucket.
These feature boundaries currently emit causal DEBUG, not per-tick/per-frame TRACE. The helper
supports focused details but a focus property does not invent an uninstrumented native observer.
Hot diagnostics guard before native context/field creation; no extra inventory/chunk scan,
RNG draw, entity construction, packet or persistent ledger is added.

## Native source fixture boundary

A fresh retained `run-gametest/attempts/<UUID>` directory is selected through the actual Forge
RunConfig. Old directories/evidence stay untouched; absent-only native properties preparation
preserves existing bytes. The pure setup guard checks defaults/preservation and ERROR-FATAL
negative input; native verification requires exactly2 authored required cases and strict
error-free logs, not merely Gradle exit0. The new projection/fixture still needs that full gate.

## Verification boundary

The unchanged shared helper contract covers off/lazy behavior, escaping/truncation,
budget/cardinality/overflow/fakeclock/reset/focus and own-metadata validation. The owner-specific
source contract asserts actual event seams/privacy markers, not native success. Existing native
feature tests and the owning documented full gate remain required before publication. Exact
candidate gameplay, paired native UI review where relevant and performance acceptance are
separate; do not infer them from a causal log or a source-only pass.

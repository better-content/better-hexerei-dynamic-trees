package com.bettercontent.betterhexereidynamictrees.diagnostics;
import java.util.function.Supplier;
/** Pure owner-specific projection of already computed native outcomes; no gameplay writes or RNG. */
public final class FeatureObservation {
    private FeatureObservation() {}
    public static void cancellation(PlaytestLog log, boolean matches, boolean canceled, Object binding, Object feature) {
        if (!log.enabled("worldgen")) return;
        log.event("worldgen", "feature.cancel", "server", canceled ? "canceled" : "delegated", !matches ? "different_feature" : canceled ? "native_namespace_admitted" : "namespace_not_selected", () -> PlaytestLog.fields("binding", binding, "feature", feature == null ? "UNKNOWN" : feature));
    }
    public static void decision(PlaytestLog log, String category, String event, String result, String reason) {
        if (!log.enabled(category)) return;
        log.event(category, event, "server", result, reason, () -> "");
    }
}

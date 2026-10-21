package com.bettercontent.betterhexereidynamictrees.diagnostics;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
/** Actual scalar production emitter; native cancellation API supplies no world/tick context. */
final class FeatureObservationTest {
    public static void main(String[] args) { new FeatureObservationTest().actualEmitterStagesPrivacyAndObservationalPurity(); }
    @Test void actualEmitterStagesPrivacyAndObservationalPurity() {
        var records = new ArrayList<String>(); boolean[] enabled = {false};
        var log = new PlaytestLog("better_hexerei_dynamic_trees", new PlaytestLog.Sink() {
            public boolean enabled(String category, String level) { return enabled[0]; }
            public void write(String category, String level, String text) { records.add(text); }
        }, () -> 0L, new Properties());
        for (boolean active : new boolean[]{false, true}) {
            enabled[0] = active;
            FeatureObservation.cancellation(log, true, true, "hexerei:willow_tree", "hexerei:willow_tree");
            FeatureObservation.cancellation(log, false, false, "hexerei:willow_tree", "minecraft:tree");
            FeatureObservation.cancellation(log, true, false, "hexerei:willow_tree", "hexerei:willow_tree");
            FeatureObservation.cancellation(log, false, false, "hexerei:willow_tree", null);
            if (!active) assertTrue(records.isEmpty());
        }
        assertEquals(4, records.size());
        assertTrue(records.get(0).contains("result=canceled reason=native_namespace_admitted"));
        assertTrue(records.get(1).contains("result=delegated reason=different_feature"));
        assertTrue(records.get(2).contains("result=delegated reason=namespace_not_selected"));
        assertTrue(records.get(0).contains("binding=hexerei:willow_tree feature=hexerei:willow_tree"));
        assertTrue(records.get(1).contains("binding=hexerei:willow_tree feature=minecraft:tree"));
        assertTrue(records.get(3).contains("feature=UNKNOWN"));
        assertTrue(records.stream().noneMatch(s -> s.contains("seed=") || s.contains("generated=") || s.contains("nbt=") || s.contains("dimension=") || s.contains(" tick=")));
    }
}

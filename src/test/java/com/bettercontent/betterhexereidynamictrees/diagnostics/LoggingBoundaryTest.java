package com.bettercontent.betterhexereidynamictrees.diagnostics;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/** Supplementary wiring/privacy invariant, not the actual emitter or native behavior test. */
final class LoggingBoundaryTest {
    @Test void authoritativeFeatureBoundaryAndPrivacyRemainExplicit() throws Exception {
        String source = Files.readString(Path.of("src/main/java/com/bettercontent/betterhexereidynamictrees/HexereiTreeFeatureCanceller.java")) +
            Files.readString(Path.of("src/main/java/com/bettercontent/betterhexereidynamictrees/diagnostics/FeatureObservation.java"));
        assertTrue(source.contains("FeatureObservation.cancellation"), "FeatureObservation.cancellation");
        assertTrue(source.contains("feature.cancel"), "feature.cancel");
        assertTrue(source.contains("different_feature"), "different_feature");
        assertTrue(source.contains("native_namespace_admitted"), "native_namespace_admitted");
        assertFalse(source.contains("fields(\"nbt\""));
        assertFalse(source.contains("fields(\"payload\""));
        assertFalse(source.contains("fields(\"seed\""));
        assertTrue(source.contains("enabled("));
    }
}

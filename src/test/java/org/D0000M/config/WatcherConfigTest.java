package org.D0000M.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WatcherConfigTest {
    @Test
    public void testConfigSetAndGet() {
        WatcherConfig config = new WatcherConfig();
        config.setChannel("my_channel");
        config.setVerbose(true);
        config.setLocalId("test-id");
        Assertions.assertEquals("my_channel", config.getChannel());
        Assertions.assertTrue(config.isVerbose());
        Assertions.assertEquals("test-id", config.getLocalId());
    }
} 
package org.D0000M.watcher;

import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.persist.Watcher;
import org.junit.jupiter.api.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

public class JCasbinPostgresWatcherTest {
    private static final String URL = "jdbc:postgresql://localhost:5432/casbin_test";
    private static final String USER = "postgres";
    private static final String PASSWORD = "wo4ton0DneAr2Fts7hpq";
    private static final String CHANNEL = "test_channel";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testWatcherCallback() throws Exception {
        try (JCasbinPostgresWatcher watcher1 = new JCasbinPostgresWatcher(URL, USER, PASSWORD, CHANNEL);
             JCasbinPostgresWatcher watcher2 = new JCasbinPostgresWatcher(URL, USER, PASSWORD, CHANNEL)) {
            AtomicBoolean called = new AtomicBoolean(false);
            watcher2.setUpdateCallback(() -> called.set(true));
            // watcher1 triggers update
            watcher1.update();
            Thread.sleep(1000);
            Assertions.assertTrue(called.get(), "watcher2 should receive notification from watcher1 and trigger callback");
        }
    }

    @Test
    public void testWatcherCallbackRunnable() throws Exception {
        try (JCasbinPostgresWatcher watcher1 = new JCasbinPostgresWatcher(URL, USER, PASSWORD, CHANNEL);
             JCasbinPostgresWatcher watcher2 = new JCasbinPostgresWatcher(URL, USER, PASSWORD, CHANNEL)) {
            AtomicBoolean called = new AtomicBoolean(false);
            watcher2.setUpdateCallback((Runnable) () -> called.set(true));
            // watcher1 triggers update
            watcher1.update();
            Thread.sleep(1000);
            Assertions.assertTrue(called.get(), "Policy change should trigger callback (Runnable)");
        }
    }

    @Test
    public void testWatcherCallbackConsumer() throws Exception {
        try (JCasbinPostgresWatcher watcher1 = new JCasbinPostgresWatcher(URL, USER, PASSWORD, CHANNEL);
             JCasbinPostgresWatcher watcher2 = new JCasbinPostgresWatcher(URL, USER, PASSWORD, CHANNEL)) {
            AtomicBoolean called = new AtomicBoolean(false);
            watcher2.setUpdateCallback((Consumer<String>) msg -> {
                try {
                    Map<String, Object> messageMap = objectMapper.readValue(msg, Map.class);
                    Assertions.assertEquals("update", messageMap.get("method"));
                    called.set(true);
                } catch (Exception e) {
                    Assertions.fail("JSON parsing failed", e);
                }
            });
            // watcher1 triggers update
            watcher1.update();
            Thread.sleep(1000);
            Assertions.assertTrue(called.get(), "Policy change should trigger callback (Consumer<String>)");
        }
    }

    @Test
    public void testWatcherEx() throws Exception {
        try (JCasbinPostgresWatcher watcher1 = new JCasbinPostgresWatcher(URL, USER, PASSWORD, CHANNEL);
             JCasbinPostgresWatcher watcher2 = new JCasbinPostgresWatcher(URL, USER, PASSWORD, CHANNEL)) {

            final AtomicBoolean called = new AtomicBoolean(false);
            final String[] receivedParams = new String[3];

            watcher2.setUpdateCallback((Consumer<String>) msg -> {
                try {
                    Map<String, Object> messageMap = objectMapper.readValue(msg, Map.class);
                    String method = (String) messageMap.get("method");
                    Assertions.assertEquals("updateForAddPolicy", method);

                    Map<String, Object> params = (Map<String, Object>) messageMap.get("params");
                    Assertions.assertEquals("p", params.get("sec"));
                    Assertions.assertEquals("p", params.get("ptype"));

                    List<String> policy = (List<String>) params.get("params");
                    receivedParams[0] = policy.get(0);
                    receivedParams[1] = policy.get(1);
                    receivedParams[2] = policy.get(2);

                    called.set(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    Assertions.fail("JSON parsing failed");
                }
            });

            Thread.sleep(200); // Wait for listener to be ready

            watcher1.updateForAddPolicy("p", "p", "alice", "data1", "read");

            Thread.sleep(1000);

            Assertions.assertTrue(called.get(), "WatcherEx callback should be called");
            Assertions.assertArrayEquals(new String[]{"alice", "data1", "read"}, receivedParams);
        }
    }
} 
package org.D0000M.watcher;

import org.casbin.jcasbin.persist.Watcher;
import org.junit.jupiter.api.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MultiInstanceSyncTest {
    private static final String URL = "jdbc:postgresql://localhost:5432/your_database";
    private static final String USER = "postgres";
    private static final String PASSWORD = "your_password";
    private static final String CHANNEL = "test_channel";

    @Test
    public void testMultiInstanceSync() throws Exception {
        JCasbinPostgresWatcher watcher1 = new JCasbinPostgresWatcher(URL, USER, PASSWORD, CHANNEL);
        JCasbinPostgresWatcher watcher2 = new JCasbinPostgresWatcher(URL, USER, PASSWORD, CHANNEL);
        AtomicBoolean called = new AtomicBoolean(false);
        watcher2.setUpdateCallback(() -> called.set(true));
        // watcher1 triggers update
        watcher1.update();
        Thread.sleep(1000);
        Assertions.assertTrue(called.get(), "watcher2 should receive notification from watcher1");
        watcher1.close();
        watcher2.close();
    }
} 
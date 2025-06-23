package org.D0000M.watcher;

import org.junit.jupiter.api.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PostgresWatcherTest {
    private PostgresWatcher listener;
    private PostgresWatcher notifier;
    private static final String URL = "jdbc:postgresql://localhost:5432/testdb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String CHANNEL = "test_channel";

    @BeforeEach
    public void setUp() throws Exception {
        listener = new PostgresWatcher(URL, USER, PASSWORD);
        listener.connect();
        notifier = new PostgresWatcher(URL, USER, PASSWORD);
        notifier.connect();
    }

    @AfterEach
    public void tearDown() {
        listener.close();
        notifier.close();
    }

    @Test
    public void testNotifyAndListen() throws Exception {
        AtomicBoolean received = new AtomicBoolean(false);
        listener.listen(CHANNEL, msg -> {
            if ("hello".equals(msg)) received.set(true);
        });
        
        // Brief wait to ensure LISTEN command is effective in the database
        Thread.sleep(200);

        // Send notification using another instance
        notifier.notify(CHANNEL, "hello");
        Thread.sleep(1000);
        Assertions.assertTrue(received.get(), "Should receive NOTIFY message");
    }
} 
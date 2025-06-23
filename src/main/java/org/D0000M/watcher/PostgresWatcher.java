package org.D0000M.watcher;

import java.sql.*;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * The core class of Postgres Watcher, responsible for database connection, listening, and notification.
 */
public class PostgresWatcher {
    private final String url;
    private final Properties props;
    private Connection conn;
    private Thread listenThread;
    private volatile boolean running = false;

    public PostgresWatcher(String url, String user, String password) {
        this.url = url;
        this.props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
    }

    /**
     * Connecting to the database
     */
    public void connect() throws SQLException {
        conn = DriverManager.getConnection(url, props);
    }

    /**
     * Listen to the specified channel and invoke a callback when a message is received.
     */
    public void listen(String channel, Consumer<String> callback) {
        running = true;
        listenThread = new Thread(() -> {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("LISTEN " + channel);
                org.postgresql.PGConnection pgConn = conn.unwrap(org.postgresql.PGConnection.class);
                while (running) {
                    // Wait for notification.
                    org.postgresql.PGNotification[] notifications = pgConn.getNotifications(1000);
                    if (notifications != null) {
                        for (org.postgresql.PGNotification n : notifications) {
                            callback.accept(n.getParameter());
                        }
                    }
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        listenThread.start();
    }

    /**
     * sent NOTIFY
     */
    public void notify(String channel, String message) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(String.format("NOTIFY %s, '%s'", channel, message));
        }
    }

    /**
     * Close the connection and the listening thread.
     */
    public void close() {
        running = false;
        try {
            if (listenThread != null) listenThread.join();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 
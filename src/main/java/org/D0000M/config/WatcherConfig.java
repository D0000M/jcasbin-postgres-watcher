package org.D0000M.config;

/**
 * Watcher Configuration Class
 */
public class WatcherConfig {
    private String channel = "casbin_psql_watcher";
    private boolean verbose = false;
    private String localId = java.util.UUID.randomUUID().toString();

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public boolean isVerbose() { return verbose; }
    public void setVerbose(boolean verbose) { this.verbose = verbose; }

    public String getLocalId() { return localId; }
    public void setLocalId(String localId) { this.localId = localId; }
} 
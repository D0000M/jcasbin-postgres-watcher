# jcasbin-postgres-watcher

[![Java](https://img.shields.io/badge/language-Java-orange.svg)](https://www.java.com)[![Maven](https://img.shields.io/badge/build-Maven-red.svg)](https://maven.apache.org/)[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

jCasbin PostgreSQL Watcher is a [PostgreSQL](https://www.postgresql.org/) watcher for [jCasbin](https://github.com/casbin/jcasbin).

## Installation

**For Maven**

 ```
<dependency>
    <groupId>org.casbin</groupId>
    <artifactId>jcasbin-postgres-watcher</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
 ```

## Simple Example

if you have two casbin instances A and B

**A:**  **Producer**

```java
// Initialize PostgreSQL Watcher
String channel = "casbin_channel";
JCasbinPostgresWatcher watcher = new JCasbinPostgresWatcher(
    "jdbc:postgresql://localhost:5432/your_db",
    "postgres",
    "your_password",
    channel
);
// Support for advanced configuration with WatcherConfig
// WatcherConfig config = new WatcherConfig();
// config.setChannel(channel);
// config.setVerbose(true);
// config.setLocalId("instance-1");
// JCasbinPostgresWatcher watcher = new JCasbinPostgresWatcher(url, user, password, config);

Enforcer enforcer = new SyncedEnforcer("examples/rbac_model.conf", "examples/rbac_policy.csv");
enforcer.setWatcher(watcher);

// The following code is not necessary and generally does not need to be written unless you understand what you want to do
/*
Runnable updateCallback = () -> {
    // Custom behavior
};
watcher.setUpdateCallback(updateCallback);
*/

// Modify policy, it will notify B
enforcer.addPolicy(...);

// Using WatcherEx specific methods for fine-grained policy updates
// Add a policy
enforcer.addPolicy(...);
watcher.updateForAddPolicy(...);

```

**B:** **Consumer**

````Java
// Initialize PostgreSQL Watcher with same channel
String channel = "casbin_channel";
JCasbinPostgresWatcher watcher = new JCasbinPostgresWatcher(
    "jdbc:postgresql://localhost:5432/your_db",
    "postgres",
    "your_password",
    channel
);

Enforcer enforcer = new SyncedEnforcer("examples/rbac_model.conf", "examples/rbac_policy.csv");
enforcer.setWatcher(watcher);
// B set watcher and subscribe to the same channel, then it will receive the notification of A, and then call LoadPolicy to reload policy
````

## Getting Help

- [jCasbin](https://github.com/casbin/jCasbin)
- [pgjdbc](https://github.com/pgjdbc/pgjdbc)

## License

This project is under Apache 2.0 License. See the [LICENSE](https://github.com/jcasbin/redis-watcher/blob/master/LICENSE) file for the full license text.
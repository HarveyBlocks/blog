# Connection

## MongoClient

使用 `MongoClient` 类连接到MongoDB并与之通信

每个 `MongoClient` 代表一个具有线程安全性的**数据库连接池**, 因此一个应用只需要一个`MongoClient`

`MongoClient` 是Closable的

```java
try (MongoClient mongoClient = MongoClients.create(URI)) {
    // ...
}
```

也可以使用`MongoClientSettings`作为参数

```java
MongoClientSettings settings = MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(URI))
        .timeout(3, TimeUnit.SECONDS)
        .build();
try (MongoClient mongoClient = MongoClients.create(settings)) {
    // ...
}
```

设置连接池参数

```java
MongoClientSettings settings = MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(URI))
        .applyToConnectionPoolSettings(builder ->
                builder.maxWaitTime(3, TimeUnit.SECONDS)
                        .maxSize(5)
                        .maxConnectionIdleTime(10, TimeUnit.SECONDS)
        ).build();
try (MongoClient mongoClient = MongoClients.create(settings)) {
    execute(mongoClient);
}
```


# Demo

## 导入依赖

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.mongodb</groupId>
            <artifactId>mongodb-driver-bom</artifactId>
            <version>5.6.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

```xml
<dependencies>
    <!--可选-->
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.5.20</version>
    </dependency>
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-sync</artifactId>
    </dependency>
</dependencies>
```

## 代码

```java
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class MongoClientDemo {
    public static final String URI = "mongodb://MongoRoot:123456@centos:27017/test?authSource=admin";
    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
            MongoCollection<Document> collection = database.getCollection("movies");
            Document doc = collection.find(Filters.eq("title", "Back to the Future")).first();
            String docJson = Optional.ofNullable(doc)
                    .map(Document::toJson)
                    .orElseGet(() -> "No matching documents found.");
            System.out.println(docJson);
        }
    }
}
```


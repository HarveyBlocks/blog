# Paths与Files

## Path与Paths

>   JDK7引入

-   Path用来表示文件路径

-   Paths是工具类, 用来获取Path实例

    ```java
    Path dataPath = Paths.get(RESOURCE_PATH_PREFIX, "data.txt");
    System.out.println(dataPath);
    // C:\Users\27970\Desktop\IT\JDK\nio\src\main\resources\data.txt
    System.out.println(dataPath.normalize());
    // C:\Users\27970\Desktop\IT\JDK\nio\src\main\resources\data.txt
    
    Path targetPath = Paths.get(RESOURCE_PATH_PREFIX, "..\\target.txt");
    System.out.println(targetPath);
    // C:\Users\27970\Desktop\IT\JDK\nio\src\main\resources\..\target.txt
    System.out.println(targetPath.normalize());// 常规化路径
    // C:\Users\27970\Desktop\IT\JDK\nio\src\main\target.txt
    ```




## File与Files

### 判断文件存在

```java
System.out.println(Files.exists(targetPath));
```

### 创建文件夹

```java
try {
    Files.createDirectory(Paths.get("hello\\world"));
} catch (IOException e) {
}
```

-   文件夹同名文件存在, 且不是文件夹者, 报错: `FileAlreadyExistsException`
-   需要创建多级文件夹者, 报错: `NoSuchFileException`

#### 创建多级文件夹

```java
try {
    Files.createDirectories(Paths.get("hello\\world"));
} catch (IOException e) {
}
```

### 文件拷贝

```java
try {
    Files.copy(dataPath,targetPath,StandardCopyOption.REPLACE_EXISTING);
} catch (IOException e) {
    e.printStackTrace();
}
```

-   `StandardCopyOption.REPLACE_EXISTING`
    -   没有的话, 如果拷贝的目标文件已存在, 就会报错`FileAlreadyExistsException`
    -   加了参数之后表示**覆盖**
-   底层使用了操作系统的函数, 性能和`transferTo`差不多

### 文件移动

```java
Files.move(dataPath,targetPath,StandardCopyOption.ATOMIC_MOVE);
```

-   `StandardCopyOption.ATOMIC_MOVE`
    -   保证文件移动时的原子性

### 文件删除

```java
Files.delete(dataPath)
```

-   如果文件已存在, 就会报错`FileAlreadyExistsException`

### 删除目录

```java
Files.delete(dataPath)
```

-   如果目录内有内容, 就会报`DIrctoryNotEmptyException`

### 遍历目录

>   1.8

-   `walkFileTree(path,visit)`

    ```java
    Path rootPath = Paths.get("");
    System.out.println(rootPath);
    try {
        Files.walkFileTree(rootPath, new SimpleFileVisitor<>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) 
                throws IOException {
                System.out.println(file);
                return FileVisitResult.CONTINUE;
            }
        });
    } catch (IOException e) {
        e.printStackTrace();
    }
    ```

-   `walk(path).forEach()`

    ```java
    try (Stream<Path> walk = Files.walk(rootPath)) {
        walk.forEach(System.out::println);
    } catch (IOException e) {
        e.printStackTrace();
    }
    ```

-   拷贝文件夹

    ```java
    long start = System.currentTimeMillis();
    String source = "C:\\Users\\27970\\Desktop\\blog";
    String target = "C:\\Users\\27970\\Desktop\\target";
    try (Stream<Path> walk = Files.walk(Paths.get(source))) {
        walk.forEach((path -> {
            Path targetPath = Paths.get(path.toString().replace(source, target));
            try {
                if (Files.isDirectory(path)) {
                    Files.createDirectory(targetPath);
                }else if(Files.isRegularFile(path)){
                    // 是普通文件
                    Files.copy(path,targetPath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    } catch (IOException e) {
        e.printStackTrace();
    }
    long end = System.currentTimeMillis();
    log.debug("3.43G消耗: {} ms",end-start); // 7230
    ```

    




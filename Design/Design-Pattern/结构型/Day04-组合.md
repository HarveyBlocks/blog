# 组合

## 需求

文件和文件夹

文件夹下有文件

文件能读写, 文件夹不能

对于用户来说, 不想要区分节点是文件还是文件夹, 想要它们能有一个一致的操作

## 概念

>   Composite Pattern

又称为**部分-整体模式**

用于把一组相似的对象当成一个单一的对象(抽象父类中定义所有操作变量的方法, 子类选择性实现方法)

子类不能含有可能和外界产生关联的变量

组合模式依据树形结构(整体分为部分, 部分里又能分出整体)来组合对象, 用来表示部分以及整体层次

组合模式让客户端忽略层次的差异, 方便对整个层次进行大一统的操作

组合模式中增加新种树枝和新种叶子都不会产生侵入, 符合开闭原则

## 结构

-   抽象根节点
    -   Component
    -   定义系统各层次对象共有的方法和属性
    -   预定义一些默认的行为和属性
-   树枝节点
    -   Composite
    -   定义树枝节点的行为
    -   存储子节点
    -   组合树枝节点和叶子节点形成一个树形结构
-   叶子节点
    -   Leaf
    -   其下无分支
    -   系统层次遍历的最小对象单位

## 分类

### 透明组合模式

抽象节点角色中声明了所有用于管理对象的方法

确保所有的构建类都有相同的接口

是标准模式

但存在子类重载没有意义的父类方法造成错误, 这只能靠程序员的规范和小心来保证

### 安全组合模式

没有任何意义, 千万不要去了解

### 奇怪的想法

在抽象节点定义:

1.  判断是哪个类型的子类的方法
2.  将抽象父类型做转换, 转换成子类的方法
3.  这些方法都定义成finnal

在子类中定义子类的具体实现

已经完全失去组合模式的意义啦

## 文件树实现流程

### 根

```java
public abstract class FileRoot {

    private final String name;
    private int level;

    public FileRoot(String name) {
        this.name = name;
        this.level = 0;
    }

    public final int level() {
        return level;
    }

    public final String name() {
        return name;
    }

    protected void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "FileRoot{" +
                "name='" + name + '\'' +
                ", level=" + level +
                '}';
    }

    public boolean isFile() {
        return false;
    }

    public boolean isDirectory() {
        return false;
    }

    public FileRoot add(FileRoot node) {
        throw new UnsupportedOperationException("Can't add to a file");
    }

    public FileRoot remove(String name) {
        throw new UnsupportedOperationException("Can't remove to a file");
    }

    public Map<String, FileRoot> children() {
        return null;
    }
}
```

### 树枝

```java
public class DirectoryBranch extends FileRoot {
    private final Map<String, FileRoot> children;

    public DirectoryBranch(String name) {
        super(name);
        children = new HashMap<>();
    }

    @Override
    public final boolean isDirectory() {
        return true;
    }

    @Override
    public FileRoot add(FileRoot node) {
        node.setLevel(super.level() + 1);
        addLevel(node);
        children.put(node.name(), node);
        return this;
    }

    private static void addLevel(FileRoot node) {
        if (node.isDirectory()) {
            node.children().forEach((childName, root) -> {
                root.setLevel(node.level() + 1);
                if (root.isDirectory()) {
                    addLevel(root);
                }
            });
        }
    }

    @Override
    public FileRoot remove(String name) {
        return children.remove(name);
    }

    @Override
    public Map<String, FileRoot> children() {
        return children;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (FileRoot value : children.values()) {
            sb.append("\n").append("\t".repeat(value.level())).append(value).append(",");
        }
        return "DirectoryBranch{" +
                "super=" + super.toString() +
                ", children=" + sb +
                "} ";
    }
}
```

### 叶子

```java
public class FileLeaf extends FileRoot {
    public FileLeaf(String name) {
        super(name);
    }

    @Override
    public final boolean isFile() {
        return true;
    }

    @Override
    public String toString() {
        return "FileLeaf{" +
                "super=" + super.toString() +
                "} ";
    }
}
```

### 文件树

不是组合, 只是遍历用小工具

```java
public class FileTree {

    public static FileRoot createFileRoot(File directory) {
        FileRoot root = new DirectoryBranch(directory.getName());
        directoryScan(directory, root);
        return root;
    }

    private static void directoryScan(File directory, FileRoot root) {
        if (directory.isDirectory()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                String name = file.getName();
                if (file.isFile()) {
                    root.add(new FileLeaf(name));
                } else if (file.isDirectory()) {
                    DirectoryBranch branch = new DirectoryBranch(name);
                    root.add(branch);
                    directoryScan(file, branch);
                } else {
                    throw new RuntimeException("Unknown File State");
                }
            }
        }
    }

    public static void scanAndPrint(FileRoot root) {
        if (root.isFile()) {
            System.out.println(root.name());
        } else if (root.isDirectory()) {
            root.children().forEach((name, file) -> {
                System.out.println("\t".repeat(Math.max(0, file.level())) + name);
                if (file.isDirectory()) {
                    scanAndPrint(file);
                }
            });
        } else {
            throw new RuntimeException("Unknown File State");
        }
    }
}
```

### Demo

```java
public static void demo() {
    FileRoot root = createDemoTree();
    System.out.println(root.name());
    FileTree.scanAndPrint(root);
}

public static void printToString() {
    System.out.println(createDemoTree());
}

private static FileRoot createDemoTree() {
    return new DirectoryBranch("A")
            .add(new DirectoryBranch("B"))
            .add(new DirectoryBranch("C")
                    .add(new DirectoryBranch("a"))
                    .add(new DirectoryBranch("b"))
                    .add(new DirectoryBranch("c"))
                    .add(new DirectoryBranch("d"))
                    .add(new DirectoryBranch("e")
                            .add(new FileLeaf("u"))
                            .add(new FileLeaf("v"))
                            .add(new FileLeaf("w"))
                            .add(new FileLeaf("x"))
                            .add(new FileLeaf("y"))
                            .add(new FileLeaf("z"))
                    )
                    .add(new DirectoryBranch("D"))
                    .add(new FileLeaf("X"))
                    .add(new FileLeaf("Y"))
                    .add(new FileLeaf("Z")));
}

public static void file2Tree() {
    String filename = "C:\\Users\\27970\\Desktop\\study";
    FileRoot fileRoot = FileTree.createFileRoot(new File(filename));
    System.out.println(fileRoot);
}
```


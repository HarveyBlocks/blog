# 访问修饰

-   public
-   protected
-   internal
-   protected internal
-   private
-   专用受保护

## 摘要

### 访问修饰符-调用权限

| 调用方的位置           | `public` | `protected internal` | `protected` | `internal` | `private protected` | `private` | `file` |
| :--------------------- | :------: | :------------------: | :---------: | :--------: | :-----------------: | :-------: | :----: |
| 在文件中               |    ✔️️     |          ✔           |      ✔      |     ✔      |          ✔          |     ✔     |   ✔    |
| 在类内                 |    ✔️️     |          ✔           |      ✔      |     ✔      |          ✔          |     ✔     |   ❌    |
| 派生类（相同程序集）   |    ✔     |          ✔           |      ✔      |     ✔      |          ✔          |     ❌     |   ❌    |
| 非派生类（相同程序集） |    ✔     |          ✔           |      ❌      |     ✔      |          ❌          |     ❌     |   ❌    |
| 派生类（不同程序集）   |    ✔     |          ✔           |      ✔      |     ❌      |          ❌          |     ❌     |   ❌    |
| 非派生类（不同程序集） |    ✔     |          ❌           |      ❌      |     ❌      |          ❌          |     ❌     |   ❌    |

### 复合类型-可用访问修饰符

| 成员        | 默认成员可访问性 | 允许的成员的声明的可访问性                                   |
| :---------- | :--------------- | :----------------------------------------------------------- |
| `enum`      | `public`         | 无                                                           |
| `class`     | `private`        | `public`  `protected`  `internal`  `private`  `protected internal`  `private protected` |
| `interface` | `public`         | `public`  `protected`  `internal`  `private`  `protected internal`  `private protected` |
| `struct`    | `private`        | `public`  `internal`  `private`                              |

-   具有 `private` 可访问性的 `interface` 成员必须具有默认的实现。


# MethodHandler

>   方法句柄

Java7以来, 据说比原来的要快

MethodHandler创建时就必须做权限检查，而不是invoke运行时做权限检查

通过将method handle置为static final的变量，invoke甚至可以达到直接调用的效率

## 相关类

-   Lookup
    -   工厂, 用于创建MethodHandle
    -   检查工作是在创建时处理的，而不是在调用时处理。
-   MethodType
    -   代表方法的签名
    -   涉及返回值类型+参数列表
-   MethodHandle
    -   方法句柄，用于动态访问类型信息了

## MethodType

函数签名, 由返回值类型和参数列表组成

```java
Class<?> returnType = String.class;
Class<?> paramType = int.class;
// someMethod(int)->String
MethodType methodType = MethodType.methodType(returnType, paramType);
```

## Lookup

工厂, 用于创建MethodHandler



### 获取工厂

```java
// 凡是调用类支持的字节码操作，lookup都支持。
MethodHandles.Lookup lookup = MethodHandles.lookup();
```

```java
// 只能访问public成员
MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
```

在A类调用了`MethodHandles.XXLookup()`, 获取了Lookup, 再从lookup中反射出方法

作用域上能否通过, 就相当于在A类, 调用该方法能否在作用域上通过

### 使用

#### 获取字节码对象

```java
MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
try {
    Class<?> aClass = publicLookup.findClass("java.lang.Class");
    System.out.println(aClass);
} catch (ClassNotFoundException | IllegalAccessException e) {
    throw new RuntimeException(e);
}
```



## MethodHandler

### 从Lookup中创建

#### Public构造器

```java
MethodHandle constructor = lookup.findConstructor(targetClass, constructorType);
```

查找`TestTargetObject#TestTargetObject(String,int)`

```java
// 凡是调用类支持的字节码操作，lookup都支持。
MethodHandles.Lookup lookup = MethodHandles.lookup();
Class<TestTargetObject> targetClass = TestTargetObject.class;
// 获取构造器
Class[] paramTypes = {String.class, int.class};
Class<Void> returnType = void.class; // 构造器的返回值类型就是void
MethodType constructorType = MethodType.methodType(returnType, paramTypes);
try {
    MethodHandle constructor = lookup.findConstructor(targetClass, constructorType);

    System.out.println(constructor);
} catch (NoSuchMethodException | IllegalAccessException e) {
    throw new RuntimeException(e);
}
```

#### Public静态方法

```java
MethodHandle staticMethod = lookup.findStatic(targetClass, "valueOf",staticMethodType);
```



查找`String#valueOf(int)->String`

```java
// 凡是调用类支持的字节码操作，lookup都支持。
MethodHandles.Lookup lookup = MethodHandles.lookup();
Class<String> targetClass = String.class;
// 获取静态方法
Class[] paramTypes = {int.class};
Class<String> returnType = String.class; // 构造器的返回值类型就是void
MethodType staticMethodType = MethodType.methodType(returnType, paramTypes);
try {
    MethodHandle staticMethod = lookup.findStatic(targetClass, "valueOf",staticMethodType);
    System.out.println(staticMethod);
} catch (NoSuchMethodException | IllegalAccessException e) {
    throw new RuntimeException(e);
}
```

#### Public实例方法

```java
MethodHandle objectMethod = lookup.findVirtual(targetClass, "methodName",objectMethodType);
```

查找`String#substring(int)->String`

```java
// 凡是调用类支持的字节码操作，lookup都支持。
MethodHandles.Lookup lookup = MethodHandles.lookup();
Class<String> targetClass = String.class;
// 获取实例方法
Class[] paramTypes = {int.class};
Class<String> returnType = String.class; // 构造器的返回值类型就是void
MethodType objectMethodType = MethodType.methodType(returnType, paramTypes);
try {
    MethodHandle objectMethod = lookup.findVirtual(targetClass, "substring",objectMethodType);
    System.out.println(objectMethod);
} catch (NoSuchMethodException | IllegalAccessException e) {
    throw new RuntimeException(e);
}
```

#### 非Public的方法

```java
Method method = targetClass.getDeclaredMethod(methodName, paramTypes);
method.setAccessible(true);
// 使用本方法
MethodHandle protectedMethod = lookup.unreflect(method);
```


```java
try {
    MethodHandle protectedMethod = lookup.findVirtual(targetClass, methodName, objectMethodType);
    System.out.println("By findVirtual : " + protectedMethod);
} catch (NoSuchMethodException | IllegalAccessException ignore) {
    try {
        Method method = targetClass.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        // 使用本方法
        MethodHandle protectedMethod = lookup.unreflect(method);
        System.out.println("By unreflect : " + protectedMethod);
    } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new RuntimeException(e);
    }
}
```

#### 字段的读写

一些字段没有直接的读写的方法, 但是可以将其读写的过程看作一个方法, 一同封装为一个MethodHadler类

获取这种读写字段的MethodHandler的方法就是Getter和Setter(这个Getter和Setter和这个类中是否真实存在Getter和Setter无关)

下面以获取Getter为例

```java
MethodHandle getter = lookup.findGetter(targetClass, fieldName, fieldType);
```



```java
String fieldName = "publicField";
Class<?> fieldType = int.class;
try {
    MethodHandle getter = lookup.findGetter(targetClass, fieldName, fieldType);
    System.out.println(getter);
} catch (IllegalAccessException | NoSuchFieldException e) {
    throw new RuntimeException(e);
}
```

private字段的Getter和Setter的获取通过`Lookup#unreflectGetter`和`Lookup#unreflectSetter`

### 对方法调用执行

Static, 实例, 字段等方法的调用执行都一样

```java
Object methodResult = methodHandler.invoke(targetObject, param1,param2,...);
```

或

```java
MethodHandler methodHandlerBinded = methodHandler.bindTo(targetObject);
Object methodResult = methodHandler.invoke(param1,param2,...);
```

targetObject必须是满足:

```java
targetClass.isAssignableFrom(targetObject.getClass())==true
```

对方法执行的具体逻辑, 实际上使用的是targetObject里的逻辑, 以实现多态性



```java
// 凡是调用类支持的字节码操作，lookup都支持。
MethodHandles.Lookup lookup = MethodHandles.lookup();
Class<TestTargetObject> targetClass = TestTargetObject.class;
TestTargetObject targetObject = new TestTargetObject();
String fieldName = "publicField";
Class<?> fieldType = int.class;
int setterParam = 12;
try {
    // Setter
    MethodHandle setter = lookup.findSetter(targetClass, fieldName, fieldType);
    MethodHandle setterBindToTargetObject = setter.bindTo(targetObject);

    Object setterResult = setterBindToTargetObject.invoke(setterParam);
    System.out.println(setterResult);
    System.out.println(setter == setterBindToTargetObject);// false
    // Getter
    MethodHandle getter = lookup.findGetter(targetClass, fieldName, fieldType);
    Object getterResult = getter.invoke(targetObject);
    System.out.println(getterResult);


    System.out.println(((int) getterResult) == setterParam); // true
} catch (IllegalAccessException | NoSuchFieldException e) {
    System.err.println("reflect error: " + e.getMessage());
} catch (Throwable e) {
    System.err.println("invoke error: " + e.getMessage());
}
```



## JVM原理(大概?)

`MethodHandler#invoke`本身就是一个native

-   检查
    -   `MethodHandle`创建时就进行了类型检查
    -   `Method#invoke`每次调用都需要进行检查
-   `Method#invoke`是用数组包装参数的，每次都需要创建一个新的数组
-   内联
    -   `MethodHandle`在创建之后就是固定的，`MethodHandler#invoke`自身都可以被内联
    -   `Method#invoke`所有对方法的反射调用都需要经过层层函数调用，它自身就很难被内联到调用方

## Lookup#findSpecial(Class\<?\> reflectMethodFrom, String methodName, MethodType type,Class\<?\> SpecialCaller)

倒了八辈子血霉要搞这个东西

以下遵循优先级

1.  Lookup的声明位置和SpecialCaller的类型不同就抛异常
    -   既然如此为啥还要这个参数????
2.  reflectMethodFrom和SpecialCaller完全一致时, 总是不报错(自己类内调用本类成员肯定有权限啊)
3.  SpecialCaller可以不是reflectMethodFrom的子类, 但一定具有继承关系





操作同JVM指令`invokespecial`, `invokespecial`是用于实现`super`关键字的指令

其他就不知道了

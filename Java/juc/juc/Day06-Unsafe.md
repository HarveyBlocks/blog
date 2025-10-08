# Unsafe

提供底层的操控内存和线程的方法

Unsafe对象不能直接调用, 只能通过反射获取

just like this:

```java
class UnsafeAccess {
    static final Unsafe UNSAFE = Unsafe.getUnsafe();

    UnsafeAccess() {
    }
}
```

AtomicInteger, LockSupport都是调用Unsafe方法

## 模拟CAS

```java
public static void main(String[] args) {
    Unsafe unsafe = getUnsafe();
    long idOffset;
    long nameOffset;
    try {
        idOffset = getOffset(unsafe, Student.class, "id");
        nameOffset = getOffset(unsafe, Student.class, "name");
    } catch (NoSuchFieldException e) {
        throw new RuntimeException(e);
    }
    Student student = new Student(1, "A", new MyNumber(91));
    int oldId = unsafe.getInt(student, idOffset);
    String oldName = (String)  unsafe.getObject(student, nameOffset);
    System.out.println(student);
    boolean idSuccess = unsafe.compareAndSwapInt(student, idOffset, oldId, oldId + 1);
    boolean nameSuccess = unsafe.compareAndSwapObject(student, nameOffset, oldName, oldName + 1);
    System.out.println(idSuccess);
    System.out.println(nameSuccess);
    System.out.println(student);
}

private static long getOffset(Unsafe unsafe, Class<?> type, String filedName) throws NoSuchFieldException {
    return unsafe.objectFieldOffset(type.getDeclaredField(filedName));
}

private static Unsafe getUnsafe() {
    Unsafe unsafe;
    try {
        Field unsafeField = Unsafe.class
                .getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        unsafe = (Unsafe) unsafeField
                .get(null); // 静态字段, 没有实例
    } catch (IllegalAccessException | NoSuchFieldException e) {
        throw new RuntimeException(e);
    }
    return unsafe;
}
```

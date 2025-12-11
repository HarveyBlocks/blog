# 泛型和反射

- Class\<T\>&Constructor\<T\>

``` java
Class<Student> studentClass = Student.class;
Constructor<Student> constructor = studentClass.getConstractor();
Person person = constructor.newInstance();
```

对比:

```java
Class studentClass = Student.class;
Constructor constructor = studentClass.getConstractor();
Object object = constructor.newInstance()
```

见[运行时类型](..\java反射\Day30-运行时类型.md)


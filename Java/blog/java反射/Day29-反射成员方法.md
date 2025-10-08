# 反射成员方法



```java
private void say() {
    System.out.println("Student "+ name + " is "+ age + " now ");
}

@Override
private String toString() {
    return "Student{" +
            "name='" + name + '\'' +
            ", age=" + age +
            '}';
}

public String getName() {return name;}
public void setName(String name) {this.name = name;}
public int getAge() {return age;}
public void setAge(int age) {this.age = age;}
```

## 获取成员方法
Class类里的方法:

| Modifier and Type | Method                         | Description                         |
| ----------------- | ------------------------------ | ----------------------------------- |
| Method            | getDeclaredMethod(String name) | 依据标识符,返回public的成员方法对象 |
| Method[]          | getDeclaredMethods()           | 返回所有成员方法的数组              |
| Method            | getMethod(String name)         | 依据标识符,返回public的成员方法对象 |
| Method[]          | getMethods()                   | 返回所有public成员方法的数组        |
```java
public static void main(String[] args)
        throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {
    Class studentClass = Class.forName("LearnReflection.Student");
    System.out.println(
            studentClass.getMethod("setName", String.class)//抛出异常
    );

    Arrays.stream(studentClass.getMethods())
            .forEach(System.out::println);

    System.out.println(
            studentClass.getDeclaredMethod("toString")//抛出异常
    );

    Arrays.stream(studentClass.getDeclaredMethods())
            .forEach(System.out::println);
}
```

## 获取([获取到了的方法]的)信息的方法

Method中的方法:

```java
public String getName() {...}
public int getModifiers() {...}//获取权限修饰符,返回整形
public Class<?> getReturnType() {...}//获取方法的返回值类型
public Parameter[] getParameters(){...} //返回构造方法形参的数组
public Class<?>[] getExceptionTypes() {...}//返回抛出的异常的数组

public Object invoke(Object obj, Object... args)throws ...{...}//方法运行
```

### invoke()

```java
public static void main(String[] args)
        throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Class studentClass = Class.forName("LearnReflection.Student");
    Method studentSetName = studentClass.getMethod("setName", String.class);
    Student student = new Student();

    /*
    * Method中用于创建对象的方法
    * Object invoke (Object obj ,Object ....args){...}
    * 参数一:用obj调用该方法
    * 参数二:调用方法的传递参数(空参就不写)
    * 返回值:方法的返回值,没有就不写;有就把Object强转成返回值的类型
    * */

    System.out.println(studentSetName.invoke(student, "Mike"));//null
    //method.invoke(obj,args)=>obj.method(args);
    System.out.println(student.getName());//Mike
}
```

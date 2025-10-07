# 泛型与数组

- 泛型和数组是矛盾的,泛型会进行类型的擦除,而编译器会一直去持有数组的数据类型



- 可以声明带泛型的数组引用

```java
T[] t;//可以
ArrayList<String>[] listArray = new ArratList[5];//可以
ArrayList[] listArray = new ArratList[5];//可以,但有坏处,类型转换异常(和当初引入泛型的原因一样)
```

- 不能直接创建带泛型的数组对象

``` java
T[] t = new T[5];//不可以
ArrayList<String>[] listArray = new ArratList[5];//不可以
```

- 可以通过java.lang.reflect.Array的newInstance(Class<T>,int)创建T[]数组

## java.lang.reflect.Array的newInstance(Class<T>,int)

```java
public static Object newInstance(Class<?> componentType, int length)
    throws NegativeArraySizeException {
    return newArray(componentType, length);
}
```

```java
Student<String> students = new Student(String.class, length);
```

```java
import java.lang.reflect.Array;
public class Student<T> {
    private  T[] arrayT;
    private Class<T> prameterClass;
    private int length;
    public Student(Class<T> prameterClass,int length){
        arrayT =(T[]) Array.newInstance(prameterClass, length);
        this.prameterClass = prameterClass;
        this.length = length;
    }
    public void set(int index,T value) {arrayT[index] = value;}
	public T[] getArray(){return arrayT;}
    public T get(int index) {return arrayT[index];}
}    
```


```java
public static void main(String[] args) throws UnsupportedEncodingException {
    int length = 26;
    Student<String> students = new Student(String.class, length);
    for (int i = 0; i < length; i++) {
        students.set(i,new String(new byte[]{(byte)(i+65)},"ascii"));
    }
    for (int i = 0; i <length;  i++) {;
        System.out.print(students.get(i)+",");
    }
}
```
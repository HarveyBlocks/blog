# Lambda表达式

- JDK8
- 用于简化匿名内部类的代码形式

```java
(被重写方法的形参列表[无参就空着]) -> {
    被重写方法的方法体代码
}
```





```java
package LearnLambda;

/**
 * @author HarveyBlocks
 * @date 2023/09/04 18:13
 **/
public class Demo {
    public static void main(String[] args) {
        Animal dog = new Animal() {
            @Override
            public void run() {
                System.out.println("dog run");
            }
        };//得到了匿名内部类,是animal的子类

        dog.run();

        /*
    Animal cat = () -> {
            System.out.println("cat run");
    };
    qwq,编译异常
    */


abstract class Animal{
    public abstract void run();
}

```


这是因为:
- lambda表达式不能简化所有匿名内部类的写法
- 只能简化函数时接口的匿名内部类

## 函数式接口():

1. 接口
2. 接口里有且仅有一个抽象方法

### 注解@FunctionalInterface

- 函数式接口的注解
- 有这个注解的**一定**时函数式接口
- 函数式接口**不一定**有这个注解



```java
public class Demo {
    public static void main(String[] args) {Human I = new Human() {
            @Override
            public void study() {
                System.out.println("I study");
            }
        };

        I.study();


        Human you = () -> {
            System.out.println("you study");
        };

        you.study();
    }
}


@FunctionalInterface
interface Human{
    void study();
}
```

## 运用

### Array.sort(T[] a, Comparator<? super T> c)

```java
public static void main(String[] args) {
    Integer[] nums = {1, 4, 3, 5, 1};

    Arrays.sort(nums,new Comparator<Integer>(){
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.intValue()-o2.intValue();
        }
    });
    for (Integer num:nums) {
        System.out.print(num+",");
    }
    //升序

    System.out.println("\n-----------------------------");

    Arrays.sort(nums, (Integer o1, Integer o2)-> {
            return o2.intValue()-o1.intValue();
        });
    for (Integer num:nums) {
        System.out.print(num+",");
    }
    //降序
}
```

###  TreeSet(Comparator<? super E> comparator)


```java
TreeSet<Student> treeSet = new TreeSet<>(new Comparator<Student>() {

    @Override
    public int compare(Student o1, Student o2) {//先年龄,再成绩,最后名字
        int nameDifference = o1.getName().compareTo(o2.getName());
        int ageDifference = o1.getAge() - o2.getAge();
        int scoreDifference = (o1.getScore() - o2.getScore());

        return ageDifference == 0 ?
                scoreDifference == 0 ?
                        nameDifference :scoreDifference
                :ageDifference;

    }

});
```

```java
TreeSet<Student> treeSet = new TreeSet<>((Student o1, Student o2) -> {
    	//先年龄,再成绩,最后名字
        int nameDifference = o1.getName().compareTo(o2.getName());
        int ageDifference = o1.getAge() - o2.getAge();
        int scoreDifference = (o1.getScore() - o2.getScore());

        return ageDifference == 0 ?
                scoreDifference == 0 ?
                        nameDifference :scoreDifference
                :ageDifference;

});
```

## 进一步简化

- **参数类型可以省略**

- **如果只有一个参数**,参数类型省略的同时()可以省略

  - 如果不是一个参数 , 不能省略() ,举个例子:
	
	  ```java
	  /*
	  Arrays.sort(students,new Comparator<Student>() {
		@Override
	  	public int compare(Student student1, Student student2){
	  		return student1.getScore() - student2.getScore();
	  	}
	  });
	  */
	  
	  Arrays.sort(students,(student1, student2)->
	  	student1.getScore() - student2.getScore()
	  );//这是合理的
	  
	  Arrays.sort(students,student1,student2->
	  	student1.getScore() - student2.getScore()
	  );//这是不合理的
	  
	  Arrays.sort(students,Student student1, Student student2->
	  	student1.getScore() - student2.getScore()
	  );//这也是不合理的
	  ```
	  
	  
	
	
	
- **如果只有一行代码且这行代是`return <....>;`**,可以省略return , ; ,{}

- **遵守最后条但不遵守前两条也可以**

### 示例

1. 先建一个函数式接口

```java
public class Demo {
}
@FunctionalInterface
interface TryLambda{
    int test(int value);
}
```

2. 建一个输出的方法方便一会儿输出比较

``` java
private static void print(int num1,int num2,TryLambda tryLambda){
    System.out.println("num1 = " + num1);
    System.out.println("num2 = " + num2);
    System.out.println("---------------------------------");
    
    num1 = tryLambda.test(num2);
    
    System.out.println("num1 = " + num1);
    System.out.println("num2 = " + num2);
    System.out.println("=================================");
}
```

3. 1. 匿名内部类老老实实写:

``` java
TryLambda tryLambda1 = new TryLambda(){
    @Override
    public int test(int value) {
        return value;
    }
};
print(1, 0, tryLambda1);
```

3. 2. Lambda表达式之基础写法

```java
TryLambda tryLambda2 = (int value) -> {
        return value;
};
print(1, 0, tryLambda2);
```

3. 3. 参数类型省略

```java
TryLambda tryLambda3 = (value) -> {
    return value;
};
print(1, 0, tryLambda3);
```

3. 4. **如果只有一个参数**,参数类型省略的同时()可以省略

```java
TryLambda tryLambda4 = value -> {//不能只去括号,不去掉参数类型
    return value;
};
print(1, 0, tryLambda4);
```

3. 5. return,";",{}同时去掉

```java
TryLambda tryLambda5 = value -> value;//return,";",{}要去一起去,不能不明不白的
print(1, 0, tryLambda5);
```

3. 6. 遵守最后条但不遵守前两条也可以


```java
TryLambda tryLambda2 = (int value) ->value;
print(1, 0, tryLambda2);
```



4. 输出

结果都是一样哒!

## 利用函数式接口写一个转化器

```java
public static <RESOURCE,TARGET> List<TARGET> convert(List<RESOURCE> resources,Function<RESOURCE,TARGET> convertor){
    return resources.stream().map(convertor).collect(Collectors.toList());
}
```



-   使用:

```java
public static void main(String[] args) {
    ArrayList<Person> people = new ArrayList<>();
    people.add(new Person());
    convert(people,person->{
        Human human = new Human();
        human.setName(person.getName());
        human.setAge(person.getAge());
        return human;
    });
}
```

## JDK的几个典型的函数式接口

-   Supplier**无参有返回**供给型函数、
-   Consumer**有参无返回**消费型函数
-   Runnable**无参无返回**型函数
-   Function **有参有返回**型函数。

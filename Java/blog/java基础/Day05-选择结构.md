# 选择结构

## if-else

``` java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);

        while(true) {
            System.out.println("请输入");
            if (scanner.hasNextInt()) {
                int i = scanner.nextInt();
                System.out.println("输入的内容为整数" + i);
            } else if (scanner.hasNextDouble()) {
                double d = scanner.nextDouble();
                System.out.println("输入的内容是小数：" + d);
            } else if (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                System.out.println("输入的内容为字符串:" + str);
                break;
            }

        }

        scanner.close();
    }
}
```

直接输入一个字符串，成功的输出了

[图片]<src=>

### ？？？

先输入一个整型，再输入一个字符串的情况，str直接没有被赋到值

### 解决方法

- 因为在输入字符串之前输入别的东西，会遗留下一个\n，scanner就会把\n作为一个字符读进去

``` java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("请输入");
            if (scanner.hasNextInt()) {
                int i = scanner.nextInt();
                System.out.println("输入的内容为整数" + i);
            } else if (scanner.hasNextDouble()) {
                double d = scanner.nextDouble();
                System.out.println("输入的内容是小数：" + d);
            } else if (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                if (!str.isEmpty()) {       // 如果输入的内容不为空字符串
                    System.out.println("输入的内容为字符串:" + str);
                    break;
                }
            }

        }

        scanner.close();
    }
}

```

## swich

``` java
swich(expression){
    case value:
    	[语句]
    	break;
    case value1[,value2]://JDK8只允许一个,不允许增强的switch
    	[语句]
    	break;
    .....
    default:
    	[语句]
}
```

运用了swiich-case的赋值语句(JDK14qwq)

```java
String season = switch(month){
    case 1,2,3 -> "Winter";
    case 4,5,6 -> "Spring";
    case 7,8,9 -> "Summer";
    case 10,11,12 -> "Fall";
    default ->"???"; 
}
```


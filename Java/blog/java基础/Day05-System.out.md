System.out.print (),end=""

System.out.println(),end="\n"

System.out.printf()

``` java
String str="Java"; 
double pi=3.14; 
int i=100; 
//"%"表示进行格式化输出，其后是格式的定义* 
System.out.printf("%f\n",pi);
//"f"表示格式化输出浮点数* 
    System.out.printf("%d\n",i);
    //"d"表示格式化输出十进制整数* 
        System.out.printf("%o\n",i);
//"o"表示格式化输出八进制整数* 
    System.out.printf("%x\n",i);
//"x"表示格式化输出十六进制整数* 
    System.out.printf("%s\n",str);
//"s"表示格式化输出字符串* 
    System.out.printf("一个字符串：%s，一个浮点数：%f，一个整数：%d",str,pi,i);
//可以一次输出多个变量，注意顺序即可*
```


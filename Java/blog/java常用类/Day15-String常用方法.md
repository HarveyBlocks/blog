# String常用方法

### 注意

由于字符串具有不可变性以下方法皆不对其本身进行更改，而是返回一个新值，例如

```java
public class Main {
    public static void main(String[] args) {
        String str = "hiDE BEhind yoUR bACk";
        
        System.out.println(str.toUpperCase());
        //HIDE BEHIND YOUR BACK
        
        System.out.println(str);
        //hiDE BEhind yoUR bACk
        
        System.out.println(str.toLowerCase());
        //hide behind your back
        
        System.out.println(str);
        //hiDE BEhind yoUR bACk
    }
}
```



##  获取String信息

| Modifier Type | Method | Description |
| ------------- | ------ | ----------- |
| int      | length()| 返回字符串长度|

### length()

```java
public int length() {
    return value.length;
}
```

返回字符串长度 

```java
System.out.println("hide behind your back".length());//21
```


## 查找下标

| Modifier Type | Method | Description |
| ------------- | ------ | ----------- |
| int      | indexOf(String str) | 查找str首次出现的下标。存在，则返回该下标；不存在，则返回-1  |
| int      | lastIndexOf(String str) | 查找str最后一次出现的下标。存在，则返回该下标；不存在，则返回-1 |

### indexOf(String str)

```java
public int indexOf(String str) {
    return indexOf(str, 0);
}
```

```java
public int indexOf(String str, int fromIndex) {
    return indexOf(value, 0, value.length,
            str.value, 0, str.value.length, fromIndex);
}
```



查找str首次出现的，str的首字符一致的下标。存在，则返回该下标；不存在，则返回-1

```java
System.out.println("hide behind your back".indexOf("hi"));//0
//------------------↑---------------------
```

#### indexOf(String str, int fromIndex)

```java
System.out.println("hide behind your back".indexOf("hi",4));//7
```

### lastIndexOf(String str)

```java
public int lastIndexOf(String str) {
    return lastIndexOf(str, value.length);
}
```

```java
public int lastIndexOf(String str, int fromIndex) {
    return lastIndexOf(value, 0, value.length,
            str.value, 0, str.value.length, fromIndex);
}
```



查找str最后一次出现的，str的首字符一致的下标。存在，则返回该下标；不存在，则返回-1

```java
System.out.println("hide behind your back".lastIndexOf("hi"));//7
//-------------------------↑-----------------------------
```

#### lastIndexOf(String str, int fromIndex)

```java
System.out.println("hide behind your back".lastIndexOf("hi",4));//0
```






## 获取元素/切片

| Modifier Type | Method | Description |
| ------------- | ------ | ----------- |
| char     | charAt(int index)   | 根据下标获取字符  |
| String   | substring(int beginIndex, int endIndex)    | **切片！！！！！！！！！！！！qwq**  |

### charAt(int index)

```java
public char charAt(int index) {
    if ((index < 0) || (index >= value.length)) {
        throw new StringIndexOutOfBoundsException(index);
    }
    return value[index];
}
```

根据下标获取字符

```java
System.out.println("hide behind your back".charAt(0));//h
```

下标越界

```java
System.out.println("hide behind your back".charAt(21));
```

异常：***java.lang.StringIndexOutOfBoundsException***


### substring(int beginIndex, int endIndex)

```java
public String substring(int beginIndex, int endIndex) {
    if (beginIndex < 0) {
        throw new StringIndexOutOfBoundsException(beginIndex);
    }
    if (endIndex > value.length) {
        throw new StringIndexOutOfBoundsException(endIndex);
    }
    int subLen = endIndex - beginIndex;
    if (subLen < 0) {
        throw new StringIndexOutOfBoundsException(subLen);
    }
    return ((beginIndex == 0) && (endIndex == value.length)) ? this
            : new String(value, beginIndex, subLen);
}
```

**切片！！！！！！！！！！！！qwq**

```java
System.out.println(
        "subString".substring(0,2)
);//su

System.out.println(
        "subString".substring(1)
);//ubString
```

str.substring()
str.subSequence()一模一样

## 转化

| Modifier Type | Method | Description |
| ------------- | ------ | ----------- |
| char[]   | toCharArray()                                          | 将字符串转化为字符数组                                       |
| String[] | split(String regex)                                    | 分割字符串为列表                                             |
| String | public String(byte bytes[], String charsetName) | 编码转字符串 |

## String(byte[] ,"编码规则")

```java
public static void main(String[] args) throws UnsupportedEncodingException {
        String str1 = "你个二货";
        byte[] bytes = str1.getBytes("utf-8");//ASCII
        String str2 = new String(bytes,"GB2312");//US-ASCII
        System.out.println(str1+" -> "+str2);
        //你个二货 -> 浣�涓�浜�璐�
}
```



### toCharArray()

```java
public char[] toCharArray() {
    // Cannot use Arrays.copyOf because of class initialization order issues
    char result[] = new char[value.length];
    System.arraycopy(value, 0, result, 0, value.length);
    return result;
}
```

将字符串转化为字符数组

```java
import java.sql.Array;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        char[] charList="hide behind your back".toCharArray();
        System.out.println(charList);
        System.out.println(Arrays.toString(charList));
        for (char charValue:charList) {
            System.out.print(charValue);
        }
    }
}
```

输出结果：


```java
/*
hide behind your back
[h, i, d, e,  , b, e, h, i, n, d,  , y, o, u, r,  , b, a, c, k]
hide behind your back
*/
```
### split(String regex)

```java
public String[] split(String regex) {
    return split(regex, 0);
}
```

分割字符串为列表

```java
import java.sql.Array;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
       
        System.out.println(
                Arrays.toString(
                        "hide behind your back"
                                .split(" ")
                )
        );
        //[hide, behind, your, back]
        
        //多种分隔符的情况
        
        System.out.println(
                Arrays.toString(
                        "Hide behind yours.Your what?Your back."
                                .split("[ .?]")//算是正则表达式
                )
        );
        //[Hide, behind, yours, Your, what, Your, back]
    
        
      //连续多个分隔符的情况  
        
       System.out.println(
                Arrays.toString(
                        "hide..behind.your.....back....."
                                .split("[.]+")
                )
        );
        //[hide, behind, your, back]
    }
}
```


## 修改(含删除)

| Modifier Type | Method | Description |
| ------------- | ------ | ----------- |
| String   | trim()                                                 | 去掉字符串前后的空格                                         |
| String   | toUpperCase()                                          | 将小写转换成大写                                             |
| String   | toLowerCase()                                          | 将大写转换成小写                                             |
| String   | replace(CharSequence target, CharSequence replacement) | 将target字符串转化为replacement字符串（字符转字符也行）      |



### trim()

```java
public String trim() {
    int len = value.length;
    int st = 0;
    char[] val = value;    /* avoid getfield opcode */

    while ((st < len) && (val[st] <= ' ')) {
        st++;
    }
    while ((st < len) && (val[len - 1] <= ' ')) {
        len--;
    }
    return ((st > 0) || (len < value.length)) ? substring(st, len) : this;
}
```

去掉字符串**前后**的空白符,但不可以去除大于U+0020的空白符

```java
System.out.println("         hide behind your back        ".trim());
```

#### scrip(),但是版本好像不允许

 去掉字符串**前后**的空白符,但可以去除大于U+0020的空白符

```java
System.out.println("         hide behind your back        ".trim());
```



### toUpperCase()/toLowerCase()

- 不会对原字符串做更改,而是返回一个新的字符串

```java
public String toUpperCase() {
    return toUpperCase(Locale.getDefault());
}


public String toLowerCase() {
    return toLowerCase(Locale.getDefault());
}
```

将小写转换成大写/将大写转换成小写

```java
System.out.println("hide behind your back".toUpperCase());
//HIDE BEHIND YOUR BACK
System.out.println("hiDE BEhind yoUR bACk".toLowerCase());
//hide behind your back
```
### replace(CharSequence target, CharSequence replacement)

```JAVA
public String replace(CharSequence target, CharSequence replacement) {
    return Pattern.compile(target.toString(), Pattern.LITERAL).matcher(
            this).replaceAll(Matcher.quoteReplacement(replacement.toString()));
}
```

将target字符串转化为replacement字符串（字符转字符也行）

```java
System.out.println("hide behind your back".replace("hi","HIIIIIII"));
//HIIIIIIIde beHIIIIIIInd your back


System.out.println("hide behind your back".replace('h','H'));
//Hide beHind your back
```



## 判断

| Modifier Type | Method | Description |
| ------------- | ------ | ----------- |
| boolean  | contains(CharSequence s)                               | 判断本字符串是否包含某个子字符串                             |
| boolean  | endsWith(String suffix)                                | 判断字符串是否以suffix结尾                                   |
| boolean  | startsWith(String suffix)                              | 判断字符串是否以suffix开头                                   |

### contains(CharSequence s)

```java
public boolean contains(CharSequence s) {
    return indexOf(s.toString()) > -1;
}
```

判断本字符串是否包含某个子字符串

```java
System.out.println("hide behind your back".contains("hi"));//true
```

### endsWith(String suffix)/startsWith(String suffix)

```java
public boolean endsWith(String suffix) {
    return startsWith(suffix, value.length - suffix.value.length);
}


public boolean startsWith(String prefix) {
    return startsWith(prefix, 0);
}
```

判断字符串是否以suffix结尾/判断字符串是否以suffix开头

```java
System.out.println("hiDE BEhind yoUR bACk".endsWith("Ck"));//true
System.out.println("hiDE BEhind yoUR bACk".endsWith("cK"));//false


System.out.println("hiDE BEhind yoUR bACk".startsWith("hi"));//true
System.out.println("hiDE BEhind yoUR bACk".startsWith("HI"));//false
```


## 比较

| Modifier Type | Method | Description |
| ------------- | ------ | ----------- |
| boolean  | equals(Object anObject)                                | 比较                                                         |
| boolean  | equalsIgnoreCase(String anotherString)                 | 比较忽略大小写                                               |
| int      | compareTo(String anotherString) | 比较，返回字典表差值         |


### equals(Object anObject)

见<String类>

#### equalsIgnoreCase(String anotherString)

```java
public boolean equalsIgnoreCase(String anotherString) {
    return (this == anotherString) ? true
            : (anotherString != null)
            && (anotherString.value.length == value.length)
            && regionMatches(true, 0, anotherString, 0, value.length);
}
```

比较忽略大小写

```java
System.out.println("hELLO".equalsIgnoreCase("Hello"));//ture
```

### compareTo

```java
public int compareTo(String anotherString) {
    int len1 = value.length;
    int len2 = anotherString.value.length;
    int lim = Math.min(len1, len2);
    char v1[] = value;
    char v2[] = anotherString.value;

    int k = 0;
    while (k < lim) {
        char c1 = v1[k];
        char c2 = v2[k];
        if (c1 != c2) {
            return c1 - c2;
        }
        k++;
    }
    return len1 - len2;
}
```

逐个字符比较，相等继续向下比，直到遇到不相等的。

不相等就返回两个字符的在字典表上差值，原减新。

返回差值后，就不再比较

如下：

```java
System.out.println("1311432".compareTo("21453"));//-1
System.out.println("21352".compareTo("11325"));//1
```

全部相等的：

```java
System.out.println("1".compareTo("1"));//0
```

如遇前面全部相同，长度不同者，比较长度。

返回长度的差值：

```java
System.out.println("1".compareTo("123"));//-2
System.out.println("123".compareTo("1"));//2
```

#### compareToIgnoreCase(String str)

忽略大小写



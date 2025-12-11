String[] list=str.split("分隔符")

换/删

String str1=str.replace("不对的","换上去的")

str转int

int i = Integer.parseInt([String]); 

或 i = Integer.parseInt([String],[int radix]);

2、 int i = Integer.valueOf(my_str).intValue();

取消转义

```java
private static String decode(char c) {
    String result;
    switch (c) {
        case '\\':result = "\\\\" ; break;
        case '\b':result = "\\b"  ; break;
        case '\f':result = "\\f"  ; break;
        case '\n':result = "\\r\\n"; break;
        case '\r':result = "\\r"  ; break;
        case '\t':result = "\\t"  ; break;
        case '\'':result = "\\'"  ; break;
        case '\"':result = "\\\"" ; break;
        case '\0':result = "\\0"  ; break;
        default:  result = c + "" ;
    }
    return result;
}
private static String decode(String str) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
        result.append(decode(str.charAt(i)));
    }
    result.append("\\0");
    return result.toString();
}
```


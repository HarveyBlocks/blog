# 枚举处理器

>   status=0 正常
>
>   status=1 异常

0和1不利于阅读和维护

## 作用

将枚举代替数字

MyBatis的TypeHandler(一推处理器)将数据类型(**Bool,数值,字符串,BigDecimal等**)改变成数据库的类型

MyBatisPlus的TypeHandler将**枚举和Json类型**的数据转化成数据库的类型

## 使用

### 枚举类

```java
public enum Gender {

    FEMALE("女","女性"),MALE("男", "男性");

    @EnumValue
    private final String value;// 这里由于之前创建表的时候的限制, 以后还是用int
    private final String desc;// description


    Gender(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
```

### 配置枚举处理器

```yaml
mybatis-plus:
    default-enum-type-handler: com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler
```

###改实体类的字段

```java
private Gender gender;
public void setGender(Gender gender) {
    this.gender = gender;
}
public Gender getGender() {
    return gender;
}
```

### 改变对字段的使用方式

```java
user.setGender(Gender.FEMALE);
```


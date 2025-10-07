package LearnAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyTest2 {
    int value();
    //一旦,唯一属性名,且为value,即为特殊属性,
    //就可以在注解里省略"value"
        //否则(违反任意一条),则不能省略

    //但是,当增加一条有默认值的属性
    int num() default 0;
    //也可以省略value
}


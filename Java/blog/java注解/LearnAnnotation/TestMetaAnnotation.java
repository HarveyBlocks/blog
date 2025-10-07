package LearnAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//元注解--注解注解的注解
@Retention(RetentionPolicy.RUNTIME)
//声明注解的存在周期
//RetentionPolicy.SOURCE--只作用在源码阶段,运行阶段该注解将不存在
//RetentionPolicy.CLASS--默认值保留到字节码阶段,运行阶段不存在
//RetentionPolicy.RUNTIME--一直保留到运行状态(开发常用)
@Target({ElementType.TYPE,ElementType.METHOD,ElementType.FIELD})
//@Target({参数一,参数二,...}}声明该注解能注解的位置只有"参数一","参数二"
@Documented
//被@Documented注解的注解,将在形成最终的文件时被保留
public @interface TestMetaAnnotation {
    String value();
}
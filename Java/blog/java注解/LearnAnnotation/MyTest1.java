package LearnAnnotation;

//自定义注解
//注解的本质是一个继承了Annoattion的接口
public @interface MyTest1 {
    //应该加上()
    String name();//这些属性其实是抽象方法
    boolean isOpen() default true;
    String[] skills();
}

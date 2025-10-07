package LearnAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author HarveyBlocks
 * @date 2023/09/26 13:27
 **/
@MyTest2(value = 2)//可以直接给value的值;
public class AnnotationTest2 {
    /*注解的解析:
     * 就是判断类上,方法上.成员变量上是否存在注解,并把注解里的内容解析出来
     *
     * 要如何解析注解?:
     * - 指导思想:要解析谁上面的注解,就应该先拿到谁
     * - 要解析类上的注解,则应该获取该类的Class对象,再通过Class对象解析其上面的注解
     * Class,Method,Field,Constructor,都实现了AnnotateElement皆苦,他们都拥有解析注解的能力
     * - 要解析成员方法上的注解,则应该获取该成员方法的Method对象,再通过Method对象解析其上面的注解
     * */

    //解析AnnotationTest1.java里的注解
    @TestMetaAnnotation("二货")
    public static void main(String[] args) {
        Class c = AnnotationTest1.class;
        MyTest1 myTest1 = (MyTest1) c.getDeclaredAnnotation(MyTest1.class);//所有注解
        System.out.println(myTest1);
        Method[] Methods = c.getDeclaredMethods();

        for (Method method:Methods) {
            if (method.isAnnotationPresent(MyTest2.class)){
                System.out.println(method.getName()+" is Annotated MyTest2");
                MyTest2 myTest2 =method.getDeclaredAnnotation(MyTest2.class);
                System.out.println("myTest2.value = " + myTest2.value());
                System.out.println("myTest2.num = " + myTest2.num());
            }else {
                System.out.println(method.getName()+" isn't Annotated MyTest2");
            }
        }
    }
}

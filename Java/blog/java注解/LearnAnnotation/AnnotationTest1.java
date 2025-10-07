package LearnAnnotation;

/**
 * @author HarveyBlocks
 * @date 2023/09/26 13:14
 **/
//可以用注解标记类,成员变量,方法,构造器等

//注解类
//--------------------------------------↓有默认值的,可以不在这里写
@MyTest1(name = "Mike",skills = {"sing","dance","rap","basketball"})
//@一个注解其实在创建注解的实现类对象,封装注解里的属性值
//可以调用注解里的方法来调用注解里的属性值的
public class AnnotationTest1 {
    //注解成员变量
    @MyTest1(name = "John",skills = {"guitar","violin"})
    int sum;

    //注解方法
    @MyTest2(value = 2,num = 25)
    public void say(){
        System.out.println("hello");
    }

    //注解构造器
    @MyTest1(
            name = "Oliver",isOpen = false,skills = {
                "football","soccer"
            }
    )
    public AnnotationTest1(){

    }

    @TestMetaAnnotation("王五")
    public static void main(String[] args) {
        new AnnotationTest1().say();
    }

}

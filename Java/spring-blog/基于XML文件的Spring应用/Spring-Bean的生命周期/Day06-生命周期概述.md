# Spring Bean的生命周期

>   Bean->BeanDefinition->Object->singletonObjects

1.  从Bean 实例化之后
    -   即通过反射创建出对象之后
2.  到Bean成为一个**完整对象**
    -   例如:
        -   **属性注入前就不能是完整对象**
        -   进行AOP增强
        -   还有很多步要走
3.  最终存储到单例池中

## Bean的实例化阶段

**Spring框架会取出BeanDefinition的信息进行判断**

-   BeanFactoryPostProcessor
    -   BeanDefinitionRegistryPostProcessor
-   当前Bean的范围是否是singleton的
-   是否不是延迟加载的
-   是否不是FactoryBean
-   等

最终将一个**普通的singleton的Bean**通过反射进行实例化

## Bean的初始化阶段

**Bean创建之后还仅仅是个“半成品”**，

-   还需要对Bean实例的属性进行填充
-   执行一些Aware接口方法
-   执行BeanPostProcessor方法
-   执行InitializingBean接口的初始化方法
-   执行自定义初始化init方法
-   等。

该阶段是Spring**最具技术含量和复杂度**的阶段,涉及

-   Aop增强功能
-   Spring的注解功能
-   等

### Bean的循环引用问题



## Bean的完成阶段

Bean成为了一个完整的Spring Bean

被存储到单例池singletonObjects中去了

即**完成了Spring Bean的整个生命周期**。

## Bean的销毁阶段

见名知意


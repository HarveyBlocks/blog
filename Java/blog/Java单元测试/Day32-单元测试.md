# Junit

-   用psvm测试之坏处

    ![image-20231029140915283](../../assets/Untitled/image-20231029140915283.png)

IDEA已经集成了Junit

## 优点

![image-20231029141033993](../../assets/Untitled/image-20231029141033993.png)

## 使用

![image-20231029141314389](../../assets/Untitled/image-20231029141314389.png)





对AaaaAaAaa类的测试类->AaaaAaAaaTest

对aaaAaaAaa方法的测试方法->testAaaAaaAaa



对有参数的方法,测试的时候传个null



## 断言机制

断言->预测

```java
@Test
public void tT(){
    Assert.assertEquals("方法内部有Bug",12,AppTest.t(12));
}
public static int t(int a) {
    System.out.println(a);
    return a;
}
```

-   ok↑

-   不ok↓

```test
junit.framework.AssertionFailedError: 方法内部有Bug expected:<1> but was:<12>
预期:1
实际:12
```

## 常用注解



![image-20231029145418402](../../assets/Untitled/image-20231029145418402.png)

```
@BeforeClass
	@Before
	@Test
	@After

	@Before
	@Test
	@After

	@Before
	@Test
	@After

	...
@AfterClass
```

![image-20231029150158876](../../assets/Untitled/image-20231029150158876.png)

玩弄我感情是吧


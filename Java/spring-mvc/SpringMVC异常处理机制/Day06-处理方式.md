## 处理方式

-   简单异常处理器:
    -   SimpleMappingExceptionResolver
-   自定义异常处理器
    -   实现HandlerExceptionResolver
-   注解方式(用的最多)
    -   @ControllerAdvicer + @ExceptionHandler

### 模拟异常产生

```java
@RestController
public class ExceptionController{
    @RequestMapping("/ex1")
    public String ex1() {
        int i = 1/0;
        return "1/0";
    }

    @RequestMapping("ex2")
    public String ex2() throws Exception {
        try {
            int i = 1/0;
        }catch (Exception e){
            throw new Exception("I want to throw an Exception, then I do it.");
        }
        return "Exception";
    }
}
```

### 简单异常处理器

-   SpringMVCConfig.java

    -   不管什么异常,都统一响应同一个视图

        1.  创建一个一场解析器
        2.  指定异常解析器的视图
        3.  将一场解析器放入SpringMVC容器

        ```java
        @Bean
        public SimpleMappingExceptionResolver simpleMappingExceptionResolver(){
            SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
            resolver.setDefaultErrorView("/exp.jsp");
            return resolver;
        }
        ```

    -   区分异常的类型, 根据不同的异常类型,跳转不同的视图

        ```java
        @Bean
        public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
        
            SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
            Properties properties = new Properties();//键值对 key-异常全限定名 value-跳转视图名
            properties.setProperty("java.lang.RuntimeException","exp0.jsp");
            properties.setProperty("java.lang.Exception","exp1.jsp");
            //含有继承关系的,如即使RuntimeException也是Exception,
            //但它不会直接跳到Exception,而是会跳到RuntimeException
            resolver.setExceptionMappings(properties);
            //DefaultErrorView可以用来兜底
            resolver.setDefaultErrorView("/exp0.jsp");
        
            return resolver;
        }
        ```

目前还无法让前端显示错误的具体信息



### 自定义异常处理器

>   实现HandlerExceptionResolver

1.  创建一个类
2.  实现HandlerExceptionResolver
3.  响应友好的页面

-   不做异常的区分

    ```java
    package com.harvey.exp;
    
    import ...
    
    /*description*/
    @Component //不属于Service,不属于Dao ,不属于web层
    public class MyHandlerExceptionResolver implements HandlerExceptionResolver {
        @Override
        public ModelAndView resolveException(
            HttpServletRequest request, 
            HttpServletResponse response,
            Object handler, Exception ex) {
            //简单的响应一个友好提示页面
            /*
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("/exp0.jsp");
            */
            return new ModelAndView("/exp0.jsp");
        }
    }
    ```

-   做简单的区分

    ```java
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //简单的响应一个友好提示页面
        ModelAndView modelAndView = new ModelAndView();
        try {
            throw ex;
        }catch (RuntimeException e){
            modelAndView.setViewName("/exp0.jsp");
        }catch (Exception e){
            modelAndView.setViewName("/exp1.jsp");
        }
        return modelAndView;
    }
    ```

    这不比if instandof 好用多了(doge)





#### 前后端分离,响应Json格式的字符串

-   想要输出的Json

    ```json
    {
        "code":404,
        "message":"未找到页面",
        "data":"这个地方看需求吧",
    }
    ```

-   pojo.Result

    普普通通实体类

    ```java
    public class Result {
        private Integer code;
        private String message;
        private Object data;
    
        ....
            
    }
    ```

-   尝试响应Json字符串

    这里的代码没有格式,也不严谨, 只是自己大概写的

    ```java
    @Override
    public ModelAndView resolveException(
        HttpServletRequest request, 
        HttpServletResponse response, 
        Object handler, Exception ex) {
        //简单的响应一个Json字符串
        //这些是瞎写的
        try{
            throw ex;
        }catch (IOException ioException){
            Log.err(ioException);
            //意义不明
        }catch (Exception e){
            Log.err(e);
        }
        try {
            Result result = new Result(200,ex.getMessage(),"data is what");
            String string = new ObjectMapper().writeValueAsString(result);
            response.getWriter().write(string);
            //这个写法让人难过,所以不好
        } catch (IOException e) {
            resolveException(request,response,handler,ex);
            //我瞎写的,我只是觉得这个异常处理器里会产生异常还是太抽象了
        }
        return null;
    }
    ```

### 灵活的注解方式

-   能响应视图

-   能返回Json字符串

    ```java
    @ControllerAdvice //AOP增强的Controller
    public class GlobalExceptionHandler {
    
        @ExceptionHandler(RuntimeException.class)
        public ModelAndView runtimeExceptionResolve(RuntimeException re) {
            Log.err(re.getMessage());
            //简单的响应一个友好提示页面
            return new ModelAndView("/exp0.jsp");
        }
    
        @ExceptionHandler(Exception.class)
        @ResponseBody
        public Result ExceptionResolve(Exception e) throws JsonProcessingException {
            Log.err(e.getMessage());
            //简单的响应一个json字符串
            return new Result(0, e.getMessage(), "data is what");
        }
    }
    ```

    




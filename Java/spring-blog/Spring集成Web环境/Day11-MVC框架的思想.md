[MVC模式和三层架构](../../blog/java网络/网络/Day41-MVC模式和三层架构.md)

![image-20231120174007697](../../assets/Day11-SpringMVC思想/image-20231120174007697.png)

-   一个页面Servlet只有一个,不合理
-   Servlet获取请求,然后响应,太套路
-   就像Dao注入Service一样,把Service注入Web层

![image-20231120174212251](../../assets/Day11-SpringMVC思想/image-20231120174212251.png)

![image-20231120174338074](../../assets/Day11-SpringMVC思想/image-20231120174338074.png)

-   这个共有的Servlet被称为前端控制器


# 远程调用

A项目对B项目产生依赖, 它需要B依赖的Bean

这俩服务在网络上是相通的, 可以通过网络相连

A项目可以对B项目发送请求

## RestTemplate工具

Spring提供,可以方便地发送http请求

### 使用远程调用

1.  注册RestTemplate的Bean

    ```java
    @Configuration
    public class BeanRegisterConfig {
        @Bean
        public RestTemplate restTemplate(){
            return new RestTemplate();
        }
    }
    ```

2.  拷贝实体类

    <img src="../../assert/Day02-%E8%BF%9C%E7%A8%8B%E8%B0%83%E7%94%A8/image-20240107120415053.png" alt="image-20240107120415053" style="zoom:77%;" />

3.  注入

    ```java
    @Resource
    private RestTemplate restTemplate;
    ```

4.  调用

    ```java
    String placeholders = "ids";// 占位符
    String url = String.format("http://localhost:8081/items?ids={%s}", placeholders);
    HttpMethod method = HttpMethod.GET;// enum
    HttpEntity<ItemDTO> requestEntity = null;// 请求实体,对于简单请求直接为null
    
    // Class<ItemDTO> responseType = ItemDTO.class;单个类型可以直接用Class做参数
    
    // 但是由于是集合, 不能直接用泛型,类型会擦除, 也不能之列List.class, 就不知道转成了个啥了
    ParameterizedTypeReference<List<ItemDTO>> responseType =
              new ParameterizedTypeReference<>() {};
    // Parameterized泛Type型Reference引用
    Map<String, String> uriVariables = Map.of(placeholders, CollUtil.join(itemIds, ","));
    // hutool
    // 2.查询商品
    // 自动把Json的字符串反序列化
    ResponseEntity<List<ItemDTO>> responseEntity = restTemplate
            .exchange(url, method, requestEntity, responseType, uriVariables);
    if (!responseEntity.getStatusCode().is2xxSuccessful()){
        return;
    }
    List<ItemDTO> items = responseEntity.getBody();
    if (CollUtils.isEmpty(items)) {
        return;
    }
    // 3.转为 id 到 item的map
    ```


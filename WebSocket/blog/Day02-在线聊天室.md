# 在线聊天室

## 需求

-   用户名系统
    -   这里不做认证授权
    -   不存数据库
    -   显示本用户用户名
-   显示**在线/离线**提示
-   好友列表
    -   现在在线的所有用户
    -   显示在线用户的用户名
-   聊天界面
    -   显示聊天内容
    -   显示正在聊天的用户的用户名



## 实现流程

------------------------------------------------------------------------------------------------------------------------------------------==@OnClose==

![image-20231230134913166](../assert/Day02-%E5%9C%A8%E7%BA%BF%E8%81%8A%E5%A4%A9%E5%AE%A4/image-20231230134913166.png)



## 消息格式

-   客户端->服务端

    ```json
    {
        "toName": "张三",
        "message": "你好"
    }
    ```

    客户端向`张三`发送`你好`的消息

-   服务端->浏览器

    -   系统消息

        ```json
        {
            "system": true,// 是否是系统消息
            "fromName": "null", 
            "message": ["李四","王五"]
        }
        ```

        返回在线用户信息

    -   用户消息

        ```json
        {
            "system": false,
            "fromName": "李四",
            "message": "你好"
        }
        ```

        `李四`发来的`你好`的消息





##代码实现

>   spring-boot整合webSocket

###用户系统

#### 实体类



```java
package com.harvey.hotel.pojo.entity;

/**
 * 用户实体类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2023-12-30 14:19
 */
public class User {
    private String userId;
    private String password;
    private String username;

	Getter & Setter
}
```



```java
package com.harvey.hotel.pojo.result;

/**
 * 用于封装响应数据的实体类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2023-12-30 14:19
 */
public class Result {
    private boolean flag;
    private String message;
	Getter & Setter
}
```





####用户Controller

```java
package com.harvey.hotel.controller;

import com.harvey.hotel.pojo.entity.User;
import com.harvey.hotel.pojo.result.Result;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 用户Controller
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2023-12-30 14:17
 */
@RestController
@RequestMapping("user")
public class UserController {
    public static final String USERNAME_SESSION_KEY = "user";

    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpSession session) {
        Result result = new Result();
        if (user != null && "123".equals(user.getPassword())) {
            result.setFlag(true);
            //将数据存入Session
            session.setAttribute(USERNAME_SESSION_KEY, user.getUsername());
        } else {
            result.setFlag(false);
            result.setMessage("登录失败");
        }
        return result;
    }

    @GetMapping("/getUsername")
    public String getUsername(HttpSession session) {
        return (String) session.getAttribute(USERNAME_SESSION_KEY);
    }
}
```



### 消息与Json互转

#### 消息实体类

-   客户端->服务器

```java
public class Message {
    private String toName;
    private String message;
}
```

#### 响应消息实体类

-   服务器->客户端

```java
public class ResultMessage {
    private boolean isSystem;
    private String fromName;
    private Object message;
    Getter & Setter
}
```

####消息工具类

使用FastJson

```java
/**
 * 消息工具类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2023-12-30 14:29
 */
public class MessageUtils {
    public static String getResultMessage(boolean isSystem ,String fromName, Object message){
        ResultMessage result = new ResultMessage();
        result.setSystem(isSystem);
        result.setMessage(message);
        result.setFromName(fromName==null?"null":fromname);
        return JSON.toJSONString(result);
    }
    public static Message getMessage(String json){
        return JSON.parseObject(json,Message.class);
    }
}
```



###引入坐标

```xml
<!--WebSocket-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

`spring-boot-starter-web`也是要的





### 编写配置类

####注册`ServerEndpointExporter`

-   注册`ServerEndpointExporter`的Bean
    -   `ServerEndpointExporter`, 用来**扫描注解了@ServerEndpoint的类**

```java
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        // 注入ServerEndpointExporter
        return new ServerEndpointExporter();
    }
}
```

#### 获取HttpSession

-   WebSocket无法直接获取使用`HttpSession`

```java
/**
 * 获取HttpSession的配置类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2023-12-30 15:11
 */
public class GetHttpSessionConfig extends ServerEndpointConfig.Configurator {
    /**
     * 握手
     *
     * @param sec      配置对象
     * @param request  握手请求对象
     * @param response 握手响应对象
     */
    @Override
    public void modifyHandshake(
        ServerEndpointConfig sec, 
        HandshakeRequest request, 
        HandshakeResponse response) {
        // 获取HttpSession
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        // 将HttpSession保存起来, 存到ServerEndpointConfig对象里面
        Map<String, Object> userProperties = sec.getUserProperties();
        userProperties.put(
                HttpSession.class.getName(),// 唯一即可
                httpSession
        );
    }
}
```

### 编写Endpoint

```java
package com.harvey.hotel.controller;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * WebSocket聊天
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2023-12-30 13:37
 */
@ServerEndpoint("/chat")
@Component
public class ChatEndpoint {
    @OnOpen
    public void onOpen(Session session, EndpointConfig config){
        // 连接建立时被调用
        // 与Http不同的是, Http的会话是HttpSession
    }
    @OnMessage
    public void onMessage(String message){
        // 接收到客户端发送的数据时被调用
    }
    @OnClose
    public void onClose(Session session){
        // 连接关闭时被调用
    }
}
```

####onOpen

##### onOPen主要方法



![image-20231230152632195](../assert/Day02-%E5%9C%A8%E7%BA%BF%E8%81%8A%E5%A4%A9%E5%AE%A4/image-20231230152632195.png)

```java
/**
 * WebSocket在线聊天室
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2023-12-30 13:37
 */
@ServerEndpoint(value = "/chat",configurator = GetHttpSessionConfig.class)
// "/chat"需要和前端对应
@Component
public class ChatEndpoint {

    // ConcurrentHashMap, 线程安全的Map集合
    private static final Map<String ,Session> ONLINE_USERS = new ConcurrentHashMap<>();
    private HttpSession httpSession;

    /**
     * 建立Websocket连接之后被调用<br>
     * 1. 将Session进行保存<br>
     * 2. 广播已登录用户消息<br>
     * @param session websocket的会话
     * @param config config de su
     */
    @OnOpen
    public void opOpen(Session session, EndpointConfig config){
        // 1. 将HttpSession进行保存
		httpSession = config.getUserProperties.get(HttpSession.class.getName());
        // 1.1 获取WebSocket的session
        Map<String, Object> userProperties = config.getUserProperties();
        this.httpSession = (HttpSession) userProperties.get(HttpSession.class.getName());

        // 1.2 存入集合. 需要键username(从httpsession中来)
        ONLINE_USERS.put(getNowUsername(),session);

        // 2. 广播消息
        // 需要将登录的所有用户的用户名推送给所有用户
        broadcastAllUser(MessageUtils.getMessage(
                true,// 系统广播? 也可以认为用户主动上线了, 也可以是用户广播吧?
                null,// 可也以把当前用户的用户名放上去, 广播所有人XXX已上线之类的?
                getAllUsernames()// 好友列表
        ));
    }

    @OnMessage
    public void onMessage(String message){
        // 接收到客户端发送的数据时被调用
    }
    @OnClose
    public void onClose(Session session){
        // 连接关闭时被调用
    }
}
```

##### 获取当前用户的用户名

```java
private String getNowUsername() {
    return (String) httpSession
            .getAttribute(UserController.USERNAME_SESSION_KEY);
}
```

##### 广播所有用户

```java
/**
 * 广播所用用户
 * @param message json数据
 */
private void broadcastAllUser(String message){
    // 遍历map集合
    Set<Map.Entry<String, Session>> entries = ONLINE_USERS.entrySet();
    // ONLINE_USERS变成了entries单列集合

    for (Map.Entry<String, Session> entry : entries) {
        Session session = entry.getValue();
        try {
            session.getBasicRemote()// 发送同步消息
                    .sendText(message);
        } catch (IOException e) {
            // 记录日志...
        }
    }
}
```
##### 返回好友列表

```java
private Set<String> getAllUsernames() {
    return ONLINE_USERS.keySet();
}
```

#### onClose

```java
/**
 * 连接关闭时被调用<br>
 * 1. 从在线好友列表中剔除<br>
 * 2. 同时广播消息, 提醒其他用户该人已下线<br>
 * @param session websocket的session
 */
@OnClose
public void onClose(Session session){
    // 1. 从在线好友列表中剔除当前用户的Session对象,
    ONLINE_USERS.remove(getNowUsername());
    // 2. 通知其他所有用户当前用户已下线
    broadcastAllUser(MessageUtils.getMessage(
            true,
            null,
            getAllUsernames()
    ));
}
```

####onMessage

```java
/**
 * 接收到客户端发送的数据时被调用<br>
 * 1. 张三向李四发消息的时候<br>
 * 2. 找到李四的Endpoint对象<br>
 * 3. 使用李四Endpoint里面的方法<br>
 * 4. 给李四发消息<br>
 *
 * @param json json格式的Message数据
 */
@OnMessage
public void onMessage(@RequestParam("message")// 不确定和前端的关系
                      String json) {
    Message message = MessageUtils.getMessage(json);
    // 获取消息接收方的名子
    String toName = message.getToName();
    String msg = message.getMessage();
    // 获得李四的webSocket的Session对象
    Session session = ONLINE_USERS.get(toName);
    String resultMessage = MessageUtils.getResultMessage(
            false,
            getNowUsername(),
            msg
    );
    try {
        session.getBasicRemote().sendText(resultMessage);
    } catch (IOException e) {
        // 记录日志...
    }
}
```
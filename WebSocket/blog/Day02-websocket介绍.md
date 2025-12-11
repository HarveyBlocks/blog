# WebSocket

>   基于TCP连接上进行全双工通信的协议

## 流程

-   第一次**握手**

    -   Http向服务器请求`UPgradeLwebsocket`

        ![image-20231229224638131](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/WebSocket/Day02-websocket介绍/image-20231229224638131.png)

    -   服务器响应`101 Switch Protocols`完成==HTTP协议转换成WrbSocket协议==

        ![image-20231229224655249](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/WebSocket/Day02-websocket介绍/image-20231229224655249.png)

-   从此, 浏览器可以主动发数据给服务器, 服务器也可以主动发数据给服务端s

## 客户端API

-   **Html5支持WebSocket协议**

1.  在web客户端创建websocket对象

    ```javascript
    let ws = new WebSocket(URL);
    ```

    `URL`的协议部分应该是`ws`

2.  websocket对象的相关事件

    ![image-20231229225134408](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/WebSocket/Day02-websocket介绍/image-20231229225134408.png)

    error																						发生处一万元事故触发

3.  websocket对象提供的方法

    ```javascript
    send()
    ```

    通过websocket对象调用该方法发送数据给服务端 

-   前端

    ```html
    <html lang="en-US">

    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width" />
        <title>My test page</title>
    </head>

    <body>
        Hello world<br>
        <script>
            let ws = new WebSocket("ws://localhost/chat");
            ws.onopen = function () {

            };
            ws.onmessage = function (event) { 
                // 传入参数事件对象,事件对象带有服务器发送的数据
                // 通过 event.data 可以获取服务器发送的数据
            };
            ws.onclose = function () {
    			s
            };
        </script>
    </body>

    </html>
    ```

## 服务端API

-   Tomcat从7.0.5开始支持WebSocket, 并且实现了Java WebSocket规范(JavaEE里的一个规范)

### Endpoint端点

-   Java WebSocket 应用由一系列的**Endpoint**组成

    -   **Endpoint**是一个Java对象, 代表了WebSocket 连接的一端 
        -   在在线聊天中, 有用户1和用户2 , 这两个用户就分别是WebSocket的一端, 也就是**Endpoint**
        -   上面的用户与服务器连接之后, 服务器就会自动为他的**客户端**创建一个**Endpoint**
        -   是一对一的关系
    -   对于服务器, **Endpoint**可以视为处理具体WebSocket消息的接口

-   **Endpoint**的两种**定义方式**

    -   编程式

        基础`javax.websocket.Endpoint`并实现其方法

    -   注解式

        定义一个POJO, 并添加@ServerEndpoint及其相关注解

-   **Endpoint**生命周期

    -   实例在**WebSocket握手时创建**
    -   在客户端与服务端**连接过程中有效**
    -   在**链接关闭时结束**

    ![image-20231230131939140](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/WebSocket/Day02-websocket介绍/image-20231230131939140.png)

### 双向数据传输

#### 服务端接收客户端发送数据

-   编程式

    添加MessageHandler消息处理器来接收消息

    -   **创建一个类实现MessageHandler**
    -   这个类的方法在接收到浏览器发送的消息之后就会自动被执行

-   注解式

    定义Endpoint时, **通过`@OnMessage`注解指定接收消息的方法**

    `@OnMessage`注解的方法, 在接收到消息时自动被执行

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
    public void opOpen(Session session, EndpointConfig config){
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

![image-20231230134333139](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/WebSocket/Day02-websocket介绍/image-20231230134333139.png)

#### 服务端推送数据给客户端

1.  `RemoteEndpoint`, 其实例由Session维护

2.  `session`获取消息发送的实例

    -   `getBasicRemockte`获取**同步**消息发送的实例

    2.  `getAsyncRemockte`获取**异步**消息发送的实例

3.  调用`RemoteEndpoint` 的 `sendXxx()`方法发送消息


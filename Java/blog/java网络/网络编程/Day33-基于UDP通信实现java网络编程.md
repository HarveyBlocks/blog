# 基于UDP通信实现java网络编程

-   使用DatagramSocket类
    -   Socket - 插座；灯座；承口；

## 创建客户端,服务端

![image-20231011194330097](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络编程/Day33-基于UDP通信实现java网络编程/image-20231011194330097.png)

-   要实现一发一收

## 创建数据包

![image-20231011194344578](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络编程/Day33-基于UDP通信实现java网络编程/image-20231011194344578.png)

## 实践

### 一发一收

-   Server服务器类

```java
package net;

import java.io.IOException;
import java.net.*;

/**
 * 服务端,和Client类连接
 * @author HarveyBlocks
 * @date 2023/10/11 19:48
 **/
public class Server {
    public static void main(String[] args) throws IOException {
        //创建服务对象,需要注册一个端口
        DatagramSocket socket = new DatagramSocket(6666);

        System.out.println("Service has been initiated");

        /*
         * 创建数据包接收客户端发来的数据
         * 不接收到会等待
         * */
        byte[] bytes = new byte[1024 * 64]; //64KB,一包数据包的数据不会超过64KB
        DatagramPacket packet = new DatagramPacket(
                bytes,
                bytes.length
        );

        //开始接收客户端的数据包
        socket.receive(packet);

        //从字节数组中,把接收到的数据直接打印出来
        String rs = new String(bytes,0,packet.getLength());//接收多少就倒出多少;

        //打印其他信息
        System.out.println(packet.getAddress());
        System.out.println(packet.getPort());

        System.out.println("Server receive message succeed");
        System.out.println(rs);
        //释放资源
        socket.close();
    }
}
```

-   Client客户端类

```java
package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * 客户端,和Service类连接
 * @author HarveyBlocks
 * @date 2023/10/11 19:47
 **/
public class Client {
    public static void main(String[] args) throws IOException {
        //创建客户端对象,无参会为它随机分配端口
        DatagramSocket socket = new DatagramSocket();

        System.out.println("Client has been initiated");

        /*
        * 创建数据包对象封装要发出去的数据
        * public DatagramPacket(
        *       byte buf[], 封装要发出去的数据
        *       int length,发送除去的数据大小
        *       InetAddress address,服务端的IP地址
        *        int port服务端程序的端口
        * )
        * */
        byte[] bytes = "Hello World!".getBytes();
        DatagramPacket packet = new DatagramPacket(
                bytes,
                bytes.length,
                InetAddress.getByName("127.0.0.1"),
                6666
        );

        //开始发送资源
        socket.send(packet);

        System.out.println("Client send message succeed");
        //释放资源
        socket.close();

    }
}
```

### 多发多收

-   Server服务器端

```java
package net;

import java.io.IOException;
import java.net.*;

/**
 * 服务端,和Client类连接
 * @author HarveyBlocks
 * @date 2023/10/11 19:48
 **/
public class Server {
    public static void main(String[] args) throws IOException {
        //创建服务对象,需要注册一个端口
        DatagramSocket socket = new DatagramSocket(6666);

        System.out.println("Service has been initiated");

        /*
         * 创建数据包接收客户端发来的数据
         * 不接收到会等待
         * */
        byte[] bytes = new byte[1024 * 64]; //64KB,一包数据包的数据不会超过64KB
        DatagramPacket packet = new DatagramPacket(
                bytes,
                bytes.length
        );

        while (true) {
            socket.receive(packet);

            System.out.println("Server received message from:");
            System.out.println(packet.getAddress()+":"+packet.getPort());
            String rs = new String(bytes,0,packet.getLength());
            System.out.println(rs+"\n");
        }

        //socket.close();在应用中,服务端是不会关闭的
    }
}
```

-   Client客户端

```java
package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

/**
 * 客户端,和Service类连接
 * @author HarveyBlocks
 * @date 2023/10/11 19:47
 **/
public class Client {
    public static void main(String[] args) throws IOException {
        Client client = new Client();
        System.out.println("Client has been initiated");
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.print("Please type what you want say:");
            String string = scanner.nextLine();
            if("exit".equals(string)) {
                System.out.println("DO YOU WANT TO EXIT THIS SERVICE?(Y/N)");
                String ans = scanner.nextLine().trim();//去除空白符
                if ("Y".equals(ans) || "y".equals(ans)){
                    break;
                }
            }
            client.sendMessage(string);
            System.out.println("Message: \""+ string +"\" send successfully\n");
        }
        System.out.println("You have exist this service...");
        scanner.close();
        client.socket.close();
    }
    private final DatagramSocket socket = new DatagramSocket();
    //计算机随机分配端口,不必担心端口重发

    public Client() throws SocketException {

    }

    public void sendMessage(String string) throws IOException {
        byte[] bytes = string.getBytes();
        DatagramPacket packet = new DatagramPacket(
                bytes,bytes.length,
                InetAddress.getByName("127.0.0.1"),6666
        );
        //开始发送资源
        this.socket.send(packet);

        System.out.println("Client send message succeed");
        //释放资源

    }
}
```

### 多个客户端对一个服务器

-   **Shift + Alt + F10**打开**编辑配置窗口**
-   设置**允许多个实例**

![image-20231011210006281](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络编程/Day33-基于UDP通信实现java网络编程/image-20231011210006281.png)


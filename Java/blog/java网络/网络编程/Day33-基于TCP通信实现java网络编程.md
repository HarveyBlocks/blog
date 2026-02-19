# 基于TCP通信实现java网络编程

-   java.net.Socket类实现

## 客户端Socket

-   创建Socket对象,构建客户端

![image-20231012002905000](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络编程/Day33-基于TCP通信实现java网络编程/image-20231012002905000.png)

-   常用方法

![image-20231012002947289](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络编程/Day33-基于TCP通信实现java网络编程/image-20231012002947289.png)

-   实现步骤



![image-20231012003934057](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络编程/Day33-基于TCP通信实现java网络编程/image-20231012003934057.png)



### 实例代码

```java
package TCP_NET;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 客户端发消息
 * @author HarveyBlocks
 * @date 2023/10/12 00:33
 **/
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        OutputStream os = socket.getOutputStream();

        //把低级的字节输出流包装成数据输出流
        DataOutputStream dos = new DataOutputStream(os);

        //开始写数据除去给服务端
        dos.writeUTF("Hello");
        dos.flush();//立刻发出去,防止占内存

        dos.close();
        socket.close();
    }
}
```

## 服务端ServerSocket

![image-20231012010641405](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络编程/Day33-基于TCP通信实现java网络编程/image-20231012010641405.png)

-   实现步骤

![image-20231012012021534](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络编程/Day33-基于TCP通信实现java网络编程/image-20231012012021534.png)

### 案例实现

```java
package TCP_NET;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务端收消息
 * @author HarveyBlocks
 * @date 2023/10/12 00:33
 **/
public class Server {
    public static void main(String[] args) throws IOException {

        //创建ServerSocket对象, 同时为服务段注册端口
        ServerSocket serverSocket = new ServerSocket(8888);

        //使用serverSocket对象,调用一个accept方法,等待客户端的连接请求
        Socket socket = serverSocket.accept();

        //从socket通信管道中得到一个字节输入流
        InputStream is = socket.getInputStream();

        //把原始字节输入流包装成数据输入流
        DataInputStream dis = new DataInputStream(is);

        //使用数据输入流读取客户端发来的信息
        String message = dis.readUTF();
        System.out.println(message);

        //获取客户端IP地址
        System.out.println(socket.getRemoteSocketAddress());

        //释放资源
        dis.close();
        socket.close();

    }
}
```

## 多发多收

-   Client

```java
package TCP_NET;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.security.PrivilegedActionException;
import java.util.Scanner;

/**
 * 客户端发消息
 * @author HarveyBlocks
 * @date 2023/10/12 00:33
 **/
public class Client {
    public static void main(String[] args) throws IOException, PrivilegedActionException {
        Client client = new Client();
        System.out.println("Client has been initiated");

        OutputStream os = client.socket.getOutputStream();//socket的os
        //把低级的字节输出流包装成数据输出流
        DataOutputStream dos = new DataOutputStream(os);

        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.print("Please type what you want say:");
            String string = scanner.nextLine();

            //出口
            if("exit".equals(string)) {
                System.out.println("DO YOU WANT TO EXIT THIS SERVICE?(Y/N)");
                String ans = scanner.nextLine().trim();//去除空白符
                if ("Y".equals(ans) || "y".equals(ans)){
                    break;
                }
            }

            //开始写数据除去给服务端
            dos.writeUTF(string);
            dos.flush();//立刻发出去,防止占内存

            System.out.println("Message: \""+ string +"\" send successfully\n");
        }
        System.out.println("You have exist this service...");

        scanner.close();
        dos.close();
        client.socket.close();
    }
    private final Socket socket = new Socket("127.0.0.1", 8888);

    public Client() throws IOException {

    }
}
```

-   Serveice

```java
package TCP_NET;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务端收消息
 * @author HarveyBlocks
 * @date 2023/10/12 00:33
 **/
public class Server {
    public static void main(String[] args) throws IOException {

        //创建ServerSocket对象, 同时为服务段注册端口
        ServerSocket serverSocket = new ServerSocket(8888);
        System.out.println("Service has been initiated");

        while (true) {
            //使用serverSocket对象,调用一个accept方法,等待客户端的连接请求
            Socket socket = serverSocket.accept();

            //从socket通信管道中得到一个字节输入流
            InputStream is = socket.getInputStream();

            //把原始字节输入流包装成数据输入流
            DataInputStream dis = new DataInputStream(is);

            //使用数据输入流读取客户端发来的信息
            String message = dis.readUTF();

            System.out.print("Server received message from:");
            //获取客户端IP地址和端口号
            System.out.println(socket.getRemoteSocketAddress());

            System.out.println(message +"\n");
        }

        //释放资源
//        dis.close();
//        socket.close();

    }
}
```

-   客户端退出后, 服务端报出异常
-   由于`dis.readUTF()`一直在等待客户端的消息,客户端却断开了连接

![image-20231012120052754](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络编程/Day33-基于TCP通信实现java网络编程/image-20231012120052754.png)

-   用try-catch

#### 对多发多收的改进

-   Service

    ```java
    package TCP_NET;

    import java.io.*;
    import java.net.ServerSocket;
    import java.net.Socket;
    import java.io.IOException;
    import java.io.InputStream;

    /**
     * 服务端收消息
     * @author HarveyBlocks
     * @date 2023/10/12 00:33
     **/
    public class Server {
        public static void main(String[] args) throws IOException {
            new ServerMethod();
        }
    }

    class ServerMethod{
        public ServerMethod() throws IOException {
            this.creatAndReceive();
        }
        private void creatAndReceive() throws IOException {
            //创建ServerSocket对象, 同时为服务段注册端口
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("Service has been initiated");

            //使用serverSocket对象,调用一个accept方法,等待客户端的连接请求
            Socket socket = serverSocket.accept();

            //从socket通信管道中得到一个字节输入流
            InputStream is = socket.getInputStream();

            //把原始字节输入流包装成数据输入流
            DataInputStream dis = new DataInputStream(is);
            String message;
            while (true) {
                try {
                    //使用数据输入流读取客户端发来的信息
                    message = dis.readUTF();
                } catch (Exception e){
                    System.out.println(socket.getRemoteSocketAddress()+" offline");
                    break;
                }

                System.out.print("Server received message from:");
                //获取客户端IP地址和端口号
                System.out.println(socket.getRemoteSocketAddress());
                System.out.println(message +"\n");
            }

            //释放资源
            dis.close();
            socket.close();
        }
    }
    ```

-   Client

    ```java
    package TCP_NET;

    import java.io.DataOutputStream;
    import java.io.IOException;
    import java.io.OutputStream;
    import java.net.*;
    import java.util.Scanner;

    /**
     * 客户端发消息
     * @author HarveyBlocks
     * @date 2023/10/12 00:33
     **/

    public class Client {
        public static void main(String[] args) throws IOException {
            new ClientMethod();
        }
    }
    class ClientMethod{
        public ClientMethod() throws IOException {
            this.creatAndSend();
        }
        private Socket socket ;
        private void creatAndSend() throws IOException {
            this.initiate();
            this.send();
        }
        private static final int PORT = 8888;

        private void initiate(){
            boolean isConnect = false;
            while (!isConnect) {
                try {
                    this.socket= new Socket("127.0.0.1", PORT);
                    isConnect = true;
                } catch (ConnectException e) {
                    System.out.println("服务器未开启,等待3秒后将重新尝试连接");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {

                    }
                    System.out.println("再次尝试重新连接");
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Client has been initiated");
        }
        private void send() throws IOException {
            Scanner scanner = new Scanner(System.in);
            while(true){
                OutputStream os = this.socket.getOutputStream();//socket的os

                //把低级的字节输出流包装成数据输出流
                DataOutputStream dos = new DataOutputStream(os);
                System.out.print("Please type what you want say:");
                String string = scanner.nextLine();

                //出口
                if(exit(string,scanner)){
                    dos.close();
                    this.socket.close();
                    break;
                }

                //开始写数据除去给服务端
                dos.writeUTF(string);
                dos.flush();//立刻发出去,防止占内存

                System.out.println("Message: \""+ string +"\" send successfully\n");
            }
            System.out.println("You have exist this service...");
            scanner.close();
        }
        private boolean exit(String string,Scanner scanner) throws IOException {
            if("exit".equals(string)) {
                System.out.println("DO YOU WANT TO EXIT THIS SERVICE?(Y/N)");
                String ans = scanner.nextLine().trim();//去除空白符
                if ("Y".equals(ans) || "y".equals(ans)){
                    return true;
                }
            }
            return false;
        }
    }
    ```

### 多客户端

TCP一条线程,只能一个客户端


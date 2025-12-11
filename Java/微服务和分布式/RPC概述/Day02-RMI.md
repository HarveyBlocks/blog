# RMI

Java原生支持的远程调用

## JRMP

>   Java Remote MEssageing Protocol

## 实现步骤

1.  **在api端**, 创建远程接口, 并继承`java.rmi.Remote`

    ```java
    import java.rmi.Remote;

    public interface RmiService extends Remote {
        User sayHello(String name);
    }
    ```

2.  **在服务提供端**, 实现远程接口, 继承`UnicastRemoteObject`

    ```java
    import java.rmi.RemoteException;
    import java.rmi.server.UnicastRemoteObject;

    public class RmiServiceImpl extends UnicastRemoteObject implements RmiService {
        public RmiServiceImpl() throws RemoteException {
            // 必须抛出这个RemoteException异常

        }

        @Override
        public User sayHello(String name) throws RemoteException{// 每个方法也要抛出RemoteException
            return new User(name);
        }
    }
    ```

3.  **在服务提供端**, 创建服务器程序, `createRegistry()`注册远程对象

    **在api端**, 创建连接工具

    ```java
    public class RmiUtil {
        private final String rmiUrl;
        private final String host;
        private final int port;
        private boolean registered = false;

        public RmiUtil(String host, int port) throws RemoteException {
            this.host = host;
            this.port = port;
            this.rmiUrl = "rmi://" + host + ":" + port + "/";
        }

        public void register()
                throws RemoteException {
            if (!registered) {
                LocateRegistry.createRegistry(this.port);
                registered = true;
            }
        }

        public <T extends Remote, R extends T> void bind(Class<T> serviceClass, R serviceImpl)
                throws MalformedURLException, AlreadyBoundException, RemoteException {
            Naming.bind(rmiUrl + serviceClass.getSimpleName(), serviceImpl);
        }
    }

    ```

    ```java
    public static void main(String[] args) {
        try {
            RmiUtil rmiUtil = new RmiUtil("localhost",8090);
            rmiUtil.register();
            rmiUtil.bind(RmiService.class, new RmiServiceImpl());
        } catch (RemoteException | MalformedURLException | AlreadyBoundException e) {
            System.err.println(e.getMessage() + e);
        }
    }
    ```

4.  **在服务消费端**, 创建客户端程序, 获取注册信息

    **在api端**, 创建获取代理工具

    ```java
    public class RmiUtil {
        private final String rmiUrl;
        private final String host;
        private final int port;

        public RmiUtil(String host, int port) throws RemoteException {
            this.host = host;
            this.port = port;
            this.rmiUrl = "rmi://" + host + ":" + port + "/";
        }
    	// ...

        public <T extends Remote> T getProxy(Class<T> serviceClass)
                throws MalformedURLException, RemoteException, NotBoundException {
            return (T) Naming.lookup(rmiUrl + serviceClass.getSimpleName());
        }
    }
    ```

    **在服务消费端**, 调用接口方法

    ```java
    try {
        RmiUtil rmiUtil = new RmiUtil("localhost", 8090);
        RmiService proxy = rmiUtil.getProxy(RmiService.class);
        User user = proxy.sayHello("张三");
        System.out.println("user = " + user);
    } catch (RemoteException | MalformedURLException | NotBoundException e) {
        System.err.println(e.getMessage() + e);
    }
    ```


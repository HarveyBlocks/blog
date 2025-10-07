package TCP_NET;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;


/**
 * 服务端收消息
 * @author HarveyBlocks
 * @date 2023/10/12 00:33
 **/
public class Server {
    public static final int PORT = 8888;
    public static void main(String[] args) throws IOException {
        new Thread(new Time()).start();
        new ServerMethod(PORT);
    }
}
class Time implements Runnable{
    @Override
    public void run() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss  ");
        while (true) {
            Date date = new Date();
            time = sdf.format(date);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }
        }
        //TODO 由于服务器不会关,所以这个线程不会关

    }
    public static String time;
}

class User{
    private Socket cSocket;

    public Socket getCSocket() {
        return cSocket;
    }

    private String userName;

    public String getUserName() {
        return userName;
    }


    public User(Socket cSocket, String userName) {
        this.cSocket = cSocket;
        this.userName = userName;
    }
}


class ServerMethod {

    List<User> users ;
    private ServerSocket serverSocket ;
    private final int port;
    public ServerMethod(int port) throws IOException {
        this.port = port;
        users = new ArrayList<>();
        createAndReceive();
    }
    //服务器端口

    public void createServer() throws IOException {
        //创建ServerSocket对象, 同时为服务端注册端口
        this.serverSocket= new ServerSocket(port);
        System.out.println("\033[1;32m服务器已成功创建\033[0m");
    }


    //接收客户端
    public void createAndReceive() throws IOException {
        createServer();

        //创建线程池
        ExecutorService pool = new ThreadPoolExecutor(
                3,
                5,
                20,
                TimeUnit.SECONDS,//枚举变量TimeUnit
                new ArrayBlockingQueue<>(6),//基于数组,限制大小,最多缓存4个队伍
                Executors.defaultThreadFactory(),//获取默认的线程工厂,一般用这个
                new ThreadPoolExecutor.AbortPolicy()
                //AbortPolicy是ThreadPoolExecutor的内部类,实现了RejectedExecutionHandler接口
                //抛出异常,丢弃任务
        );


        while (true) {
            //使用serverSocket对象,调用一个accept方法,等待客户端的连接请求
            Socket cSocket = serverSocket.accept();
            InputStream is = cSocket.getInputStream();

            //把原始字节输入流包装成数据输入流
            DataInputStream dis = new DataInputStream(is);
            String userName = dis.readUTF();
            User user = new User(cSocket, userName);
            users.add(user);
            System.out.print(Time.time);
            System.out.println("\033[1;35m"+cSocket.getRemoteSocketAddress()+" enter"+"\033[0m");
            //把socket分配给独立线程
            pool.execute(new ServerReaderThread(user,users));
            //pool.execute(Runnable);执行Runnable任务

            //线程池会自动创建一个新线程,自动处理这个任务,自动执行;
        }

        //想关掉?:
        //pool.shutdown();
    }
}
class ServerReaderThread implements Runnable{
    private User user;
    private List<User> users;

    public ServerReaderThread(User user,List<User> users) throws IOException {
        this.user = user;
        this.users = users;
        sendToAll("\033[1;35m"+
                user.getUserName()+"进入房间"
                +"\033[0m\n");
    }

    //发送给所有在线的socket管道接收
    private void sendToAll(String s) throws IOException {
        for (User targetUser: users) {
            if (targetUser.getCSocket().equals(user.getCSocket())){
                continue;
            }
            OutputStream os = targetUser.getCSocket().getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(s);
            dos.flush();
        }
    }

    @Override
    public void run() {
        //从socket通信管道中得到一个字节输入流
        InputStream is = null;
        try {
            is = user.getCSocket().getInputStream();
        } catch (IOException ignored) {

        }

        //把原始字节输入流包装成数据输入流
        DataInputStream dis = new DataInputStream(is);

        String message;
        while (true) {
            try {
                //使用数据输入流读取客户端发来的信息
                message = dis.readUTF();
            } catch (Exception e){
                System.out.print(Time.time);
                System.out.println("\033[1;34m"+
                                user.getCSocket().getRemoteSocketAddress()+" left"
                                +"\033[0m"
                );
                try {
                    sendToAll("\033[1;34m"+
                            user.getUserName()+" 已离开"
                            +"\033[0m\n");
                } catch (IOException ignored) {

                }
                users.remove(user);
                break;
            }

            //获取客户端IP地址和端口号
            System.out.print(Time.time);
            System.out.print(user.getCSocket().getRemoteSocketAddress()+":");
            System.out.println("\033[1;32m"+message+"\033[0m" );
            try {
                sendToAll(user.getUserName()+" :");
                sendToAll("\t\033[1;36m"+message+"\033[0m" +"\n");
            } catch (IOException ignored) {

            }

        }

        //释放资源
        try {
            dis.close();
            user.getCSocket().close();
        }catch (IOException ignored) {

        }
    }
}
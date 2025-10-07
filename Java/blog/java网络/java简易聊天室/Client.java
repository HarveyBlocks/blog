package TCP_NET;

import java.io.*;
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
    private final static Scanner SCANNER = new Scanner(System.in);
    public ClientMethod() throws IOException {
        this.creatAndSend();
    }
    private String userName;

    private Socket socket ;
    private void creatAndSend() throws IOException {
        this.initiate();
        this.send();
    }

    private void setUserName(){
        System.out.print("是否为自己取一个名字?(Y/N)");
        String choice = SCANNER.nextLine().toUpperCase().trim();

        switch (choice){
            case "Y":
            case "YES":
                System.out.print("请输入用户名:");
                userName = SCANNER.nextLine().trim();
                System.out.println("取名成功");
                break;
            default:
                System.out.println("将自动为你创建一个名字");
                if(socket!=null) {
                    userName = socket.getLocalAddress().toString()+":"+
                            socket.getLocalPort();
                }
        }
    }
    private static final int SERVER_PORT = Server.PORT;
    private static final String SERVER_IP = "169.254.128.11";
    private void initiate(){

        isConnect = false;
        while (!isConnect) {
            try {
                this.socket= new Socket(SERVER_IP, SERVER_PORT);
                setUserName();
                isConnect = true;
            } catch (ConnectException e) {
                System.out.println("\033[1;31m服务器未开启\033[0m , 等待3秒后将尝试重新连接");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {

                }
                System.out.println("再次尝试重新连接");
            } catch (IOException ignored) {

            }
        }
        System.out.println("\033[1;32m客户端已连接服务器\n" +
                "您已进入房间\033[0m"
        );
        System.out.println("\033[1;33m" +
                "*****************************************************\n"+
                "*                      使用须知                    \t*\n"+
                "*                 这是一个群聊的聊天室               \t*\n"+
                "*               请在空白处键入你想说的话             \t*\n"+
                "*        本聊天室不支持一次性输入多行的文字,请注意     \t*\n"+
                "*            如果你要离开,请输入exit(全小写)         \t*\n"+
                "*   如果你想在聊界面中输入\"exit\",在末尾输入空格即可   \t*\n"+
                "*****************************************************\n"+
                "\033[0m");
        //此时创建一个线程随时帮助接收服务器发来的消息
        new Thread(new ClientReaderThread(socket,this)).start();

    }
    private boolean isConnect;
    public void setIsConnect(boolean isConnect){
        this.isConnect = isConnect;
    }
    public boolean getIsConnect(){
        return isConnect;
    }
    private void send() throws IOException {

        OutputStream os = this.socket.getOutputStream();//socket的os

        //把低级的字节输出流包装成数据输出流
        DataOutputStream dos = new DataOutputStream(os);

        //发送用户名
        if(isConnect){
            //开始写数据除去给服务端
            dos.writeUTF(userName);
            dos.flush();//立刻发出去,防止占内存
        }else {
            System.out.println("\033[1;34m服务器已下线!\033[0m");
        }

        label:while(true){
            String string = SCANNER.nextLine();
            //出口
            string = exit(string);
            switch (string){
                case "0"://YES
                    dos.close();
                    this.socket.close();
                    break label;
                case "1"://NO
                    continue;
                default:
                    break;
            }

            if(isConnect){
                //开始写数据除去给服务端
                dos.writeUTF(string);
                dos.flush();//立刻发出去,防止占内存
            }else {
                System.out.println("\033[1;34m服务器已下线!\033[0m");
                break;
            }
        }

        System.out.println("\033[1;34m您已退出该服务\033[0m");
        SCANNER.close();
    }
    private String exit(String string) {
        if("exit".equals(string)) {
            System.out.print("\033[1;31m是否退出?(Y/N)\033[0m");
            string = SCANNER.nextLine();//去除空白符
            String ans = string.trim().toUpperCase();
            if ("Y".equals(ans) || "YES".equals(ans)){
                return "0";
            } else if ("N".equals(ans) || "NO".equals(ans)) {
                return "1";
            }
        }
        return string;
    }
}
class ClientReaderThread implements Runnable{
    private final ClientMethod clientMethod;
    private final Socket socket;

    public ClientReaderThread(Socket socket, ClientMethod clientMethod){
        this.socket = socket;
        this.clientMethod = clientMethod;
    }
    @Override
    public void run() {

        //从socket通信管道中得到一个字节输入流
        InputStream is = null;
        try {
            is = socket.getInputStream();
        } catch (IOException ignored) {

        }

        //把原始字节输入流包装成数据输入流
        DataInputStream dis = new DataInputStream(is);
        String message;
        while (true) {
            try {
                //使用数据输入流读取服务器发来的信息
                message = dis.readUTF();
                clientMethod.setIsConnect(true);
            } catch (Exception e) {
                clientMethod.setIsConnect(false);
                break;
            }
            //获取客户端IP地址和端口号
            System.out.print(message);
        }
    }
}

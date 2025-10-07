package iostream;

import org.junit.Test;
import sun.nio.cs.ext.GBK;

import java.io.*;

/**
 * 文件字节输入流
 * @author HarveyBlocks
 * @date 2023/10/13 18:33
 **/
public class FileInputStreamTest {
    /*
     * int read()
     * */
    public static void readTest(String[] args) throws IOException {
        //构造
        InputStream is = new FileInputStream(new File("D:/resources/ab.txt"));
        is = new FileInputStream("D:/resources/ab.txt");//推荐

        byte by;

        /*
         *
         * read();
         * 读一个
         * 文件结束返回-1
         * 很慢
         * 要锻炼硬盘很多次
         * */
        while ((by = (byte) is.read()) >= 0) {
            if (by < 0) break;
            System.out.printf("%c", by);
        }
        /*
         * 释放资源
         * */
        is.close();
    }

    /*
     * int read(byte b[])
     * */
    public static void readByteTest(String[] args) throws IOException {
        InputStream is = new FileInputStream("D:/resources/ab.txt");

        byte[] buffer = new byte[3];//每次装几个字节
        int len;
        String rs;
        while ((len = is.read(buffer)) > 0) {
            rs = new String(buffer, 0, len);
            System.out.print(len + ":" + rs + "\n");
        }
        //中文依旧会乱码
        //但可以用来拷贝文件
        is.close();
    }


    /*
     * 一次读完全部字节
     * */
    public static void readAllText() throws IOException {
        File file = new File("D:/resources/ab.txt");
        InputStream is = new FileInputStream(file);

        byte[] bytes = new byte[(int) file.length()];//int 最高2GB,数组上限也是int的最大值
        System.out.println(is.read(bytes) == (int) file.length());
        /*
        * 不要担心不够用
        * 文本文件都不会超过2GB
        * 非文本文件没有字符,不需要所谓分的清清楚楚,可以多搞几个数组存
        * */
        System.out.println(new String(bytes));
        is.close();
    }


}
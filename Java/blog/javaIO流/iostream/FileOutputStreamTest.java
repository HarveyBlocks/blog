package iostream;

import org.junit.Test;

import java.io.*;

/**
 * 文件字节输出流
 * @author HarveyBlocks
 * @date 2023/10/14 11:43
 **/
public class FileOutputStreamTest {

    public static byte[] readBytes() throws IOException {
        File file = new File("D:/resources/ab.txt");
        InputStream is = new FileInputStream(file);

        byte[] bytes = new byte[(int) file.length()];//int 最高2GB,数组上限也是int的最大值
        is.read(bytes);
        return bytes;
    }

    /*
    * 覆盖写
    * */
    public static void writeFromHead(String[] args) throws IOException {
        //覆盖写
        OutputStream os = new FileOutputStream("D:\\resources\\ab(2).txt");

        os.write('a');//每次只能写 一 个 字 节!!!
        os.write(97);
        os.write('好');

        //文件复制
        os.write(readBytes());
        /*
        * os.write(bytes,  0  , readBytes().length);等价
        *        字节数组,字节数组起始位置,写入字节长度
        *                   这个长度用方法就很没必要好吗,还要读两次
        * */

        os.close();
    }


    /*
    * 追加写
    *
    * */
    public static void readAppend() throws IOException {
        final boolean APPEND = true;
        OutputStream os = new FileOutputStream("D:\\resources\\ab(2).txt",APPEND);


        os.write('a');//每次只能写 一 个 字 节!!!
        os.write(97);
        os.write('好');

        //文件复制
        os.write(readBytes());

        os.close();
    }


    /*
    * 复制照片
    * */
    public static void copyFile(String[] args) throws IOException {
        InputStream is = new FileInputStream("D:\\resources\\UMA.jpg");
        byte[] bytes = new byte[16];//一次16个字节
        int len;

        OutputStream os = new FileOutputStream("C:\\Users\\27970\\Desktop\\UMA.jpg",true);

        while ((len=is.read(bytes))>0) os.write(bytes,0,len);

        System.out.println("success");

        //良好的习惯,关闭的顺序
        os.close();
        is.close();

    }


    /*
    * 流的释放
    * 如果在创建流之后,释放流之前,长须出现了异常
    * 那么流将无法被释放
    * 那么就很不好
    * 所以
    * try-catch-finally
    * try-with-resource
    *
    * */
    public static void resourceClose(String[] args) {
        InputStream is = null;
        OutputStream os = null;

        //try-catch-finally
        try {
            is = new FileInputStream("D:\\resources\\UMA.jpg");
            byte[] bytes = new byte[16];//一次16个字节
            int len ;

            os = new FileOutputStream("C:\\Users\\27970\\Desktop\\UMA.jpg",true);

            while ((len=is.read(bytes))>0) os.write(bytes,0,len);

            System.out.println("success");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(os!=null)os.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //这里抛异常?why?
            try {
                if(is!=null)is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //1. os,is还没被new出来,就有了异常,os,is还是null,会有NullPointException
            //2. 担心前面已经关过is,os了重复关会有异常
        }

        //try-with-resource  JDK7 专门释放资源,更简洁
        try (
                InputStream is1 = new FileInputStream(
                        "D:\\resources\\UMA.jpg"
                );
                OutputStream os1 = new FileOutputStream(
                        "C:\\Users\\27970\\Desktop\\UMA.jpg", true
                )
                // 只能放置资源对象
                // 资源对象:直接或间接实现AutoCloseable接口的
        ) {
            byte[] bytes = new byte[16];//一次16个字节
            int len;
            while ((len = is1.read(bytes)) > 0) os1.write(bytes, 0, len);
            System.out.println("success");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    /*
    * 复制文件夹
    * 需要:read()递归
    * */
    @Test
    public void testCopyDir() throws IOException {
        copyDir("D:/resources","C:/Users/27970/Desktop/resources");
    }

    /*
    * String startDir 起始路径
    * String targetDir 目标路径
    * 先假定输入的是绝对路径
    * */
    public static void copyDir(String startDir,String targetDir){
        startDir = new File(startDir).getAbsolutePath();
        targetDir = new File(targetDir).getAbsolutePath();

        new File(targetDir).mkdirs();

        File[] files = new File(startDir).listFiles();
        if(files == null) return;

        for (File file :files) {

            String path = file.getAbsolutePath();
            //file.getPath(),targetDir+file.getPath().subSequence()
            String newStartPath = path;
            String newTargetPath = targetDir +
                    newStartPath.substring(startDir.length(), path.length());
            System.out.println(newStartPath + "->" + newTargetPath);

            if (file.isDirectory()){

                //深入读取文件夹
                copyDir(newStartPath,newTargetPath);
            }else {
                try (
                        InputStream is = new FileInputStream(newStartPath);
                        OutputStream os = new FileOutputStream(newTargetPath, true);
                ) {
                    //复制文件
                    byte[] bytes = new byte[1024];//一次 1KB
                    int len;


                    while ((len = is.read(bytes)) > 0) os.write(bytes, 0, len);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }




}

package iostream;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * 创建一个file文件
 * @author HarveyBlocks
 * @date 2023/10/13 14:12
 **/
public class TestFile {

    //资源文件夹绝对路径
    private static final String DIR = "D:/resource";


    /*
    * 打开文件或文件夹
    * 相对路径和绝对路径
    * 看文件大小
    * 查看文件是否存在
    * */
    public static void openFile(String[] args) {
        File file = new File("D:\\resource\\ab.txt");
        File file1 = new File("D:/resource/ab.txt");//这种常见简单


        String sep = File.separator;//系统指定文件分割符
        File file2 = new File("D:"+sep+"resource"+sep+"ab.txt");

        //只有智障才会混着用
        System.out.println(new File(DIR+"\\ab.txt").exists());//true
        //一整个被无语住了
        System.out.println(new File(DIR+"////ab.txt").exists());//true
        System.out.println(new File(DIR+"/\\//\\/ab.txt").exists());//true

        System.out.println(file.length()+"Byte");//30
        System.out.println(file1.length()+"Byte");//30
        System.out.println(file2.length()+"Byte");//30

        //读文件夹的大小,若表示的是文件夹,下一个"/"打不打无所谓
        File dir = new File("D:/resource");
        System.out.println(dir.length());
        //返回的大小是文件夹本身的大小,而不是里面所有文件的路径
        //这里是0就很奇妙
        System.out.println(dir.exists());
        //true,存在

        File nullFile = new File("E:");        //读不存在的文件
        System.out.println(nullFile.length());//0
        System.out.println(nullFile.exists());//false 表示不存在

        //相对路径 - .idea文件夹所在的路径未 .
        File srcDir = new File("./src");
        System.out.println(srcDir.length());//4096

    }

    /*
    * File文件常用方法-1
    * 判断file是文件还是路径的
    *           isFile() 和 isDirectory()
    * 返回file最后修改时间的
    *           LastModified()
    * 返回各种file路径的
    *           getPath() getAbsolutePath() 和 toString()
    * */
    public static void getFileMessage(String[] args) {

        /*
         * boolean isFile()
         * */
        System.out.println(new File(".").isFile());
        //false
        System.out.println(new File("./lib/junit-4.13.1.jar").isFile());
        //true

        /*
         * Boolean isDirectory
         * */
        System.out.println(new File("./").isDirectory());
        //true
        System.out.println(new File("./lib/junit-4.13.1.jar").isDirectory());
        //false

        /*
         * long lastModified()
         * 返回最后创建的时间
         * */
        System.out.print(
                new File(".").lastModified() + "->"
        );//返回时间毫秒值
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        System.out.println(
                sdf.format(
                        new File(".").lastModified()
                )
        );


        /*
         * String getPath();
         * 获取创建文件时指定的路径
         * */
        System.out.println(
                new File(".").getPath()
        );// .
        System.out.println(
                new File("C:/Users/27970/Desktop/IT/JDK/Learn").getPath()
        );//C:\Users\27970\Desktop\IT\JDK\Learn
        System.out.println(
                //施涵特有的没事找事
                new File(
                        "../../../../../.." +
                                "/Users/27970/Desktop/IT/JDK/Learn"
                ).getPath()
        );//..\..\..\..\..\..\Users\27970\Desktop\IT\JDK\Learn


        /*
         * public String toString() {
         *       return getPath();
         * }
         * */
        System.out.println(new File("."));


        /*
         *String getAbsolutePath()
         * 返回绝对路径
         * */
        System.out.println(
                new File(".").getAbsoluteFile()
        );// C:\Users\27970\Desktop\IT\JDK\Learn\.
        System.out.println(
                new File("C:/Users/27970/Desktop/IT/JDK/Learn").getAbsolutePath()
        );//C:\Users\27970\Desktop\IT\JDK\Learn

    }

    /*
    * 文件创建和删除
    * 目录创建和删除
    * To be or not to be
    * */
    public static void createAndDelete(String[] args) throws IOException {
        /*
        * 创建空文件
        * boolean createNewFile()
        * 返回是否创建成功
        * */
        File file = new File("D:/resource/abc.txt");

        System.out.println(file.exists());//false
        System.out.println(file.createNewFile());//抛出异常,true

        System.out.println(file.createNewFile());//false,文件已存在

        System.out.println(file.exists());//true
        System.out.println(file.length());//0,空文件


        /*
        * "只能"创建一级文件夹:
        * boolean mkdir()
        * "可以"创建多级文件夹:
        * boolean mkdirs()
        *
        * */
        System.out.println(
                new File("D:/resource/first/second/third").mkdir()
        );//false

        System.out.println(
                new File("D:/resource/first").mkdirs()
        );//true

        /*
        * boolean delete()
        * 不能删除非空文件夹
        * 不能删除非空文件
        * */
        //尝试删除非空文件
        System.out.println(new File("D:/resource/ab.txt").delete());
        //false

        //尝试删除空文件
        if (file.exists()&&file.length()!=0) {
            System.out.println(file.delete());//true
        }//删除了不会进回收站你找不回来的
    }

    /*
    * 获取文件名和文件重命名名
    * String getName()
    * boolean renameTo(File file)
    * */
    public static void main(String[] args) {
        File file = new File("D:/resource");
        System.out.println(file.getName());//resource

        System.out.println(file.renameTo(new File("D:/","resources")));
        //若新名字的文件已存在,则返回false
    }

    /*
    * 遍历文件夹
    * */
    public static void traverseFile(String[] args) {
        /*
        * String[] list()
        * 获取当前目录下所有
        * 一级
        * 文件名称
        * */
        for (String fileName : new File(DIR).list()){
            System.out.println(fileName);
        }

        System.out.println("=====================");

        /*
        * File[] listFiles()
        * 获取当前目录下所有
        * 一级
        * 文件(夹)对象
        * */
        for (File file:new File(DIR).listFiles()){
            System.out.println(file.getPath());//绝对路径
            System.out.println(file.exists());//全是true
            System.out.println(file.length());
            System.out.println("-------------");
        }

        System.out.println("=========================");

        /*
         * ATTENTION
         * ----about listFiles()
         * */

        /*
        * 主调是文件
        * 或者
        * 路径不存在
        *  ->    返回null值
        * */

        //首先:
        System.out.println(null == new File("D:/asaf"));//false
        //因为之后可以再创建这个目录,所以也不会得到null

        System.out.println("-----------------------");

        //但是:
        System.out.println(new File(DIR + "/ab.txt").listFiles() == null);//true
        System.out.println(new File("D:/asaf").listFiles() == null);//true

        /*
        * 主调未空文件夹
        * 返回长度为0的数组
        * */

        /*
        * 主调是有内容的文件夹
        * 一级目录和文件夹一并返回
        * */


        /*
        * 主调是文件夹
        * 内含隐藏文件
        * 隐藏文件也返回
        * */

        /*
        * 主调是文件夹
        * 没有权限
        * 返回null
        * */

    }



    /*
    * 作为void read()的一个Demo
    * */
    public static void readDir(String[] args) {
        read("C:\\Users\\27970\\Desktop\\IT\\JDK\\JavaDailyBlog");
        System.out.println("-------------------------");
        read(".");
    }

    /*
    * 递归算法遍历文件夹下所有(一级,二级...)文件
    * 缺点:不能把前面冗余的去掉绝对路径
    * */
    public static void read(String dir){
        File[] files = new File(dir).listFiles();
        if(files == null) return;
        for (File file :files) {
            System.out.println(file.getAbsoluteFile());
            if (file.isDirectory()){
                read(file.getPath());
            }
        }
    }

}
class ReadFileName{
    public static void read(String dir,String fileSuffix) {
        File[] files = new File(dir).listFiles();
        if (files!=null){//没权限的文件
            for (File file:files) {
                if (file.isDirectory()){
                    read(file.getPath(),fileSuffix);
                }else{
                    String name = file.getName();
                    if(name.endsWith(fileSuffix)/*&&!"README.md".equals(name)*/){
                        System.out.println(name);
                    }

                }
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10000; i++) {
            System.out.printf("%04d->%05d\n",i,buy(i, 0, 0));
        }

    }

    /**
     * 啤酒问题
     * 2元一瓶
     * 4盖子一瓶
     * 2瓶子一瓶
     * 有10元
     * money:钱
     * bottle:瓶
     * lid:盖子
     * return:买到的瓶数
     **/
    public static int buy(int money,int bottle,int lid) {
        int count = 0;//瓶数
        if(money<2&&bottle<2&&lid<4){
            //递归出口,买不到了
            return count;
        }
        count = money/2+bottle/2+lid/4;
        money%=2;
        bottle=bottle%2+count;
        lid = lid%4+count;
        return count + buy(money,bottle,lid);
    }
}

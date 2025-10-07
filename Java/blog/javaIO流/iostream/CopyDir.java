package iostream;

import ch.qos.logback.core.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.*;

/**
 * @author HarveyBlocks
 * @date 2023/10/14 16:22
 **/
public class CopyDir {
    /*
     * 复制文件夹
     * 需要:read()递归
     * */
    public static void main(String[] args)  throws IOException {
        copyDir("D:/BilibiliDownload",
                "C:/Users/27970/Desktop/BilibiliDownload",
                1024*32);//32KB比较合适
        System.out.println("success");
    }

    /*
     * String startDir 起始路径
     * String targetDir 目标路径
     * 先假定输入的是绝对路径
     * 经测试,5.56GB 费时 16.5 s
     * */
    public static void copyDir(String startDir,String targetDir,int n){
        startDir = new File(startDir).getAbsolutePath();
        targetDir = new File(targetDir).getAbsolutePath();

        new File(targetDir).mkdirs();

        File[] files = new File(startDir).listFiles();

        if(files == null) return;

        for (File file :files) {
            String newStartPath = file.getAbsolutePath();
            String newTargetPath = targetDir +
                    newStartPath.substring(startDir.length());

            if (file.isDirectory()){
                //深入读取文件夹
                copyDir(newStartPath,newTargetPath,n);
            }else {
                copyFile(newStartPath,newTargetPath,n);
            }

        }

    }

    public static void copyFile(String startPath,String targetPath,int n){
        try (
                InputStream is = new FileInputStream(startPath);
                OutputStream os = new FileOutputStream(targetPath, true);
                BufferedInputStream bis = new BufferedInputStream(is,n);
                BufferedOutputStream bos = new BufferedOutputStream(os, n);
        ) {
            //复制文件
            byte[] bytes = new byte[1024];//一次 1KB
            int len;
            while ((len = bis.read(bytes)) > 0) bos.write(bytes, 0, len);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    * 5.56G 8s
    * 你nb好吧
    * */
    @Test
    public void IOC() throws IOException {
        FileUtils.copyDirectory(new File("D:/BilibiliDownload"),new File("C:/Users/27970/Desktop/BilibiliDownload"));
    }

}
interface asd extends Runnable {
    @Override
    default void run() {

    }
}
package iostream;

import org.junit.Test;

import java.io.*;

/**
 * @author HarveyBlocks
 * @date 2023/10/14 13:46
 **/

public class FileReaderTest {
    @Test
    public void readText() {
        try(
                Reader reader1 = new FileReader("./src/TCP_NET/Client.java");
                ){

            //记每次字符的编号
            int character;
            while((character =reader1.read())>=0){
                System.out.print((char) character);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("\r\n==============================分割线============================");

        try(
                Reader reader = new FileReader("./src/TCP_NET/Server.java");
        ){

            //记每次字符的编号
            char[] chars = new char[16];
            int len;
            while((len =reader.read(chars))>=0){
                System.out.print(new String(chars,0,len));

            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /*
    *
    * */
    @Test
    public void writeText() {
        try (
                Reader reader = new FileReader("./src/TCP_NET/Client.java");
                BufferedReader bReader = new BufferedReader(reader,1024*16);
                //文件若不存在,就会自己创建
        ) {
            String line;
            while ((line = bReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * 用InputStreamReader对字符集一顿操作
    * 进行复制
    * */
    @Test
    public void copyGBK(){
        try (
                InputStream is = new FileInputStream("D:/resources/ab.txt");
                Reader reader = new InputStreamReader(is, "GBK");
                BufferedReader bReader = new BufferedReader(reader);

                OutputStream os = new FileOutputStream("C:/Users/27970/Desktop/ab.txt",true);
                Writer writer = new OutputStreamWriter(os, "GB2312");
                BufferedWriter bWriter = new BufferedWriter(writer);
        ) {
            String s;
            while ((s = bReader.readLine()) != null) {
                bWriter.write(s);
                bWriter.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test(){

    }
}

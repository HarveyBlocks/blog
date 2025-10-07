package iostream;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * @author HarveyBlocks
 * @date 2023/10/13 17:38
 **/
public class TryEncode {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String string = "afs阿达d你a阿凡达dkjh";

        //编码
        byte[] bytes = string.getBytes();//不写默认,看左下方,UTF-8
        System.out.println(Arrays.toString(bytes));

        //解码
        String string1 ;
        string1 = new String(bytes);
        System.out.println("UTF-8->UTF-8\t\t" + string1);

        //编码
        byte[] bytesGBK = string.getBytes("GBK");
        System.out.println(Arrays.toString(bytesGBK));

        //解码
        String string2 ;
        string2 = new String(bytesGBK);
        System.out.println("UTF-8->GBK\t\t\t" + string2);

        string2 =new String(bytesGBK,"GBK");//指定字节数组编码方式,解码成当前系统的编码防止
        System.out.println("UTF-8->GBK->UTF-8\t" + string2);

        //错误解码
        string2 =new String(bytes,"gbk");//明明是UTF-8的编码方式,却指鹿为马地告诉他这是GBK的坏家伙
        System.out.println(string2);
    }
}

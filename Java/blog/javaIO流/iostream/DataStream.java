package iostream;

import org.junit.Test;

import java.io.*;

/**
 * DataInputStream
 * DataOutputStream
 * @author HarveyBlocks
 * @date 2023/10/14 18:57
 **/
public class DataStream {
    private static final String PATH = "D:/resources/data.txt";

    /*
    * DataInputStream
    * */
    public void input(){
        try (DataInputStream dis = new DataInputStream(new FileInputStream(PATH))) {
            //按照顺序读,否则会出错
            System.out.println(dis.readInt());
            System.out.println(dis.readDouble());
            System.out.println(dis.readBoolean());
            System.out.println(dis.readLong());
            System.out.println(dis.readChar());
            System.out.println(dis.readChar());
            System.out.println(dis.readUTF());
            System.out.println(dis.readUTF());
            System.out.println(dis.readUTF());
            System.out.println(dis.readUTF());
            System.out.println(dis.readUTF());
            System.out.println(dis.readUTF());
            System.out.println(dis.readUTF());
            System.out.println(dis.readUTF());
            System.out.println(dis.readUTF());
            System.out.println(dis.readUTF());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    * DataOutputStream
    * */
    public void output(){
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(PATH))) {
            dos.writeInt(1);
            dos.writeDouble(1.1);
            dos.writeBoolean(true);
            dos.writeLong(12L);
            dos.writeChar('a');
            dos.writeChar(97);
            dos.writeUTF("我不是卷王111 qwq");
            dos.writeUTF("我不是卷王222 qwq");
            dos.writeUTF("我不是卷王333 qwq");
            dos.writeUTF("我不是卷王444 qwq");
            dos.writeUTF("我不是卷王555 qwq");
            dos.writeUTF("我不是卷王666 qwq");
            dos.writeUTF("我不是卷王777 qwq");
            dos.writeUTF("我不是卷王888 qwq");
            dos.writeUTF("我不是卷王999 qwq");
            dos.writeUTF("我不是卷王000 qwq");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test(){
        output();
        input();
    }
}

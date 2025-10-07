package iostream;

import org.junit.Test;

import java.io.*;
import java.util.Objects;

/**
 * @author HarveyBlocks
 * @date 2023/10/14 19:47
 **/
public class ObjectStream {
    private static final String PATH = "D:/resources/data.txt";

    /*
     * DataInputStream
     * */
    public void input(){
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PATH))) {
            User user =(User) ois.readObject();
            System.out.println(user.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * DataOutputStream
     * */
    public void output(){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PATH))) {
            User user = new User(12,"A");
            oos.writeObject(user);
            System.out.println(user.getName());
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




/*
* 对象如果需要序列化,必须实现序列化接口
* */
class User implements Serializable{
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //加了transient,就不会序列化
    private transient String name;

    public User(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public User() {
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getAge() == user.getAge() && Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAge(), name);
    }

    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
# XML文件

- 可拓展标记语言
- 后缀.xml
- 可在浏览器里渲染

- 存储多个用户的多个信息

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!-- 上面是xml文件的"抬头声明",一定要写,否则出错 -->
<!--这是注释 你好-->

<users>
    <Administrator id="1" employee = "2">
        <name>Bob</name>
        <age>18</age>
        <sex>male</sex>
        <!--  <check> 4<5 && 5<6 </check> ,这会报错-->
    </Administrator>
    <user id="2">
        <name>Mike</name>
        <age>17</age>
        <sex>male</sex>
        <check>  4&lt;5 &amp;&amp; 5&gt;6 </check>
    </user>
    <user id="3">
        <name>Amy</name>
        <age>18</age>
        <sex>female</sex>
        <check> <![CDATA[ 4<5 && 5<6 ]]> </check>
    </user>
</users>
```

## 适合XML的结构

- 人1
  - name=张三
  - age=18
  - sex=male
- 人2
  - name=李四
  - age=18
  - sex=female

## xml的标签

### 标签

- 形如<标签名> 的称之为标签或元素
- 标签一般成对出现,需要正确嵌套
- xml中的标签可以自己命名
- XML中只能有**一个**根标签
  - 根标签形如上例<users>
- XML的标签可以有属性
  - 属性形如上例<user id="1">
  - 属性是一个键值对

## 注意





- 不要乱在xml文件里写>,<,&啥的(><会被认定是标签)
  - 平替:![image-20231004004846059](../assets/Day30-xml%E6%96%87%E4%BB%B6/image-20231004004846059.png)
  - 可以用浏览器查看是否替换成功:
    - xml文件里的样子:<img src="../assets/Day30-xml%E6%96%87%E4%BB%B6/image-20231004005029309.png" alt="image-20231004005029309" style="zoom: 50%;" />
    - ​												↓
    - 浏览器里的样子:<img src="../assets/Day30-xml%E6%96%87%E4%BB%B6/image-20231004005058669.png" alt="image-20231004005058669" style="zoom:50%;" />

  - 也可以输入"CD"一回车![image-20231004005522775](../assets/Day30-xml%E6%96%87%E4%BB%B6/image-20231004005522775.png)
  - 召唤出特殊数据区:![image-20231004005636626](../assets/Day30-xml%E6%96%87%E4%BB%B6/image-20231004005636626.png)

## 应用场景

- 能存数据结构和特殊的数据关系
- 系统的配置文件
  - 存用户名,密码
- 在网络中转播

## 解析XML文件

### IO流代码

- 难度大
- 繁琐

### 解析XML框架(以Dom4j框架为例)

- Dom4j(第三方研发)
- Dom4j是运用自上而下的获取元素方法
  - 必须:
    1. 先获取文件
    2. 再获取根元素
    3. 然后获取根元素的子元素
    4. 然后再获取根元素的子的子
    5. ..........
- 我估摸着这不就是**树**吗?这不是**前序遍历**吗?

#### 获取Dom4j框架

1. 网上下载Dom4j.jar文件
2. 在项目文件下添加lib目录 
3. 把Dom4j.jar文件拷贝到lib目录下
4. 右键Dom4j.jar,选中**添加为库(在偏下的位置)**

#### 文档对象模型

- Dom4j框架运用文档对象模型
- Dom4j框架(通过SAXReader解析器)解析XML文档后,将其转为**Document类**

![image-20231004144127769](../assets/Day30-xml%E6%96%87%E4%BB%B6/image-20231004144127769.png)



#### 使用Dom4j框架

##### 解析XML文档,获取Document对象

- Dom4j框架**解析XML文档,获取Document对象**常用API:

  ![image-20231004144243331](../assets/Day30-xml%E6%96%87%E4%BB%B6/image-20231004144243331.png)

```java
package learnSpecialDoc;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author HarveyBlocks
 * @date 2023/10/04 14:43
 **/
public class ParseXml {
    public static void main(String[] args) throws Exception {
        //创建一个SAXReader解析器
        SAXReader saxReader = new SAXReader();

        //获取document对象
        Document document =
                //使用saxReader读取需要解析的xml文件
                saxReader.read("src/learnSpecialDoc/MyXML.xml");
                //.read抛出DocumentException异常

        //获取根元素
        Element root = document.getRootElement();
        System.out.println(root.getName());//users
    }
}
```

#### 获取元素及其相关信息

![a](../assets/Day30-xml%E6%96%87%E4%BB%B6/image-20231004150648774.png)

| 方法名                              | 说明                                              |
| ----------------------------------- | ------------------------------------------------- |
| List\<Attribute\> attributes()      | 得到当前元素所有属性(属性名+属性值)               |
| String elementTextTrim(String name) | 得到指定名称的子元素的文本,并为文本去除当前后空格 |





```java
package learnSpecialDoc;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.List;

/**
 * @author HarveyBlocks
 * @date 2023/10/04 14:43
 **/
public class ParseXml {
    public static void main(String[] args) throws Exception {
        Document document;

        {//创建一个SAXReader解析器
            SAXReader saxReader = new SAXReader();

            //获取document对象
            document =
                    //使用saxReader读取需要解析的xml文件
                    saxReader.read("src/learnSpecialDoc/MyXML.xml");
            //.read抛出DocumentException异常

        }//解析XML文档,获取Document对象,见上文

        //获取根元素
        Element root = document.getRootElement();
        System.out.println(root.getName());

        System.out.println("\n\t以下是user&Administrator:");

        List<Element> firstElements = root.elements();
        for (Element element:firstElements) {
            System.out.print(
                    "\tid = " +
                            element.attributeValue("id")
            );
            System.out.println(
                    ",name = " +
                            element.elementText("name")
            );
        }

        System.out.println("\n\t以下是user:");

        List<Element> firstUsers = root.elements("user");
        for (Element element:firstUsers) {
            System.out.print(
                    "\tsex = " +
                            element.elementText("sex")
            );
            System.out.println(
                    ",check = " +
                            element.elementTextTrim("check")
            );

        }

        System.out.println("\n\t以下是Administrator:");
        List<Attribute> administrator = root.element("Administrator").attributes();
        for(Attribute at:administrator){
            System.out.println("\t"+at.getName()+" = "+at.getValue());
        }
    }
}
```

## 把内容写入XML文件

- Dom4j可以做到,但很麻烦
- 推荐把数据写成XML格式,再用IO流写进去就行了

![image-20231004160651541](../assets/Day30-xml%E6%96%87%E4%BB%B6/image-20231004160651541.png)

![image-20231004160730603](../assets/Day30-xml%E6%96%87%E4%BB%B6/image-20231004160730603.png)

## 约束XML文件的书写

- XML文件是可拓展标记语言,很自由
- 越自由,越是要约束
- 限制XML文件只能按照某种格式书写



### 约束文档

- DTD文档**不能约束数据类型**
- Schema文档**能约束数据类型**

### DTD约束文档

- 后缀必须是.dtd

  ```dtd
  <!ELEMENT 书架(书+)>
  <!ELEMENT 书(书名,作者,售价)>
  <!ELEMENT 书名(#DCPATA)>
  <!ELEMENT 作者(#DCPATA)>
  <!ELEMENT 售价(#DCPATA)>
  ```

  - 书架(书+)表示书架的书至少一本以上

  约束后的正确格式:

  ![image-20231004162355349](../assets/Day30-xml%E6%96%87%E4%BB%B6/image-20231004162355349.png)
  
### Schema约束文档

- 后缀必须是.xsd

  ![image-20231004163619730](../assets/Day30-xml%E6%96%87%E4%BB%B6/image-20231004163619730.png)

  ![image-20231004163530192](../assets/Day30-xml%E6%96%87%E4%BB%B6/image-20231004163530192.png)

  

  约束后的正确格式:

  ![image-20231004163722984](../assets/Day30-xml%E6%96%87%E4%BB%B6/image-20231004163722984.png)

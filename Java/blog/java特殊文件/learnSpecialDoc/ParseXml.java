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

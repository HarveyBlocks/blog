package learnSpecialDoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author HarveyBlocks
 * @date 2023/10/04 22:28
 **/
public class LearnLog {
    public static final Logger LOGGER = LoggerFactory.getLogger("LearnLog");
    public static void main(String[] args) {
        Double ans = null;
        try {
            LOGGER.info("除法开始执行");
            // info表示重要
            // 日志会记录到文件,控制台
            // 是由核心控制文件控制的
            ans = divide(10, 0.0);

            LOGGER.info("除法执行成功");
            //System.out.println(ans);没必要了
        }catch (Exception e){
            LOGGER.error("除零错误,除法执行失败");//规范且支持后面的使用
        }
        LOGGER.info("ans = " + ans);
    }
    public static Double divide(double a,double b) throws Exception {
        LOGGER.debug("参数a:"+a);//程序的执行流程
        LOGGER.debug("参数b:"+b);//程序的执行流程
        if (b<1e-15){
            throw new Exception("divide 0");
        }
        Double c = a / b;
        LOGGER.debug("return "+c);
        return c;
    }
}

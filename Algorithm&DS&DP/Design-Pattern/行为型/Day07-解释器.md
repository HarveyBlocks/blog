# 解释器

解释器解释一个表达式, 来达到输入自由的目的



## 文法表达式

定义表达式的表达式

```
expretion ::= value | plus | value
plus ::= expression '+' expretion
minus ::= expression '-' expretion
value ::= integer
```

-   `::=` 定义为
-   `|` 逻辑或
-   `''` 运算字符

## 抽象语法树

## 结构

-   抽象表达式
    -   Abstract Expression
    -   包含解释方法`interpreter(Context context)`
-   终结符表达式
    -   Terminal Expression
    -   文法中每一个终结符都有一个具体终结表达式对应
-   非终结符表达式
    -   Nonterminal Expression
    -   非终结表达式角色
    -   每个规则都对应一个非终结表达式
-   上下文
    -   Context
    -   包含解释器需要的数据或公共功能
    -   传递所有解释器共享的数据, 以至于后面的解释器可以获得这个值
-   客户端
    -   将需要的分析的句子或表达式转换为使用解释器对象描述的抽象语法树
    -   调用解释器的解释方法
    -   也可以通过上下文访问解释器的方法

## 逻辑表达式(不含括号)



Context Map\<VariableName, Value\> get-set

Expression VariableName value

OrExpresion right:Expression or left:Expression

​	incepter()->(this.right or this.left)

AndExpresion right:Expression and left:Expression

NotExpresion not value:Expression

如果要逻辑表达式和算数表达式统一, 还是要让bool的看作是0和1比较好

```java
Context context = new Context();

Variable a = new Variable("a");
Variable b = new Variable("b");
Variable c = new Variable("c");
Variable d = new Variable("d");
Variable e = new Variable("e");
//Value v = new Value(1);

context.assign(a, 1);
context.assign(b, 2);
context.assign(c, 3);
context.assign(d, 4);
context.assign(e, 5);

AbstractExpression expression = new Minus(new Plus(new Plus(new Plus(a, b), c), d), e);

System.out.println(expression + " = " + expression.interpret(context));
```

```java
public class Plus extends AbstractExpression {
    private AbstractExpression left;
    private AbstractExpression right;

    public Plus(AbstractExpression left, AbstractExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret(Context context) {
        return left.interpret(context) + right.interpret(context);
    }

    @Override
    public String toString() {
        return "(" + left + " + " + right + ")";
    }
}
```



## 缺点

复杂文法难以维护, 一个文法一个类, 文法多了呢?

执行效率低, 解释器模式建立在文法树上, 就有循环和递归, 反复调用, 解释速度慢

代码的调试过程麻烦



越是想要解释器能适配更多的表达式(例如算数+逻辑), 就越要往底层靠, 越向底层靠, 解释器递归和循环调用的缺点就越显现, 就越不会使用解释器.....所以对解释器的要求要低, 别想着用解释器做的十全十美, 解释器只能做成被偶尔使用小工具

## 使用场景

文法简单, 可化为抽象语法树(Corn表达式做得到吗?)

1. 构建AST
2. 构建符号表(未初始化的变量使用, 重复声明)
3. 检查id和检查类型
4. break/continue 是否在循环内(如果在语义分析阶段, break在循环里还是switch里, 会产生歧义) 
5. return 语句是否应该有返回值/返回值类型
6. 常量表达式求值(数组维度越界等)
7. 构建中间代码



### 构建符号表

设计符号表

1. no序列号
2. 标识符
3. 类型
4. 是否初始化

构建符号表

1. 规约时, 发现产生式的右部含有id, 则依据产生式决定
   - 是declaration, 则id注册到符号表
   - 否则, 则从符号表查询id对应的no, 用no代替
2. 考虑作用域
   - 遇到`{`, 局部变量表栈弹入一个新表
   - 遇到`}`, 局部变量表栈弹出栈顶



### break和continue

break和continue是否在合适的位置

1. 遇到break和continue的时候, **注册**这两个token
2. 每次规约时, 注册表信息**向上传递**
3. while/switch/do-while 的产生式规约, 将其右部涉及到的break/continue的, 则通过
4. 函数/内部代码块/静态代码块/程序顶等产生式, 遇到这种规约时, 进行一次判断, break和continue的注册表里不得含有元素, 有元素则不通过, 对于此处的break和continue进行报错



### return

return 语句是否应该有返回值, 返回值类型是否和函数类型适合

实验课不一定会遇到函数

1. 记录每一个return
2. 对于函数/内部代码块/静态代码块/程序顶等产生式的规约, 进行一次判断, 此时能获取到声明的返回值类型, 也能对于所有的return的信息
3. 对于匹配的return进行通过(内部代码块和静态代码块的return的返回值类型是void)
4. 对于不匹配的return进行报错

### 常量表达式求值

1. 每一个节点记录是否能直接或间接变成常量值(定义此节点是常量节点)
2. 每一个节点记录常量值(如果此节点是常量节点)
3. 如果产生式的每一个孩子节点都是常量节点(参考具体文法), 那么此产生式规约时产生的父亲节点将变成常量节点. 其值是每一个孩子节点计算的结果



### 类型检查

参考常量表达式求值

### 构建中间代码

要有产生式->中间代码构建方法的映射

- 需要重构文法的类, 但是这样在语法分析阶段就引入了语义分析阶段的构建中间代码策略, 不够解耦了
- 构建一个Adaptor类, 专门用于构建这种映射, 但是需要识别到底是哪个产生式, 困难
  - 解决方案: **使用产生式编号**, 因为构造分析表的时候, 已经有产生式编号这种东西了

break和continue如何转换成中间代码? goto;

```
while(expr) {
	// codes1
	break;
	// codes2
}
// code3

start_while:
	st_push expr
	ifn_goto st_top end_while
	// codes1
	goto end_while // 需要知道 end_while 对象
	// codes2
	goto start_while
end_while:
	// code3

```

下表中的序号, 本来是和分析表对应的, 但是只要修改了分析表(文法), 就会变动, 因此很麻烦

```
13	: program->stmt_list
	// this.command[0] = stmt_list.command();
0	: stmt_list->ε
	// 不需要
33	: stmt_list->stmt stmt_list  	
	// this.command[0] = stmt.command();
	// this.command[1] = stmt_list.command();
9	: stmt->unmatched_stmt
	// this.command[0] = unmatched_stmt.command();
10	: stmt->matched_stmt
	// this.command[0] = matched_stmt.command();
2	: matched_stmt->declaration_stmt
	// this.command[0] = declaration_stmt.command();
8	: matched_stmt->do_while_stmt
	// this.command[0] = do_while_stmt.command();
14	: matched_stmt->block
	// this.command[0] = block.command();
18	: matched_stmt->matched_if_stmt
	// this.command[0] = matched_if_stmt.command();
22	: matched_stmt->assignment_stmt
	// this.command[0] = assignment_stmt.command();
24	: matched_stmt->expr_stmt
	// this.command[0] = expr_stmt.command();
20	: matched_stmt->empty_stmt
	// this.command[0] = empty_stmt.command();
30	: matched_stmt->matched_while_stmt
	// this.command[0] = matched_while_stmt.command();
15	: unmatched_stmt->unmatched_if_stmt
	// this.command[0] = unmatched_if_stmt.command();
28	: unmatched_stmt->unmatched_while_stmt
	// this.command[0] = unmatched_while_stmt.command();
41	: matched_while_stmt->while ( expr ) matched_stmt
	// this.command[0].setLabel(L1);
	// this.command[0] = expr.command();
	// this.command[1] = Command.ifn_st_goto(L2);
	// this.command[2] = matched_stmt.command();
	// this.command[3] = Command.goto(L1);
	// this.setLabelAfterCommands(L2);
40	: unmatched_while_stmt->while ( expr ) unmatched_stmt
	// this.command[0].setLabel(L1);
	// this.command[0] = expr.command();
	// this.command[1] = Command.ifn_st_goto(L2);
	// this.command[2] = unmatched_stmt.command();
	// this.command[3] = Command.goto(L1);
	// this.setLabelAfterCommands(L2);
46	: matched_if_stmt->if ( expr ) matched_stmt else matched_stmt
	// this.command[0] = expr.command();
	// this.command[1] = Command.ifn_st_goto(L1);
	// this.command[2] = matched_stmt.command();
	// this.command[3] = Command.goto(L2);
	// this.command[4].setLabel(L1);
	// this.command[4] = matched_stmt.command();
	// this.setLabelAfterCommands(L2);
44	: unmatched_if_stmt->if ( expr ) stmt
	// this.command[0] = expr.command();
	// this.command[1] = Command.ifn_st_goto(L1);
	// this.command[2] = stmt.command();
	// this.setLabelAfterCommands(L1);
45	: unmatched_if_stmt->if ( expr ) matched_stmt else unmatched_stmt
	// this.command[0] = expr.command();
	// this.command[1] = Command.ifn_st_goto(L1);
	// this.command[2] = matched_stmt.command();
	// this.command[3] = Command.goto(L2);
	// this.command[4].setLabel(L1);
	// this.command[4] = unmatched_stmt.command();
	// this.setLabelAfterCommands(L2);
47	: do_while_stmt->do stmt while ( expr ) ;
	// this.command[0].setLabel(L1);
	// this.command[0] = stmt.command();
	// this.command[1] = expr.command();
	// this.command[2] = Command.if_st_goto(L1);
38	: block->{ stmt_list }
	// this.command[0] = stmt_list.command();
34	: declaration_stmt->type id ;
	// 这个其实什么也做不了
42	: declaration_stmt->type id = expr ;
	// this.command[0] = expr.command();
	// 将栈顶的值复制到局部变量表
	// this.command[1] = Command.assign_st_to_variable(id.no());
39	: assignment_stmt->lvalue = expr ;
	// this.command[0] = expr.command();
	// this.command[1] = lvalue.command();
	// 从栈里拿出两个, top, down. down 的值赋值到 top 指向的那片内存上
	// this.command[2] = Command.assign_st_down_to_top();
5	: empty_stmt->;
	// 什么都不做
3	: type->string
	// 什么都不做
4	: type->int32
	// 什么都不做
6	: type->char
	// 什么都不做
16	: type->float64
	// 什么都不做
26	: type->boolean
	// 什么都不做
31	: expr_stmt->expr ;
	// this.command[0] = expr.command();
37	: expr->expr + term
	// this.command[0] = expr.command();
	// this.command[1] = term.command();
	// this.command[2] = Commnad.st_plus();
11	: expr->term
	// this.command[0] = term.command();
35	: term->term * factor
	// this.command[0] = expr.command();
	// this.command[1] = term.command();
	// this.command[2] = Commnad.st_multi();
12	: term->factor
	// this.command[0] = factor.command();
1	: factor->primary
	// this.command[0] = primary.command();
36	: primary->( expr )
	// this.command[0] = expr.command();
7	: primary->const_string
	// this.command[0] = Command.load_static_string(token);
17	: primary->constant_float
	// this.command[0] = Command.load_static_float(token);
21	: primary->true
	// this.command[0] = Command.load_static_boolean(token);
23	: primary->constant_character
	// this.command[0] = Command.load_static_character(token);
25	: primary->false
	// this.command[0] = Command.load_static_boolean(token);
29	: primary->constant_integer
	// this.command[0] = Command.load_static_integer(token);
27	: primary->lvalue
	// this.command[0] = lvalue.command();
	// 将 lvalue 的 command 后栈顶是引用, 需要转成值
	// this.command[1] = Command.st_top_reference_to_value();
32	: lvalue->id
	// this.command[0] = Command.load_variable_reference(id.no());
19	: lvalue->lvalue [ expr ]
	// 获取 offset
	// this.command[0] = expr.command();
	// 获取 base
	// this.command[1] = lvalue.command();
	// 弹出base[弹出offset]
	// 此时栈顶是base, base弹出后, 栈顶是offset
	// this.command[2] = Command.st_at();
```

以`unmatched_while_stmt->while ( expr ) unmatched_stmt`为例, 说明`setLabelAfterCommands`的更好的设计: 

```java
interface CommandRegister {
	void register(CommandNode outer, int i);
}
interface CommandNode {	
    CommandNode[] getChildren();
    
    void flat(List<Command> result) {
        // 递归
        for(CommandNode child:this){
        	child.flat(result);
        }
    }
}

interface TerminalCommandNode extends CommandNode {
    private Command command; 
    @Override
    void flat(List<Command> result) {
        result.add(this.command);
    }
}
interface LabelNode extends CommandNode {
    private final Label label;
    @Override
    void flat(CommandList result) {
        this.label.setIndex(result.size());
    }
}
@AllArgumentsConstractor
class UnmatchedWhileStmt implements CommandRegister {
	private final CommandNode commands;
    private final Label whileEnd;
	
    @Override
	void register(CommandNode outer) {
		outer.addCommand(this.commands); // 向外注册自己的产品
        // 让内部的拿到外部的, 有利于注册label
		outer.addLabel(whileEnd);
	}

	class Factory {
        CommandRegister create(
        	CommandRegister expr, CommandRegister unmatched_stmt, LabelGenerator lg){
            CommandNode commands;
            Label whileStart = lg.next();
            Label whileEnd = lg.next();
            commands.addLabel(whileStart);
            expr.register(commands);
            commands.addCommand(CommandFactory.ifn_st_goto(whileEnd)); // 静态工厂
            matched_stmt.register(commands);
            commands.addCommand(CommandFactory.goto(whileStart)); // 静态工厂
            return new UnmatchedWhileStmt(commands, whileEnd);
		}
	}
}
```

总结一下规律

- 每个stmt之后, 栈应该为空
- 对于形如 `A->B`的简单的产生式, 直接 `this.command[0] = B.command()` 即可
- 对于涉及到中缀表达式的 `A->B oper C` 的, 直接在第三个增加一条命令, 运算两个即可
- 对于控制结构的, 比较复杂, 需要另外讨论
- 对于涉及到 `lvalue` 或者 `id` 的, 需要特别注意到存在栈里的到底是引用还是值, 然后调整一下
- 对于涉及到字面量的, 需要向栈里加载字面量即可
- 对于 break 和 continue
  1. 遇到break的表达式, 向构造一个 `goto L?` 的命令占位, 然后注册这个`break`的token和command
  2. 用 `while` 或者 `switch` 进行规约的时候, 将注册了的 break 都把未确认的 L 给确认了
  3. 如果迟迟没有合适的规约, 说明break的位置不对, 报错
- 其实上面的构建的是树, 后面还要拍平, 转化为序列.
- 转化为序列的时候, Label是否考虑成index?我不好说, 暂定吧(实验一般是没有要求的)
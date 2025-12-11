# grep命令

## 语法

```Linux
grep [-n] 关键字 文件路径
```

-  -n 可选 表示结果中匹配的行的行号
- 关键字,必填,表示过滤的关键字,还有空格或其他符号**建议使用" "将关键字包围起来**
- 文件路径.必填,表示要过滤内容的文件路径,**可作为内容的输入端口**

## 示例

![image-20230929222413269](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/指令/Day03-过滤文件内容命令/image-20230929222413269.png)

```Linux
grep "know" ./test.txt
```

![image-20230929222830246](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/指令/Day03-过滤文件内容命令/image-20230929222830246.png)

```Linux
grep -n "know" ./test.txt
```

![image-20230929223030108](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/指令/Day03-过滤文件内容命令/image-20230929223030108.png)


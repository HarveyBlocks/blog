# 铁令

1. 切换分支前一定要提交本地修改
2. 代码即使提交,提交过就不会丢
3. 遇到任何问题都不要删除文件

```bash
git init
```

把这个文件夹初始化为一个仓库

```bash
git remote add origin  https://github.com/HarveyBlocks/collection-java
```

这个链接更改为你的仓库地址 就是上一张图片的那个地址

```bash
git branch -M main
```

切换到main分支

```bash
echo "# Hello World" > README.md   
```

将"# Hello World!" 输入到README.md文件

如果这个文件不存在的话会自动创建

  ```bash
git add .
  ```
添加全部的发生了更改的文件

  ```bash
git commit -m "add:first commit"
  ```

设置提交消息

  ```bash
git push -u origin main
  ```

将你的代码push到远程仓库


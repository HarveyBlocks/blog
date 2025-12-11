# 抓取和拉去

- 对于一个仓库clone一次到本地,然后这个仓库被别人进行了小改进,你想要得到这个改进,要再clone一遍吗?

## 抓取

```Git
git fetch [remote name] [branch name]
```

- [remote name] origin
- 抓取,即将仓库的更新都抓到本地,**不会进行合并**
- 缺省[remote name]和[branch name]就抓取所有分支

## 拉取

````Git
git pull [remote name] [branch name]
````

- 拉取,,即将仓库的更新都抓到本地**并进行合并**,相当于fetch + **merge**
- 缺省[remote name]和[branch name]就**抓取并合并**所有分支
- 也会产生与**merge**一样的冲突问题
  - 解决经验:在push之前先pull以下解决merge问题

- fetch之后不影响执行pull


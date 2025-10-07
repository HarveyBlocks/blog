# 本地仓库推到远程仓库

## 本地仓库绑定远程仓库

```Git
git remote add 远端名称 远程仓库地址
```

- 远端名称随便取,一般是origin

### 查看本地仓库是否绑定远程仓库

```Git
git remote
```

- 若成功,返回你取的名字

## 提交本地仓库到远程仓库

``` Git
git push origin master
```

- origin就是远端名称
- master是分支,其他的分支也可以交

```Git
git push[-f] [--set-upstream] [远端名称 [本地分支名][:远端分支名]]
```

- -f 远程仓库和本地仓库同一个文件内容冲突,强制**覆盖**远程仓库的内容
- --set-upstream第一遍写了之后对应之后就可以只写`git push`
- 本地分支名和远端分支名一致可以省略[:远端分支名]

## 本地分支和远程分支

```Git
git branch -vv
```

- 查看本地分支和远程分支的对应关系
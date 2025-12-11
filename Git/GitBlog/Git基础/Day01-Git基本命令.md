| 命令                                      | 描述                                                         |      |
| ----------------------------------------- | ------------------------------------------------------------ | ---- |
| `git init`                                | 初始化本地仓库                                               |      |
| `git add 文件名`                          | 文件从工作区到暂存区                                         |      |
| `git add .`                               | 通配符**.**表所有                                            |      |
| `git commit -m "给这一次提交命名"`        | 文件从暂存区到本地仓库                                       |      |
| 修改文件                                  | 文件子自动拉到工作区                                         |      |
| `git status`                              | 查看暂存区和缓存区的文件状态                                 |      |
| `git log`                                 | 查看本地仓库提交历史                                         |      |
| `git log --all`                           | 显示所有分支                                                 |      |
| `git log --pretty=online`                 | 将提交信息显示为一行                                         |      |
| `git log --abbrev-commit`                 | 使输出的commit更加简短,abbrev(优化)                          |      |
| `git log --graph`                         | 以图的方式显示                                               |      |
| `git log --pretty=online --abbrev-commit` | 可以像这样夹buff                                             |      |
| `git reset --hard commitID`               | 版本回退,commitID见`git log`                                 |      |
| `git reflog`                              | 查看所有操作的记录(`git log`只会记录**有效的**提交记录,也就是说,版本绘图之后`git log`不会看见回退版本后的 提交记录) |      |
| `touch .gitignore`                        | 创建一个文件,指定不希望被git管理的文件(用vi进入编辑文件,支持通配符*) |      |
|                                           |                                                              |      |
|                                           |                                                              |      |
|                                           |                                                              |      |
|                                           |                                                              |      |
|                                           |                                                              |      |
|                                           |                                                              |      |
|                                           |                                                              |      |
|                                           |                                                              |      |


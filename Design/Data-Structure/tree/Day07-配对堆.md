# 配对堆

无法可持久化

合并

## 结构

小根堆

![image-20240701222022505](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Design/Data-Structure/tree/Day07-配对堆/image-20240701222022505.png)

化为: 

![image-20240701222030417](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Design/Data-Structure/tree/Day07-配对堆/image-20240701222030417.png)

以链表的形式存储处于同一父节点下, 同一深度的所有孩子节点

## 查询最小值

## 合并/插入

堆A,堆B, 根节点小的那个指向根节点大的那个, 让根节点大的那个成为根节点小的那个的孩子

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Design/Data-Structure/tree/Day07-配对堆/image-20240701222541263.png" alt="image-20240701222541263" style="zoom:50%;" />

## 删除根

将所有的孩子以优先级合并

这一步的最终目的是为了选举出新的根, 

然后其他不是根, 但是同属于根的孩子的节点, 在这一步中的调整并不被关注

合并方法是, 遍历根节点的孩子链表, 将第一个和第二个断绝兄弟关系, 然后合并

这一次合并将选举出父亲, 这个父亲和第三个兄弟断绝关系, 然后新父亲和第三个兄弟合并, 同时选举出新新父亲, 以此类推

如果要删除特定的节点, 就将那个节点作为根, 那个节点以下的树一个子树再操作即可

## 提高优先级

对于小根堆来说, 提高优先度就是将值减小

如果要提高X的优先级, 导致和其父亲不再具备高低关系, 就从原树种剔除X为根的子树

然后拿出合并以X为根的子树和原树


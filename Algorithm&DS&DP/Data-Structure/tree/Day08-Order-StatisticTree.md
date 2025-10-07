#Order-StatisticTree

本质是红黑树, 包含属性size, 表示包括这棵节点的子树的大小

可以用于快速找出第N位的元素

## updateSize


$$
x.size=x.right.size+x.left.size+1
$$

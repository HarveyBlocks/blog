# 数据模型

Zookeeper是一个树形目录, 其数据模型拥有一个层次化结构

树的每个节点都称为**ZNode**

##ZNode

每个节点上都会保存自己的**数据**和**节点信息**

节点允许又有子节点, 同时也允许少量(1MB)数据存储在该节点之下

节点四大类:

-   `Persistent` 持久化节点 
-   `Ephemeral` 临时节点 `-e`
-   `Persistent_Sequential` 持久化序列(创建出来之后会加上编号)节点 `-s`
-   `Ephemeral_Sequential` 临时顺序写点 `-es`
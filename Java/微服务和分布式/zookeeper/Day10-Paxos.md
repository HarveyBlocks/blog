# Paxos算法

> 消息广播算法

基于消息传递, 有高度容错

保证网络分区, 网络延迟, 节点失效的情况下, 所有的节点都能处于同一种状态

**过半理论/半数原则** - 少数服从多数

-   Basic Paxos
-   Multi Paxos
-   Fast Paxos



## 角 色

-   Client
    -   系统外部角色
    -   请求发布者
-   Proposer
    -   分发提案
-    Acceptor
    -   表决者
    -   是否accept该提案, 
    -   超过半数`Acceptor`接收了提案, 改提案才被认定为选中
    -   标示ID`myid`
    -   保存自己的Accept的最大提案编号(MAXN)
-   Leaner
    -   提案被选定后, 同步执行提案
    -   不参与决策

因为半数原则, 不需要全部的Acceptor参加, 半数成员参加就行了

如果Proposer挂了, 其中一个Acceptor变成Processor解决问题

## 流程

1.  prepare阶段
    1.  Proposer 提出提案, 编号N, 发送给所有Acceptor
    2.  Acceptor收到prepare请求时, 比较N和自己的最大提案编号(MAXN)比较
    3.  N大于MAXN, 接收提案, 并将**曾经Accept过的, 编号N最大的提案**返回给Proposer
    
2.  accept阶段
    1.  Proposer收到超过半数Accpetor反馈(不会继续向下面的没发到的Accptor发送提案, 没接收提案的Accptor不会有编号N的记录), Proposer将真正的提案内容发送给所有Acceptor

    2.  Acceptor接收提案后将自己曾经Accept过的, 最大的提案编号和反馈过的Prepare的最大编号进行比较

        N大于两个编号, 则当前Acceptor会accept该提案, 并反馈给Processor, 

        否则拒绝该提案

    3.  编号N就是用来给那些上一次没有参与实质投票的Accptor更多的机会

3.  若Processor没有接收到accept反馈 ,则重新进入prepare阶段, 递增提案编号, 重新提出prepare请求,

    若收到半数以上的accept, 则其他未向Processor反馈的Acceptor成为Leaner, 主动同步提议者的提案

## base paxos活锁问题

>   dueling

多个相互协助的线程, 对彼此响应, 并更改自己的状态

当对任何一个线程都无法继续执行, 就发生活锁问题



1.  Proposer(P1)以访问权限1向Accept发起第一阶段, Accept访问权限升级到1

2.  Proposer(P2)以访问权限2向Accept发起第一阶段, Accept访问权限升级到2

3.  P1以访问权限1向Accept发起第二阶段, Accept访问权限2, P1无权访问

4.  P1重试, 以访问权限3向Accept发起第一阶段, Accept访问权限升级到3

5.  P2以访问权限2向Accept发起第二阶段, Accept访问权限3, P2无权访问

6.  P2重试, 以访问权限4向Accept发起第一阶段, Accept访问权限升级到4

    .......

活锁的避免和解开: 时间差
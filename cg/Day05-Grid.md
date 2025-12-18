# 网格表示

正方形网格

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210194011972.png" alt="image-20251210194011972" style="zoom:50%;" />

为何计算机上选择正方形作为pixel单元的形状

- 总是有四个单元相邻
- 容易使用索引来对应
- 容易进行平均这种附加操作
- 易于存储

## 表面Surface

- 仅仅是表层, 是边界, 是空心的

- 是流形(Manifold)的, 流形的每一个部分放大后应该趋于平面

  存在一些图形是非流形的

  <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210194631064.png" alt="image-20251210194631064" style="zoom:50%;" />

  中间部分无法趋于平面, 下面是其他的例子

  <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210194806637.png" alt="image-20251210194806637" style="zoom:50%;" />

- 流形连通性, 是"扇面" ,而不是鳍

  - 每条边只包含在两个多边形中
  - 汇聚点四周应该以多个扇面的形式存在. 汇聚点周边的图形应该在同一个多边形环/扇里

  <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210195335057.png" alt="image-20251210195335057" style="zoom:50%;" />

流形保证目标几何体是简单的, 不需要考虑太多的特殊情况

边界Boundary

- 是表面结束的地方
- 每个边界都是闭环的

## 半边三角形网格

polygon mesh的一种

基于链表实现, 便于写操作

半边三角形只能在流形的前提下构建

```cpp
struct Halfedge {
    /**
     * twin->twin == this
     * twin != this
     */
    Halfedge* twin;
    /**
     * this 必是某个 halfedge 的 next
     */
    Halfedge* next;
    Vertex* start;
    Edge* edge;
    Face* face;
};
struct Edge {Halfedge* halfedge;};
struct Face {Halfedge* halfedge;};
struct Vertex {
    Vector3 point;
    Halfedge* halfedge;
};
```

但Halfedge构建时, `twin` 和 `next` 符合要求, 则满足改网络描述的是一个流形

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210202751136.png" alt="image-20251210202751136" style="zoom:50%;" />

遍历面的所有顶点

```cpp
Halfedge* h = f->halfedge;
do{
    // h->start...
    h = h->next;
}while(h != f->halfedge);
```

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210203511738.png" alt="image-20251210203511738" style="zoom:50%;" />

获取 `Halfedge` 的 `end ` `Vertex`

```cpp
Vertex* end = h->twin->start;
```

访问`Vertex`周边所有的`halfedge`

```cpp
Halfedge* h = v->halfedge;
do{
    // Vertex* end = h->twin->start;...
    // h->edge...
    // h->face...
    h = h->twin ->next;
}while(h != v->halfedge);
```

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210203451586.png" alt="image-20251210203451586" style="zoom: 50%;" />

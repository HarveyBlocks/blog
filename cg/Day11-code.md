# 采样

## 在三角形内

```cpp
bool RasterizerImp::is_point_in_triangle(
    Vector2D sample, Vector2D p0, Vector2D p1, Vector2D p2) {
    // 使用lambda函数封装叉积计算
    auto cal_l = [&](Vector2D v_i, Vector2D v_j) -> float {
        return (sample.x - v_i.x) * (v_j.y - v_i.y) - 
               (sample.y - v_i.y) * (v_j.x - v_i.x);
    };
    // 计算三个边的方程值
    float L0 = cal_l(p0, p1);
    float L1 = cal_l(p1, p2);
    float L2 = cal_l(p2, p0);
    // 判断是否在同侧（包含边界）
    return (L0 >= 0 && L1 >= 0 && L2 >= 0) || 
        (L0 <= 0 && L1 <= 0 && L2 <= 0);
}
```

```xml

```



## 包围盒

```cpp
struct Box {
    Vector2D start;
    Vector2D end;
    Box(const Vector2D& s, const Vector2D& e) : start(s), end(e) {}
    
    // 计算三角形的整数像素包围盒
    static Box box_triangle(const Vector2D& a, const Vector2D& b, const Vector2D& c) {
        // 计算浮点数包围盒
        float min_x = min(a.x, min(b.x, c.x));
        float max_x = max(a.x, max(b.x, c.x));
        float min_y = min(a.y, min(b.y, c.y));
        float max_y = max(a.y, max(b.y, c.y));
        
        // 转换为整数像素坐标（向下取整和向上取整）
        Vector2D start((int) floor(min_x), (int) floor(min_y));
        Vector2D end((int) ceil(max_x), (int) ceil(max_y));
        return Box(start, end);
    }
    
    // 保证包围盒在边界内
    Box& clamp_to_image(int width, int height) {
        start.x = max(0, start.x);
        start.y = max(0, start.y);
        end.x = min(width - 1, end.x);
        end.y = min(height - 1, end.y);
        return *this;
    }
};
```

## 超采样

```cpp
void RasterizerImp::fill_pixel(size_t x, size_t y, Color c) {
    if (x >= width || y >= height) return;
    // 超采样：为当前像素的所有子采样点填充相同颜色
    size_t pixel_index = (y * width + x) * sample_rate;
    for (unsigned int i = 0; i < sample_rate; i++) {
        sample_buffer[pixel_index + i] = c;
    }
}
```

采样

```cpp
// 计算三角形包围盒
Box bbox = Box::box_triangle(p0, p1, p2)
    .clamp_to_image(width,heigth);
int grid_size = (int) sqrt(sample_rate);

// 遍历包围盒内的所有像素
for (int y = bbox.start.y; y <= bbox.end.y; y++) {
    for (int x =  bbox.start.x; x <=  bbox.end.x; x++) {
        // 当前像素在采样缓冲区中的起始索引
        size_t pixel_index = (y * width + x) * sample_rate;
        // 遍历当前像素内的所有子采样点
        for (int i = 0; i < grid_size; i++) {
            for (int j = 0; j < grid_size; j++) {
                Vector2D sample(x + (i + 0.5f) / grid_size, y + (j + 0.5f) / grid_size);
                if (is_point_in_triangle(sample, p0, p1, p2)) {
                    int sample_index = i * grid_size + j;
                    sample_buffer[pixel_index + sample_index] = color;
                }
            }
        }
    }
}
```

## Z-Buffer

```cpp
draw_sample(x,y,depth,color){
	if(pass_depth_test(depth,z_buffer[x][y])){
		// triangle now is closer
        z_buffer[x][y] = depth;
        color_buffer[x][y] = color
	}else{
		// do nothing
	}
}
```

# 纹理映射

## 重心比重

```cpp
struct TriangleWeight{
    float alpha;
    float beta;
    float gamma;
    TriangleWeight(Vector2D p,Vector2D p0,Vector2D p1,Vector2D p2, float deno){
        // 计算重心坐标
      	alpha = ((p1.y-p2.y)*(p.x-p2.x)+(p2.x-p1.x)*(p.y-p2.y))/deno;
      	beta = ((p2.y-p0.y)*(p.x-p2.x)+(p0.x-p2.x)*(p.y - p2.y))/deno;
      	gamma = 1.0f - alpha - beta;
    }
    
    bool in_triangle() const {
        return alpha >= 0.0f && alpha <= 1.0f && 
            beta >= 0.0f && beta <= 1.0f &&
            gamma >= 0.0f && gamma <= 1.0f;
    }
    
    float interpolate(float v0,float v1,float v2) const {
        return alpha * v0 + beta * v1 + gamma * v2
    }
    
    Vector2D interpolate_uv(Vector2D uv0, Vector2D uv1, Vector2D uv2) const {
    	return Vector2D(
        	interpolate(uv0.x, uv1.x, uv2.x),
        	interpolate(uv0.y, uv1.y, uv2.y)
        );
    }
}
```



## 重心插值

```cpp
void RasterizerImp::rasterize_interpolated_color_triangle(
    Vector2D p0, Color c0, Vector2D p1, Color c1, Vector2D p2, Color c2)
    // 计算三角形包围盒
    Box bbox = Box::box_triangle(p0, p1, p2)
        .clamp_to_image(width,heigth);
    // 计算重心坐标分母
    float deno = (p1.y-p2.y)*(p0.x-p2.x)+(p2.x-p1.x)*(p0.y-p2.y);
    if (fabs(deno) < 1e-6) return;
    // 遍历包围盒
    for (int y = bbox.start.y; y <= bbox.end.y; y++) {
        for (int x = bbox.start.x; x <= bbox.end.x; x++) {
            Vector2D p(x + 0.5f, y + 0.5f);
            TriangleWeight weight(p, p0, p1, p2, deno);
            if (!weight.in_triangle()) continue;
            // 插值颜色
            Color ip_color(
              	weight.interpolate(c0.r, c1.r, c2.r),
               	weight.interpolate(c0.g, c1.g, c2.g),
               	weight.interpolate(c0.b, c1.b, c2.b),
            );
            fill_pixel(x, y, ip_color);
        }
    }
}
```



## Texture采样

```cpp
void RasterizerImp::rasterize_textured_triangle(
    Vector2D p0, Vector2D uv0, Vector2D p1, Vector2D uv1,
    Vector2D p2, Vector2D uv2, Texture &tex) {
    
    Box bbox = Box::box_triangle(p0, p1, p2).clamp_to_image(width, height);
    // denominator
    float deno = (p1.y - p2.y) * (p0.x - p2.x) + (p2.x - p1.x) * (p0.y - p2.y);
    if (fabs(deno) < 1e-6) return;
    // 计算每个像素内的采样点数量和步长
	int grid_size = (int) sqrt(sample_rate);
    // lambda 函数
    auto init_param = [&](TriangleWeight weight, Vector2D sample){/*见下方*/}
    // 遍历包围盒内的所有像素
    for (int y = bbox.start.y; y <= bbox.end.y; y++) {
        for (int x = bbox.start.x; x <= bbox.end.x; x++) {
            // accumulated_color
            Color acc_color = Color(0, 0, 0);
            int cnt = 0;
        	for (int i = 0; i < grid_size; i++) {
            	for (int j = 0; j < grid_size; j++) {
                    Vector2D sample(x+(i+0.5f)/grid_size,y+(j+0.5f)/grid_size);
                    TriangleWeight weight(sample, p0, p1, p2, deno);
                    if (!weight.in_triangle()) continue;
                    Vector2D p_uv = weight.interpolate_uv(uv0, uv1, uv2);
					Color tex_color = tex.sample(init_param(p_uv, sample));
                    acc_color += tex_color, cnt++;
                }
            }
            // 如果有样本命中三角形，计算平均颜色并填充像素
            if (cnt > 0) fill_pixel(x, y, acc_color * (1.0f / cnt));
        }
    }
}
```

初始化SampleParams

```cpp
auto init_param = [&](Vector2D p_uv, Vector2D sample) {
    // 构建SampleParams结构体
    SampleParams sp;
    sp.psm = psm;
    sp.lsm = lsm;
    sp.p_uv = p_uv;
    // 计算相邻点 (x+1, y) 的重心坐标和纹理坐标
    TriangleWeight weight_dx(sample + Vector2D(1.0f, 0.0f), p0, p1, p2, deno);
    sp.p_dx_uv = weight_dx.interpolate_uv(uv0, uv1, uv2);
    // 计算相邻点 (x, y+1) 的重心坐标和纹理坐标
    TriangleWeight weight_dy(sample + Vector2D(0.0f, 1.0f), p0, p1, p2, deno);
    sp.p_dy_uv = weight_dy.interpolate_uv(uv0, uv1, uv2);
    return sp;
};
```
## sample_nearest

```cpp
Color MipLevel::get_texel(int tx, int ty) {
    return Color(&texels[tx * 3 + ty * width * 3]);
}

Color Texture::sample_nearest(Vector2D uv, int level) {
    if (level < 0 || level >= mipmap.size()) {
        return Color(1, 0, 1); // 返回洋红色表示无效层级
    }
    auto &mip = mipmap[level];
    // 处理UV坐标的环绕（wrap）
    // 将u,v限制在[0,1)范围内
    float u = uv.x - floor(uv.x), v = uv.y - floor(uv.y);
    // 映射到纹理坐标，并翻转v坐标
    float x = u * mip.width;
    float y = (1.0f - v) * mip.height; // 翻转v坐标
    // 四舍五入到最近的整数坐标
    int tex_x = (int) floor(x + 0.5f);
    int tex_y = (int) floor(y + 0.5f);
    // 处理边界情况（环绕）
	tex_x = ((tex_x % mip.width) + mip.width) % mip.width;
    tex_y = ((tex_y % mip.height) + mip.height) % mip.height;
    // 获取纹理像素
    return mip.get_texel(tex_x, tex_y);
}

```

## sample_bilinear

```cpp
Color Texture::sample_bilinear(Vector2D uv, int level) {
    if (level < 0 || level >= mipmap.size()) {
        return Color(1, 0, 1); // 返回洋红色表示无效层级
    }
    auto &mip = mipmap[level];
    // 处理UV坐标的环绕, 将uv限制在[0,1)范围内
    float u = uv.x - floor(uv.x), v = uv.y - floor(uv.y); 
    // 映射到纹理坐标，并翻转v坐标
    float x = u * mip.width;
    float y = (1.0f - v) * mip.height; // 翻转v坐标
    // 找到四个最近的纹理像素（使用双线性插值的标准方法）
    // 减去0.5，因为纹理像素的中心在整数坐标上
    int x0 = (int) floor(x - 0.5f), y0 = (int) floor(y - 0.5f);
    int x1 = x0 + 1, y1 = y0 + 1;
    // 计算插值权重
    float dx = x - 0.5f - x0, dy = y - 0.5f - y0;
    // 处理边界情况（环绕）
	x0 = ((x0 % mip.width) + mip.width) % mip.width;
    x1 = ((x1 % mip.width) + mip.width) % mip.width;
	y0 = ((y0 % mip.height) + mip.height) % mip.height;
    y1 = ((y1 % mip.height) + mip.height) % mip.height;
    // 获取四个纹理像素的颜色
    Color c00 = mip.get_texel(x0, y0);
    Color c10 = mip.get_texel(x1, y0);
    Color c01 = mip.get_texel(x0, y1);
    Color c11 = mip.get_texel(x1, y1);
    // 水平方向插值
    Color top = c00 * (1 - dx) + c10 * dx;
    Color bottom = c01 * (1 - dx) + c11 * dx;
    // 垂直方向插值
    Color result = top * (1 - dy) + bottom * dy;
    return result;
}
```



### sample

```cpp
Color Texture::sample(const SampleParams &sp) {
    if (sp.lsm == L_ZERO) {
        // 直接从第0层采样
        if (sp.psm == P_NEAREST) {
            return sample_nearest(sp.p_uv, 0);
        } else {
            return sample_bilinear(sp.p_uv, 0);
        }
    } else if (sp.lsm == L_NEAREST) {
        // 计算最接近的mipmap层级
        float level = get_level(sp);
        int nearest_level = min(max(0, (int) floor(level)), mipmap.size() - 1);
        if (sp.psm == P_NEAREST) {
            return sample_nearest(sp.p_uv, nearest_level);
        } else {
            return sample_bilinear(sp.p_uv, nearest_level);
        }
    } else if (sp.lsm == L_LINEAR) {
        // 计算连续的mipmap层级
        float lv = get_level(sp);
        int lv0 = min(max(0, (int) floor(level)), mipmap.size() - 2);
        int lv1 = lv0 + 1;
        // 对两个相邻层级分别采样
        Color color0, color1;
        if (sp.psm == P_NEAREST) {
            color0 = sample_nearest(sp.p_uv, lv0);
            color1 = sample_nearest(sp.p_uv, lv1);
        } else {
            color0 = sample_bilinear(sp.p_uv, lv0);
            color1 = sample_bilinear(sp.p_uv, lv1);
        }
        float weight = lv - lv0;
        return color0 * (1.0f - weight) + color1 * weight;
    }
    return Color(1, 0, 1);// 默认返回品红色表示无效
}
```

# 贝塞尔

## de Casteljau 评估

已知三次贝塞尔曲线由控制点 $\mathbf{P}_0, \mathbf{P}_1, \mathbf{P}_2, \mathbf{P}_3 \in \mathbb{R}^2$（或 $\mathbb{R}^3$）定义，其参数方程为：


$$
\mathbf{B}(t) = (1-t)^3\mathbf{P}_0 + 3(1-t)^2t\mathbf{P}_1 + 3(1-t)t^2\mathbf{P}_2 + t^3\mathbf{P}_3, \quad t \in [0, 1].
$$
为了减少丢失线性评估

```cpp
/**
 * 使用给定的点和标量参数t（函数参数）评估 de Casteljau 算法的一个步骤。
 *
 * @param points    三维点的向量
 * @param t         标量插值参数
 * @return 包含中间点或最终插值向量的向量
 */
std::vector<Vector3D> BezierPatch::evaluateStep(
    std::vector<Vector3D> const &points, double t) const {
    if (points.size() <= 1) return points;
    std::vector<Vector3D> result(points.size() - 1);
    // 在每对相邻点之间执行线性插值
    for (size_t i = 0; i < points.size() - 1; ++i) {
        // 线性插值公式：(1-t)*p_i + t*p_{i+1}
        result[i] = (1 - t) * points[i] + t * points[i + 1];
    }
    return result;
}

/**
 * 在标量参数t处对点向量完整执行 de Casteljau 算法评估
 *
 * @param points    三维点的向量
 * @param t         标量插值参数
 * @return 最终插值向量
 */
Vector3D BezierPatch::evaluate1D(std::vector<Vector3D> const &points, double t) const {
    std::vector<Vector3D> cur;
    for (cur = points;cur.size() > 1;cur = evaluateStep(cur, t));
    return cur[0];
}

/**
 * 在参数(u, v)处评估Bezier曲面
 *
 * @param u         标量插值参数
 * @param v         标量插值参数（沿另一轴）
 * @return 最终插值向量
 */
Vector3D BezierPatch::evaluate(double u, double v) const {
    std::vector<Vector3D> intermediatePoints;
    // 沿u方向对每一行执行 de Casteljau 评估
    for (const auto &cp: controlPoints) {
        intermediatePoints.push_back(evaluate1D(cp, u));
    }
    // 在中间点上沿v方向执行 de Casteljau 评估
    return evaluate1D(intermediatePoints, v);
}
```

# HalfEdge

## normal

求法向量

```cpp
Vector3D Vertex::normal(void) const {
    // 返回此顶点处的近似单位法向量
    // 通过计算相邻三角形法向的面积加权平均值
    // 然后归一化得到
    Vector3D normalSum(0, 0, 0);
    HalfedgeCIter h = halfedge();
    HalfedgeCIter hStart = h;
    do {
        Vector3D p0 = h->vertex()->position;
        Vector3D p1 = h->next()->vertex()->position;
        Vector3D p2 = h->next()->next()->vertex()->position;
        // 叉积给出面的法向量（模长为面积的2倍）
        Vector3D faceNormal = cross(p1 - p0, p2 - p0);
		// 加入加权和（faceNormal的模长 = 2 * 三角形面积）
        normalSum += faceNormal;
        h = h->twin()->next();
    } while (h != hStart);
    // 归一化
    return normalSum.unit();
}
```

## flip

```cpp
EdgeIter HalfedgeMesh::flipEdge(EdgeIter e0) {
    // This method should flip the given edge and return an iterator to the flipped edge.

    // Check if the edge is on boundary
    if (e0->isBoundary()) {
        return e0;
    }

    // Get the two halfedges of this edge
    HalfedgeIter h_v1_v2 = e0->halfedge();
    HalfedgeIter h_v2_v1 = h_v1_v2->twin();

    // Check if either face is boundary
    if (h_v1_v2->face()->isBoundary() || h_v2_v1->face()->isBoundary()) {
        return e0;
    }

    // Get all halfedges around the two triangles
    HalfedgeIter h_v2_v3 = h_v1_v2->next();
    HalfedgeIter h_v3_v1 = h_v2_v3->next();
    HalfedgeIter h_v1_v0 = h_v2_v1->next();
    HalfedgeIter h_v0_v2 = h_v1_v0->next();
    // Get all vertices
    VertexIter v1 = h_v1_v2->vertex();      // vertex at h_v1_v2's base
    VertexIter v2 = h_v2_v1->vertex();      // vertex at h_v2_v1's base (other end of edge)
    VertexIter v3 = h_v3_v1->vertex(); // opposite vertex in first triangle
    VertexIter v0 = h_v0_v2->vertex(); // opposite vertex in second triangle

    // Get the two faces
    FaceIter f_v0_v2_v3 = h_v1_v2->face(); //f_v1_v2_v3 -> f_v0_v2_v3
    FaceIter f_v0_v3_v1 = h_v2_v1->face(); //f_v2_v1_v0 -> f_v0_v3_v1

    HalfedgeIter &h_v3_v0 = h_v1_v2;
    HalfedgeIter &h_v0_v3 = h_v2_v1;

    // f_v0_v2_v3
    HalfedgeIter &h_v2_v0 = h_v0_v2->twin();
    HalfedgeIter &h_v3_v2 = h_v2_v3->twin();
    h_v3_v0->setNeighbors(h_v0_v2, h_v0_v3, v3, e0, f_v0_v2_v3);
    h_v0_v2->setNeighbors(h_v2_v3, h_v2_v0, v0, h_v0_v2->edge(), f_v0_v2_v3);
    h_v2_v3->setNeighbors(h_v3_v0, h_v3_v2, v2, h_v2_v3->edge(), f_v0_v2_v3);
    // f_v0_v3_v1
    HalfedgeIter &h_v1_v3 = h_v3_v1->twin();
    HalfedgeIter &h_v0_v1 = h_v1_v0->twin();
    h_v0_v3->setNeighbors(h_v3_v1, h_v3_v0, v0, e0, f_v0_v3_v1);
    h_v3_v1->setNeighbors(h_v1_v0, h_v1_v3, v3, h_v3_v1->edge(), f_v0_v3_v1);
    h_v1_v0->setNeighbors(h_v0_v3, h_v0_v1, v1, h_v1_v0->edge(), f_v0_v3_v1);

    // update vertex
    if (v1->halfedge() == h_v1_v2) v1->halfedge() = h_v1_v0;
    if (v2->halfedge() == h_v2_v1) v2->halfedge() = h_v2_v0;
    v3->halfedge() = h_v3_v1;
    v0->halfedge() = h_v0_v2;
    // Update face
    f_v0_v2_v3->halfedge() = h_v3_v0;
    f_v0_v3_v1->halfedge() = h_v0_v3;

    // Update edge
    e0->halfedge() = h_v3_v0;
    return e0;
}
```

## splite

```cpp
VertexIter HalfedgeMesh::splitEdge(EdgeIter e0) { 
    // Check if the edge is on boundary
    if (e0->isBoundary()) {
        return VertexIter();
    }
    // for example e0 is v1 to v2;
    // v1->v2->v3->v1
    // v2->v1->v0->v2
    // Get the two halfedges of this edge
    HalfedgeIter h_v1_v2 = e0->halfedge(); // v1->v2  TOBE DELETE
    HalfedgeIter h_v2_v1 = h_v1_v2->twin(); // v2->v1  TOBE DELETE
    // Get all halfedges around the two triangles
    HalfedgeIter h_v2_v3 = h_v1_v2->next();
    HalfedgeIter h_v3_v1 = h_v2_v3->next();
    HalfedgeIter h_v1_v0 = h_v2_v1->next();
    HalfedgeIter h_v0_v2 = h_v1_v0->next();

    // original faces
    FaceIter f_v1_v2_v3 = h_v1_v2->face(); // TOBE DELETE
    FaceIter f_v2_v1_v0 = h_v2_v1->face(); // TOBE DELETE
    deleteEdge(e0);
    deleteFace(f_v1_v2_v3);
    deleteFace(f_v2_v1_v0);
    deleteHalfedge(h_v1_v2);
    deleteHalfedge(h_v2_v1);

    // edge twins
    HalfedgeIter h_v3_v2 = h_v2_v3->twin();
    HalfedgeIter h_v1_v3 = h_v3_v1->twin();
    HalfedgeIter h_v0_v1 = h_v1_v0->twin();
    HalfedgeIter h_v2_v0 = h_v0_v2->twin();

    // edges around
    EdgeIter e_v10 = h_v1_v0->edge();
    EdgeIter e_v02 = h_v0_v2->edge();
    EdgeIter e_v23 = h_v2_v3->edge();
    EdgeIter e_v31 = h_v3_v1->edge();

    // Get all vertices
    VertexIter v0 = h_v0_v2->vertex();
    VertexIter v2 = h_v2_v3->vertex();
    VertexIter v3 = h_v3_v1->vertex();
    VertexIter v1 = h_v1_v0->vertex();

    // Create new elements
    VertexIter m = newVertex();  // new vertex at midpoint
    // Set position of new vertex to midpoint
    m->position = (v1->position + v2->position) * 0.5;
    m->isNew = true;
    // Create new edges
    EdgeIter e_m_v0 = newEdge();     // m to v0
    EdgeIter e_m_v1 = newEdge();     // m to v1
    EdgeIter e_m_v2 = newEdge();     // m to v2
    EdgeIter e_m_v3 = newEdge();     // m to v3
    e_m_v0->isNew = true;
    e_m_v1->isNew = false;
    e_m_v2->isNew = false;
    e_m_v3->isNew = true;
    // Create new faces
    FaceIter f_m_v1_v0 = newFace();     // new face
    FaceIter f_m_v0_v2 = newFace();     // new face
    FaceIter f_m_v2_v3 = newFace();     // new face
    FaceIter f_m_v3_v1 = newFace();     // new face

    // Create new halfedges
    HalfedgeIter h_m_v0 = newHalfedge(); // from m to v0
    HalfedgeIter h_m_v1 = newHalfedge(); // from m to v1
    HalfedgeIter h_m_v2 = newHalfedge(); // from m to v2
    HalfedgeIter h_m_v3 = newHalfedge(); // from m to v3
    HalfedgeIter h_v0_m = newHalfedge(); // from v0 to m
    HalfedgeIter h_v1_m = newHalfedge(); // from v1 to m
    HalfedgeIter h_v2_m = newHalfedge(); // from v2 to m
    HalfedgeIter h_v3_m = newHalfedge(); // from v3 to m


    // Reorganize the mesh into 4 distinct triangles

    // f_m_v1_v0
    h_m_v1->setNeighbors(h_v1_v0, h_v1_m, m, e_m_v1, f_m_v1_v0);
    h_v1_v0->setNeighbors(h_v0_m, h_v0_v1, v1, e_v10, f_m_v1_v0);
    h_v0_m->setNeighbors(h_m_v1, h_m_v0, v0, e_m_v0, f_m_v1_v0);
    // f_m_v0_v2
    h_m_v0->setNeighbors(h_v0_v2, h_v0_m, m, e_m_v0, f_m_v0_v2);
    h_v0_v2->setNeighbors(h_v2_m, h_v2_v0, v0, e_v02, f_m_v0_v2);
    h_v2_m->setNeighbors(h_m_v0, h_m_v2, v2, e_m_v2, f_m_v0_v2);
    // f_m_v2_v3
    h_m_v2->setNeighbors(h_v2_v3, h_v2_m, m, e_m_v2, f_m_v2_v3);
    h_v2_v3->setNeighbors(h_v3_m, h_v3_v2, v2, e_v23, f_m_v2_v3);
    h_v3_m->setNeighbors(h_m_v2, h_m_v3, v3, e_m_v3, f_m_v2_v3);
    // f_m_v3_v1
    h_m_v3->setNeighbors(h_v3_v1, h_v3_m, m, e_m_v3, f_m_v3_v1);
    h_v3_v1->setNeighbors(h_v1_m, h_v1_v3, v3, e_v31, f_m_v3_v1);
    h_v1_m->setNeighbors(h_m_v3, h_m_v1, v1, e_m_v1, f_m_v3_v1);


    // Update vertex halfedge pointers
    v1->halfedge() = h_v1_m;
    m->halfedge() = h_m_v3;
    v2->halfedge() = h_v2_m;
    v3->halfedge() = h_v3_m;
    v0->halfedge() = h_v0_m;

    // Update face halfedge pointers
    f_m_v1_v0->halfedge() = h_v1_v0;
    f_m_v0_v2->halfedge() = h_v0_v2;
    f_m_v2_v3->halfedge() = h_v2_v3;
    f_m_v3_v1->halfedge() = h_v3_v1;

    // Update edge halfedge pointers
    e_m_v0->halfedge() = h_m_v0;
    e_m_v1->halfedge() = h_m_v1;
    e_m_v2->halfedge() = h_m_v2;
    e_m_v3->halfedge() = h_m_v3;
    return m;
}
```

## upsample

```cpp
#define U_VALUE(n) ((n) == 3 ? 3.0 / 16 : 3.0 / 8 / (n))
#define STRAIGHT_FORWARD(h) ((h)->next()->twin()->next()->twin()->next())
#define TARGET_VERTEX(h) ((h)->twin()->vertex())

void MeshResampler::upsample(HalfedgeMesh &mesh) {
    for (auto v = mesh.verticesBegin(), iEnd = mesh.verticesEnd(); v != iEnd; ++v) {
        v->isNew = false;
    }
    vector<EdgeIter> tasks;
    for (auto e = mesh.edgesBegin(), iEnd = mesh.edgesEnd(); e != iEnd; ++e) {
        e->isNew = false;
        tasks.push_back(e);
    }
    for (const auto &e: tasks) {
        mesh.splitEdge(e)->isNew = true;
    }
    tasks.clear();
    for (auto e = mesh.edgesBegin(), iEnd = mesh.edgesEnd(); e != iEnd; ++e) {
        auto &v1 = e->halfedge()->vertex();
        auto &v2 = e->halfedge()->twin()->vertex();
        if (v1->isNew != v2->isNew && e->isNew)
            // 不同时new
            tasks.push_back(e);
    }
    for (const auto &e: tasks) {
        mesh.flipEdge(e);
    }
    for (auto v = mesh.verticesBegin(), iEnd = mesh.verticesEnd(); v != iEnd; ++v) {
        if (v->isNew) {
            HalfedgeIter &h_m_a = v->halfedge();
            while (h_m_a->twin()->vertex()->isNew) {
                h_m_a = h_m_a->twin()->next();
            }
            VertexIter &a = h_m_a->twin()->vertex();
            VertexIter &c = TARGET_VERTEX(STRAIGHT_FORWARD(h_m_a->next()));
            HalfedgeIter &h_m_b = STRAIGHT_FORWARD(h_m_a->twin());
            VertexIter &b = TARGET_VERTEX(h_m_b);
            VertexIter &d = TARGET_VERTEX(STRAIGHT_FORWARD(h_m_b->next()));
            v->newPosition = 3.0 / 8 * (a->position + b->position) + 
                1.0 / 8 * (c->position + d->position);
        } else {
            int n = 0;
            Vector3D neighbor_sum;
            auto it = v->halfedge()->twin();
            HalfedgeIter &itEnd = v->halfedge()->twin();
            do {
                n++;
                neighbor_sum += it->vertex()->position;
                it = it->next()->twin();
            } while (it != itEnd);
            double u = U_VALUE(n) * 2;
            v->newPosition = (1 - n * u) * v->position + u * neighbor_sum;
        }
    }
    for (auto v = mesh.verticesBegin(), iEnd = mesh.verticesEnd(); v != iEnd; ++v) {
        v->position = v->newPosition;
        v->isNew = false;
    }
}
```
# 辐射学

## 光线

### 相机发射光线

1. 将水平和垂直视场角（hFov, vFov）从度转换为弧度

2. 计算传感器平面的实际尺寸（宽和高）

   ```cpp
   double sensor_width = 2.0 * tan(hFov_rad / 2.0);
   double sensor_height = 2.0 * tan(vFov_rad / 2.0);
   ```

3. 将输入坐标转换为传感器平面上的坐标（原点在中心）

   ```cpp
   double sensor_x = (x - 0.5) * sensor_width;
   double sensor_y = (y - 0.5) * sensor_height;
   ```

4. 输入坐标的 z 设置成 -1

5. 相机空间中的光线方向是从原点指向传感器点的向量

6. 将方向向量从相机坐标系转换到世界坐标系

7. 设置光线的最近和最远裁剪距离（nClip, fClip）

### 追踪像素

1. 初始化总辐射值为零向量。

2. 循环生成指定数量的样本（ns_aa次）。

3. 每个样本中，使用网格采样器获取随机采样点。

   ```cpp
   Vector2D sample = gridSampler->get_sample();
   ```

4. 将像素坐标与采样点结合，转换为归一化的图像平面坐标。

   ```cpp
   double u = (x + sample.x) / sampleBuffer.w;
   double v = (y + sample.y) / sampleBuffer.h;
   ```

5. 根据归一化坐标生成相机光线。

   ```cpp
   Ray ray = camera->generate_ray(u, v);
   ```

6. 调用全局照明辐射`est_radiance_global_illumination`估计函数计算光线辐射值，并累加到总辐射值。

7. 循环结束后，计算总辐射值的平均值。

   ```cpp
   total_radiance += est_radiance_global_illumination(ray);
   ```

8. 将平均辐射值更新到像素缓冲区，并记录样本数量。

   ```cpp
   sampleBuffer.update_pixel(total_radiance / ns_aa, x, y);
   sampleCountBuffer[x + y * sampleBuffer.w] = ns_aa;
   ```

### 光线和三角形相交

1. 判断是否光线和三角形相交

2. 判断这个相交的三角形是否比这个光线已经相交的三角形更接近光线起点

   ```cpp
   if (t < r.min_t || t > r.max_t) return false;
   ```

3. 执行质心插值

   ```cpp
   isect->n = (a * n1 + b * n2 + (1-a-b) * n3).normalize();
   ```

4. 设置打中的目标是这个三角形

   ```cpp
   isect->primitive = this; // 设置打击目标
   isect->bsdf = get_bsdf(); // 设置材质
   ```

5. 重新设置光强起点

   ```cpp
   if (t < r.max_t) { // 设置
       const_cast<Ray&>(r).max_t = t;
   }
   ```

### 光线与球面相交



### 光线与包围盒相交

```cpp
bool BBox::intersect(const Ray &r, double &t0, double &t1) const {
    double t_min = t0;
    double t_max = t1;
    for (int i = 0; i < 3; i++) { // 三个轴向
        if(r.d[i] == 0) continue;
        double t_near = (min[i] - r.o[i]) / r.d[i];
        double t_far = (max[i] - r.o[i]) / r.d[i];
        if (t_near > t_far) std::swap(t_near, t_far);
        t_min = std::max(t_near, t_min);
        t_max = std::min(t_far, t_max);
        if (t_min > t_max) return false; // 包围盒的特殊性
    }
    t0 = t_min;
    t1 = t_max;
    return true;
}
```

## BVH

```cpp
struct BVHNode{
    BBox bbox;
    BVHNode* child1;
    BVHNode* child2; // nullable
    list<Primitive*> primList; // 列表. 对于叶子有效, 存储对象, 比如三角形
    bool leaf() const { return !child1 && !child2; }
};
```

### 构建

找到最优的分割轴

```cpp
int best_axis = -1;
int best_split = -1;
double best_cost = std::numeric_limits<double>::max();
for (int axis = 0; axis < 3; axis++) {
    std::sort(start, end, [axis](Primitive *a, Primitive *b) {
        return a->get_bbox().centroid()[axis] < b->get_bbox().centroid()[axis];
    });
    // 动态规划
    std::vector <BBox> right_bboxes(primitive_size);
    BBox right_bbox;
    for (int i = primitive_size - 1; i >= 0; i--) {
        right_bbox.expand((*(start + i))->get_bbox());
        right_bboxes[i] = right_bbox;
    }
    BBox left_bbox;
    for (int i = 0; i < primitive_size - 1; i++) {
        left_bbox.expand((*(start + i))->get_bbox());
        double left_area = left_bbox.surface_area();
        double right_area = right_bboxes[i + 1].surface_area();
        double cost = 0.125 +
            (left_area * (i + 1) + right_area * (primitive_size - i - 1)) / bbox.surface_area();
        if (cost < best_cost) {
            best_cost = cost; best_axis = axis; best_split = i;
        }
    }
}
```

递归地构建

```cpp
BVHNode *BVHAccel::construct_bvh(std::vector<Primitive *>::iterator start,
                                 std::vector<Primitive *>::iterator end,
                                 size_t max_leaf_size) {
    size_t primitive_size = end - start;
    BBox bbox;
    for (auto p = start; p != end; p++) {
        bbox.expand((*p)->get_bbox());
    }
    auto *node = new BVHNode(bbox);
    node->start = start, node->end = end;
    if (primitive_size <= max_leaf_size) { // 叶子
        node->l = nullptr;
        node->r = nullptr;
        return node;
    }
    int best_axis = -1;
    int best_split = -1;
    double best_cost = std::numeric_limits<double>::max();
    // 找出最好的best_axis, best_split
    //...
    // -----------------------------------
    if (best_axis == -1) {
        node->l = nullptr;
        node->r = nullptr;
        return node;
    }
    std::sort(start, end, [best_axis](Primitive *a, Primitive *b) {
        return a->get_bbox().centroid()[best_axis] < b->get_bbox().centroid()[best_axis];
    });
    auto mid = start + best_split + 1;
    node->l = construct_bvh(start, mid, max_leaf_size);
    node->r = construct_bvh(mid, end, max_leaf_size);
    return node;
}
```
### 击中

```cpp
struct Intersection {
    Primitive* prim;
    float t;
    static Intersection NOT_INTERSECT = { nullptr, FLT_INF };
    bool intersect() const { return !prim; }
    bool closer(const HitInfo& info) const {
        return !hit() || info.hit() && info.t < this->t;
    }
    HitInfo& operator =(const HitInfo& info){
        if(this != &info){
            this->prim = info.prim;
            this->t = info.t;
        }
        return this;
    }
}
Intersection find_closest_intersect(const Ray& ray, BVHNode* node){
    if (!node||!node->bbox.test(ray)) return Intersection::NOT_INTERSECT; // 测试无接触, 返回
    if (!node->leaf()){
        Intersection intersection1 = find_closest_intersect(ray, node->child1);
        Intersection intersection2 = find_closest_intersect(ray, node->child2);
        return intersection1.closer(intersection2) ? intersection1 : intersection2;
    }
    Intersection closest = Intersection::NOT_INTERSECT;
    for(Primitive* item: primList){
        Intersection intersection = item->test_intersect(ray);
        if(intersection.intersect() && intersection.closer(closet)){
            closest = intersection;
        }
    }
    return closest;
}
```



## 光追迭代算法

```cpp
// 计算从位置 position 在方向 omega 传入的辐射
float ComputeRadianceIn(const Vector3& position, const Vector3& omega) {
    Intersection intersection = intersect_scene(position, omega);  // 计算交点
    // 返回零次反射辐射与至少一次反射辐射之和
    return zero_bounce_radiance(intersection, -omega) + 
        at_least_one_bounce_radiance(intersection, -omega);
}

// 计算零次反射辐射（直接由表面自身发射）
float zero_bounce_radiance(const Intersection& intersection, const Vector3& outgoing_direction) {
    // 返回交点处表面向外发射的光
    return intersection.bsdf->get_emission(outgoing_direction);
}


// 计算一次反射辐射（直接光照）
float one_bounce_radiance(const Intersection& intersection, const Vector3& outgoing_direction) {
    if (direct_hemisphere_sample) {// 均匀半球采样
        return estimate_direct_lighting_hemisphere(r, isect);
    } else { // 重要性采样
        return estimate_direct_lighting_importance(r, isect);
    }
}

// 计算直接光照采样
float DirectLightingSampleLights(const Intersection& intersection, 
                                 const Vector3& outgoing_direction) {
    Vector3 omega_i;
    float pdf;
    
    // 重要性采样，从光源获取入射方向 omega_i 及其概率密度 pdf
    LightSample lightSample = lights.sampleDirection(intersection);
    
    // 判断是否被遮挡（阴影检测）
    if (scene.shadowIntersection(intersection, omega_i)) {
        return 0.0f;  // 若被遮挡则无光照贡献
    }
    
    // 若无遮挡，计算光源贡献
    return lightSample.intensity * intersection.brdf(omega_i, outgoing_direction)
           * cosTheta(omega_i, intersection.normal) / pdf;
}

```

### 均匀半球采样

```cpp
Vector3D PathTracer::estimate_direct_lighting_hemisphere(
	const Ray &r, const Intersection &isect) {
    // Estimate the lighting from this intersection coming directly from a light.
    // For this function, sample uniformly in a hemisphere.

    // Note: When comparing Cornel Box (CBxxx.dae) results to importance sampling, you may find the "glow" around the light source is gone.
    // This is totally fine: the area lights in importance sampling has directionality, however in hemisphere sampling we don't model this behaviour.

    // make a coordinate system for a hit point
    // with N aligned with the Z direction.

    Matrix3x3 o2w;
    make_coord_space(o2w, isect.n);
    Matrix3x3 w2o = o2w.T();
    // w_out points towards the source of the ray 
    // e.g., toward the camera if this is a primary ray
    const Vector3D hit_p = r.o + r.d * isect.t;
    const Vector3D w_out = w2o * (-r.d);
    Vector3D radiance;
    // This is the same number of total samples as
    // estimate_direct_lighting_importance (outside of delta lights). We keep the
    // same number of samples for clarity of comparison.

    // Write your sampling loop here
    // return direct lighting instead of normal shading
    int num_samples = scene->lights.size() * ns_area_light;

    // Uniform hemisphere sampling
    for (int i = 0; i < num_samples; i++) {
        Vector3D wi_local = hemisphereSampler->get_sample();
        double pdf = 1.0 / (2.0 * PI);  // PDF for hemisphere sampling
        Vector3D wi_world = o2w * wi_local;
        // Ray from the intersection point in the sampled direction
        Ray light_ray(hit_p, wi_world);
        light_ray.min_t = EPS_F;
        Intersection light_isect;
        if (bvh->intersect(light_ray, &light_isect)) {
            Vector3D emission = light_isect.bsdf->get_emission();
            if (emission.norm() > 0) {
                Vector3D f = isect.bsdf->f(w_out, wi_local);
                double cos_theta = wi_local.z;
                radiance += emission * f * cos_theta / pdf;
            }
        }
    }

    return radiance / num_samples;
}
```


### 重要性采样

```cpp
Vector3D PathTracer::estimate_direct_lighting_importance(
	const Ray &r, const Intersection &isect) {
    // Estimate the lighting from this intersection coming directly from a light.
    // To implement importance sampling, sample only from lights, not uniformly in
    // a hemisphere.

    // make a coordinate system for a hit point
    // with N aligned with the Z direction.
    Matrix3x3 o2w;
    make_coord_space(o2w, isect.n);
    Matrix3x3 w2o = o2w.T();

    // w_out points towards the source of the ray (e.g.,
    // toward the camera if this is a primary ray)
    const Vector3D hit_p = r.o + r.d * isect.t;
    const Vector3D w_out = w2o * (-r.d);
    Vector3D radiance;
    // 遍历所有光源
    for (auto light: scene->lights) {
        int num_samples = light->is_delta_light() ? 1 : ns_area_light;
        Vector3D radiance_light(0, 0, 0);

        // 对每个光源进行采样
        for (int i = 0; i < num_samples; i++) {
            Vector3D wi_world;
            double distToLight;
            double pdf;

            // 从光源采样
            Vector3D radianceL_in = light->sample_L(hit_p, &wi_world, &distToLight, &pdf);

            // 将方向转换到局部坐标系
            Vector3D wi_local = w2o * wi_world;

            // 检查方向是否在法线半球
            if (wi_local.z <= 0) {
                continue;
            }

            // 创建从交点指向光源的射线
            Ray shadow_ray(hit_p, wi_world);
            shadow_ray.min_t = EPS_F;
            shadow_ray.max_t = distToLight - EPS_F; // 避免在光源处相交

            Intersection shadow_isect;
            // 检查是否有遮挡物
            if (!bvh->intersect(shadow_ray, &shadow_isect)) {
                // 没有遮挡，计算贡献
                Vector3D f = isect.bsdf->f(w_out, wi_local);
                double cos_theta = wi_local.z;

                radiance_light +=radianceL_in * f * cos_theta / pdf;
            }
        }

        // 平均当前光源的样本
        if (num_samples > 0) {
            radiance += radiance_light / num_samples;
        }
    }

    return radiance;
}
```
### 至少一次采样

伪代码

```cpp
// 计算至少一次反射的辐射（包括间接光照）
float at_least_one_bounce_radiance(
    const Intersection& intersection, const Vector3& outgoing_direction, int bounceCount = 0) {
    if (bounceCount > MAX_BOUNCE_COUNT) return 0.0f;  // 限制最大反射次数，防止无限递归
    
    // 计算直接光照（一次反射）
    float radiance = one_bounce_radiance(intersection, outgoing_direction);
    
    // 重要性采样，获取反射方向 omega_i 及其概率密度 pdf
    Vector3 omega_i;
    float pdf;
    std::tie(omega_i, pdf) = intersection.brdf.sampleDirection(outgoing_direction);
    
    // 计算沿 omega_i 方向的下一个交点
    Intersection new_isect = intersect_scene(intersection, omega_i);
    
    // 计算俄罗斯轮盘法的连续概率
    float continuationPdf = continuationProbability(
        intersection.brdf, omega_i, outgoing_direction
    );
    
    // 俄罗斯轮盘法递归估计间接光
    if (random01() < continuationPdf) {
        radiance += at_least_one_bounce_radiance(new_isect, -omega_i, bounceCount + 1)
                    * intersection.brdf(omega_i, outgoing_direction)
                    * cosTheta(omega_i, intersection.normal) / pdf / continuationPdf;
    }
    
    return radiance;
}
```

更详细一点

```cpp
Vector3D PathTracer::at_least_one_bounce_radiance(
    const Intersection &isect, const Ray &r) {
    Vector3D radiance = one_bounce_radiance(r, isect);
    // 检查是否达到最大递归深度
    if (r.depth >= max_ray_depth) return radiance;
    // 使用Russian Roulette决定是否继续追踪
    // 建议使用0.3-0.4的终止概率，这里使用0.35的终止概率
    if (coin_flip(0.35)) return radiance; // 终止追踪
    // 根据BSDF采样入射方向
    Matrix3x3 o2w;
    make_coord_space(o2w, isect.n);
    Matrix3x3 w2o = o2w.T();
    // 获取pdf
    Vector3D w_out = w2o * (-r.d);
    Vector3D wi;
    double pdf;
    Vector3D f = isect.bsdf->sample_f(w_out, &wi, &pdf);
    // 检查pdf是否有效
    if (pdf <= 0.0) return radiance;
    Ray new_ray = init_new_ray(insect, r, o2w, wi, pdf);
    Intersection new_isect;
    // 检查新射线是否与场景相交
    if (!bvh->intersect(new_ray, &new_isect)) return radiance;
    // 递归获取辐照度
    Vector3D radiance_indirect = at_least_one_bounce_radiance(new_ray, new_isect);
    // 根据渲染方程计算贡献，并除以继续概率来保持无偏估计
    float cos_theta = wi.z;
    Vector3D contribution = f * radiance_indirect * cos_theta / (pdf * continue_probability);
    // 根据isAccumBounces设置决定如何累加
    if (isAccumBounces) {
        // 累积所有反射
        radiance += contribution;
    } else {
        // 只返回最深层次的反射
        // 注意：这里需要确保至少有一次反弹
        if (r.depth + 1 >= max_ray_depth) {
            radiance = contribution;
        }
    }

    return radiance;
}
```
```cpp
Ray init_new_ray(const Intersection &isect, const Ray &r,
                 Matrix3x3 o2w, Vector3D wi){
    // 创建新的射线从交点出发
    Vector3D hit_p = r.o + r.d * isect.t;
    Vector3D wi_world = o2w * wi; // 将局部坐标系的wi转换到世界坐标系
    Ray new_ray(hit_p, wi_world);
    new_ray.min_t = EPS_F;
    new_ray.depth = r.depth + 1;  // 增加递归深度
    return new_ray;
}
```


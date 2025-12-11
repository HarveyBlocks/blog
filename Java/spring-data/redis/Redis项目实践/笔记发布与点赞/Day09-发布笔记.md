# 发布笔记

## Blog表

![image-20240127193921756](../../../assets/Day09-%E5%8F%91%E5%B8%83%E7%AC%94%E8%AE%B0/image-20240127193921756.png)

## 前端交互

1.  图片是单独传一次的`UploadController`,已完成

    ![image-20240127194241015](../../../assets/Day09-%E5%8F%91%E5%B8%83%E7%AC%94%E8%AE%B0/image-20240127194241015.png)

    ![image-20240127194329582](../../../assets/Day09-%E5%8F%91%E5%B8%83%E7%AC%94%E8%AE%B0/image-20240127194329582.png)
    
2.  发布笔记

    `BlogController`

3.  查看笔记

    ![image-20240128114423571](../../../assets/Day09-%E5%8F%91%E5%B8%83%E7%AC%94%E8%AE%B0/image-20240128114423571.png)

    `BlogController`

    ```java
    @GetMapping("/{id}")
    public Result viewBlog(@PathVariable("id") Long id) {
        return Result.ok(blogService.viewBlog(id));
    }
    ```

    `BlogServiceImpl`

    ```java
    @Override
    public Blog viewBlog(Long id) {
        // 查看blog
        Blog blog = this.getById(id);
    	// 一篇博客需要作者信息
        User user = userService.getById(blog.getUserId());
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
        return blog;
    }
    ```


# 发布笔记

## Blog表

![image-20240127193921756](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/笔记发布与点赞/Day09-发布笔记/image-20240127193921756.png)

## 前端交互

1.  图片是单独传一次的`UploadController`,已完成

    ![image-20240127194241015](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/笔记发布与点赞/Day09-发布笔记/image-20240127194241015.png)

    ![image-20240127194329582](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/笔记发布与点赞/Day09-发布笔记/image-20240127194329582.png)

2.  发布笔记

    `BlogController`

3.  查看笔记

    ![image-20240128114423571](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/笔记发布与点赞/Day09-发布笔记/image-20240128114423571.png)

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


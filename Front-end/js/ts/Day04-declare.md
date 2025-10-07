# 声明文件

用于制作声明文件

声明文件就是代类型提示的, 统统不需要编译, 也不需要实质性的实现

## 全局库

不需要使用任何的import即可使用

常常

声明

-   顶级的变量或function声明
-   赋值到window的成员的语句
-   不会require和define(因为这些使用在Node.js/CommonJS中)

使用命名空间防止命名冲突

```ts
declare namespace cats {
    interface KittySettings { }
}
```

## 模块化库

需要模块加载器

-   调用`require`或`define`
-   像`import * as a from 'b';` or `export c;`这样声明
-   赋值给`exports`或`module.exports`

常常用于Node.js库

## UMD

>   Universal Module Definition
>
>   模块定义规范

既可以作为模块使用（通过导入）又可以作为全局（在没有模块加载器的环境里）使用的模块


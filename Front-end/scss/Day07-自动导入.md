# 自动导入

项目中一些组件共享的色值会以scss变量的方式统一放到一个名为`var.scss`的文件中

正常组件中使用, 需要先导入scss文件, 再使用内部变量, 比较繁琐

自动导入免去手动导入的步骤, 直接使用内部变量

`var.scss`

```scss
$color-primary: #27ba9b;
$color-success: #1dc779;
$color-warning: #ffb302;
$color-danger: #e26237;
$color-error: #cf4444;
$color-info: #909399;
```

一般的调用

```scss
<style scoped lang="scss">
@use "@/styles/var";
#children-route {
  border: 2px var.$color-primary solid;
  width: 600px;
  height: 400px;
}
</style>
```

## 配置

再`vite.config.js`中配置自动导入文件

```js
export default defineConfig({
  /*其他配置*/
  // 添加css 文件目录
  css: {
    preprocessorOptions: {
      scss: {
        // 指定自动导入的文件
        additionalData: `
            @use "@/styles/var.scss" as *;
        `,/*取别名, 可以用, vite可以正常运行渲染, 但是IDE会告警, 而*不会告警*/
      },
    },
  },
})
```

由于IDE的限制, 此处仅记录这项技术, 而不会使用


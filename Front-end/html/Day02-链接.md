# 链接

## 到本网站

```html
<a href="/index2.html">链接到第二个页面</a>
```

-   开头`/`表示这段地址是直接加载`host:port`之后的, `host:port/resource.html`

-   如果缺省开头`/`, 那么就是替换掉当前资源路径

    `host:port/path/resource.html`->`host:port/path/target.html`

## 到万维网

```html
<a href="https://www.baidu.com">链接到其他网站</a>
```

## 属性

### `target`

>   打开方式

-   `_blank`

    在新窗口或新标签页中打开链接。

-   `self`

    在当前窗口或标签页中打开链接（默认）。

-   `_parent`

    在父框架中打开链接。

-   `top`

    在整个窗口中打开链接，取消任何框架。

### `rel`

>   定义链接与目标页面的关系。

-   `noopener`

    防止新的浏览上下文（页面）访问`window.opener`属性和`open`方法。

-   `noreferrer`

    不发送referer header（即不告诉目标网站你从哪里来的）

-   `noopener noreferrer`

    两者效果叠加

-   `nofollow`

    搜索引擎不应跟踪该链接，常用于外部链接。

### `download`

```html
<a href="网络上的资源文件路径" download="下载后的文件名">下载文件</a>
```

### `title`

鼠标悬停有提示

```html
<a href="https://www.baidu.com" title="到百度哦">链接到其他网站</a>
```

<a href="https://www.baidu.com" title="到百度哦">链接到其他网站</a>

## 链接到本页面

```html
<div id="label1">顶部</div>
```

```html
<a href="#label1">返回到顶部</a>
```


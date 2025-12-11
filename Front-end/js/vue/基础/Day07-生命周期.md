# 生命周期

-   Create
    -   设置组件的数据、计算属性、方法和事件侦听器
-   Mount 挂载
    -   将组件添加到 DOM
-   Update
    -   组件的数据发生更改
-   Unmount
    -   组件从 DOM 中删除
-   ErrorCapture
    -   子/后代组件中发生错误
-   RenderTracked
    -   
-   Re

-   BeforeCreate
    -   可用于设置全局事件监听器
-   Created
    -   可用于通过 HTTP 请求获取数据，或设置初始数据值
-   BeforeMount
    -   
-   Mounted
    -   有机会执行与属于该组件的 DOM 元素相关的操作，例如使用 `ref` 属性和 `$refs` 对象
-   BeforeUpdate
    -   **可以**对应用程序进行更改，而不会触发新的更新
-   Updated
    -   **不可以**对应用程序进行更改，会触发新的更新
-   BeforeUnmount
-   Unmounted
    -   可用于删除事件侦听器或取消计时器或间隔
-   ErrorCaptured
    -   子/后代组件中发生错误时
    -   可用于错误处理、记录或向用户显示错误
    -   参数列表: 
        1.  错误
        2.  触发错误的组件
        3.  错误源类型
-   RenderTracked
    -   渲染函数**设置为**跟踪或监视反应式组件时
    -   旨在用于调试，并且仅在开发模式下可用
-   RenderTriggered
    -   跟踪的反应式组件发生变化时
    -   旨在用于调试，并且仅在开发模式下可用
-   Activated
    -   添加或删除缓存的动态组件
    -   keep-alive有关
-   Deactivated
    -   添加或删除缓存的动态组件
    -   keep-alive有关
-   ServerPrefetch
    -   服务器端渲染 (SSR) 期间


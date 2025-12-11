# ElementPlus

Element-UI的Vue3适配

[Element Plus](https://element-plus.org/zh-CN/guide/quickstart.html)

## 安装

```shell
 npm install element-plus --save
```

## 自动按需引入

不用写import了, 非常神奇

安装插件

```shell
 npm install -D unplugin-vue-components unplugin-auto-import
```

自动按需引入

vite.config.js

```js
import { defineConfig } from 'vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig({
  // ...
  plugins: [
    // ...
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
})
```

## 主题定制

[主题](https://element-plus.org/zh-CN/guide/theming.html)

安装sass

```shell
npm i sass -D
```

创建文件`src/styles/element/index.scss`

```scss
@forward 'element-plus/theme-chalk/src/common/var.scss' with (
  $colors: (
    'primary': (
      'base': #27ba9b,
    ),
    'success': (
      'base': #1dc779,
    ),
    'warning': (
      'base': #ffb302,
    ),
    'danger': (
      'base': #e26237,
    ),
    'error': (
      'base': #cf4444,
    ),
    'info': (
      'base': #909399,
    ),
  )
);
```

自动导入配置

配置element plus 采用sass样式配色系统, 自动导入定制化样式文件进行样式覆盖 

`vite.config.js`

```js
export default defineConfig({
  plugins: [
    /*...*/
    Components({
      resolvers: [ElementPlusResolver({
        // 1. 使用自动导入的, 提示样式采用sass
        importStyle: "sass"
      })],
    }),
  ],
  resolve: {/*...*/},
  // 添加css 文件目录
  css: {
    preprocessorOptions: {
      scss: {
        // 指定样式覆盖的文件目录
        additionalData: `@use "@/styles/element/index.scss" as *;`,
      },
    },
  },
})

```

## 多选框-不确定状态

exclude非常方便

![image-20250827064251206](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/vue/通用型组件/ElementPlus/image-20250827064251206.png)

```vue
<template>
  <el-checkbox
    v-model="checkAll"
    :indeterminate="isIndeterminate"
    @change="handleCheckAllChange"
  >
    Check all
  </el-checkbox>
  <el-checkbox-group
    v-model="checkedCities"
    @change="handleCheckedCitiesChange"
  >
    <el-checkbox v-for="city in cities" :key="city" :label="city" :value="city">
      {{ city }}
    </el-checkbox>
  </el-checkbox-group>
</template>

<script lang="ts" setup>
import { ref } from 'vue'

import type { CheckboxValueType } from 'element-plus'

const checkAll = ref(false)
const isIndeterminate = ref(true)
const checkedCities = ref(['Shanghai', 'Beijing'])
const cities = ['Shanghai', 'Beijing', 'Guangzhou', 'Shenzhen']

const handleCheckAllChange = (val: CheckboxValueType) => {
  checkedCities.value = val ? cities : []
  isIndeterminate.value = false
}
const handleCheckedCitiesChange = (value: CheckboxValueType[]) => {
  const checkedCount = value.length
  checkAll.value = checkedCount === cities.length
  isIndeterminate.value = checkedCount > 0 && checkedCount < cities.length
}
</script>

```


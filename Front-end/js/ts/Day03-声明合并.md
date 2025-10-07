# 声明合并

同名的声明被合并

## 接口

成员应该是唯一的, 如果不唯一, 类型应该一致, 不一致会产生错误

当接口 `A`与后来的接口 `A`合并时，后面的接口具有更高的优先级。

```ts
interface Cloner {
    clone(animal: Animal): Animal;
}

interface Cloner {
    clone(animal: Sheep): Sheep;
    clone(animal: "panda"): Panda;
}

interface Cloner {
    clone(animal: Dog): Dog;
    clone(animal: Cat): Cat;
}
```

每组接口里的声明顺序保持不变，但各组接口之间的顺序是后来的接口重载出现在靠前位置

如果签名里有一个参数的类型是 *单一*的字面量类型(不能是联合类型)，那么它将会被提升到重载列表的最顶端。

合并后等价于

```ts
interface Cloner {
    clone(animal: "panda"): Panda;
    clone(animal: Dog): Dog;
    clone(animal: Cat): Cat;
    clone(animal: Sheep): Sheep;
    clone(animal: Animal): Animal;
}
```

## 命名空间的合并

### 命名空间合并自身

[命名空间](Day03-命名空间#同名命名空间)

### 命名空间和类

用于扩展类的内容

```ts
class Album {
    constructor(public label:Album.AlbumLabel) {
    }
}
namespace Album {
    export class AlbumLabel { }
}
```

编译时不会再使用`var Album`

```ts
class Album {
    constructor(label) {
        this.label = label;
    }
}
(function (Album) {
    class AlbumLabel {
    }
    Album.AlbumLabel = AlbumLabel;
})(Album || (Album = {})); // 此时已经有Album构造器, Album不再是undefined
```



### 命名空间和函数

用于扩展函数内容, 与类同理

### 命名空间和枚举合并

用于扩展枚举内容, 与类同理

也就是说, 可以增加枚举方法

```ts
enum Color {
    red = 1,
    green = 2,
    blue = 4
}

namespace Color {
    export function mixColor(colorName: string): number {
        switch (colorName) {
            case "yellow":
                return Color.red + Color.green;
            case "white":
                return Color.red + Color.green + Color.blue;
            case "magenta":
                return Color.red + Color.blue;
            case "cyan":
                return Color.green + Color.blue;
            default:
                throw new Error("unknown colorName: " + colorName);
        }
    }
}
```
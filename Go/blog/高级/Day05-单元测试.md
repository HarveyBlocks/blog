# 单元测试

## 测试用例

准备一个测试文件`文件名_test.go`

```go
package main

import (
    "testing"
)

func TestFormatedNow(t *testing.T) {

    if res := Add(1,1); res != "2" {
       t.Error("不好哟")
    }
}
```

## 启动测试命令

```shell
go test [-v]
```

运行package下所有的测试用例

-    -v 详细信息


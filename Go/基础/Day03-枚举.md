# 枚举

用常量+类型定义实现枚举

```go
type Color int

const (
    RED Color = iota
    ORANGE
    YELLOW
    GREEN
    BLUE
    PURPLE
)

// String 打印时自动调用String方法
func (c Color) String() string {
    switch c {
    case RED:
       return "RED"
    case ORANGE:
       return "ORANGE"
    case YELLOW:
       return "YELLOW"
    case GREEN:
       return "GREEN"
    case BLUE:
       return "BLUE"
    case PURPLE:
       return "PURPLE"
    default:
       return "UNKNOWN"
    }
}

func main() {
    fmt.Println(RED)                          // RED
    fmt.Printf("%d-%s\n", RED, RED)           // 0-RED
    fmt.Printf("%d-%s\n", ORANGE, ORANGE)     // 1-ORANGE
    fmt.Printf("%d-%s\n", YELLOW, YELLOW)     // 2-YELLOW
    fmt.Printf("%d-%s\n", GREEN, GREEN)       // 3-GREEN
    fmt.Printf("%d-%s\n", BLUE, BLUE)         // 4-BLUE
    fmt.Printf("%d-%s\n", PURPLE, PURPLE)     // 5-PURPLE
    fmt.Printf("%d-%s\n", PURPLE-1, PURPLE-1) // 4-BLUE
}
```


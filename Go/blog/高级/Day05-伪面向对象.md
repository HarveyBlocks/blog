# 伪OOP

```go
type Student struct {
    name string
    age  int
}

func (stu *Student) study(course string) string {
    return fmt.Sprintf("%s, who is %d years old, is studing %s.", stu.name, stu.age, course)
}

func main() {
    student := Student{name: "Mike", age: 12}
    fmt.Println(student.study("math"))
}
```

## 面向对象和高并发之间的矛盾

>   面向对象要封装, 并行要开放

-   过度封装在高并发下引发数据不一致
-   多态需要维护虚函数表, 造成了性能的损失
-   每个成员函数可能涉及少量的成员变量, 而运行时却会将所有的成员变量加载到缓存(面向数据编程DOP)

## 结构体

### 声明

```go
type 结构体名 struct{
    字段一 字段一类型
    字段二 字段二类型
    ...
}
```

```go
type Student struct {
    name string
    age  int
}
```

不能设置初始值

### 实例化

```go
对象指针 := 对象类型{字段名一: 字段值一, 字段名二: 字段值二,...}
```



```go
var student Student = Student{name: "Mike", age: 12}
var pStudent *Student = &Student{name: "Tom"} // // 使用age的int默认值0

```

### new

```go
对象指针 := new(对象类型)
```



```go
var newStudent *Student = new(Student)
```



## 方法

### 声明

```go
func (对象指针名 *对象类型) 方法名(参数 参数类型, ...)(返回值 返回值类型,...){
    // 拷贝对象的指针
    // 方法体
}
```

or

```go
func (对象名 对象类型) 方法名(参数 参数类型, ...)(返回值 返回值类型,...){
   	// 拷贝一个新对象
    // 方法体
}
```



```go
func (stu *Student) study(course string) string {
    return fmt.Sprintf("%s, who is %d years old, is studing %s.", stu.name, stu.age, course)
}
```

### 调用

```go
返回值 := 对象.方法(实参)
```

```go
fmt.Println(student.study("math"))
```





```go
func (stu *Student) study(course string) string {
    stu.score++
    return fmt.Sprintln(stu)
}
func (stu Student) play(game string) string {
    stu.score--
    return fmt.Sprintln(stu)
}
```

```go
var student Student = Student{name: "Mike", age: 12, score: 10}
var pStudent *Student = &Student{name: "Tom", age: 15, score: 10}
fmt.Println(student)
fmt.Println(student.study("math"))
fmt.Println(student) // 对象发生改变
fmt.Println(pStudent.play("Portals"))
fmt.Println(student) // 对象不发生改变

fmt.Println(pStudent)
fmt.Println(pStudent.study("science"))
fmt.Println(pStudent) // 对象发生改变
fmt.Println(pStudent.play("Noita"))
fmt.Println(pStudent) // 对象不发生改变
```

## 接口

一个结构体的所有实现了一个接口的所有抽象方法, 那么认为这个结构体实现了这个接口, 否则认为没有实现

```go
type 接口名 interface {  
	抽象方法签名
}
```

```go
type Person interface {
    getName() string
    getAge() int
}
```



### 实现

```go
type Person interface {
    getName() string
    getAge() int
}

type Student struct {
    name string
    age  int
}

func (stu *Student) getName() string {
    return stu.name
}
func (stu *Student) getAge() int {
    return stu.age
}
```



### 使用

```go
type Person interface {
    getName() string
    getAge() int
}

type Student struct {
    name string
    age  int
}

func (stu *Student) getName() string {
    return stu.name
}
func (stu *Student) getAge() int {
    return stu.age
}

type Teacher struct {
    name string
    age  int
}

func (tea *Teacher) getName() string {
    return tea.name
}
func (tea *Teacher) getAge() int {
    return tea.age
}
func msg(person Person) string {
    return fmt.Sprintf("%s %d", person.getName(), person.getAge())
}
func main() {
    student := &Student{name: "Mike", age: 10}
	var person Person = student // student是指针
	fmt.Println(msg(person))
    teacher := &Teacher{name: "Tom", age: 30}
    fmt.Println(msg(teacher))
}
```

### 空接口

如果定义了一个没有任何方法的空接口，那么这个接口可以表示任意类型。例如

# 类

## 成员方法和属性

所有类默认继承`object`



## 魔术方法

类内置方法

就是`object`里的方法, 去重载它

## 封装

私有化

函数名/属性名前+ `__`

如`__name`

或`__get_name()`



```python
class Student:
    __class_id: int = 2024

    def __init__(self, name: str, age: int, grade: float):
        self.__name: str = name 
            # 都是非静态成员, 存放在self中,如果没有`__`就能访问到
            # 如果没有使用`__`,外界使用`Student.属性`的方法访问的是一个新的静态成员
        self.__age: int = age
        self.__grade: float = grade

    def __str__(self):
        return f'Student(name={self.__name}, age={self.__age}, grade={self.__grade},class={Student.__class_id})'

    def __eq__(self, __value):
        return (self.__age == __value.__age
                and self.__grade == __value.__grade
                and self.__name == __value.__name)

    # Getter,  @property能让Getter不用加括号, 看起来更像属性,其实是方法
    @property
    def grade(self) -> float:
        return self.__grade

    @property
    def name(self) -> str:
        return self.__name

    @property
    def age(self) -> int:
        return self.__age

    # Setter
    def set_grade(self, grade: float):
        self.__grade = grade
        return self

    def set_name(self, name: str):
        self.__name = name
        return self

    def set_age(self, age: int):
        self.__age = age
        return self


    @staticmethod
    def class_id() -> int:
        """
        静态方法
        :return: 课程id
        """
        return Student.__class_id

    def study(self)->None:
        print(f"{self.__name}会学习")
```

### 更好的Getter和Setter

像使用属性一样使用Getter,Settter方法

创建



```python
# Getter , 注解`@property`
@property
def name(self) -> str:
    return self.__name

@property
def age(self) -> int:
    return self.__age

# Setter, 注解`@属性名.setter`, 
# 方法不能有返回值
@name.setter
def name(self, name: str):
    self.__name = name

@age.setter
def age(self, age: int): 
    self.__age = age
```







使用

```python
s1: Student = Student(name="张三", age=19, grade=76.24)

s1.name = "lisi"  # 看起来是个属性, 其实是方法
print(s1.name)  # lisi
```



类属性方式

```python
# Getter
# @property
def get_grade(self) -> float:
    return self.__grade

# Setter
# @grade.setter
def set_grade(self, grade: float):
    self.__grade = grade
    return
grade = property(get_grade, set_grade)
```

不好用, 别用

## 继承

1.  子类继承父类

    ```python
    class Student(Person):
    ```

2.  父类的构造方法

    ```python
    class Person:
        def __init__(self, name: str, age: int):
            self.__name: str = name
            self.__age: int = age
    ```

3.  子类调用父类的静态方法

    ```python
    class Student(Person):
    
        def __init__(self, name: str, age: int, grade: float):
            super().__init__(name, age)
            self.__name: str = name
            self.__age: int = age
            self.__grade: float = grade
    ```

4.  父类有Getter/Setter

    ```python
    class Person:
        def __init__(self, name: str, age: int):
            self.__name: str = name
            self.__age: int = age
    
        def __str__(self):
            return f'Person(name={self.__name}, age={self.__age})'
    
        def __eq__(self, __value):
            return (self.__age == __value.__age
                    and self.__name == __value.__name)
    
        # Getter
    
        @property
        def name(self) -> str:
            return self.__name
    
        @property
        def age(self) -> int:
            return self.__age
    
        # Setter
        def set_name(self, name: str):
            self.__name = name
            return self
    
        def set_age(self, age: int):
            self.__age = age
            return self
    
        def say(self)->None:
            print(f"{self.__name}会学习")
    ```

    子类可以继承父类的Getter/Setter(子类不用写)

    ```python
    from pojo.Person import Person
    
    
    class Student(Person):
    
        def __init__(self, name: str, age: int, grade: float):
            super().__init__(name, age)
            self.__name: str = name
            self.__age: int = age
            self.__grade: float = grade
    
        # Getter
        @property
        def grade(self) -> float:
            return self.__grade
    
        # Setter
        def set_grade(self, grade: float):
            self.__grade = grade
            return self
    ```

    子类依旧可以调用父类的静态方法

    ```python
    s1: Student = Student(name="张三", age=19, grade=76.24)
    
    # s1.name = "lisi" 看起来是个属性, 其实是方法AttributeError: can't set attribute
    
    print(s1.name)  # 张三
    print(s1.age)  # 19
    ```
    
5.  **Python支持多继承**

   ```python
    class Student(Person,Human):
   ```
   
   如果两个父类有方法或属性重叠了怎么办?
   
   其实`(Person,Human)`是个元组, 有顺序的
   
   优先调用前面一个父类的成员
   
   

## 多态

-   父类方法

    ```python
    class Person:
        def __init__(self, name: str, age: int):
            self.__name: str = name
            self.__age: int = age
    
        def __str__(self):
            return f'Person(name={self.__name}, age={self.__age})'
    
        def __eq__(self, __value):
            return (self.__age == __value.__age
                    and self.__name == __value.__name)
    
        def say(self) -> None:
            print(f"{self.__name}会说话")
    ```

-   子类重写

    ```python
    def __str__(self):
        # 由于子类没有了自己的name和age属性, 这里调用的是父类的Getter方法
        return f'Student(name={self.name}, age={self.age}, grade={self.__grade},class={Student.__class_id})'
    
    def __eq__(self, __value):
        return (self.age == __value.age
                and self.__grade == __value.__grade
                and self.name == __value.name)
    
    def say(self) -> None:
        print(f"{self.name}是人, 也会说话")
        
    # 这是子类独有的方法
    def study(self) -> None:
        print(f"{self.name}会学习")
    ```

-   多态

    父类方法调用子类重载

    ```python
    s1: Student = Student(name="张三", age=19, grade=76.24)
    s1.say()  # 张三是人, 也是学生, 也会说话 
    s1.study()  # 张三会学习
    
    s2: Person = Student(name="李四", age=19, grade=76.24)
    s2.say()  # 李四是人, 也是学生, 也会说话
    # s2.study() 类 'Person' 的未解析的特性引用 'study'
    
    p: Person = Person(name="王五", age=19)
    p.say() # 王五会说话
    ```

## 静态与非静态

```python
__class_id: int = 2024  
    # 静态属性, 显示地写在类里的可能是静态属性,也可能是非静态属性
    # 写在类里方便给静态属性赋初值

# 没有self做参数
@staticmethod
def class_id() -> int:
    """
    静态方法
    :return: 课程id
    """
    return Student.__class_id

# 非静态方法,有self做参数
def study(self) -> None:
    print(f"{self.name}会学习")
    return
```



静态成员和非静态成员的**区别在于**: **一个是用`self`或实例调用, 一个是用`类名`调用**

静态成员和非静态成员名可以相同

直接调用是调用不到的

## 私有化构造器

-   实现单例模式

```python
class OnlyCreatable(object):

    __create_key = object()

    @classmethod
    def create(cls, value):
        return OnlyCreatable(cls.__create_key, value)

    def __init__(self, create_key, value):
        assert(create_key == OnlyCreatable.__create_key), \
            "OnlyCreatable objects must be created using OnlyCreatable.create"
        self.value = value
```








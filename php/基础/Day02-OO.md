# OO

> Object Oriental

类名的标识符是大小写不敏感的

继承用关键字`extends`, 访问控制有`public`等, 引用静态成员有`Type::member`, 引用动态成员有`$this->member`

```php
class A {
    public $x;

    public function __construct($x) {
        $this->x = $x;
    }

    public function getX() {
        return $this->x;
    }

}

class B extends A {
    private $y;

    public function __construct($y) {
        parent::__construct($y);
        $this->y = $y;
    }

    public function sum() {
        // $this->x
        // 不存在 parent::x 或 parent::$x
        return $this->y + parent::getX();
    }
}
```

## 异常机制

```php
try {
    throw new Exception();
} catch (Exception $e) {
    // ignore $e
} catch (Throwable $t) {
    echo $t->getMessage();
} finally {
    echo 'finally';
}
```
# 信号和槽

## Connect

信号被监听, 监听到信号后调用槽函数的过程

一个信号可以对应一个槽函数

一个信号可以对应多个槽函数

一个槽函数可以被多个信号触发

```cpp
public slots: // 声明槽函数
    void helpCallback();
```

监听ui的`HelpButton`被`clicked`的信号, 回调`helpback`函数

```cpp
connect(ui->getHelpButton(), SIGNAL(clicked()), this, SLOT(helpCallback()));
```




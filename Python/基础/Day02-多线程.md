# 多线程编程

```python
import threading
import time

def task(msg: str):
    name: str = threading.currentThread().name
    while True:
        print(f"{name} 在执行, 输出消息:{msg}")
        time.sleep(1)

if __name__ == '__main__':
    thread1 = threading.Thread(
        target=task,
        name="任务一",  # 可省
        args=("参数",)  # 可省,按照元组传参即是位置参数,单个元素的元组也不能往了逗号
    )
    thread2 = threading.Thread(
        target=task,
        name="任务二",
        kwargs={'msg': "信息"}  # 按照字段传参
    )
    thread1.start()
    thread2.start()
```

输出: 

```text
任务一 在执行, 输出消息:参数
任务二 在执行, 输出消息:信息
任务一 在执行, 输出消息:参数
任务二 在执行, 输出消息:信息
任务二 在执行, 输出消息:信息任务一 在执行, 输出消息:参数

任务二 在执行, 输出消息:信息
任务一 在执行, 输出消息:参数
任务一 在执行, 输出消息:参数
任务二 在执行, 输出消息:信息
任务一 在执行, 输出消息:参数
```


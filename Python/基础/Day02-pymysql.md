# 操作MySQL

## 安装库

pymysql

## 使用

```python
from pymysql import Connection
from pymysql.cursors import Cursor

if __name__ == '__main__':
    conn: Connection = Connection(
        host="centos",
        port=3306,
        user="root",
        password="123"
    )
    print(conn.get_server_info())  # 8.0.27
    # 选择数据库
    conn.select_db("h_video")

    # 获取游标
    cursor: Cursor = conn.cursor()
    # 执行语句
    cursor.execute("select * from tb_user limit 0,5")

    # 获取查询结果, 二维元组
    tb_user = cursor.fetchall()
    for user in tb_user:
        print(user)

    # 关闭游标
    cursor.close()

    # 关闭连接
    conn.close()
```

```python
from pymysql import Connection
from pymysql.cursors import Cursor

def exec_print(cursor:Cursor):
    # 执行语句
    cursor.execute("select * from tb_user limit 0,5")
    # 获取查询结果, 二维元组
    tb_user = cursor.fetchall()
    for user in tb_user:
        print(user)

def connect_exec():
    coon: Connection
    with  Connection(
            host="centos",
            port=3306,
            user="root",
            password="123"
    ) as conn:
        print(conn.get_server_info())  # 8.0.27
        # 选择数据库
        conn.select_db("h_video")

        # 获取游标
        cursor: Cursor
        with conn.cursor() as cursor:
            exec_print(cursor)

if __name__ == '__main__':
    connect_exec()
```

## 事务

游标就是一个事物

**pymysql对事物默认不会自动提交**

```python
Connection(
        host="centos",
        port=3306,
        user="root",
        password="123",
        autocommit=True # 配置自动提交
)
```

```python
with conn.cursor() as cursor:
    try:
        exec_print(cursor)
        conn.commit() # 用conn提交事务
    except Exception as e:
        conn.rollback() # 用conn关闭事务
        print(e)
```

事务的提交就是事务的结束也是游标的关闭


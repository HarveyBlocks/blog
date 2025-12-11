# Socket

>   套接字, 负责网络数据传输

## 回声服务器

服务端

```python
import socket

if __name__ == '__main__':
    server_socket = socket.socket()
    server_socket.bind(('localhost', 8082))  # 参数是一个元组
    server_socket.listen(5)  # 传入参数为允许的连接数量,可省略,省略则分配合理值
    conn, addr = server_socket.accept()  # 返回的是一个二元元组, 这么写也可以
    # accept是阻塞方法
    print('Got connection from: ', addr)
    while True:
        data = (conn.recv(1024)  # recv是阻塞式的
                .decode("UTF-8"))  # 字节数组和字符串的转换
        if data == "exit":
            break
        conn.send(f"your data is: {data}".encode("UTF-8"))
    conn.close()
    server_socket.close()
```

客户端

```python
import socket

if __name__ == '__main__':
    client_socket = socket.socket()
    client_socket.connect(('localhost', 8082))  # 连接服务端的IP和端口
    while True:
        send_msg: str = input()
        print(send_msg)
        client_socket.send(send_msg.encode("utf-8"))
        if send_msg == 'exit':
            break
        recv_msg: str = client_socket.recv(1024).decode("utf-8")
        print(recv_msg)
    client_socket.close()
```


# 策略

## 七层负载

| 算法名称   | 说明                       |
| ---------- | -------------------------- |
| 轮询       | 默认方式, 即默认`weigth=1` |
| weight     | 权重方式                   |
| ip_hash    | 依据ip分配方式             |
| least_conn | 依据最少连接方式           |
| url_hash   | 依据URL分配方式            |
| fair       | 依据响应时间方式           |

### 轮询



### 加权

常用于基于服务器硬件性能

```nginx
upstream lb_test{
	server localhost:8001 weight=1;
	server localhost:8002 weight=5;
	server localhost:8003 weight=25;
}
```

### ip_hash

Session共享

将同一个**客户端IP**就会被定点到同一个服务器

```nginx
upstream lb_test{
	ip_hash;
    server localhost:8001;
	server localhost:8002;
	server localhost:8003;
}
```

加权就不能起作用了

### least_conn

假设使用轮询, 每台服务器干的活一定是一样的, 但是各个服务器的处理效率不同, 处理慢的服务器可能导致请求堆积

```nginx
upstream lb_test{
	least_conn;
    server localhost:8001;
	server localhost:8002;
	server localhost:8003;
}
```

### url_hash

缓存, 相同的URL请求指向特定的服务器, 在特定的服务器上缓存 

提高了缓存的命中率

```nginx
upstream lb_test{
	hash $request_uri;
    server localhost:8001;
	server localhost:8002;
	server localhost:8003;
}
```

### fair

yum-nginx也没有装`nginx_upstream_fair`模块

[github-nginx_upstream_fair](https://github.com/gnosek/nginx-upstream-fair)

```nginx
upstream lb_test{
	fair;
    server localhost:8001;
	server localhost:8002;
	server localhost:8003;
}
```

## 四层负载

1.9版本之后

yum已经安装了`--with-stream`

### stream

**在main下, 和http同级**

指定流服务器指令的配置文件上下文

例如, Redis, MySQL等, 不使用Http协议, 用Nginx也可以做一个反向代理+负载均衡

```nginx
stream{
    
}
```



### upstream

和http的upstream指令类似

### 需求分析

希望以端口分配服务器

端口81指向Server1,Server2, 端口82指向Server3



### 基础配置

```nginx
http {
	# ...
	
    # 不一定使用http{}里的server作为目标, 仅仅是因为便于测试
    server {
		listen   8001;
		server_name localhost;
		default_type text/html;
		location /{
			return 200 '<h1>8001</h1>';
		}
	}
	server {
		listen   8002;
		server_name localhost;
		default_type text/html;
		location /{
			return 200 '<h1>8002</h1>';
		}
	}
	server {
		listen   8003;
		server_name localhost;
		default_type text/html;
		location /{
			return 200 '<h1>8003</h1>';
		}
	}
    
	
}
stream {
    upstream test_stream1 {
        server localhost:8001; # 可以引向Redis,Zookeeper等其他服务器
        server localhost:8002;
    }
    upstream test_stream2 {
		server localhost:8003;
    }
    server {
        listen  81;
        # 没有Server_name
		proxy_pass test_stream1;
    }
    server {
   		listen	82;
   		proxy_pass test_stream2;
    }
}

```

能负载均衡, 但不是轮询, 周期有点长





`http`里的`server`和`stream`里的`server`如果监听同一个端口, 那么会代理到哪里去?

`stream`在第四层 ,`http`在第七层, `stream`的配置生效

# 系统优化

-   Buffer 缓冲
-   Cache 缓存



```nginx
proxy_buffering on;
proxy_buffer_size 4 32k;
proxy_busy_buffers_size 64k;
proxy_temp_file_write_size 64k;
```


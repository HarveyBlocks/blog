## 公私钥对

### 配置SSH公钥

#### 生成SSH公钥

- ```Dos
  ssh-keygrn -t rsa
  ```

- 不断回车

  - 如果公钥存在就**自动覆盖**

#### 获取公钥

- ```
  cat ~/.ssh/id_rsa.pub
  ```

- 
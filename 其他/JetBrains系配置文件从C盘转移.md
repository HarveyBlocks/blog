用`?????`表示IDE的名字

1. 在`C:\Users\Lenovo\AppData\Roaming\JetBrains\?????`下的文件拷贝到`D:\IT_study\JetBrains\?????\config`

2. 在`C:\Users\Lenovo\AppData\Local\JetBrains\????`下的文件拷贝到`D:\IT_study\JetBrains\?????\system`

3. 打开`D:\IT_study\?????\bin\idea.properties`, 在文件末尾添加

   ```properties
   # Roaming->config
   # Local->system
   idea.config.path=D:/IT_study/JetBrains/?????/config
   idea.system.path=D:/IT_study/JetBrains/?????/system
   idea.plugins.path=${idea.config.path}/plugins
   idea.log.path=${idea.system.path}/log
   ```

4. 删除C盘山文件


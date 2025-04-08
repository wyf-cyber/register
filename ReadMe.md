# 挂号系统后端
> test.sql 是本项目数据库的建库文件

### 2月26日更新
- 引入预约记录表appointments
- 添加对医生信息表doctorsX的增删改查，可以向用户展示医生信息列表，搜索满足条件的医生
- 添加挂号服务，支持挂号、取消挂号、查询个人挂号信息

### 3月6日更新
- 邮箱验证码发送功能需要使用 Redis，需要自行安装 Redis 并启动
- 修改了邮箱验证码发送的频率限制，现在每30秒只能发送一次

### 3月11日更新
- 添加了二维码生成服务，可以生成二维码并提供链接，模拟扫码付款

### 3月15日更新
- 添加了Redis自动启动功能，系统会在需要时自动启动Redis服务
- 需要确保Redis已正确安装在默认路径（C:\Program Files\Redis\redis-server.exe）
- 如果Redis安装在其他路径，请修改RedisHealthCheck.java中的redisPath变量

### Redis配置说明
1. 安装Redis
   - 下载Redis for Windows: https://github.com/microsoftarchive/redis/releases
   - 运行安装程序，建议安装到默认路径：C:\Program Files\Redis
   - 安装时选择"Add the Redis installation folder to the PATH environment variable"

2. 验证安装
   - 打开命令提示符，输入 `redis-cli ping`
   - 如果返回 "PONG"，说明Redis安装成功

3. 自动启动功能
   - 系统会在应用启动时自动检查Redis状态
   - 如果Redis未运行，会自动启动Redis服务
   - 如果Redis已运行，则直接使用现有服务

4. 注意事项
   - 确保Redis安装路径正确
   - 如果Redis安装在其他路径，需要修改RedisHealthCheck.java中的redisPath变量
   - 首次运行时可能需要以管理员权限运行应用

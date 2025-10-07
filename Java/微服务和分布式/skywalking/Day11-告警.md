# 告警

[Alerting | Apache SkyWalking](https://skywalking.apache.org/docs/main/v9.7.0/en/setup/backend/backend-alarm/)

发邮件之类的

-   `op`操作符
-   `threshould` 阈值
-   `period` 检查间隔时间
-   `count`  操过阈值几次就告警
-   `slince-period` 在触发告警后, 停止向人员发送告警的时间间隔
-   `message` 告警通知的信息

## 配置文件

`alarm-setting.yml`

```yml
rules:
  service_resp_time_rule: # 服务响应时间
    expression: sum(service_resp_time > 1000) >= 3
    period: 10
    silence-period: 5 # 秒
    # 在过去的十分钟, 服务响应时间超过1s的情况, 在三分钟出现
    message: Response time of service {name} is more than 1000ms in 3 minutes of last 10 minutes.
#  service_resp_time_rule:
#    expression: avg(service_resp_time) > 1000
#    period: 10
#    silence-period: 5
#    message: Avg response time of service {name} is more than 1000ms in last 10 minutes.
  service_sla_rule: # 成功率
    expression: sum(service_sla < 8000) >= 2
    # The length of time to evaluate the metrics
    period: 10
    # How many times of checks, the alarm keeps silence after alarm triggered, default as same as period.
    silence-period: 3
    message: Successful rate of service {name} is lower than 80% in 2 minutes of last 10 minutes
  service_resp_time_percentile_rule:
    expression: sum(service_percentile{_='0,1,2,3,4'} > 1000) >= 3
    period: 10
    silence-period: 5
    message: Percentile response time of service {name} alarm in 3 minutes of last 10 minutes, due to more than one condition of p50 > 1000, p75 > 1000, p90 > 1000, p95 > 1000, p99 > 1000
  service_instance_resp_time_rule:
    expression: sum(service_instance_resp_time > 1000) >= 2
    period: 10
    silence-period: 5
    message: Response time of service instance {name} is more than 1000ms in 2 minutes of last 10 minutes
  database_access_resp_time_rule:
    expression: sum(database_access_resp_time > 1000) >= 2
    period: 10
    message: Response time of database access {name} is more than 1000ms in 2 minutes of last 10 minutes
  endpoint_relation_resp_time_rule:
    expression: sum(endpoint_relation_resp_time > 1000) >= 2
    period: 10
    message: Response time of endpoint relation {name} is more than 1000ms in 2 minutes of last 10 minutes
    
    
# 发送警告的目标
#hooks:
#  webhook:
#    default:
#      is-default: true
#      urls:
#        - http://127.0.0.1/notify/
#        - http://127.0.0.1/go-wechat/


```



##Hook

接收告警信息

AlarmMessage的字段

-   scopeId
-   name
-   id0
-   id1
-   alarmMessage
-   startTime
-   ruleName





```java
@PostMapping("/notify")
public void alert(@RequestBody List<AlarmMessage> message){
    
}
```


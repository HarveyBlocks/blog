# 链路追踪



## APM

>   Applicaiton Performance Management 应用性能管理系统

识别大型系统的问题排查和解决

分析系统发生的故障, 快速定位, 解决问题

-   日志
    -   Kafka 中转
    -   ElastickSearch + Kibana + Logstash
-   指标Metrics
    -   聚合数据
    -   存储空间小
    -   乐意观察数据的状态和指标
    -   缺乏细节
    -   中间件: Prometheus
-   **链路追踪** Tances
    -   多个不同系统之间的相互调用关系
    -   能够以可视化的形式展示调用形式
    -   Skywalking, Java探针和动态字节码编辑技术
    -   PingPoint



## OpenTracing 标准

定义一套API, 分布式链路追踪的接口

### Dapper

论文

[Dapper](.\dapper.pdf)

[Dapper， bigbully](./Dapper)

[OpenTracing specification](https://opentracing.io/specification/)

## OpenTracing

[OpenTracing](https://github.com/opentracing-contrib/opentracing-specification-zh)

[OpenTracing语义标准](./specification)
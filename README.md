#### 介绍
> Netting-im 是使用netty编写的高性能IM服务器，除了提供IM服务以外，还提供 JSSDK、小程序SDK、Cocos Creator SDK，以及Http管理API。
> 
> 主要特点：
> * **开箱即用** - Netting-im 既支持集群单机部署、也支持集群部署；开发者可不引入任何中间件启动。
> * **高性能** - Netting-im 使用Netty作为NIO框架，成熟稳定，性能可靠。并支持集群部署，支撑百万连接。
> * **急速启动** - Netting-im 无冗余依赖，使用Guice作为IoC框架，可秒起服务。 
> * **高扩展性** - 采用业务与连接分离的设计，业务与连接采用适配层进行解耦。二次开发者，可完全不关注连接层进行开发。
 
#### 使用手册

管理端API:

JSSDK:

微信小程序SDK:

Cocos Creator SDK:

#### 性能报告


#### 开发计划

| 功能描述                  | 状态   | 完成时间     | 维护者   |
|-----------------------|------|----------|-------
| 管理端API Web 服务框架       | -[x] | 23.02.25 | w.wei |
| 单机 连接层                | -[x] | 23.02.27 | w.wei |
| 集群 连接层                | -[]  |          | w.wei |
| Terminal 登入许可权限认证     | -[x] | 23.02.28 | w.wei |
| 单机 事件投递与路由            | -[x] | 23.03.01 | w.wei |
| 集群 事件投递与路由            | -[]  |          | w.wei |
| 行为权限过滤                | -[]  |          | w.wei |
| 管理端API CRUD用户         | -[]  |          |       |
| 管理端API 创建群组           | -[]  |          |       |
| Terminal 创建群组 - 后端    | -[]  |          |       |
| Terminal 创建群组 - SDK   | -[]  |          |       |
| 管理端API 用户加入群主         | -[]  |          |       |
| Terminal 用户加入群主 - 后端  | -[]  |          |       |
| Terminal 用户加入群主 - SDK | -[]  |          |       |
| Create Task ....      | -[]  |          |       |



#### 二次开发文档


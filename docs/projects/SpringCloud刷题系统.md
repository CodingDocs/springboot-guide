> 推荐👍：
> - [接近100K star 的 Java 学习/面试指南](https://github.com/Snailclimb/JavaGuide)
> - [Github 95k+点赞的 Java 面试/学习手册.PDF](https://www.yuque.com/docs/share/f0c5ffbc-48b5-45f3-af66-2a5e6f212595)

今天给小伙伴们推荐一个朋友开源的面试刷题系统。

这篇文章我会从系统架构设计层面详解介绍这个开源项目，并且会把微服务常用的一些技术都介绍一下。即使你对这个项目不感兴趣，也能了解到很多微服务相关的知识。美滋滋！

_昨晚肝了很久~原创不易，若有帮助，求赞求转发啊！_

不得不说，这个刷题系统确实是有点东西，你真的值得拥有！首先，这是一个微服务的项目，其次这个系统涵盖了市面上常用的主流技术比如 SpringBoot、Spring Cloud 等等（后面会详细介绍）。

不论是你想要学习分布式的技术，还是想找一个实战项目练手或者作为自己的项目经验，这个项目都非常适合你。

另外，因为项目作者提供了详细的技术文档，所以你不用担心上手太难！

![PassJava文档](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-12-21/PassJava%E6%96%87%E6%A1%A3%E5%9C%B0%E5%9D%80.png)

## 效果图

我们先来看看这个面试刷题系统的效果图。这里我们只展示的是这个系统的前端（微信小程序），后台管理系统这里就不展示了。

![PassJava前端展示](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-12-21/PassJava%E5%89%8D%E7%AB%AF%E5%B1%95%E7%A4%BA.png)

可以看到，除了少部分地方的颜色搭配比较难看之外，页面整体 UI 还是比较美观的。

## 技术栈

再聊聊大家最关心的问题：“**这套系统的技术栈是什么样的呢?**”。

这套系统采用了目前企业都在用的主流技术：SpringBoot（基础框架）、Spring Cloud（微服务）、MyBatis（ORM框架）、Redis（缓存）、MySql（关系型数据库）、MongoDB（NoSQL）、RabbitMQ（消息队列）、Elasticsearch（搜索引擎）。并且，这个系统是以 Docker 容器化的方式进行部署的。非常实用！

## 系统架构设计

了解了技术栈之后，那必然需要简单了解一下整个 **系统的架构设计** ，这是系统的灵魂所在了（图源：[PassJava 官方文档](http://jayh2018.gitee.io/passjava-learning/#/01.项目简介/2.项目微服务架构图 "PassJava官方文档")）。

![](http://cdn.jayh.club/blog/20200407/scg1XhlvGbUV.png?imageslim)

### 网关

网关负责认证授权、限流、熔断、降级、请求分发、负载均衡等等操作。一般情况下，网关一般都会提供这些功能。

这里使用的是 **Spring Cloud Gateway** 作为网关。Spring Cloud Gateway 是 Spring Cloud 官方推出的第二代网关框架，目的是取代 netflix 的 Zuul 网关。

### 注册中心和配置中心

注册中心和配置中心这块使用的是阿里巴巴开源的 **Nacos** 。Nacos 目前属于 Spring Cloud Alibaba 中的一员。主要用于发现、配置和管理微服务，类似于 Consul、Eureka。并且，提供了分布式配置管理功能。

Nacos 的基本介绍如下（图源：[官网文档-什么是 Nacos](https://nacos.io/zh-cn/docs/what-is-nacos.html "官网文档-什么是 Nacos")）：

![nacos_map](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-12-21/nacosMap.jpg)

详解介绍一下 Nacos 在这个项目中提供的两个核心功能：

- **注册中心** ：API 网关通过注册中心实时获取到服务的路由地址,准确地将请求路由到各个服务。
- **配置中心** ：传统的配置方式需要重新启动服务。如果服务很多，则需要重启所有服务，非常不方便。通过 Nacos，我们可以动态配置服务。并且，Nacos 提供了一个简洁易用的 UI 帮助我们管理所有的服务和应用的配置。

关于配置中心，我们这里再拓展一下，除了 Nacos ，还有 Apollo、SpringCloud Config、K8s ConfigMap 可供选择。

### 分布式链路追踪

> 不同于单体架构，在分布式架构下，请求需要在多个服务之间调用，排查问题会非常麻烦。我们需要分布式链路追踪系统来解决这个痛点。

分布式链路追踪这块使用的是 Twitter 的 **Zipkin** ，并且结合了 **Spring Cloud Sleuth** 。

Spring Cloud Sleuth 只是做一些链路追踪相关的数据记录，我们可以使用 Zipkin Server 来处理这些数据。

相关阅读：[《40 张图看懂分布式追踪系统原理及实践》](https://mp.weixin.qq.com/s?__biz=Mzg2OTA0Njk0OA==&mid=2247492112&idx=3&sn=4eb100f4ecef98a475d7f7650c993222&chksm=cea1addbf9d624cdc0e868d3b72edf40a7a6880218ecb1a9ddbac50423d20dceceb8dca804f4&token=2007747701&lang=zh_CN#rd) 。

### 监控系统

监控系统可以帮助我们监控应用程序的状态，并且能够在风险发生前告警。

监控系统这块使用的是 **Prometheus + Grafana**。Prometheus 负责收集监控数据，Grafana 用于展示监控数据。我们直接将 Grafana 的数据源选择为 Prometheus 即可。

关于监控系统更详细的技术选型，可以看这篇文章：[《监控系统选型看这一篇够了！选择 Prometheus 还是 Zabbix ？》](https://mp.weixin.qq.com/s?__biz=Mzg2OTA0Njk0OA==&mid=2247490068&idx=2&sn=3ac42f6891fec8906230cc9e0ec28ba7&chksm=cea255dff9d5dcc9369c9c30eb55925cd16045d914cbfe194ea7d5630c3339630d731d654527&token=2007747701&lang=zh_CN#rd) 。

### 消息队列

我们知道，消息队列主要能为系统带来三点好处：

1. **通过异步处理提高系统性能（减少响应所需时间）。**
2. **削峰/限流**
3. **降低系统耦合性。**

常用的消息队列有：RabbitMQ（本系统所采用的方案）、Kafka、RocketMQ。

### 缓存

缓存这里使用的是 Redis ，老生常谈了，这里就不再多做介绍。

另外， 为了保证缓存服务的高可用，我们使用 Redis 官方提供了一种 Redis 集群的解决方案 Redis Sentinel 来管理 Redis 集群。

### 数据库

数据库这里使用的是 MySQL ，并使用主从模式实现读写分离，以提高读性能。

### 对象存储

由于是分布式系统，传统的将文件上传到本机已经没办法满足我们的需求了。

由于自己搭建分布式文件系统也比较麻烦，所以对象存储这里我们使用的是阿里云 OSS，它主要用于存储一些文件比如图片。

### 快速开发脚手架

另外，为了后台的快速搭建这里使用的是 **[renren-fast](https://gitee.com/renrenio/renren-fast "renren-fast")** 快速开发脚手架。使用这个脚手架配合上代码生成器 **[renren-generator](https://gitee.com/renrenio/renren-generator "renren-generator")** ，我们可以快速生成 70%左右的前后端代码。绝对是快速开发项目并交付以及接私活的利器了！

我在之前的也推荐过这个脚手架，详情请看下面这两篇文章：

1. [听说你要接私活？Guide 连夜整理了 5 个开源免费的 Java 项目快速开发脚手架。](https://mp.weixin.qq.com/s?__biz=Mzg2OTA0Njk0OA==&mid=2247487002&idx=1&sn=e9db79f1bbd561b3ead1c9475f8fd7f5&chksm=cea241d1f9d5c8c72fa33764208a26a26de5d2d3e4ac2a6794e0792ba779bb11414b3fd506e3&token=2007747701&lang=zh_CN#rd)
2. [解放双手，再来推荐 5 个 Java 项目开发快速开发脚手架！项目经验和私活都不愁了！](https://mp.weixin.qq.com/s?__biz=Mzg2OTA0Njk0OA==&mid=2247487467&idx=1&sn=c2d2bd28918b58d8646f1441791aeaaf&chksm=cea24020f9d5c9364afc22013fac3266fabffcaa370f708434a6a304274c6b1d8bce235b7a24&token=2007747701&lang=zh_CN#rd)

## 总结

这篇文章我主要从架构设计层面分析了朋友开源的这个基于微服务的刷题系统。

当然了，朋友使用微服务开发这个项目的主要目的也是为了自己实践微服务相关的知识，同时也是为了给需要微服务相关实战项目经验的小伙伴一个可以学习的项目。不然的话，直接用单体就完事了，完全可以支撑这个项目目前的并发量以及可预见的未来的并发量。

- 项目地址：https://github.com/Jackson0714/PassJava-Platform
- 文档地址：http://jayh2018.gitee.io/passjava-learning/#/


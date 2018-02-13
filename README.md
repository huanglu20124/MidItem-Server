## OverView

《三国词典+游戏》（期中项目的后台）:<br>
我们的期中 proj 是一个联网的 app 应用，实现了三国人物词典应该具有的基础增删改
查功能，同时也增加了一些拓展功能，包括以下：
1. 使用云服务器 tomcat 和数据库 mysql
2. 针对人物简介进行关键词全文检索
3. 对人物点赞、并根据点赞数生成排行榜
4. 增加 app 启动页动画
5. 基于“田忌赛马”回合制的联网游戏对战功能
6. 人物播放录音功能
web服务器为tomcat，数据库为mysql，部署在阿里云上面。后端应用采用了Spring MVC + Spring + JDBC的三层架构，另外还使用了websocket、redis、solr搜索引擎的技术。


## Requirements

* tomcat服务器
* eclipse
* mysql数据库
* redis数据库
* solr搜索服务器

## Details
* 采用Spring MVC+Spring+JDBC的MVC模式，安卓端作为视图层View。
![img](https://github.com/huanglu20124/ImgRespository/blob/master/miditem/MVC.png?raw=true)
* Redis的zset结构做人物人气排行榜。
* Websocket实现人物对战功能，原理如下图：
![img](https://github.com/huanglu20124/ImgRespository/blob/master/miditem/websocket.png?raw=true)
* 采用Solr作为搜索服务器，IkAnalyzer作为中文分词器，通过关键字可以快速检索武将。
![img](https://github.com/huanglu20124/ImgRespository/blob/master/miditem/solr.png?raw=true)

## Screenshot
![img](https://github.com/huanglu20124/ImgRespository/blob/master/miditem/1.png?raw=true)
![img](https://github.com/huanglu20124/ImgRespository/blob/master/miditem/2.png?raw=true)
![img](https://github.com/huanglu20124/ImgRespository/blob/master/miditem/3.png?raw=true)
![img](https://github.com/huanglu20124/ImgRespository/blob/master/miditem/4.png?raw=true)


## Author

| Author | E-mail |
| :------:  | :------: |
| huanglu | 845758437@qq.com |

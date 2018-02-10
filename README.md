## OverView

《三国词典+游戏》（期中项目的后台）:<br>
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

## Author

| Author | E-mail |
| :------:  | :------: |
| huanglu | 845758437@qq.com |

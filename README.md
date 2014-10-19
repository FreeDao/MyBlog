MyBlogClient
============

client for picksomething.cn

自己博客(www.picksomething.cn)的客户端

实现方式：

step1 http请求，获取html页面
step2 解析html页面，通过正则表达式匹配初title和URL，存入hashmap
step3 通过自定义的适配器绑定到listview上面，并显示出来

目前还有很多不完善的地方，后面再优化

后面有空再写一个通过连接自己的数据库获取数据的方式的客户端
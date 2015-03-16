MyBlogClient
============

Android client for www.picksomething.cn


![image](https://github.com/picksomething/MyBlog/blob/master/device-2015-03-16-152720.png)

![image](https://github.com/picksomething/MyBlog/blob/master/device-2015-03-16-152743.png)

![image](https://github.com/picksomething/MyBlog/blob/master/device-2015-03-16-152834.png)

![image](https://github.com/picksomething/MyBlog/blob/master/device-2015-03-16-152901.png)

![image](https://github.com/picksomething/MyBlog/blob/master/device-2015-03-16-152929.png)

用到的开源库:一个顺滑又漂亮的Android下拉刷新与加载更多列表组件。 
https://github.com/zarics/ZrcListView


APP实现方式：

step1 http请求，获取html页面

step2 解析html页面，通过正则表达式匹配出title,data和url，存入hashmap

step3 通过自定义的适配器绑定到listview上面，并显示出来

默认加载第一页的文章,下拉显示下一页的数据

目前还有很多不完善的地方，后面再优化

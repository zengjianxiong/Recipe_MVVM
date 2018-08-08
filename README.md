# Recipe_MVVM

应用功能：可以浏览各式各样的菜谱，在网络保存用户收藏的菜谱。 此应用是我在工作生活闲暇之余的作品，算是我拿来练手的APP。非常感谢给予我灵感和帮助的开源项目的作者。 此应用开发时，使用的设计模式，数据来源，界面设计，使用的GitHub开源项目如下：

1、本应用使用的开发设计模式是MVVM设计模式。

2、数据来源于Mob官网的“菜谱大全”，并且数据是完全免费的，非常感谢Mob官网为移动开发者所提供的数据服务。Mob官网地址：http://www.mob.com

3、网络Json数据请求方面，使用了开源框架retrofit 框架地址：https://github.com/square/retrofit

4、图片请求方面，使用的是开源框架Glide。框架地址：https://github.com/bumptech/glide

5、因为涉及到网络请求和本地IO数据处理，使用的是优秀的异步处理框架RxJava。框架地址：https://github.com/ReactiveX/RxJava

6、使用了Apollo，可以避免大量handler和本地广播的代码出现 框架地址：https://github.com/lsxiao/Apollo

7、使用了BGARefreshLayout框架，来处理下拉刷新，上拉加载更多的事件。框架地址：https://github.com/bingoogolapple/BGARefreshLayout-Android

8、使用了阿里云的ARouter框架，进行组件化开发。框架地址：https://github.com/alibaba/ARouter

9、使用了Google的LifeCycle框架来监听Activity的生命周期

10、使用了Google的DataBinding框架，避免了大量findViewById以及dagger，butterKnife等框架的注解

11、关于Google框架的内容了解可以前往google的Android开发者网站(需要翻墙)，地址：https://developer.android.com/arch

12、github上google发布的Android Architecture Components samples也可以拿来参照，地址：https://github.com/googlesamples/android-architecture-components

作者：瘸腿蚊 GitHub主页：https://github.com/zfl541091999
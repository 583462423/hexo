---
title: Failed to execute aapt
date: 2017-02-18 12:28:22
tags:
---

出现这个错误，表示很苦恼，上网找了好多资料和解决方法，都进行了尝试，都不行，于是自己静下心来进行仔细查看。

<!--more-->
最终还是发现问题出现在build.gradle文件中，首先，项目中有4个build.gradle，每个对应不同的module,我用了友盟的SDK，所以有一个build.gradle是友盟的。

最终确定问题出在友盟的build.gradle中。

在友盟的build.gradle中，compileSdkVersion是22，buildToolsVersion 是22.0.1,因为在打开这个项目的时候，提示android studio没有23.0.2这个版本的tools，所以就安装了。接着尝试把22.0.1修改为23.0.2，然后重新编译，成功！

至于为什么，我也不知道啊！

compileSdkVersion和buildToolsVersion本来就是对应的，按道理来讲应该可以的，但是提示是aapt出错，那可能就是buildToolsVersion 22.0.1这个版本的不能用了，因为aapt版本就是buildToolsVersion的版本，所以尝试换成其他的，buildToolsVersion的版本可以增高的。参考(http://www.bubuko.com/infodetail-1008155.html)

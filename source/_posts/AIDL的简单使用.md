---
title: AIDL的简单使用
date: 2017-03-03 20:11:22
tags:
---

进程间的通信比较复杂，如果想要搞懂是需要很大的耐心。AIDL如果是简单的使用还是很容易的。
<!-- more -->

首先我们可以看下AIDL用法，他是基于Service而进行通信的。我们可以让Service（可以绑定的）和AIDL进行比较，这样更容易掌握AIDL的用法。

首先看下Service的用法

首先声明一个类继承自Service，实现其中的onBind方法，并创建一个类，继承于IBinder，我们需要实现的方法，都要写其中，如下：

```
class MyBinder extends Binder{
    public void myMethod()
    {
        Log.i("我想要执行的任务","-----------");
    }
}
    
class MyService entends Service{
	...
  
    private MyBinder myBinder=  new MyBinder();
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("-------","OnBind");
        return myBinder;
    }
    
    ...
}
```
接着在使用的Activity或Fragment中，实例ServiceConnection对象，重写内部onServiceConnected方法，和onServiceDisconnected方法。在ServiceConnection中，当绑定时，会调用onServiceConnected方法，一般我们会在该方法中取得IBinder对象，这样我们才能使用我们在IBinder中创建的方法。

```
private MyBinder binder = null;
ServiceConnection sc = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
		binder = (MyBinder)service;
    }
    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
}
```
接着，我们绑定服务，就可以使用binder中的方法了。

```
bindService(new Intent(this,MyService.class),sc,BIND_AUTO_CREATE);
```
好了，这就是service的简单用法，接下来我们看一下AIDL的用法

我们首先要创建一个AIDL文件，创建出来的AIDL只是一个接口，比如我们创建的一个AIDL文件叫Tmp.aidl
那么这个Tmp.aidl会生成一个接口，interface Tmp{},我们可以在接口中添加方法，比如名字叫method，那么这个文件中的内容将是

```
// Tmp.aidl
package com.test.testandroid;
// Declare any non-default types here with import statements
interface Tmp {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void method();
}
```
接着我们使用Build-Make Project来生成对应的java文件，文件会在build/source/aidl/debug目录下，
而自动生成的代码非常复杂，但是我们不用关注这些。

好了，这样我们就可以编写我们的service文件了，在上面例子的基础上，我们只需要更改一点代码。首先，将MyBinder的继承对象更改

```
class MyBinder extends Tmp.Stub{
	@Override
    public void method(){
		Log.i("我想要执行的任务","-----------");
	}
}
```

接着将Activity中声明的MyBinder binder改为 Tmp binder,即myBinder变为Tmp接口对象。即代码
```
private MyBinder binder = null;

修改为

private Tmp binder = null;
```
这个时候，ServiceConnection中重写的onServiceConnected的赋值操作也需要更改,如下

```
public void onServiceConnected(ComponentName name,IBinder service)
   {
   	binder = Tmp.Stub.asInterface(service);//没改之前返回的是IBinder并强转为MyBinder，而修改之后返回的是Tmp接口，所以myBinder的声明方式为Tmp myBinder;
   }
```

然后绑定服务，这样就可以使用myBinder中的方法了。

那么总结，AIDL使用方法和Service使用方法差不多，修改Service变为AIDL的方法有几步

1.定义aidl接口文件，如Tmp.aidl,然后Build-Make Project生成Tmp.java
2.将原来的MyBinder继承对象修改为Tmp.Stub
3.将原先在Activity中声明的MyBinder对象改为Tmp类型
4.在onServiceConnected中，赋值操作替换替换为binder = Tmp.Stub.asInterface(service)
5.完

这样在绑定服务后，就可以使用远程方法了。

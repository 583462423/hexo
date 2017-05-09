---
title: 常用Java虚拟机参数
date: 2017-05-09 13:04:26
tags: JVM
---

在校可能觉得JVM离我们非常遥远,但是最近看了一篇微信公众号的推送,讲解了一下项目中遇到了频繁FULL GC的问题如何解决,顿时感觉,JVM离我们不是很遥远,只是我们所做的项目没有深度罢了.为了提高自己,遂进行阅读JVM实战一书,颇有感悟,记录之.

<!--more-->

# GC跟踪
`-XX:+PrintGC`:使用这个参数启动Java虚拟机之后,只要遇到GC就会打印日志,如:
比如下面这段代码:
```

List<Object> list = new ArrayList<Object>();
//不断创建变量,导致内存不足发生GC
while(true){
    Object o = new Object();
    list.add(o);
}
```
运行后,打印日志为:
```
[GC (Allocation Failure)  31744K->20852K(121856K), 0.0330050 secs]
[GC (Allocation Failure)  52596K->45511K(153600K), 0.0467573 secs]
[GC (Allocation Failure)  96857K->96119K(160256K), 0.1033293 secs]
[Full GC (Ergonomics)  96119K->80500K(260096K), 1.1317846 secs]
```
从上可以看出,一共有4次GC,其中FULL GC一次,括号内容是GC的原因,随后内容表示GC后,堆空间的变化,最后的时间表示GC所花费的时间.

如果要显示更加详细的信息,可以使用`-XX:+PrintGCDetails`参数.
打印日志为下:
```
[GC (Allocation Failure) [PSYoungGen: 31744K->5116K(36864K)] 31744K->20844K(121856K), 0.0394304 secs] [Times: user=0.08 sys=0.00, real=0.04 secs] 
[GC (Allocation Failure) [PSYoungGen: 36860K->5120K(68608K)] 52588K->40747K(153600K), 0.0428718 secs] [Times: user=0.10 sys=0.00, real=0.04 secs] 
[GC (Allocation Failure) [PSYoungGen: 56466K->5104K(68608K)] 92093K->91347K(155136K), 0.0963041 secs] [Times: user=0.27 sys=0.01, real=0.10 secs] 
[Full GC (Ergonomics) [PSYoungGen: 5104K->0K(68608K)] [ParOldGen: 86243K->80538K(188928K)] 91347K->80538K(257536K), [Metaspace: 3267K->3267K(1056768K)], 1.0248705 secs] [Times: user=1.64 sys=0.01, real=1.03 secs] 
[GC (Allocation Failure) [PSYoungGen: 63488K->5120K(99840K)] 180081K->156121K(288768K), 0.1642157 secs] [Times: user=0.40 sys=0.01, real=0.17 secs] 
[Full GC (Ergonomics) [PSYoungGen: 5120K->0K(99840K)] [ParOldGen: 151001K->140019K(294912K)] 156121K->140019K(394752K), [Metaspace: 3268K->3268K(1056768K)], 0.9482000 secs] [Times: user=1.93 sys=0.00, real=0.94 secs] 
[GC (Allocation Failure) [PSYoungGen: 94720K->5120K(132096K)] 288822K->287118K(427008K), 0.2567920 secs] [Times: user=0.95 sys=0.03, real=0.26 secs] 
[Full GC (Ergonomics) [PSYoungGen: 5120K->0K(132096K)] [ParOldGen: 281998K->250833K(493056K)] 287118K->250833K(625152K), [Metaspace: 3268K->3268K(1056768K)], 1.8197845 secs] [Times: user=4.41 sys=0.00, real=1.82 secs] 
Heap
 PSYoungGen      total 132096K, used 7604K [0x00000000d6d80000, 0x00000000ea400000, 0x0000000100000000)
  eden space 126976K, 5% used [0x00000000d6d80000,0x00000000d74ed350,0x00000000de980000)
  from space 5120K, 0% used [0x00000000de980000,0x00000000de980000,0x00000000dee80000)
  to   space 91648K, 0% used [0x00000000e4a80000,0x00000000e4a80000,0x00000000ea400000)
 ParOldGen       total 493056K, used 250833K [0x0000000084800000, 0x00000000a2980000, 0x00000000d6d80000)
  object space 493056K, 50% used [0x0000000084800000,0x0000000093cf46f0,0x00000000a2980000)
 Metaspace       used 3276K, capacity 4500K, committed 4864K, reserved 1056768K
  class space    used 364K, capacity 388K, committed 512K, reserved 1048576K
```

可以看到,每行都会有很详细的发生GC的信息,并且在最后结束的时候,会打印出整个堆新生代,老年代等内存的使用率等信息.
注意`PSYoungGen`代表的不是新生代,而是收集器.

如果想要在每次GC后,都打印出堆的信息,即上述例子中的程序退出前打印的日志,那么可以使用`-XX:+PrintHeapAtGC`
如果想要知道GC发生的时间,注意不是GC发生消费的时间,而是GC在什么时候发生的这个时间,可以使用:`-XX+PrintGCTimeStamps`,这个只是比`-XX:+PrintGC`多打印了时间而已.
而GC会导致程序停顿,使用`-XX:+PrintGCApplicationConcurrentTime`可以打印出应用程序的执行时间,使用`-XX:+PrintGCApplicationStoppedTime`可以但打印出由于GC而产生的停顿时间.
如果要跟踪系统内的软引用,弱引用,虚引用以及Finallize队列,可以使用`-XX:+PrintReferenceGC`.
默认情况下,GC日志会在控制台输出,但为了后续分析和定位,可以将日志以文件形式输出,可以使用`-Xloggc`来指定,如:`-Xloggc:log/gc.log`可以在当前目录log文件夹下的gc.log文件中,记录所有GC日志,注意仅记录GC日志.

# 类加载跟踪
随着动态代理,AOP等技术的普遍使用,系统在运行阶段可能会生成一些类,这些类比较隐蔽,无法通过文件系统找到,所以可以通过虚拟机提供的参数来跟踪.

`-verbose:class`跟踪类的加载和卸载,而`-XX:+TraceClassLoading`仅跟踪类的夹在,`-XX:+TraceClassUnloading`来跟踪类的卸载.
比如打印日志如下:
```
[Loaded java.lang.Void from /usr/lib/jvm/jdk1.8.0_111/jre/lib/rt.jar]
[Loaded sun.misc.Signal$1 from /usr/lib/jvm/jdk1.8.0_111/jre/lib/rt.jar]
[Loaded java.lang.Shutdown from /usr/lib/jvm/jdk1.8.0_111/jre/lib/rt.jar]
[Loaded java.lang.Shutdown$Lock from /usr/lib/jvm/jdk1.8.0_111/jre/lib/rt.jar]
```
上述省略了大部分的日志.可以看到,程序退出的时候,会加载Shutdown这个类.

# 系统参数查看
`-XX:+PrintVMOptions`查看程序运行时,打印虚拟机接受的命令行显示参数,注意是命令行指定的参数,如打印日志:
```
VM option '+PrintVMOptions'
```
`-XX:+PrintCommandLineFlags`打印传递给虚拟机的显示和隐式参数,隐式参数未必是命令行给的,可能是虚拟机启动时自行设置的.如:
```
-XX:InitialHeapSize=129472768 -XX:MaxHeapSize=2071564288 -XX:+PrintCommandLineFlags -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC 
```
可以看到,虚拟机启动的时候,自动设置了初始化堆容量,最大堆容量等等.

`-XX:+PrintFlagsFinal`用来打印所有系统参数的值.

# 堆的配置参数
## 堆初始大小与最大大小
`-Xms`用来指定初始堆空间,但是如果初始堆空间耗尽,虚拟机将会对堆空间进行扩展,其扩展上限为最大堆空间,而最大堆空间,由`-Xmx`来指定.比如:`-Xmx20m -Xms5m`表示初始堆空间5M,而最大堆空间为20M.
实际上,`-Xms`是`-XX:InitialHeapSize`的缩写,`-Xmx`是`-XX:MaxHeapSize`的缩写.
但注意,可用内存和最大内存不是一个概念,因垃圾收集算法可能需要一些额外空间,所以可用最大内存总会比最大内存小一点.在程序中通过`Runtime.getRuntime().maxMemory()`来得到最大可用内存.

## 新生代配置
`-Xmn`可用于设置新生代的大小,设置一个较大的新生代会减少老年代的大小,一般新生代的大小设置为堆空间的1/3到1/4左右.
`-XX:SurvivorRatio`用来设置新生代中eden和from/to空间的比例,比例关系为eden/from = eden/to,比如:`-XX:SurvivorRatio=2`表示eden/from = 2,eden/to = 2.

`-XX:NewRatio`用来设置老年代和新生代的比例.

## 堆溢出
`-XX:+HeapDumpOnOutOfMemoryError`在内存溢出的时候,导出整个堆信息,和它配合使用的还有:`-XX:HeapDumpPath`,来指定导出堆的存放路径.
如:`-Xms5m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=tmp.dump`,程序代码如下:
```
Vector v = new Vector();
for(int i=0; i<25; i++)
    v.add(new byte[1*1024*1024]);
```

打印日志为:
```
ava.lang.OutOfMemoryError: Java heap space
Dumping heap to tmp.dump ...
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at com.baidu.Main.solve(Main.java:23)
	at com.baidu.Main.main(Main.java:16)
Heap dump file created [15690241 bytes in 0.023 secs]

```

而对于.dump文件,需要使用一些工具进行分析,比如MAT.

# 非堆内存的参数配置

## 方法区配置
在JDK1.8中永久区被彻底移除,使用了新的元数据区存放类的元数据,默认情况,元数据区只受系统可用内存的限制,但依然可使用`-XX:MaxMetaspaceSize`来指定其最大可用值.

## 栈配置
`-Xss`指定线程栈大小

## 直接内存配置
最大可用直接内存可使用参数`-XX:MaxDirectMemorySize`设置,如不设置,默认值为最大堆空间,即`-Xmx`,当直接内存使用量达到`-XX:MaxDirectMemorySize`时,就会触发垃圾回收.

## Client/Server
Java虚拟机支持Client和Server两种运行模式,使用参数`-client`可以指定使用Client模式,使用参数`-server`可以指定Server模式,而使用`java -version`可以查看当前模式.
与Client模式相比,Server启动比较慢,但是Server模式运行比Client快.

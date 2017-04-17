---
title: JVM学习
date: 2017-04-03 21:25:29
tags:
---

# 内存

java运行时数据区：
![](/images/java运行时数据区.png)

<!--more-->
## 程序计数器
线程私有
当前所执行字节码的行号指示器。
唯一一个在JAVA虚拟机规范中没有规定任何OutOfMemoryError情况的区域。

## 虚拟机栈
线程私有
每个方法在执行的同事都会创建一个栈帧(Stack Frame)存储局部变量表，操作数栈，动态链接，方法入口等信息。
局部变量表存放编译期可知的基本数据类型，对象引用。
该区域会抛出StackOverflowError异常。如果无法申请到足够的内存抛OutOfMemoryError.

## 本地方法栈
线程私有
为虚拟机使用到的native方法服务.
有的虚拟机(如 Sun HotSpot)直接把本地方法栈和虚拟机栈合二为一.

## JAVA堆
所有线程共享.
虚拟机启动时创建.
用于存放对象实例.
是垃圾收集器管理的主要区域,所以也称为GC堆.
分为新生代和老年代,再细致一点则分为Eden(伊甸园),From Survivor,To Survivor,老年代等.
Java堆可以处于物理上不连续的内存空间中,只要逻辑上是连续的即可.


## 方法区
所有线程共享.
存储虚拟机加载的类信息(如类名,访问修饰符,常量池,字段描述),常量,静态变量,即时编译器(JIT)编译后的代码等数据.
别名:non-heap,永久代
垃圾收集行为在这个区域比较少出现,该区域内存回收目标主要针对常量池的回收和对类型的加载.

### 运行时常量池
是方法区的一部分.
Class文件中还有一项信息是常量池,用于存放编译器生成的各种字面量和符号引用,这部分内容将在类加载后进入方法区的运行时常量池中存放.
String.intern():如果字符串常量池中已经包含一个等于此String对象的字符串,则返回代表池中这个字符串的String对象;否则,将此String对象包含的字符串添加到常量池中,并且返回此String对象的引用,在测试运行时常量池内存溢出的时候,就可以使用该方法.

# 对象

## 对象的创建
遇到new指令时首先检查这个指令的参数能否在常量池中定位到一个类的符号引用,并且检查该符号代表的类是否已经被加载,解析和初始化,若没有则加载.

类加载检查通过后,为对象分配内存,对象所需内存大小在类加载完成后可完全确定.
Java堆规整时,采用指针碰撞分配内存,否则采用空闲列表.
虚拟机还要对对象进行必要的设置,在对象头(Object header)中设置对象哈希码,gc分代等等


## 对象的内存布局
HotSpot中,对象在内存中存储布局分为3块:对象头,实例数据和对齐填充.
对象头包含两部分,第一个部分存储的是对象自身运行时数据,如hashCode,GC分代,锁等.第二部分是类型指针,通过该指针确定这个对象是哪个类的实例.
如果对象是一个Java数组,在对象头必须有一块用于记录数组长度的数据

实例数据就是保存各种类型的字段内容,无论是父类继下来的还是子类定义的,都需要记录下来.

对齐填充不是必然存在的,仅仅起填充作用,因HotSpot自动内存管理系统要求对象起始地址必须是8字节的整数.如果数据没有对齐,那么就通过对齐填充来将对象数据对齐.

## 对象访问定位
通过栈中的引用来操作堆上的具体对象,但是Java虚拟机规范中没有定义这个引用应该如何定位,所以对不同虚拟机而言,定位方式也不同.主流的定位方式有句柄和直接指针两种.

句柄:Java堆中4划分出一块内存作为句柄池来存储句柄,栈中的对象引用就是存储的句柄地址,句柄包含对象实例数据和类型数据的地址.如:
![](/images/Java句柄.png)

直接指针:栈中引用存储的就是对象的地址,如:
![](/images/直接指针.png)

使用句柄的好处是存储稳定的句柄,对象被移动,只改变句柄的实例数据指针,引用本身不需要更改,直接指针的好处是速度更快.HotSpot使用的就是直接指针.


# 垃圾收集器和内存分配策略

## 对象是否存活

确定哪些对象还活着,使用的算法有引用计数法和可达性分析法,注意这两个方法不是垃圾收集算法,只是判断对象是否存活的方法.

### 引用计数法
给对象添加一个引用计数器,每当有一个地方引用它时,计数器加1,引用失效时,计数器减1.任何时刻计数器为0的对象就不可能再被使用.
主流的Java虚拟机里面没有使用该算法,原因在于它很难解决对象之间相互循环引用的问题.

### 可达性分析法
基本思想就是通过一系列的称为"GC Roots"的对象作为起始点,从这些节点开始向下搜索,搜索所走过的路径称为引用链,当一个对象到GC Roots没有任何引用链相连,即该对象从GC Roots不可达,证明此对象是不可用的.如图,Object5,6,7虽然互相关联,但是从GC Roots是不可达的,所以它们将被判定为是可回收对象.
![](/images/可达性分析.png)

Java中可以作为GC Roots的对象包括下面几种:
* 虚拟机栈(栈帧中的本地变量表)中引用的对象
* 方法区中静态属性引用的对象
* 方法区中常量引用的对象
* 本地方法栈中JNI引用的对象

## 引用
JDK1.2后Java对引用的概念进行了扩充,将引用分为强引用(StrongReference)、软引用(Soft Reference)、弱引用(Weak Reference)、虚引用(Phantom Reference)4种,这4种引用强度依次逐渐减弱。

强引用就是指在程序代码之中普遍存在的,类似“Object obj=new Object()”这类的引用,只要强引用还存在,垃圾收集器永远不会回收掉被引用的对象。

软引用是内存不足的时候才会进行回收.

弱引用无论当前内存是否足够,都会回收掉只被弱引用关联的对象。

虚引用也称为幽灵引用或者幻影引用,它是最弱的一种引用关系。一个对象是否有虚引用的存在,完全不会对其生存时间构成影响,也无法通过虚引用来取得一个对象实例。为一个对象设置虚引用关联的唯一目的就是能在这个对象被收集器回收时收到一个系统通知。

## 生存还是死亡
对于一个对象,如果可达性分析后发现不可达后,并非是非死不可的,这个时候它们只是处于缓刑阶段.

真正宣告一个对象死亡,至少经历两次标记过程:可达性分析后发现不可达的对象,将被第一次标记并进行筛选,筛选的条件是该对象是否有finalize()方法,如果没有覆盖或已覆盖但已执行过一次,则这些对象不会去执行finalize()方法.
如果对象有finalize()方法且从未被执行过,那么就会将该对象放在F-Queue队列中,并由虚拟机创建的低优先级的Finalizer线程去执行它.但不会等待该方法执行结束,原因在于怕导致阻塞现象.

fianlize()方法是对象自救的最后一次机会,如果自救成功,则finalize()方法就失效了,因为该方法只能被调用一次.一般不建议使用该方法,完全可以将之遗忘.


## 回收方法区

方法区的内存也是需要回收的.
永久代垃圾收集主要回收两部分内容:废弃常量和无用的类.
废弃常量举例来说,"abc"常量如果在程序中没有任何一个String的引用来指向它,如果有必要的话,"abc"就会被清理出常量池.

判断一个常量是否是废弃常量比较简单,但是判断是否是无用类比较苛刻:
* 该类的所有实例都已经被回收
* 加载该类的ClassLoader已被回收
* 该类对应的java.lang.Class对象没有在任何地方被引用,无法通过反射访问该类的方法.

## 垃圾收集算法

### 标记-清除算法
最基础的算法
分两个阶段:标记和清除,首先标记所有需要回收的对象,标记完成后进行清除工作.

缺点:效率不高;清除后产生空间碎片.
![](/images/标记清除算法.png)

### 复制算法
将内存分为大小相等的两块,每次只能使用一块,当一块内存用完时,将存活的对象复制到另一块上,并将之前的内存块清除.

缺点:浪费过多的资源,代价太高
![](/images/复制算法.png)


不过现在的商业虚拟机都采用这种算法来回收新生代,将内存分为较大的Eden空间和两块较小的Survivor空间,每次使用Eden和其中一块Survivor,当回收时,将Eden和Survivor中还存活着的对象一次性地复制到另外一块Survivor空间上,最
后清理掉Eden和刚才用过的Survivor空间.

### 标记-整理算法
标记过程仍然与“标记-清除”算法一样,但后续步骤不是直接对可回收对象进行清理,而是让所有存活的对象都向一端移动,然后直接清理掉端边界以外的内存.如图:

![](/images/标记-整理.png)

### 分代收集算法
当前商业虚拟机的垃圾收集都采用该算法,将Java堆内存分为几块,一般分为老年代和新生代,新生代中采用复制算法,老年代中采用标记-整理或标记清除算法.

### HotSpot采用的算法

首先要**枚举根节点**,即GC roots,但是一般根节点有数百兆,这样会消耗更多内存.
进行GC的时候,不能出现分析过程中对象的引用关系还在不断变化的情况,所以在GC进行时必须停顿所有的Java线程.
准确式内存管理即虚拟机可以知道内存中某个位置的数据具体是什么类型.
目前主流的Java虚拟机使用的都是准确是GC,所以当执行系统停顿下来后,并不需要一个不漏地检查完所有执行上下文和全局的引用位置,虚拟机应当是有办法直接得知哪些地方存放着对象引用.HotSpot的实现中,使用OopMap数据结构来达到这个目的.类加载完成的时候,HotSpot会把对象内偏移量上是什么数据类型计算出来,在JIT编译过程中,也会在特定位置记录栈和寄存器中哪些位置是引用.这样GC扫描的时候就可以直接得知这些信息了.

**安全点**:如果为每一条指令都分配OopMap,将会需要大量额外空间.而HotSpot也不是为每条指令生成OopMap,只是在特定位置记录这些信息,将这些位置称为安全点,即程序执行时并非在所有地方都能停顿下来开始GC,只有在到达安全点时才能暂停.
GC发生时要让所有线程跑到安全点再停顿下来,采用的方式有两种:抢先式中断和主动式中断,其中抢先式中断不需
要线程的执行代码主动去配合,在GC发生时,首先把所有线程全部中断,如果发现有线程中断的地方不在安全点上,就恢复线程,让它“跑”到安全点上.	现在几乎没有虚拟机采用这种方式.而主动式中断的思想是当GC需要中断线程的时候,不直接对线程操作,仅仅简单地设置一个标志,各个线程执行时主动去轮询这个标志,发现中断标志为真时就自己中断挂起。


**安全区域**:如果遇到线程sleep而长时间无法到达安全点,JVM显然不可能等到线程sleep结束才去中断其运行,这个时候就需要安全区域.安全区域是指:在一段代码片段之中,引用关系不会发生变化.所以安全区域中任意地方开始GC都是安全的.

## 垃圾收集器

Serial收集器:单线程收集器,新生代
ParNew收集器:Serial收集器的多线程版本
Parallel Scavenge:新生代收集器,使用复制算法,又是并行的多线程收集器
Serial Old:Serial的老年代版本,标记-整理算法
Parallel Old:老年代版本,使用多线程和标记-整理算法

CMS(Concurrent Mark Sweep)收集器是一种以获取最短回收停顿时间为目标的收集器。目前很大一部分的Java应用集中在互联网站或者B/S系统的服务端上,这类应用尤其重视服务的响应速度,希望系统停顿时间最短,以给用户带来较好的体验。CMS收集器就非常符合这类应用的需求。
使用标记清除算法实现,整个步骤分为4步:
1. 初始标记(CMS initial mark)
2. 并发标记(CMS concurrent mark)
3. 重新标记(CMS remark)
4. 并发清除(CMS concurrent sweep)
初始标记和重新标记需要暂停其他所有线程(Stop The World),初始标记仅仅只是标记一下GC Roots能直接关联到的对象,速度很快,并发标记阶段就是进行GC RootsTracing的过程,而重新标记阶段则是为了修正并发标记期间因用户程序继续运作而导致标记产生变动的那一部分对象的标记记录,这个阶段的停顿时间一般会比初始标记阶段稍长一些,但远比并发标记的时间短。由于整个过程中耗时最长的并发标记和并发清除过程收集器线程都可以与用户线程一起
工作,所以,从总体上来说,CMS收集器的内存回收过程是与用户线程一起并发执行的。

G1收集器:反正就是一个很吊的收集器...

上网搜刮的两张图片:
![](/images/收集器.png)

![](/images/收集器关系.png)

## 内存分配与回收策略
大多数情况下,对象在新生代Eden区中分配。当Eden区没有足够空间进行分配时,虚拟机将发起一次Minor GC。

Minor GC:指发生在新生代的垃圾收集动作,因为Java对象大多都具备朝生夕灭的特性,所以Minor GC非常频繁,一般回收速度也比较快。
Major GC/Full GC:指发生在老年代的GC,出现了Major GC,经常会伴随至少一次的Minor GC(但非绝对的,在Parallel Scavenge收集器的收集策略里就有直接进行Major GC的策略选择过程)。Major GC的速度一般会比Minor GC慢10倍以上。

**大对象直接进入老年代**
所谓的大对象是指,需要大量连续内存空间的Java对象,最典型的大对象就是那种很长的字符串以及数组(笔者列出的例子中的byte[]数组就是典型的大对象)。大对象对虚拟机的内存分配来说就是一个坏消息(替Java虚拟机抱怨一句,比遇到一个大对象更加坏的消息就是遇到一群“朝生夕灭”的“短命大对象”,写程序的时候应当避免),经常出现大对象容易导致内存还有不少空间时就提前触发垃圾收集以获取足够的连续空间来“安置”它们。


**长期存活的对象将进入老年代**
虚拟机给每个对象定义了一个对象年龄(Age)计数器。如果对象在Eden出生并经过第一次Minor GC后仍然存活,并且能被
Survivor容纳的话,将被移动到Survivor空间中,并且对象年龄设为1。对象在Survivor区中每“熬过”一次Minor GC,年龄就增加1岁,当它的年龄增加到一定程度(默认为15岁),就将会被晋升到老年代中。

**动态对象年龄判定**
为了能更好地适应不同程序的内存状况,虚拟机并不是永远地要求对象的年龄必须达到了MaxTenuringThreshold才能晋升老年代,如果在Survivor空间中相同年龄所有对象大小的总和大于Survivor空间的一半,年龄大于或等于该年龄的对象就可以直接进入老年代,无须等到MaxTenuringThreshold中要求的年龄。

**空间分配担保**
在发生Minor GC之前,虚拟机会先检查老年代最大可用的连续空间是否大于新生代所有对象总空间,如果这个条件成立,那么Minor GC可以确保是安全的。如果不成立,则虚拟机会查看HandlePromotionFailure设置值是否允许担保失败。如果允许,那么会继续检查老年代最大可用的连续空间是否大于历次晋升到老年代对象的平均大小,如果大于,将尝试着进行
一次Minor GC,尽管这次Minor GC是有风险的;如果小于,或者HandlePromotionFailure设置不允许冒险,那这时也要改为进行一次Full GC。

解释一下“冒险”是冒了什么风险,前面提到过,新生代使用复制收集算法,但为了内存利用率,只使用其中一个Survivor空间来作为轮换备份,因此当出现大量对象在MinorGC后仍然存活的情况(最极端的情况就是内存回收后新生代中所有对象都存活),就需要老年代进行分配担保,把Survivor无法容纳的对象直接进入老年代。与生活中的贷款担保类似,老年代要进行这样的担保,前提是老年代本身还有容纳这些对象的剩余空间,一共有多少对象会活下来在实际完成内存回收之前是无法明确知道的,所以只好取之前每一次回收晋升到老年代对象容量的平均大小值作为经验值,与老年代的剩余空间进行比较,决定是否进行Full GC来让老年代腾出更多空间。

取平均值进行比较其实仍然是一种动态概率的手段,也就是说,如果某次Minor GC存活后的对象突增,远远高于平均值的话,依然会导致担保失败(Handle Promotion Failure)。如果出现了HandlePromotionFailure失败,那就只好在失败后重新发起一次Full GC。虽然担保失败时绕的圈子是最大的,但大部分情况下都还是会将HandlePromotionFailure开关打开,避免Full GC过于频繁.


# 虚拟机性能监控及故障处理
**jps**:列出正在运行的虚拟机进程,显示虚拟机执行的主类.
`jps [options] [hostid]`,hostid为RMI注册表中注册的主机名.
|参数名|作用|
|------|---|
|-q|只输出LVMID,省略主类的名称|
|-m|输出虚拟机进程启动时传递给主类main()的参数|
|-l|输出主类的全名,如果进程执行的是jar包,输出jar包路径|
|-v|输出虚拟机进程启动时JVM参数|

说到参数这里说明一下 -Xms是指定堆的最小指,-Xmx是指定堆的最大值

**jstat**:虚拟机统计信息监控工具,注意是监控工具.
`jstat [option] [vmid] [interval] [count]`
本地虚拟机中VMID和LVMID是一致的,远程时:`protocol://vmid@hostname:port/servername`
interval是间隔时间,后面可以加上[s|ms]表示时间单位
count表示查询次数.

如`jstat -gc 2764 250 20`表示每250ms查询一次进程2764垃圾收集状况,一共查询20次.
参数为下表:
![](/images/jstat)

摘抄书中样例:
`jstat -gcutil 2764`
结果:
```
S0 S1 E O P YGC YGCT FGC FGCT GCT
0.00 0.00 6.20 41.42 47.20 16 0.105 3 0.472 0.577
```
这台服务器的新生代Eden区(E,表示Eden)使用了6.2%的空间,两个Survivor区(S0、S1,表示Survivor0、Survivor1)里面都是空的,老年代(O,表示Old)和永久代(P,表示Permanent)则分别使用了41.42%和47.20%的空间。程序运行以来共发生Minor GC(YGC,表示Young GC)16次,总耗时0.105秒,发生Full GC(FGC,表示FullGC)3次,Full GC总耗时(FGCT,表示Full GC Time)为0.472秒,所有GC总耗时(GCT,表示GC Time)为0.577秒。

**jmap**:Java内存映像工具
用于生成堆转储快照,还可查询finalize执行队列,Java堆和永久代的详细信息.`jmap [option] pid`

**jhat**:虚拟机堆转储快照分析工具
jhat 内置了一个微型的HTTP/HTML服务器,生成dump文件的分析结果后,可以在浏览器中查看.

**jstack**:Java堆栈跟踪工具

## JDK可视化工具

**JConsole**:Ubuntu环境下直接通过终端键入 `jconsole`即可进入图形化界面.开启后会自动搜索出本地运行的所有虚拟机进程,不需要用户自己使用jps来查询.

测试的时候,发现idea有一个Main()进程在运行,突然就明白了,原来idea是用Java进行开发的.= =

这个图形界面包含的东西还很多,截个图观赏下.所以如果想要分析内存泄露,用这个也是可行的.
![](/images/jconsole.png)


# 类文件结构
Class文件是一组以8字节为基础单位的二进制流.

无符号数属于基本的数据类型,以u1、u2、u4、u8来分别代表1个字节、2个字节、4个字节和8个字节的无符号数,无符号数可以用来描述数字、索引引用、数量值或者按照UTF-8编码构成字符串值。

表是由多个无符号数或者其他表作为数据项构成的复合数据类型,所有表都习惯性地以“_info”结尾。表用于描述有层次关系的复合结构的数据,整个Class文件本质上就是一张表,它由下表所示的数据项构成。
![](/images/class文件格式.png)

Class文件比较严格,没有分隔符,哪个字节代表什么含义,长度是多少,先后顺序如何都不允许改变.

## 魔数与Class文件的版本
每个Class文件的头4个字节被称为**魔数**(Magic Number),它的**唯一**作用是确定这个文件是否为一个能被虚拟机接受的Class文件。
很多文件存储标准中都使用魔数来进行身份识别,譬如图片格式,如gif或者jpeg等在文件头中都存有魔数.
魔数是固定值,用来表明这个文件是Class文件,Class文件的魔数是`0xCAFEBABE`.

紧接着魔数的4个字节存储的是Class文件的版本号:第5个和第6个字节是此版本号,第7,8个字节是主版本号.ava的版本号是从45开始的,JDK 1.1之后的每个JDK大版本发布主版本号向上加1(JDK 1.0~1.1使用了45.0~45.3的版本号),高版本的JDK能向下兼容以前版本的Class文件,但不能运行以后版本的Class文件,即使文件格式并未发生任何变化,虚拟机也必须拒绝执行超过其版本号的Class文件。

紧接着主次版本号之后的是常量池入口,常量池可以理解为Class文件中的资源仓库,它是Class文件结构中与其他项目关联最多的数据类型,也是占用Class文件空间最大的数据项目之一,同时它还是在Class文件中第一个出现的表类型数据项目。

由于常量池中常量的数量是不固定的,所以在常量池的入口需要放置一项u2类型的数据,及后的8,9字节来表示.代表常量池容量计数值,不过其计数从1开始,1表示容量为0. 为什么从1开始是有原因的,0表示其他特殊的含义.
常量池中主要存放两大类常量:字面量和符号引用.字面量比较接近Java语言层面的常量概念,如文本字符串,声明为final的常量值等.而符号引用属于编译原理方面的概念,包括:类和接口的全限定名,字段的名称和描述符,方法的名称和描述符.
常量池中每一项常量都是一个表,jdk1.7后,表结构共有14个:
![](/images/常量池的项目类型.png)

这14个表都有一个共同的特点,就是表开始的第一位是一个u1类型的标志位,代表当前这个常量属于哪种常量类型.
比如:
![](/images/常量结构.png)

tag是标志位,上面已经讲过了,它用于区分常量类型;name_index是一个索引值,它指向常量池中一个CONSTANT_Utf8_info类型常量,此常量代表了这个类(或者接口)的全限定名.


以下是总表:
![](/images/14种常量项结构总表.png)
![](/images/14种常量项结构总表2.png)


其他表集合就不一一给列举了.


# 虚拟机类加载机制

虚拟机把描述类的数据从Class文件加载到内存,并对数据进行校验,转换解析和初始化,最终形成可以被虚拟机直接使用的Java类型,这就是虚拟机的类加载机制.

与那些在编译时需要进行连接工作的语言不同,在Java语言里面,类型的加载、连接和初始化过程都是在程序运行期间完成的.


类从被加载到虚拟机内存中开始,到卸载出内存为止,它的整个生命周期包括:加载,验证,准备,解析,初始化,使用,和卸载等7个阶段.其中验证准备解析3个阶段统称为连接,如图:
![](/images/类的生命周期.png)

加载、验证、准备、初始化和卸载这5个阶段的顺序是确定的,类的加载过程必须按照这种顺序进行,但是解析阶段不一定.

虚拟机规范严格规定有且只有5种情况必须对类进行"初始化"阶段,而加载,验证,准备自然需要在此之前开始.

1. 触发new,getstatic(读取静态字段),putstatic(设置静态字段),invokestatic(调用静态方法)字节码指令时
2. 使用反射对类进行调用时
3. 初始化类的时候,发现父类没初始化,则触发父类初始化
4. 虚拟机启动时,用户指定一个要执行的主类,虚拟机会先初始化这个主类.
5. 当使用JDK1.7的动态语言支持时,如果一个java.lang.invoke.MethodHandle实例最后的解析结果REF_getStatic、REF_putStatic、REF_invokeStatic的方法句柄,并且这个方法句柄所对应的类没有进行过初始化,则需要先触发其初始化。

被动引用不会触发初始化,比如通过子类来调用父类的静态引用:
```
public class SuperClass{
	static{
		System.out.println("SuperClass init!");
	}
	public static int value=123;
}

public class SubClass extends SuperClass{
	static{
		System.out.println("SubClass init!");
	}
}
/**
*非主动使用类字段演示
**/
public class NotInitialization{
	public static void main(String[]args){
		System.out.println(SubClass.value);
	}
}
```
该类不会初始化SubClass的初始化.

# 类加载的过程

## 加载
加载是类加载的一个过程.
加载阶段,虚拟机要完成以下事情:
1. 通过一个类的权限定名来获取定义此类的二进制字节流
2. 将这个字节流所代表的静态存储结构转换为方法区的运行时数据结构
3. 在内存中生成一个代表这个类的java.lang.Class对象,作为方法区这个类的各种数据的访问入口.

获取二进制字节流,可以多种方式获取,比如jar包内,网络上,文件中等.
开发人员可以通过自定义的类加载器去控制字节流的获取方式,即重写一个类加载器的loadClass()方法.
Class对象比较特殊,它虽然是对象,但是存储在方法区里面.

## 验证
验证是链接阶段的第一步,这一阶段的目的是为了确保Class文件的字节流中包含的信息符合当前虚拟机要求.
虚拟机如果不检查输入的字节流,对其完全信任的话,很可能会因为载入了有害字节流而导致系统崩溃,所以验证是虚拟机对自身保护的一项重要工作.

如果验证到输入的字节流不符合Class文件格式的约束,虚拟机就应抛出一个java.lang.VerifyError异常或其子类异常.
验证阶段大致完成下面4个阶段的校验动作:文件格式验证、元数据验证、字节码验证、符号引用验证。
文件格式验证阶段,验证字节流是否符合Class文件格式规范,如是否以0xCAFFBABE开头,主次版本号是否符合范围...
元数据验证,该类父类是否继承了不被允许继承的类,如果该类不是抽象类,是否实现了其父类要求实现的所有方法等..
字节码验证,确定语义合法性,保证被校验类不会做出危害虚拟机安全的事件.
符号引用验证,验证符号引用的正确性,如符号引用中通过字符串权限定名是否能找到对应的类,符号引用中的类,字段,方法的访问性(private,protected,public)是否可被当前类访问.

## 准备
准备阶段是正式为类变量分配内存并设置类变量初始值的阶段,这些变量所使用的内存都将在方法区中进行分配。
这个时候内存分配的仅包括被static修饰的变量,而不包括实例变量.
对于分配的static变量,其只是零值,如:`public static int value = 123`,那么准备阶段过后初始值为0.

## 解析
解析阶段是虚拟机将常量池内的符号引用替换为直接引用的过程

## 初始化
到了初始化阶段,才真正开始执行类中定义的Java程序代码.

# 类加载器

##　类与类加载器
对于任意一个类,都需要由加载它的类加载器和这个类本身一同确立其在Java虚拟机中的唯一性,每一个类加载器,都拥有一个独立的类名称空间．这句话可以表达得更通俗一些:比较两个类是否“相等”,只有在这两个类是由同一个类加载器加载的前提下才有意义,否则,即使这两个类来源于同一个Class文件,被同一个虚拟机加载,只要加载它们的类加载器不同,那这两个类就必定不相等。

这里所指的“相等”,包括代表类的Class对象的equals()方法、isAssignableFrom()方法、isInstance()方法的返回结果,也包括使用instanceof关键字做对象所属关系判定等情况。

## 双亲委派模型
从虚拟机角度来说，类加载器有两种，一种是Bootstrap ClassLoader,这个类加载器由C++语言实现,是虚拟机自身一部分,另一种是所有其他类加载器,由Java实现,独立于虚拟机外部,并继承于抽象类java.lang.ClassLoader.

而Java开发人员角度来说,类加载器分三种:启动 类加载器(Bootstrap ClassLoader),扩展 类加载器(Extension ClassLoader),应用程序 类加载器(Application ClassLoader)等.
扩展类加载器:负责加载<JAVA_HOME>\lib\ext目录中的类.
应用程序类加载器:通过getSystemClassLoader()取得,所以一般也称为系统类加载器,负责加载用户类路径上所指定的类库.


如果有必须还可以加入自己定义的类加载器,其加载器之间的关系如:
![](/images/类加载器.png)

图上所展示的类加载器之间的关系,被称为类加载器的双亲委派模型,双亲委派模型要求除了顶层的启动类加载器外,其他的类加载器都应该有自己的父类加载器.这里的父类加载器不是通过继承实现的,而是通过使用组合关系来实现的.

双亲委派模型的工作过程是:如果一个类加载器收到了类加载的请求,它首先不会自己去尝试加载这个类,而是把这个请求委派给**父类加载器**去完成,每一个层次的类加载器都是如此,因此所有的加载请求最终都应该传送到**顶层**的启动类加载器中,只有当父加载器反馈自己无法完成这个加载请求(它的搜索范围中没有找到所需的类)时,子加载器才会尝试自己去加载。

使用双亲委派模型来组织类加载器之间的关系,有一个显而易见的好处就是Java类随着它的类加载器一起具备了一种带有优先级的层次关系。例如类java.lang.Object,它存放在rt.jar之中,无论哪一个类加载器要加载这个类,最终都是委派给处于模型最顶端的启动类加载器进行加载,因此Object类在程序的各种类加载器环境中都是同一个类.


# JVM字节码执行引擎

## 运行时栈帧结构
栈帧是一种存储方法的局部变量表,操作数栈,动态连接和方法返回地址等信息.每一个方法从调用开始至执行完成的过程,都对应着一个栈帧在虚拟机栈里面从入栈到出栈的过程。


对于执行引擎来说,在活动线程中,只有位于栈顶的栈帧才是有效的,称为当前栈帧(Current StackFrame),与这个栈帧相关联的方法称为当前方法(Current Method)。
![](/images/栈帧的概念结构.png)

### 局部变量表
局部变量表的容量以变量槽(Variable Slot)为最小单位,每个slot都应该能存储boolean,byte,char,short,int,float,reference或returnAddress类型的数据,这8中数据类型,都可以使用32位或更小的物理内存存放,但并不代表slot就占用32位内存空间,slot可以随着处理器,操作系统或虚拟机的不同而变化.

为了尽可能节省栈帧空间,局部变量表中的Slot是可以重用的.在某些情况下,slot的复用会影响到系统的垃圾收集行为.如:
```
public static void main(String[]args)(){
	byte[]placeholder=new byte[64*1024*1024];
	System.gc();
}
```
gc后并不会回收64M数据.没有回收placeholder所占的内存能说得过去,因为在执行System.gc()时,变量placeholder还处于作用域之内,虚拟机自然不敢回收placeholder的内存。但是看下面代码:
```
public static void main(String[]args)(){
	{
		byte[]placeholder=new byte[64*1024*1024];
	}
	System.gc();
}
```

这个时候,执行gc后,依然没有释放64M内存.在看下面代码:
```
public static void main(String[]args)(){
	{
	byte[]placeholder=new byte[64*1024*1024];
	}
	int a=0;
	System.gc();
}
```
执行后,发现64M内存被释放了.
被回收的原因是:局部变量表中
的Slot是否还存有关于placeholder数组对象的引用。第一次修改中,代码虽然已经离开了placeholder的作用域,但在此之后,没有任何对局部变量表的读写操作,placeholder原本所占用的Slot还没有被其他变量所复用,所以作为GC Roots一部分的局部变量表仍然保持着对它的关联。这种关联没有被及时打断,在绝大部分情况下影响都很轻微。但如果遇到一个方法,其后面的代码有一些耗时很长的操作,而前面又定义了占用了大量内存、实际上已经不会再使用的变量,手动将其设置为null值(用来代替那句int a=0,把变量对应的局部变量表Slot清空)便不见得是一个绝对无意义的操作.

虽然如此,但是不能把赋null值当做编码规则来推广.

## 分派
分派调用过程将会揭示多态性特征的一些最基本的体现.

### 静态分派
如下代码:
```
public class StaticDispatch{
	static abstract class Human{
	}

	static class Man extends Human{
	}

	static class Woman extends Human{
	}

	public void sayHello(Human guy){
		System.out.println("hello,guy!");
	}

	public void sayHello(Man guy){
		System.out.println("hello,gentleman!");
	}

	public void sayHello(Woman guy){
		System.out.println("hello,lady!");
	}
	public static void main(String[]args){
		Human man=new Man();
		Human woman=new Woman();
		StaticDispatch sr=new StaticDispatch();
		sr.sayHello(man);
		sr.sayHello(woman);
	}
}
```
执行结果是:
hello,guy!
hello,guy!

对于`Human man = new Man()`,左部`Human man`被称为静态类型,在编译时期不可变,只能在运行时期发生变化,而右部`new Man()`是实际类型只能在运行时期才能确定,在编译时候,编译器并不知道一个静态类型的实际类型是什么.所以对于上述代码其使用哪个重载,完全取决于传入对象的静态类型.因此,在编译时期,javac编译器会根据参数的静态类型决定使用哪个重载版本.

所有依赖静态类型来定位方法执行版本的分派动作被称为静态分派.静态分派的典型应用是方法重载,静态分派发生在编译阶段.

### 动态分派
动态分派和重写有着密切的关系,首先查看代码:
```
public class DynamicDispatch{
	static abstract class Human{
		protected abstract void sayHello();
	}

	static class Man extends Human{
		@Override
		protected void sayHello(){
			System.out.println("man say hello");
		}
	}

	static class Woman extends Human{
		@Override
		protected void sayHello(){
			System.out.println("woman say hello");
		}
	}

	public static void main(String[]args){
		Human man=new Man();
		Human woman=new Woman();
		man.sayHello();
		woman.sayHello();
		man=new Woman();
		man.sayHello();
	}
}
```
运行结果:
man say hello
woman say hello
woman say hello


其过程是,找到实际对象的方法后返回其引用,如果没有找到方法,则在继承中从下到上依次查找,直到找到为止.
将在运行期根据实际类型确定方法执行版本的分派过程称为动态分派.

### 虚拟机动态分派
```
public class Dispatch{
	static class QQ{}
	static class_360{}

	public static class Father{
		public void hardChoice(QQ arg){
			System.out.println("father choose qq");
		}
		public void hardChoice(_360 arg){
			System.out.println("father choose 360");
		}
	}

	public static class Son extends Father{
		public void hardChoice(QQ arg){
			System.out.println("son choose qq");
		}
		public void hardChoice(_360 arg){
			System.out.println("son choose 360");
		}
	}

	public static void main(String[]args){
		Father father=new Father();
		Father son=new Son();
		father.hardChoice(new_360());
		son.hardChoice(new QQ());
	}
}
```

基于性能考虑,虚拟机的实现是建立一个虚方法表,使用虚方法表索引来代替元数据查找以提高性能.如下图:
![](/images/方法表结构.png)

虚方法表中存放着各个方法的实际入口地址。如果某个方法在子类中没有被重写,那子类的虚方法表里面的地址入口和父类相同方法的地址入口是一致的,都指向父类的实现入口。如果子类中重写了这个方法,子类方法表中的地址将会替换为指向子类实现版本的入口地址。如上图中,Son重写了来自Father的全部方法,因此Son的方法表没有指向Father类型数据的箭头。但是Son和Father都没有重写来自Object的方法,所以它们的方法表中所有从Object继承来的方法都指向了Object的数据类型。

## 动态类型语言
动态类型语言的关键特征是它的类型检查的主体过程是在运行期而不是编译器.而Java和C++等就是最常用的静态类型语言.

静态语言显著特征就是编译期间进行严格类型检查,而动态语言只是在运行期间确定类型.

## java.lang.invoke
在Java中无法传递函数引用,而在C/C++可以传递指针来代表函数,对于动态语言,也可以将函数当一个参数传递.
不过在拥有MethodHandler之后,Java语言也可以拥有类似函数指针或者委托的方法别名的工具了.如:
```
public class MethodHandleTest{
	static class ClassA{
		public void println(String s){
			System.out.println(s);
		}
	}

	public static void main(String[]args)throws Throwable{
		Object obj=System.currentTimeMillis()%2==0?System.out:new ClassA();
		/*无论obj最终是哪个实现类,下面这句都能正确调用到println方法
		getPrintlnMH(obj).invokeExact("icyfenix");
	}

	private static MethodHandle getPrintlnMH(Object reveiver)throws Throwable{
		/*MethodType:代表“方法类型”,包含了方法的返回值(methodType()的第一个参数)和具体参数(methodType()第二个及以后的参数)*/
		MethodType mt=MethodType.methodType(void.class,String.class);
		/*lookup()方法来自于MethodHandles.lookup,这句的作用是在指定类中查找符合给定的方法名称、方法类型,并且符合调用权限的方法句柄*/
		/*因为这里调用的是一个虚方法,按照Java语言的规则,方法第一个参数是隐式的,代表该方法的接收者,也即是this指向的对象,这个参数以前是放在参数列表中进行传递的,而现在提供了bindTo()方法来完
		成这件事情*/
		return lookup().findVirtual(reveiver.getClass(),"println",mt).bindTo(reveiver);
	}
}
```
以上方法测试的时候并没有成功.= =

一般来说,这个方法可以做的事情,使用反射也可以做,但是反射是重量级,而MethodHandler是轻量级.

# 泛型与类型的擦除
Java语言中的泛型则不一样,它只在程序源码中存在,在编译后的字节码文件中,就已经替换为原来的原生类型(Raw Type,也称为裸类型)了,并且在相应的地方插入了强制转型代码,因此,对于**运行期**的Java语言来说,ArrayList<int>与ArrayList<String>就是**同一个类**,Java语言中的泛型实现方法称为类型擦除,基于这种方法实现的泛型称为伪泛型.

泛型擦除前:
```
public static void main(String[]args){
	Map<String,String>map=new HashMap<String,String>();
	map.put("hello","你好");
	map.put("how are you?","吃了没?");
	System.out.println(map.get("hello"));
	System.out.println(map.get("how are you?"));
}
```
泛型擦除后:
```
public static void main(String[]args){
	Map map=new HashMap();
	map.put("hello","你好");
	map.put("how are you?","吃了没?");
	System.out.println((String)map.get("hello"));
	System.out.println((String)map.get("how are you?"));
}
```


考虑下方代码:
```
public static void method(List<Object> l){
        
}

public static void method(List<String> l){

}

```

运行期间,两个泛型被擦除,所以这两个方法其实是一个方法,所有不会通过.

但是对于下方代码
```
public static Object method(List<Object> l){

}

public static String method(List<String> l){

}

```
使用sun的编译器会通过,因为返回值不一样,但是其他编译器就会造成拒绝.

# 自动装箱,拆箱与遍历循环
编译前:
```
public static void main(String[]args){
	List<Integer>list=Arrays.asList(1,2,3,4);
	//如果在JDK 1.7中,还有另外一颗语法糖 [1]
	//能让上面这句代码进一步简写成List<Integer>list=[1,2,3,4];
	int sum=0;
	for(int i:list){
	sum+=i;
	}
	System.out.println(sum);
}
```
编译后:
```
public static void main(String[]args){
	List list=Arrays.asList(new Integer[]{
	Integer.valueOf(1),
	Integer.valueOf(2),
	Integer.valueOf(3),
	Integer.valueOf(4)});
	int sum=0;
	for(Iterator localIterator=list.iterator();localIterator.hasNext();){
	int i=((Integer)localIterator.next()).intValue();
	sum+=i;
	}
	System.out.println(sum);
}
```
程序
```
Integer a=1;
Integer b=2;
Integer c=3;
Integer d=3;
Integer e=321;
Integer f=321;
Long g=3L;
System.out.println(c==d);
System.out.println(e==f);
System.out.println(c==(a+b));
System.out.println(c.equals(a+b));
System.out.println(g==(a+b));
System.out.println(g.equals(a+b));
```

程序运行结果是:
```
true
false
true
true
true
false
```

反编译结果是:
```
Integer a = Integer.valueOf(1);
Integer b = Integer.valueOf(2);
Integer c = Integer.valueOf(3);
Integer d = Integer.valueOf(3);
Integer e = Integer.valueOf(321);
Integer f = Integer.valueOf(321);
Long g = Long.valueOf(3L);
System.out.println(c == d);
System.out.println(e == f);
System.out.println(c.intValue() == a.intValue() + b.intValue());
System.out.println(c.equals(Integer.valueOf(a.intValue() + b.intValue())));
System.out.println(g.longValue() == (long)(a.intValue() + b.intValue()));
System.out.println(g.equals(Integer.valueOf(a.intValue() + b.intValue())));
```

对于c==d来说,其输出值为true,表示其指向同一个对象,而e==f则指向不同的对象,其实查看源码可以知道:
```
public static Integer valueOf(int i) {
        if (i >= IntegerCache.low && i <= IntegerCache.high)
            return IntegerCache.cache[i + (-IntegerCache.low)];
        return new Integer(i);
    }
```
其实很多基本的装箱类型,反编译后,都是通过valueOf()来赋值的,大可看下对应的源码,比如Boolean的valueOf源码会不论true还是false都会返回同一个对象.


表示[-128,127]的数字会直接返回IntegerCache缓存中的数,而不在这个范围内的数,会直接通过new来创建新的对象.

如果是a+b的相加操作,其最终的返回值是int,所以c = (a+b)其实是int值之间的比较.
对于g = (a+b)而言,会将a+b所得的值转换为long值之后才会进行比较,可以大胆的猜想,其短字节的值会向长字节值进行转换.
# 解释器和编译器
Java中是混合模型,有解释器也有编译器(JIT 即时编译).那么什么是解释器什么是编译器.
把源代码编译成和本地机器平台相关的机器语言，叫即时编译,另一种是编译成一种中间的字节码，与机器平台无关的，这种也是常用的，执行的时候还要在JVM上解释运行,这叫解释型的.

为了提高热点代码的执行效率,在运行时,虚拟机将会把这些代码编译成与本地平台相关的机器码,并进行各种层次的优化,完成这个任务的编译器称为即时编译器.

当程序需要迅速启动和执行的时候,解释器可以首先发挥作用,省去编译的时间,立即执行。在程序运行后,随着时间的推移,编译器逐渐发挥作用,把越来越多的代码编译成本地代码之后,可以获取更高的执行效率。

在运行过程中会被即时编译器编译的“热点代码”有两类,即:被多次调用的方法,被多次执行的循环体。

判断一段代码是不是热点代码,是不是需要触发即时编译,这样的行为称为热点探测(Hot Spot Detection),其实进行热点探测并不一定要知道方法具体被调用了多少次,目前主要的热点探测判定方式有两种 ,分别如下:
基于采样的热点探测:采用这种方法的虚拟机会**周期性地检查各个线程的栈顶**,如果发现某个(或某些)方法经常出现在栈顶,那这个方法就是“热点方法”。基于采样的热点探测的好处是实现简单、高效,还可以很容易地获取方法调用关系(将调用堆栈展开即可),缺点是很难精确地确认一个方法的热度,容易因为受到线程阻塞或别的外界因素的影响而扰乱热点探测。

基于计数器的热点探测(Counter Based Hot Spot Detection):采用这种方法的虚拟机会为每个方法(甚至是代码块)**建立计数器**,统计方法的执行次数,如果执行次数超过一定的**阈值**就认为它是“热点方法”。这种统计方法实现起来麻烦一些,需要为每个方法建立并维护计数器,而且不能直接获取到方法的调用关系,但是它的统计结果相对来说更加精确和严谨。

HotSpot虚拟机中使用的就是第二种


# Java 内存模型
首先要知道的概念是,处理器即CPU用来运算的,其运算速度和对内存读写的速度是有一定的关联的,如果对内存读写速度很慢,必然会拖慢CPU的运算速度,所以一般都会设立一个高速缓存来减轻I/O操作的压力.但是如果有多个处理器,每个处理器和内存间都存在一个高速缓存,那么这种情况,在同步数据的时候,将可能导致各自的缓冲数据不一样,这个时候就出现了问题,如下图:
![](/images/CPU内存.png)

Java虚拟机规范中试图定义一种Java内存模型 (Java Memory Model,JMM)来屏蔽掉各种硬件和操作系统的内存访问差异,以实现让Java程序在各种平台下都能达到一致的内存访问效果。

Java内存模型规定了所有的变量(实例字段,静态字段,构成数组对象的元素,不包括局部变量和方法参数,后者是线程私有的)都存储在主内存(Main Memory)中(此处的主内存与介绍物理硬件时的主内存名字一样,两者也可以互相类比,但此处仅是虚拟机内存的一部分)。每条线程还有自己的工作内存(Working Memory,可与高速缓存类比),线程的工作内存中保存了被该线程使用到的变量的主内存副本拷贝,线程对变量的所有操作(读取、赋值等)都必须在工作内存中进行,而不能直接读写主内存中的变量.不同的线程之间也无法直接访问对方工作内存中的变量,线程间变量值的传递均需要通过主内存来完成,线程、主内存、工作内存三者的交互关系如下图所示:
![](/images/线程工作内存主内存.png)

对于拷贝副本来说,如“假设线程中访问一个10MB的对象,也会把这10MB的内存复制一份拷贝出来吗?”,事实上并不会如此,这个对象的引用、对象中某个在线程访问到的字段是有可能存在拷贝的,但不会有虚拟机实现成把整个对象拷贝A一次.


## 内存间交互操作
关于主内存与工作内存之间具体的交互协议,即一个变量如何从主内存拷贝到工作内存、如何从工作内存同步回主内存之类的实现细节,Java内存模型中定义了以下8种操作来完成,虚拟机实现时必须保证下面提及的每一种操作都是原子的、不可再分的.
原子就是要么都执行要么都不执行.对于Java中的一些操作,如果操作可被中断,那么就不是原子的,比如`a = b`,会先读取b的值,然后做赋值操作,这个步骤分两部,在第二步的时候,可能会中断,也就是说操作可能不成功,所以这种操作不是原子的.

* lock(锁定):作用于主内存的变量,它把一个变量标识为一条线程独占的状态。
* unlock(解锁):作用于主内存的变量,它把一个处于锁定状态的变量释放出来,释放后的变量才可以被其他线程锁定。
* read(读取):作用于主内存的变量,它把一个变量的值从主内存传输到线程的工作内存中,以便随后的load动作使用。
* load(载入):作用于工作内存的变量,它把read操作从主内存中得到的变量值放入工作内存的变量副本中。
* use(使用):作用于工作内存的变量,它把工作内存中一个变量的值传递给执行引擎,每当虚拟机遇到一个需要使用到变量的值的字节码指令时将会执行这个操作。
* assign(赋值):作用于工作内存的变量,它把一个从执行引擎接收到的值赋给工作内存的变量,每当虚拟机遇到一个给变量赋值的字节码指令时执行这个操作。
* store(存储):作用于工作内存的变量,它把工作内存中一个变量的值传送到主内存中,以便随后的write操作使用。
* write(写入):作用于主内存的变量,它把store操作从工作内存中得到的变量的值放入主内存的变量中。


如果要把一个变量从主内存复制到工作内存,那就要顺序地执行read和load操作,如果要把变量从工作内存同步回主内存,就要顺序地执行store和write操作。注意,Java内存模型只要求上述两个操作必须按顺序执行,而没有保证是连续执行。也就是说,read与load之间、store与write之间是可插入其他指令的,如对主内存中的变量a、b进行访问时,一种可能出现顺序是read a、read b、load b、load a。除此之外,Java内存模型还规定了在执行上述8种基本操作时必须满足如下规则:
* 不允许read和load、store和write操作之一单独出现,即不允许一个变量从主内存读取了但工作内存不接受,或者从工作内存发起回写了但主内存不接受的情况出现。
* 不允许一个线程丢弃它的最近的assign操作,即变量在工作内存中改变了之后必须把该变化同步回主内存。
* 不允许一个线程无原因地(没有发生过任何assign操作)把数据从线程的工作内存同步回主内存中。一个新的变量只能在主内存中“诞生”,不允许在工作内存中直接使用一个未被初始化
* (load或assign)的变量,换句话说,就是对一个变量实施use、store操作之前,必须先执行过了assign和load操作。
* 一个变量在同一个时刻只允许一条线程对其进行lock操作,但lock操作可以被同一条线程重复执行多次,多次执行lock后,只有执行相同次数的unlock操作,变量才会被解锁。
* 如果对一个变量执行lock操作,那将会清空工作内存中此变量的值,在执行引擎使用这个变量前,需要重新执行load或assign操作初始化变量的值。
* 如果一个变量事先没有被lock操作锁定,那就不允许对它执行unlock操作,也不允许去unlock一个被其他线程锁定住的变量。
* 对一个变量执行unlock操作之前,必须先把此变量同步回主内存中(执行store、write操作)。

## volatile特殊规则
当一个变量定义为volatile之后,它将具备两种特性,第一是保证此变量对所有线程的可见性,这里的“可见性”是指当一条线程修改了这个变量的值,新值对于其他线程来说是可以立即得知的。而普通变量不能做到这一点,普通变量的值在线程间传递均需要通过主内存来完成,例如,线程A修改一个普通变量的值,然后向主内存进行回写,另外一条线程B在线程A回写完成了之后再从主内存进行读取操作,新变量值才会对线程B可见。

但是由于Java里面的运算并非原子性操作,导致volatile变量的运算在并发下一样是不安全的.比如下述例子:
```
public class VolatileTest{
	public static volatile int race=0;
	public static void increase(){
	race++;
}
private static final int THREADS_COUNT=20;
public static void main(String[]args){
	Thread[]threads=new Thread[THREADS_COUNT];
	for(int i=0;i<THREADS_COUNT;i++){
		threads[i]=new Thread(new Runnable(){
			@Override
			public void run(){
				for(int i=0;i<10000;i++){
					increase();
				}
			}
		});
		threads[i].start();
	}
	//等待所有累加线程都结束
	while(Thread.activeCount()>1)
		Thread.yield();
		System.out.println(race);
	}
}
```
可见其操作的目的是让race变量进行10000次自增操作,但是每次打印处的值,都比10000小,这就说明,volatile变量不能完全实现同步功能.

由于volatile变量只能保证可见性,在不符合以下两条规则的运算场景中,我们仍然要通过加锁(使用synchronized或java.util.concurrent中的原子类)来保证原子性:运算结果并不依赖变量的当前值,或者能够确保只有单一的线程修改变量的值。变量不需要与其他的状态变量共同参与不变约束。

volatile的使用场景:
```
volatile boolean shutdownRequested;
public void shutdown(){
	shutdownRequested=true;
}
public void doWork(){
	while(!shutdownRequested){
		//do stuff
	}
}
```
当shutdown()方法被调用时,能保证所有线程中执行的doWork()方法都立即停下来。

volitale还有一个语义作用是禁止指令重排序优化
在代码中位于有volitale修饰的变量,在赋值后,会有一个汇编指令lock,这个操作相当于内存屏障.
内存屏障是指指令重排序时,不能把后面的指令重排序到内存屏障之前的位置.
在有volitale修饰的变量被赋值的时候,会多执行一个lock操作,它的作用是使本CPU的Cache写入内存中,写入动作会引起别的CPU或者别的内核无效其Cache(是已经读入Cache中的此变量无效的意思?),这种操作相当于对Cache中的变量做了一次store和write操作,所以经过这么一个操作可让前面volitale变量的修改对其他CPU立即可见.

指令重排是指CPU采用了允许将多条指令不按程序规定的顺序分开发送给各相应电路单元处理。但并不是说指令任意重
排,但是对于相互有依赖的指令不会重排,比如`a = 1;b = a + 1;`这种指令显然不会被重排.但是对于下面的指令`a=1;b=2`其相互不存在依赖,所以完全可以重排序.在本CPU内,volitale关键字修饰的变量后执行lock操作,会把修改同步到内存中,意味着之前的操作都已经执行完成,所以就形成了指令重排序无法越过内存屏障的效果.含义就是volitale修饰的变量的操作,不会将其排到之前的操作中,也不会将其排到之后的操作中,比如:
```
x=0;//1
y=0;//2
flag=true;//3
x=4;//4
y=4;//5
```
3这条指令,不可能会被插在1,2之前或4,5之后,但是并不保证1,2之间的顺序和4,5之间的顺序,所以volitale只是保证自己当前位置的顺序.

指令重排序一般是在多线程中会遇到问题,在单线程中,指令重拍不会影响结果,但在多线程中就不一定.
比如:
```
//线程1
context = loadContext();
flag = true;

//线程2
while(!flag){
	sleep();
}
doWithContext(context);
```
如果没有被重排序,那么flag = true执行前,会先执行context,也就是说context被成功赋值,这个时候线程2循环终止执行doWithContext.
但是因为冲排序,线程1中的两个操作是完全没有关系的,所以有这么一种情况是flag=true先执行,而context后执行,那么如果flag=true执行完毕后,这个时候线程中断跳到线程2中执行,循环退出后doWithContext中用到context未被成功赋值,就容易出错.所以多线程中指令重排是会导致一些问题的.

解决方式就是使用volitale来禁止指令重排序,在flag上加上volitale关键字,也就是说在执行flag的时候,前边的指令一定已经执行完毕.

## long和double的特殊规则
Java内存模型要求lock、unlock、read、load、assign、use、store、write这8个操作都具有原子性,但是对于64位的数据类型(long和double),在模型中特别定义了一条相对宽松的规定:允许虚拟机将没有被volatile修饰的64位数据的读写操作划分为两次32位的操作来进行,即允许虚拟机实现选择可以不保证64位数据类型的load、store、read和write这4个操作的原子性,这点就是所谓的long和double的非原子性协定.

如果有多个线程共享一个并未声明为volatile的long或double类型的变量,并且同时对它们进行读取和修改操作,那么某些线程可能会读取到一个既非原值,也不是其他线程修改值的代表了“半个变量”的数值。

不过这种读取到“半个变量”的情况非常罕见(在目前商用Java虚拟机中不会出现),因为Java内存模型虽然允许虚拟机不把long和double变量的读写实现成原子操作,但允许虚拟机选择把这些操作实现为具有原子性的操作,而且还“强烈建议”虚拟机这样实现。在实际开发中,目前各种平台下的商用虚拟机几乎都选择把64位数据的读写操作作为原子操作来对待,因此我们在编写代码时一般不需要把用到的long和double变量专门声明为volatile。

## 原子性,可见性,有序性
原子性就是要么都执行,要么都不执行.
可见性就是当一个线程修改了共享变量的值,其他线程能够立即得知这个修改.除了volatile具备可见性之外,synchronized和final也可以实现可见性
有序性指一个线程中,指令执行的结果正确,代码看起来是有序的.而多线程中所有的操作是无序的.Java中提供volatile和synchronized来保证线程之间操作的有序性.

synchronized保证了代码的原子性,可见性,有序性,所以看起来是万能的,原因是被synchronized修饰的锁变量,在同一时刻只允许一条线程对其进行lock操作,这条规则决定了持有同一个锁的的两个同步快只能串行的进入.

## 先行发生规则
程序次序规则:按照控制流顺序执行
管程锁定规则:一个unlock操作先行发生于后面对同一个锁lock操作.(时间上的先后)
volitale变量规则:对一个volatile变量的写操作先行发生于后面对这个变量的读操作(时间上的先后)
线程启动规则:Thread对象的start方法先行发生于此线程的每一个动作.
线程终止规则:线程中的所有操作都先行发生于对此线程的终止检测,我们可以通过Thread.join()方法结束、Thread.isAlive()的返回值等手段检测到线程已经终止执行。
线程中断规则:对线程interrupt()方法的调用先行发生于被中断线程的代码检测到中断事件的发生,可以通过Thread.interrupted()方法检测到是否有中断发生。
对象终结规则:一个对象的初始化完成(构造函数执行结束)先行发生于它的finalize()方法的开始。
传递性:如果操作A先行发生于操作B,操作B先行发生于操作C,那就可以得出操作A先行发生于操作C的结论。

## Java与线程
JDK1.2之前,Java线程的实现通过用户线程实现,而1.2之后,是基于操作系统原生线程模型实现,也就是说,操作系统支持怎样的线程模型,在很大程度上决定了Java虚拟机的线程是怎样的映射的.


### 状态
Java语言定义5种线程状态,在任意时间点,一个线程只能有且只有其中一种状态
New:创建后尚未启动
Runnable:有可能在执行,有可能在等待
Waiting:等待被其他线程显示唤醒,以下方法会让线程处于此状态:没有设置Timeout参数的Object.wait(),没有设置Timeout参数的Thread.join()方法,LockSupport.park()方法.
Time Waiting:等待被唤醒,但到时后被自动唤醒,如Threa.sleep(),Object.wait(Timeout),Thread.join(Timeout),LockSupport.parkNanos(),LockSupport.parkUntil().
Blocked(阻塞):线程被阻塞,等待获取锁.
Terminated(终止):线程已终止

以上状态在遇到特定事件发生后会互相转换:
![](/images/线程状态转换.png)

## 线程安全
按照线程安全的安全程度由强到弱排序:不可变,绝对线程安全,相对线程安全,线程兼容和线程对立.


实现线程安全的同步方式有:互斥同步,非阻塞同步,无同步

互斥同步就是进行线程阻塞实现的同步,如synchronized,ReentrantLock等实现的同步.
JDK 1.6发布之后,人们就发现synchronized与ReentrantLock的性能基本上是完全持平了。虚拟机在未来的性
能改进中肯定也会更加偏向于原生的synchronized,所以还是提倡在synchronized能实现需求的情况下,优先考虑使用synchronized来进行同步。
互斥同步是一种悲观的并发策略.
随着硬件指令集的发展,我们有了另外一个选择:基于冲突检测的乐观并发策略,通俗地说,就是先进行操作,如果没有其他线程争用共享数据,那操作就成功了;如果共享数据有争用,产生了冲突,那就再采取其他的补偿措施(最常见的补偿措施
就是不断地重试,直到成功为止),这种乐观的并发策略的许多实现都不需要把线程挂起,因此这种同步操作称为非阻塞同步
# JVM常见面试题


---
title: Java字节码常用指令
date: 2017-05-26 15:18:25
tags: Java
---
在看多线程有关内容的时候,很多文章经常会拿出字节码的一些指令来讲解,看我晕头晕脑的,于是开始狂补字节码指令方面的内容.参考书籍为:<<实战Java虚拟机>>,不得不说,<<实战Java虚拟机>>这本书写的是真的好.

<!--more-->

Java虚拟机字节码指令多达200余个,这里只列出常用指令.
首先说明指令的第一个字符总是喜欢表示数据类型,i表示int,l为long,f为float,d为double,a为对象引用,如果指令隐含参数,则用`_`来隔开.

# 常量入栈指令

根据类型和入栈内容,可分为const,push,ldc等系列指令

const系列

|指令名称|指令功能|
|-|-|
|aconst_null|将null压入操作数栈|
|iconst_m1|将-1压入操作数栈|
|iconst_x|注意x是某个数(0~5),这里只是拿x当通配符,将x压入栈|
|lconst_0,fconst_0,dconst_0|分别是将int 0,float 0,double 0 压栈|

push,ldc系列

|指令名称|指令功能|
|-|-|
|bipush x|bipush接收8位整数,其中x为8位整数,将x入栈|
|sipush x|x为16位证书,将x入栈|
|ldc p|万能指令,p为8位,但不是实际参数,其是一个引用,指向常量池中某个int,float,String,将指向的内容压入栈|
|ldc_w p2|p2表示16位参数|
|ldw2_w p3|p3指向long,double|


# 局部变量压栈

大体分为`xload`(x为i,l,f,d,a),xload_n(x为i,l,f,d,a. n为0~3),xaload(x为i,l,f,d,a,b,c,s)

其中x表示数据类型为:

|x取值|含义|
|-|-|
|i|int|
|l|long|
|f|float|
|d|double|
|a|对象索引|
|b|byte|
|c|char|
|s|short|

指令:

|指令名称|指令功能|
|-|-|
|xload_n|将第n个局部变量压入操作数栈|
|xload x|将第x个局部变量压入操作数栈|
|xaload|将指定数据类型的数组的某个元素压入操作数栈|

xaload比较特殊,其压入栈的时候,需要操作数栈中栈顶为索引,第二个元素为数组引用a,
比如要压入一个数组元素`s[0]`,那么指令为:
```
aload_2   //将局部变量2位置的数组引用压入操作数栈
iconst_0  //将int 0 压入操作数栈
saload    //取栈中元素组成s[0],并压入操作数栈
```

# 出栈装入局部变量表指令
分为xstore_n,xastore,xstore,其用法和load基本一样.

|指令名称|指令功能|
|-|-|
|xstore_n|表示将操作数栈弹出一个x类型的数,并把其赋值给局部变量位置0处的引用.|
|xastore |针对数组操作,与xaload用法一样,也需要栈中两个辅助位置.|
|xstore x|弹出栈中数据,并赋值给局部变量x位置|

# 通用型操作

|指令名称|指令功能|
|-|-|
|dup|duplicate复制,将栈顶元素复制一份,并压入栈顶|
|pop|把元素从栈顶弹出,直接废弃|
|dup2|复制栈顶两个字长,即8个字节数据|
|pop2|丢弃栈顶8字节数据|

以为`Object obj = new Object`举例,其编译的字节码为:
```
new #3            //创建#3指定的数据类型对象,并压入栈
dup               //复制一份刚创建的对象,并压入栈
invokespecial #16 //调用该对象的初始化方法,即构造方法,并弹出栈
astore_2          //将该对象赋值给局部变量表位置2
```

# 类型转换指令

|指令名称|指令功能|
|-|-|
|x2y|栈顶x数据类型的数据转换为y类型的数据|

其中x,y表示的类型有i,l,c,b,f,d,s即前边的表格表示的类型.

# 运算指令

* 加法指令:`iadd`,`ladd`,`fadd`,`dadd`
* 减法指令:`isub`,`lsub`,`fsub`,`dsub`
* 乘法指令:`imul`,`lmul`,`fmul`,`dmul`
* 除法指令:`idiv`,`ldiv`,`fdiv`,`ddiv`
* 取余指令:`irem`,`lrem`,`frem`,`drem`
* 数值取反:`ineg`,`lneg`,`fneg`,`dneg`
* 自增指令:`iinc`
* 位运算->位移指令:`ishl`,`ishr`,`iushr`,`lshl`,`lshr`,`lushr`
* 位预算->或,与,异或:`ior lor`,`iand land`,`ixor lxor`

# 对象/数组操作指令

## 创建数组/对象
创建指令有:new,newarray,anewarray,multianewarray

|指令名称|指令功能|
|-|-|
|new #p|创建常量池p指向的数据类型对象,并将该对象压入栈|
|newarray x|x表示基本数据类型,如int,float.创建基本数据类型的数组,创建个数为栈顶值|
|anewarray #p|创常量池p指向的数据类型数组,同样个数为栈顶值|
|multianewarray #p x|创建多维数组,p指定数据类型,x指定维数,每维具体个数通过栈顶指定|

## 字段访问
字段访问指令用于访问类或者对象的字段,主要有:getfield,putfield,getstatic,putstatic

|指令名称|指令功能|
|-|-|
|getfield #p|用于获取实例对象的字段|
|putfield||
|getstatic #p|获取p指向的对象或值(静态字段),并将其压入栈|
|putstatic||

## 类型检查指令

|指令名称|指令功能|
|-|-|
|instanceof #p|检查栈顶对象是否为p指向的数据类型,并将结果压栈,对应java instanceof|
|checkcast #p|检查当前栈顶对象是否可以强转为p指向的数据类型,该指令不压入任何数据,如何转换失败,直接抛出ClassCastException|

# 比较控制指令

* 比较指令:比较两个元素大小,并将比较结果入栈:`dcmpg`,`dcmpl`,`fcmpg`,`fcmpl`,`lcmp`
* 条件转移指令:`ifeq`,`iflt`,`ifle`,`ifne`,`ifgt`,`ifge`,`ifnull`,`ifnotnull`
* 比较条件转移指令,将上述两个指令结合:`if_icmpeq`,`if_icmpne`,`if_icmplt`,`if_icmpgt`...

# switch

switch会生成两种指令,`tableswitch`,`lookupswitch`,其中`tableswitch`处理的是连续的case值,`lookupswitch`处理的是分散的case值,需要和case一一对比.所以`tableswitch`在效率上是比`lookupswitch`高的

# 函数调用与返回

|指令名称|指令功能|
|-|-|
|invokevirtual|虚函数调用,调用对象的实例方法,根据对象实际类型,调用其方法,联想多态,Java中常用调用方式|
|invokeinterface|接口方法调用|
|invokespecial|调用构造方法,私有方法,父类方法等特殊方法|
|invokestatic|调用类的静态方法|
|invokedynamic|调用动态绑定方法|
|xreturn|返回x类型|
|return|返回void|

# 同步控制

|指令名称|指令功能|
|-|-|
|monitorenter|临界区进入,如果当前对象监视器计数器为0,则允许进入,如果为1,判断持有线程的监视器是否为自己,如果是则进入,否则进行等待.|
|monitorexit|退出监视器|


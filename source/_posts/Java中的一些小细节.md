---
title: Java中的一些小细节
date: 2017-05-05 17:54:41
tags: Java
---

闲来无事,准备再通读一边<<Java核心技术>>,该文章用于记录Java知识的一些小细节.

<!--more-->

1. Java7开始,可以为数字字面量加上下划线,如:`1_000_000`表示1百万,Java编译器会自动去除这些下划线.
2. float类型的数值有一个后缀F,没有后缀F的浮点数默认为double类型.
3. System.out.println(2.0-1.1)打印的结果是0.8999999999999999,原因是二进制表示的浮点数,无法精确表示分数1/10.如果想要在浮点数计算中不含油任何舍入误差,就应该使用BigDecimal类.
4. Java中的char对于一个中文字符,不是一定是16位来表示的,早期Unicode是使用16位表示,但是后来字符数量增多,添加了中文,日文等大量字符后,16位是完全无法表示全部的字符的.比如`𫝆`这个字的Unicode编码为:`\ud86d\udf46`,所以char是不可能存取所有的汉字的,比如上述汉字`𫝆`.可到http://www.qqxiuzi.cn/zh/hanzi-unicode-bianma.php查看汉字Unicode的范围.
5. const是Java保留的关键字,但是未投入使用.
6. 整数除以0抛异常,浮点数除以0得到无穷大或者NaN(0/0).
7. 不必在数学方法名和常量名前添加前缀`Math.`,只要在源文件的顶部加上下面这行代码就可以了`import static java.lang.Math.*`,这是静态导入.
8. 比较两个字符串是否相等,并且不区分大小写,可以使用`equalsIgnoreCase()`方法.
9. case可接受`char`,`byte`,`short`,`int`的常量表达式,或者他们的封箱类型,注意是常量,也就是说必须是final类型的,也可以接受`enum`类型,而在jdk1.7之后,case接受`String`类型,也就是说case共接受10种常量表达式.
10. 在Java中,允许数组的长度为0.如果某个函数返回一个数组,但是返回的结果为空,就可以创建一个长度为0的数组返回,如`new elementType[0]`,但是注意,数组长度为0与null不是一个概念.
11. 看到这个知识可算是明白了给函数传递String类型的变量的时候,在函数中改变该变量的值后,为什么外部并不起作用.比如`String s = "abc"; change(s); sout(s);` ,`change(s)`函数的意义为将传来的参数修改为其他值,但是执行后,s的值并没有改变.Java中方法参数传递有两种,一种是基本类型值传递,一种是引用传递.但是引用传递其意义为对引用进行拷贝然后传递,所以对于传递String类型变量的时候,传递的是String类型的一个拷贝引用,而在内部改变改引用对象的时候,导致该拷贝引用指向了一个新的区域,而原引用并不改变.那么到这就应该理解了,如果企图在函数中改变某个引用的指向值,即重新new一个对象,赋值给该引用,通常会失败,但是通过该引用改变对象的内容,却是可以的.
12. 静态导入,只是可以使用某个类下的静态方法和静态域而已.
13. 还有一种内部类是局部内部类,局部内部类与局部变量的局部是一个意思,比如在某个方法中有一个局部变量,那么这个局部变量是不能被private,public等修饰的,当然对于局部内部类也一样,局部内部类定义在某些方法中,不能被private,public修饰,使用局部内部类的优势是,该方法的作用域之外的任何方法都不能访问它.
14. 局部内部类和匿名内部类访问外部包含他们的外部类的局部变量时,这些变量必须为final形的,其实匿名内部类就是局部内部类的一种形式,只是匿名内部类没有构造器
15. Java中断言机制允许在测试期间向代码中插入一些检查语句.代码发布时,这些插入的检测语句会被自动的移走.使用语法:`assert x >= 0`,或`assert x >= 0 : x`,第一种在不满足条件会抛出AssertionError,第二种则会打印消息.注意Java中assert并不是一个方法.
16. 泛型中<>内部其实是可以用合法的任意符号来表示的,字符串也可以,但是一般来说,E用来表示集合元素类型,K和V来表示关键字和值的类型,T表示任意类型.
17. 泛型中可以通过extends来指定多个限定,比如:<T extends Comparable & Serializable>,表明泛型必须实现两个接口.
18. 泛型中的限定名即使是要实现某一个接口,也不会使用implements,而是extends,比如17条中,Comparable是接口,但是依然使用extends.
19. 如果要指定多个限定,并且限定中有一个类,那么该类必须是限定列表中的第一个,限定列表中至多有一个类.
20. 类型检查instanceof不能用于泛型,如:`a instanceof Pair<String>`是错误的,泛型也不可用于强制类型转换,比如`Pair<String> p = (Pair<String>)a;`是错误的.泛型也不能用于创建数组.
21. 泛型对象不可被实例化,比如:`T.getClass().newInstance`,`new T()`等都是错误的.但是可以通过Class这个类来实例化,比如`public void method(Class<T> c)`->`c.newInstance()`.
22. List<String>和List<Object>没有任何关系,但是List<String>和List<? extends Object>是逻辑上的父子关系.
23. 使用Iterator删除对象的时候,一定要调用next越过刚删除的位置,如:`it.remove(); it.next()`.
24. 调用线程中断方法,只是让线程终止,产生中断异常的原因是线程被阻塞,无法检测中断状态.所以在sleep,或wait上调用interrupt方法时,阻塞调用会被InterruptedException异常中断.
25. Java中InputStream和Reader的区别不仅仅是一个读字节,一个读字符.在最开始的时候,个人理解为,InputStream是一个字节一个自己的读,而Reader是一个字符一个字符的读,虽然读的内容不一样但是其数据应该一样的吧?答案当然是不一样.对于InputStream而言,其读的数据就是原始数据,而Reader而言,因为是读的字符,所以就涉及到字符的编码格式,其根据编码格式的不同,读入的数据就会不一样.所以,如果将一个字节流转换为字符流时,就提供了制定字符编码的构造器.
26. 如果想知道当前jdk支持的字符编码格式,可以通过`Charset.availableCharsets()`来取得默认所有的编码,然后打印即可.
27. RandomAccessFile虽然叫随即访问文件,但是其访问文件内容时,并不是随机的,这个随机的意思是,你可以自己指定访问的位置而不像其他流一样,只能从前读到尾.
28. 读写文件可以通过`Path`和`Files`类来进行,方便快捷.如读入所有字节:`Files.readAllBytes(path)`,向指定文件写入内容:`Files.write(path,bytes)`.Files中的这些方法,可以处理中等长度的文件,如果文件内容比较大,还是应该使用熟知的流来进行处理.
29. 文件复制,移动可以通过`Files.copy(),Files.move()`等方法.文件删除可以通过`Files.deleteIfExists(path)`.创建文件目录,可用:`Files.createDirectories(path)`,创建文件`Files.createFile(path)`等.Files和path结合使用,一些方法其实可以通过`File`这个类来代替.




---
title: Java8 特性学习
date: 2017-04-16 23:55:42
tags: java8
---

Java8新特性已经出了这么久,竟然还不会用,所以抽空学习下这个新特性.学习书籍是<<java8中文>>
<!--more-->

# lambda表达式

## 语法
注意lambda而不是lamada = =.通过`()->{}`构成的表达式,()中传入参数,{}是方法体.
1. 
```
Comparator c = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(o1,o2);
            }
        };
```
可以替换为:
```
Comparator<Integer> c2 = (Integer o1, Integer o2)->o2.compareTo(o1);
```
如果在IDEA中这样写,就会有提示,但不是差错,提示可以替换成下列操作:
```
Comparator<Integer> c2 = Comparator.naturalOrder();
```
不过这里只是说明其用法.
2. 
如果逻辑比较多,可以使用{}包裹:
```
Comparator<Integer> c2 = (Integer o1, Integer o2)->{
            if(o1 < o2)return -1;
            else return 1;
        };
```

3. 如果表达式中参数的类型可以被推到,那么类型可以被省略,如:
```
Comparator<Integer> c2 = (o1,  o2)->{
            if(o1 < o2)return -1;
            else return 1;
        };
```
上边这个表达式,可以推导出o1,o2是Integer类型的,所以忽略了其类型.

4. 如果方法只有一个参数,且参数类型可以被推导,那么甚至可以省略小括号,如:
```
	EventHandler e = new javafx.event.EventHandler() {
		@Override
		public void handle(Event event) {
		    System.out.println("o");
		}
	};
```
可以写为:
```
EventHandler e = event -> System.out.println("e");
```
## 函数式接口
对于只包含一个抽象方法的接口,可以通过lambda表达式来创建该接口的对象,这种接口被称为函数式接口.

你可能奇怪函数式接口必须只有一个抽象方法,难道接口中方法不都是抽象的么?但是Comparator中不仅有抽象方法,还有Object中的一些方法,而这些方法并不是抽象的

比如
```
Comparator<Integer> c2 = (Integer o1, Integer o2)->o2.compareTo(o1);
```
右部就是一个函数式接口.函数式接口的转换是你在Java中使用lambda表达式能做的唯一一件事.

## 方法引用
```
button.setAction(event->System.out.println(event));
```
可以写为:
```
button.setAction(System.out::println);
```
其中`System.out::println`就是一个方法引用,等同于`event->System.out.println(event)`.
`::`操作符将方法名和对象或类的名字分隔开来,以下是三种主要的使用情况:
* 对象::实例方法, 如:`System.out::println`等同于`x->System.out.println(x)`注意的是,这种情况,因为对象已经有了,所以参数只能传入方法中
* 类::静态方法,如:`Math::pow`等同于`(x,y)->Math.pow(x,y)`,注意这种情况也是,因为是静态方法,所以只能传给方法参数
* 类::实例方法,这个方式比较特殊,因为是类的实例方法,这个实例方法只能通过对象来调用,所以传入的第一个参数是作为类的实例存在的,如`String::compareTo`等同于`(x,y)->x.compareTo(y)`.

对于`对象::实例方法`这个方式来说,对象可以传入`this`,`super`等,比如`this::equals`等同于`x->this.equals(x)`.如下示例:
```
class Greeter{
	public void greet(){
		System.out.println("Hello,world!");
	}
}

class ConcurrentGreeter extends Greeter{

	public void greet(){
		Thread t = new Thread(super::greet);
		t.start();
	}
}
```
其中创建线程的时候,传入的Runnable其方法会执行supre.greet()方法.`supre::greet`等同于`()->super.greet()`,又等同于
```
new Runnable(){
	public void run(){
		super.greet();	
	}
}
```
## 构造器引用
`Button::new`相当于`new Button()`但是会调用哪个构造器,还是需要根据语境.
`int[]::new`相当于`x->new int[x]`.

## 变量作用域
如果通过lambda表达式访问外部变量,其访问的变量在lambda中是不可以变化的.
```
public void method(int count){

	new Thread(()->{
		for(int i=0; i<count; i++){
			System.out.println(i);		
		}	
	}).start();
}
```
如上所示,代码是合法的,即使外部变量没有使用final修饰,如果要在代码中改变count的值,将会不合法.
但是对于Java内部类来说,其可以访问外部final修饰的变量,并且不能改变其值,但是有一种巧妙的办法可以改变值,就是声明一个长度为1的数组,比如:`int[] couter = new int[1]`,那么在内部类中,counter是final形的,但是counter[1]却可以改变.

lambda表达式的方法体与嵌套代码块有相同的作用域,lambda表达式中不允许声明与局部变量同名的参数,如:
```
String first="aaa";
Comparator<String> comp = (first,second)->Integer.compare(first.length(),second.length());
```
以上代码是错误的,因为first已被定义了.

当在lambda表达式中使用this关键字时,你会创建该lambda表达式的方法的this参数,比如:
```
public class Application{
	public void doWork(){
		Runnable r = ()->{
			System.out.println(this.toString());
		}
	}
}
```
其中this.toString()会调用Applicatoin对象的toString()方法,而不是Runnable实例的toString()方法.

## 默认方法
提供一个方法,用来将一个函数应用到集合的每个元素上,如:
`list.forEach(System.out::println);`
注意list是一个集合,顶层接口是Collection.在Java8中突然蹦出来了一个forEach方法,这样就使得每个实现了Collection接口的类都要重写forEach,这在Java中是无法被接受的,所以出现了默认方法这种东西.

对于接口中实现的默认方法,通过default进行修饰,比如:
```
interface Person{
	long getId();
	default String getName(){return "John Q. Public";}
}
```
可以看到默认方法是有实现的.那么实现Person接口的实现类,必须实现getId(),但是可以选择不实现getName().
如果某个类实现了两个接口,并且有同名的方法,其中一个是默认方法,另一个不论是否是默认方法,都会导致方法冲突,这个时候就需要使用覆盖来解决,即在该类中重写这个方法,在该方法中调用其中一个接口的方法,如:
```
class Student implements Person,Named{
	public String getName(){
		return Person.super.getName();
	}
}
```
而如果继承了父类且实现了某个接口,父类和接口中出现方法冲突,会使用父类中方法,忽略接口中默认方法.

## 接口中的静态方法
Java8中,可以为接口添加静态方法.

# Stream

## 创建Stream
Java8在Collection接口中新添加Stream方法,可以将任何集合转化为一个Stream.如果是一个数组,可以用静态的Stream.of方法将它转化为一个Stream.
Stream.of方法接口可变长度的参数,如:`Stream<String> song = Stream.of("gently","down","the","stream");`
`Arrays.stream(array,from,to)`将数组的一部分转换为Stream.
要创建一个不含任何元素的Stream,使`Stream.empty方法`.
`Stream.generate(()->"echo")`用来创建无限个"echo"的Stream,但是注意,Stream执行到这的时候,并不会真正去创建无限个"echo".
`Stream.generate(Math::random)`等同于`Stream.generate(()->Math.random())`用来创建无限个随机数.
`Stream<BigInteger> integers = Stream.iterate(BigInteger.ZERO,n->n.add(BigInteger.ONE))`用来创建0,1,2,3...的序列,第一参数是开头的数字,第二个表示后一个数和前一个数的关系,是个接口.


## filter,map和flatMap
filter方法的参数是一个Predicate<T>接口,返回boolean类型,比如过滤数组中比10大的数:
```
Integer[] a = {1,10,1,10,1,10};
Stream<Integer> stream = Arrays.stream(a);
stream.filter(n->n>10);
```
即filter方法是对stream进行过滤

map是对方法中的数据或类型进行转换,
比如将所有字符串转换为小写字母:
```
String[] s = {"ABC","EFG"};
Stream<String> stream = Arrays.stream(s);
stream.map(String::toLowerCase);
```
`stream.map(String::toLowerCase);`等同于`stream.map(ss->ss.toLowCase())`.
将字符串转为第一个字符开头的字符数组:
```
Stream<Character> cs = stream.map(n->n.charAt(0));
```
上面例子可以看到,map可以转换数据,以及数据类型.

flatMap方法是展开流,比如下面方法
```
public static Stream<Character> characterStream(String s){
	List<Character> result = new ArrayList<>();
	for(char c:s.toCharArray()) result.add(c);
	return resutl.stream();
}
```
那么对于上面的函数,characterStream("boat")返回的就是['b','o','a','t']的流对象,但是下面的函数:
```
Stream<Stream<Character>> letters = stream.map(s->characterStream(w));
```
这个流返回的就是一个包括多个流的流,因为map中是将每个字符串转换为对应的类型,原来的字符串数组在转换后,所有的字符串变成了流对象,那么最终获取的对象就是流数组.

有点绕口,比如stream是["abc","efg"]这样的流对象,那么经过转换后变为[ ['a','b','c'] , ['e','f','g'] ]这样的流对象.该流对象中的包含多个其他流对象.
但是如果我想要将所得到的流对象进行展开,比如["abc","efg"]中每个字符串都转换为数组后,进行展开,变成['a','b','c','e','f','g']的样式,这样应该怎么操作,这个时候就用到了flagMap.
那么以上代码就应该改为:
```
Stream<Character> letters = stream.flagMap(s.->characterStream(w));
```
可以看出,flagMap是针对流对象,对每个流再进行展开操作.

## 提取子流和组合流
Stream.limit(n)会返回一个包含n个元素的新流(如果原始长度小于n,则返回原始流),这个方法适用于裁剪指定长度的流,比如之前的Stream.generate来生成随机数的流,其生成的流是无限的,那么裁剪成具体个数可以写为:
```
Stream<Double> randoms = Stream.generate(Math::random).limit(100);
```
Stream.skip(n)则会抛弃前面n个元素.注意和数组下标不同,skip(1)就表示抛弃第一个元素,即下标为0的元素,skip(0)不抛弃任何元素.

Stream.concat(stream1,stream2)将两个流对象合并,第一个流的长度不能是无限的,否则第二个流永远没机会添加到第一个流后面.

> peek方法会产生一个与原始流具有相同元素的流,但是每次获取一个元素时,都会调用一个函数,方便调试,如:
```
Object[] powers = Stream.iterate(1,i->i*2)
                .peek(e-> System.out.println(e))
                .limit(10).toArray();
```
在生成数组的时候,会将每个数进行打印.
这里有点要注意:`Stream.iterate(1,i->i*2).peek(e-> System.out.println(e)).limit(10);`这样返回的流对象,是不会打印任何东西的,因为并没有生成数组,所以到这就应该明白,流对象中的内容并不是实际存在的.

## 有状态的转换
什么叫有状态的转换?在前面的流的操作都是无状态的转换,因为每转换一个对象,都不关心之前的对象是什么状态,而对于有状态的转换会记录前边的状态,比如要将一个数组返回一个无重复的数组,那么每返回一个数,都要关心前边的数中有没有相同的数,这就是有状态的转换.

Java8提供了有状态的转换,比如distinct方法会根据原始流中的元素返回一个具有相同顺序,但是无重复元素的新流,显然,该流必须记住之前的已读取的元素.
如
```
Stream<String> uniqueWords = Stream.of("lili","lili","lili").distinct();
```
操作后只返回一个有`lili`的流对象.

当然流中也提供了排序方法.Java8中提供了多个sorted方法,其中一个用于其中元素实现了Comparable接口的流,另一个接受Comparator对象.其实跟Collections中的排序意义差不多.
比如第一种:
```
Integer[] i = {1,2,3,4,5,6,7,8,9,10,2,3,4,5,6,7};
Arrays.stream(i).sorted().peek(e-> System.out.println(e)).toArray();
```
不用传参数即可进行排序,其排序使用的是Integer默认的排序的规则.
而第二种方式,传入的是Comparator:
```
String[] s = {"aaa","bbbb","ccccc"};
Arrays.stream(s).sorted(Comparator.comparing(String::length).reverse());
```
其中Comparator.comparing返回一个Comparator,reverse()同样返回一个Comparator.
还要注意一点,Collections.sort()方法是对原来的集合进行排序,而Stream.sort()是返回一个排序的流,对原始集合没有影响.

## 简单的聚合方法
聚合方法都是终止操作,当使用了终止操作后,就不能再应用其他操作了.这个不能有其他操作的意思不是在后边跟上.xxx()这样的操作,而是在以后的任何地方,都不能对stream有其他操作了
比如:
```
Stream<Integer> stream = Arrays.stream(i);
boolean first = stream.anyMatch(j->j==5);
Optional<Integer> o = stream.filter(j -> j>5).findFirst();
```
这个代码将抛出异常,因为第二行已经使用聚合方法,所以第三行是不被允许的.


比如`count()`方法就是聚合方法,该方法用于返回流对象中元素的个数.
另外两个聚合方法是`max`和`min`,分别分会流中最大值和最小值,需要注意的是,这个方法会返回一个Optional<T>值,它可能会封装返回值,也可能表示没有返回(流为空).
示例:
```
Optional<Integer> largest = stream.max(Integer::compareTo);
if(largest.isPresent()){
    System.out.println(largest.get());
}
```

`findFrist`用来返回非空集合中的第一个值,通常与filter方法结合起来用:
```
Optional<Integer> first = stream.filter(j->j>5).findFirst();
```

而如果找到所有匹配的元素中符合条件的元素(而不是第一个),可以使用`findAny`方法,这个方法在并行的时候十分有效,因为只要在任何片段中发现了第一个匹配的元素都会结束整个计算.
```
Optional<Integer> first = stream.filter(j->j>5).findAny();
```

如果只希望知道流中是否含有匹配元素,可以使用`anyMatch()`方法,该方法接受一个Predicate接口参数,所以不需要使用filter方法:
```
boolean hasFive = stream.anyMatch(j->j==5);
```
还有两个方法`allMatch`在所有元素匹配的时候返回true,`noneMatch`在没有元素匹配的时候返回true.虽然这些方法检查整个流,但是仍然可以通过并行的方法检查整个流.

## Optional类型
Optional<T>是对T类型对象的封装,不会返回null,如果存在被封装的对象,通过`get()`取得,否则会抛出NoSuchElementException.

可以看到Optional可能会抛出异常,所以高效的使用Optional的关键在于,使用一个或接受正确值,或者返回另一个替换值的方法.

`ifPresent`方法表示,如果Optional中存在值,就将该值传递给某函数,注意不是`isPresent`方法.
ifPresent()方法的参数是一个Consumer接口,该接口唯一抽象方法是`accept`.比如下列代码:
```
Optional<Integer> o = stream.filter(j -> j>5).findFirst();
o.ifPresent(s-> System.out.println(s));
```
该代码就是成功打印找到的第一个符合条件的数,但是如果o中的value是空,将不会有任何操作.

如果想要对结果进行处理,可以使用`map`方法:
```
Optional<Boolean> b = o.map(s->s==6);
```
如果返回的结果就是map中的操作返回值,返回值也可以是其他类型.

如果所得的Optional没有值的时候,希望使用默认值替换,那么操作如下:
```
Integer result = o.orElse(5);
```
如果o中不存在值的时候,返回5,否则返回o中的值.
也可以调用代码来设置默认值:
```
Integer result = o.orElseGet(()->1+5);
```
或者希望没有值的时候抛出异常:
```
o.orElseThrow(RuntimeException::new);
```
### 创建可选值
前边是使用stream来获取的Optional对象,当然也可以通过Optional中的静态方法来获取Optional对象,`Optional.of(result)`或`Optional.empty()`用来创建一个Optional对象.而`Optional.ofNullable(obj)`会返回Optional.of(obj)而如果obj为空,则返回Optional.empty().

### 使用flatMap来组合可选函数
加入有一个返回`Optional<T>`的方法`f()`,并且目标类型`T`中有一个会返回`Optional<U>`的方法g,如果它们是普通方法,那么可以通过f().g()这样的方式进行调用.但是这种组合在`Optional`形式中是行不通的,但可以通过`flatMap`方法来进行组合:`f().flatMap(T::g);`
如果f()方法返回的`Optional<T>`不为空,那么就会调用T的g方法,而如果为空,则之间返回`Optional.empty()`

通过`flatMap`就可以进行流失操作.但是理解起来比较困难.

## 聚合操作
如果你希望对元素求和,可以使用一个二元函数:
```
Optional<Integer> sum = stream.reduce((x,y)->x+y);
```
该代码含义就是对所有值求和,可以简单理解为((x+y) + y ) + y ...,即使前两个元素相加后的结果变为x再与后边的元素相加.或者写为下列代码更容易理解:
```
Optional<Integer> sum = stream.reduce((result,element)->result+element);
```
以上代码还可以改写为`stream.reduce(Integer::sum)`

如果聚合方法有一个聚合操作`op`,那么该聚合会产生`v op v1 op v2 op ...`其中vi op vj 就是编写的函数调用`op(vi,vj)`,该操作应是联合的,与组合元素的顺序无关,在数学中,如果`(x op y) op z = x op (y op z)`,这样就允许通过并行流来进行有效的聚合.

通常如果`e op x = x`,那么就可以使用e作为计算的起点,比如相加操作,因为`0 + 1 = 1`,所以0就是起点,上述代码可以改为:
```
Integer sum = stream.reduce(0,(x,y)->x+y); //即计算0 + v1 + v2 + v3 + v4的值
```
当计算到最后的时候,返回标示值,也就是参数中的第一个,其是Integer类型,最终就会返回Integer类型的最终值.

但是如果注意看,reduce的这两个方法中,传入的值的类型都是一样的,即使传入的是`(T,T)->T`这种类型,但是如果我想要计算一个字符串流中字符个数的总和,传入`(String s1,String s2)->s1.length() + s2.length()`就会出错,这个时候就需要使用另一种聚合方法,首先提供一个累加函数,这个函数(T,R)->T,这个函数进行并行累加结果,最后将所有的结果再进行累加,如:
```
int sum = stream.reduce(0,(total,word)->total+word.length(),(total1,total2)->total1+total2);
```
第二个参数传入的就是累加函数,用于自己定义的一种累加方法,最后将这些累加的结果再次做累加即可取得结果.

前两个方法,是操作同一种类型的值,而第三个方法是可以操作不同类型的值,注意第三个方法中的第三个参数是用于处理并行下的运算结果的,如果是单线程,一般这个方法不会被调用,理解就好.

一般不会大量使用聚合方法,比如累加方法计算字符串的长度累加和,可以这样写:`stream.mapToInt(String::length).sum()`.


## 收集结果
处理完流后,可以调用toArray()方法来获取所有元素的数组,但是调动这个方法只能返回Object类型的数组,如果要范湖一个指定类型的数组,需要将来类型传递给toArray()方法:
```
String[] result = words.toArray(String::new);
```

如果要收集结果到一个集合中,可以用collect方法,这个方法支持并行,所以其参数为:
1. 一个能创建目标类型的实例方法,如HashSet的构造函数
2. 一个将元素添加到目标中的方法,如add方法
3. 一个将两个对象整合到一起的方法,如addAll,用于并行.

比如添加到HashSet中:`HashSet<String> result = stream.collect(HashSet::new,HashSet::add,HashSet:addAll);`

但是实际中一般不会这么做,因为Collectors接口已提供了很多种方法,如:
```
//收集到list中
List<String> result = stream.collect(Collectors.toList());

//set中
Set<String> result = stream.collect(Collectors.toSet());

//TreeSet中
TreeSet<String> result = stream.collect(Collectors.toCollection(TreeSet::new));

//HashSet中
HashSet<String> result= stream.collect(Collectors.toCollection(HashSet::new));
//依次类推.
```

```
//如果想将流中所有字符串链接起来
String result = stream.collect(Collectors.joining());
//通过分隔符链接起来
String result = stream.collect(Collectors.joining(","));
//如果将字符串链接前,流中包含其他对象,首先要将这些对象转换为字符串,如
String result = stream.map(Object::toString).collect(Collectors.joining(","));

//如果要通过一个对象来获取流的总和,平均值,最大值,最小值,那么
    IntSummaryStatistics intSummaryStatistics = stream.collect(Collectors.summarizingInt(n->n));
    System.out.println(intSummaryStatistics.getAverage()); //平均值
    System.out.println(intSummaryStatistics.getCount());  //个数
    System.out.println(intSummaryStatistics.getMax());  //最大值
    System.out.println(intSummaryStatistics.getSum());  //总和
```

前面说的`forEach`不仅仅可以用在集合上,也可用在Stream对象上,如:
```
stream.forEach(System.out::println); //打印每个元素的值,但是在并发的时候可能会以任意顺序
stream.forEachOrdered(System.out::println); //按照顺序打印
```
注意,forEach,forEachOrdered都是终止操作,所以在调用它的时候,你不能在使用该流对象,如果想要打印某些东西,可以选择使用`peek`方法.

## 将结果收集到Map中
既然可以将Stream收集到Set,List中,当然也可以收集到Map中,这样就方便进行通过key来查找,`Collectors.toMap()`有两个参数,分别用来生成键和值.如:
```
Map<Integer,String> map = stream.collect(Collectors.toMap(Person::getId,Person::getName));
//如果想要把值变为实际元素,可以换为 Function.identity()
Map<Integer,String> map = stream.collect(Collectors.toMap(Person::getId,Function.identity()));
```
如果多个元素具有相同的键,则会抛出IllegalStateException异常,不过可以通过指定第三个函数参数,来根据已有值和新值,决定该键最终应该使用哪个值:
```
Map<Integer,String> map = stream.collect(Collectors.toMap(Person::getId,Function.identity(),(existValue,newValue)->existValue));
//这样出现冲突键,值不会变,抛弃新值
```

如果要转换成一个TreeMap对象,则需要在上述例子中加上第四个参数,构造函数参数:
```
Map<Integer,String> map = stream.collect(Collectors.toMap(Person::getId,
Function.identity(),
(existValue,newValue)->existValue),
TreeMap::new);
```

## 分组和分片
对某个流,使用流中的某一值进行分组,方法是groupingBy,类似数据库中的groupBy.
如:
```
Map<Boolean,List<Integer>> map = stream.collect(Collectors.groupingBy(i->i>5));
```
其中的键和groupingBy返回的键一一对应,这个操作最后将来stream分成两个部分,一个是大于5的,一个是小于5的.

但是如果要通过true,false进行分组,partitioningBy比groupingBY更有效率:
```
Map<Boolean,List<Integer>> map = stream.collect(Collectors.partitioningBy(i->i>5));
```
但是partitioningBy返回的boolean类型,只能分组这种类型,而groupBy可以分组任何类型.

一般上述groupingBy默认返回的值是List类型的,如果想要对列表进行操作,比如将List转换为Set,则groupingBy中就需要传入第二个参数:
```
Map<Boolean,Set<Integer>> map = stream.collect(Collectors.groupingBy(i->i>5),Collectors.toSet());
```
其中groupingBy中第二个参数被称为收集器,Java8中提供了很多收集器如:
```
//收集元素的总个数
Map<Boolean,Long> map = stream.collect(Collectors.groupingBy(i->i>5,Collectors.counting() ));
//对每个元素进行求和
Map<Boolean,Integer> map = stream.collect(Collectors.groupingBy(i->i>5,Collectors.summingInt(i->i)));
//生成每个组中的最大值或最小值,maxBy,minBy要传入一个比较器
Map<Boolean,Set<Integer>> map = stream.collect(Collectors.groupingBy(i->i>5),Collectors.maxBy(...));

```
还有其他一些收集器,不常用,所以不列举.
一定要注意,不传收集器的时候,默认返回的值是List类型的,如果传入收集器,会返回收集器对应的类型.

## 原始类型流
Stream因为使用泛型,所以如果要传入int类型,只能传入包装的Integer,这样会使得效率降低,所以Java8提供了原始类型流,如`IntStream`,`LongStream`,`DoubleStream`等,如果要存储short,char,int,boolean等数据,要使用`IntStream`,如果要存储float,double要使用`DoubleStream`.

IntStream和LongStream还拥有静态方法`range`和`rangeClosed`
如:`IntStream stream = IntStream.range(0,100)`产生0~99,而`IntStream stream = IntStream.rangeClosed(0,100)`产生0~100.

当你拥有一个对象流的时候,可以通过mapToInt(),mapToLong(),mapToDouble()方法,将它抓换为一个原始类型流.如:
将来一个字符串流,转换为字符串长度的原始类型流:
```
IntStream lengths = words.mapToInt(String::length);
```
而如果要将一个原始类型流封装为对象流,则:
```
Stream<Integer> stream = IntStream.range(0,100).boxed();
```

原始类型流和对象流的区别:
* toArray()方法返回原始类型数组
* 产生的Optional结果为OptoinalInt,OptionalLong或OptionalDouble等
* 方法sum,averange,max,min,而在对象流中没有这些方法
* summaryStatistics产生XxxSummaryStatistics等


## 并行流
流操作会创建一个串行流,而parallel可以将任意串行流转换为并行流.
如:`Stream.of(someArr).parallel()`在终止方法执行前,流处于并行模式,那么所有延迟执行的流操作都会被并行执行.
你需要确保传递给并行流操作的函数都是线程安全的.比如AtomicInteger的对象,可以用来做计数器.
可以放弃有序来加快limit()的方法速度

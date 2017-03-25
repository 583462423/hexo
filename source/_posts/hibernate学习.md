---
title: hibernate学习
date: 2017-02-15 16:10:09
tags:
---

j2ee学习四-hibernate

<!--more-->

在学习之前，首先要查询[如何使用intellij开发hibernate](http://blog.csdn.net/violet_echo_0908/article/details/50839373)


# ORM
ORM全程是Obeject/Relation Mapping，即对象关系映射。

ORM的思想是，数据库中的表的记录映射为对象。程序员可以把对数据库的操作转化为对对象的操作。

ORM采用元数据来描述对象-关系映射细节，元数据通常采用xml格式

# 简单举例

创建项目完成后，要创建一个数据库，在数据库创建一个Test表，然后通过intellij的左侧状态栏的Persistence选项，找到当前的项目，右击项目选择最后一个选项来为Test表自动创建TestEntity对象，以及相应的TestEntity.hbm.xml文件，具体方法见[如何使用intellij开发hibernate](http://blog.csdn.net/violet_echo_0908/article/details/50839373)

注意对于hbm.xml中的class标签，标签内部一定要有id标签，不然会报错。

在创建了web application,并勾选hibernate后，会自动在src目录创建hibernate.cfg.xml配置文件，在该配置文件中写入以下代码：

```
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>

        <!--配置链接数据库的基本信息-->
        <property name="connection.username">root</property>
        <property name="connection.password">root</property>
        <property name="connection.driver_class">com.mysql.jdbc.Driver</property>
        <property name="connection.url">jdbc:mysql://localhost:3306/hibernate</property>

        <!--配置hibernate的基本信息-->
        <!--hibernate所使用的数据库方言-->
        <property name="dialect">org.hibernate.dialect.MySQLInnoDBDialect</property>

        <!-- 执行操作是否在控制台打印SQL -->
        <property name="show_sql">true</property>

        <!-- 是否对SQL进行格式化-->
        <property name="format_sql">true</property>

        <!-- 制定自动生成数据表的策略-->
        <property name="hbm2ddl.auto">update</property>

        <mapping resource="com/qxg/test/TestEntity.hbm.xml"></mapping>

    </session-factory>
</hibernate-configuration>
```

写完上述代码后，如何测试往表格中插入数据呢？

具体步骤如下：

```
//1.创建Configuration对象，并执行configure()方法，与hibernate.hbm.xml文件。
Configuration configuration = new Configuration();
configuration.configure();

//2.创建SessionFactory
SessionFactory ourSessionFactory = configuration.buildSessionFactory();

//3.创建一个Session,注意使用的是sessionFactory.openSession()
Session session = ourSessionFactory.openSession();

//4.开始一个事务
Transaction transaction = session.beginTransaction();

//5. new一个TestEntity对象
TestEntity test = new TestEntity();
test.setId(1);
test.setName("test");
test.setAge(15);

//6.使用session保存到数据库
session.save(test);

//7. 提交事务
transaction.commit();

//8. 关闭session，以及sessionFactory
session.close();
ourSessionFactory.close();

//注意以上代码一些会抛异常，自行处理就好。

```


# Session
Session接口是Hibernate向应用程序提供的操纵数据库的最主要的接口，它提供了基本的保存，更新，删除和加载Java对象的方法。

Session具有一个缓存，位于缓存中的对象称为持久化对象，它和数据库中的相关记录对应。Session能够在某些时间点，按照缓存中对象的变化来执行相关的SQL语句，来同步更新胡句酷，这一过程被称为刷新缓存。

站在持久化的角度，Hibernate把对象分为4种状态：持久化状态，临时状态，游离状态，删除状态。Session的特定方法能使对象从一个状态转换到另一个状态。

## Session缓存

因为使用的是单元测试，所以代码框架如下图：

```
public class TestHibernate {

    @Before
    public void init(){

    }

    @After
    public void destroy(){

    }

    @Test
    public void Test(){


    }
}

```

测试代码是Test()，而在执行Test()会自动执行@Before标记的方法，执行结束后，会自动执行@After标记的方法。所以对于Hibernate的测试，就可以写成下列代码：

```
public class TestHibernate {

    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @Before
    public void init(){
        Configuration configuration = new Configuration().configure();
        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }

    @After
    public void destroy(){
        transaction.commit();
        session.close();
        sessionFactory.close();
    }

    @Test
    public void Test(){

    }
}
```

注意，项目中要加入junit的包，junit的包可以到Junit官网下载，要下载junit.jar包和hamcrest包。

好了，接下来是Session缓存，首先看测试代码：

```
    @Test
    public void Test(){
        TestEntity t = session.get(TestEntity.class,4);
        System.out.println(t);
    }
```
注意session.get(Class,id)是从数据库中拿出某个对象，这个测试代码是取出对象并打印，因为开启了SQL打印的服务，所以测试的日志如下：

```
Hibernate:
    select
        testentity0_.id as id1_0_0_,
        testentity0_.name as name2_0_0_,
        testentity0_.age as age3_0_0_
    from
        test testentity0_
    where
        testentity0_.id=?
[4,im test,16]
```

最后打印的那一条记录就是拿出的对象的数据。

那么再看下面测试代码：

```
    @Test
    public void Test(){
        TestEntity t = session.get(TestEntity.class,4);
        System.out.println(t);

        TestEntity t2 = session.get(TestEntity.class,4);
        System.out.println(t2);
    }
```

打印的数据是：

```
Hibernate:
    select
        testentity0_.id as id1_0_0_,
        testentity0_.name as name2_0_0_,
        testentity0_.age as age3_0_0_
    from
        test testentity0_
    where
        testentity0_.id=?
[4,im test,16]
[4,im test,16]
```

注意看打印的结果，可以看到第二次取数据，并没有打印SQL语句，原因就在Session缓存。

看下面的测试代码：

```
    public void Test(){
        TestEntity t = session.get(TestEntity.class,4);

        System.out.println(t);
        t.setName("im not test");
    }

```
我们想要的效果是，使用取得的对象修改其中的name值，那么看看打印的日志：

```
Hibernate:
    select
        testentity0_.id as id1_0_0_,
        testentity0_.name as name2_0_0_,
        testentity0_.age as age3_0_0_
    from
        test testentity0_
    where
        testentity0_.id=?
[4,im test,16]
Hibernate:
    update
        test
    set
        name=?,
        age=?
    where
        id=?
```

可以看到，日志中表明执行了update方法，也就是说，在数据库中照样修改了name值，原因在于transaction在提交的时候，调用了session.flush()方法，该方法的含义是，保持session缓存中的内容和数据库中的一致。所以会向数据库发送SQL语句进行更新。


注意在下列情况，也会执行session.flush()操作：
1. 在执行HQL或QBC查询会先进行flush()操作。
2. flush缓存的例外情况，当使用native自动生成id时，当执行session.save()操作后，会立即发送insert语句。

commit和flush的区别：flush执行一系列sql语句，但不是交事务，commit会先调用flush方法，然后提交事务。提交事务数据库内容就会被保存下来。

session.refresh(Object)：

```
TestEntity t = session.get(TestEntity.class,4);
System.out.println(t);

session.refresh(t);

System.out.println(t);
```
refresh方法的意思是发送select语句，来查询t对象是否在中间被改变，保证t对象是最新的。但是注意，如果要refresh生效，需要设置事务的隔离级别，不然refresh不会生效。

事务隔离级别可查看：(http://blog.csdn.net/fg2006/article/details/6937413)

那么通过hibernate配置文件可以配置隔离级别：

* 1：READ UNCOMMITED
* 2: READ COMMITED
* 4: REPEATABLE READ
* 8: SERIALIZEABLE

Hibernate通过为Hibernate映射文件指定hibernate.connection.isolation属性来设置事务隔离级别，一般设置为2. 即：
```
<property name="connection.isolation">2</property>
```

这样refresh就能生效了。



session还有一个方法是clear()方法，表示清除缓存，比如

```
    public void Test(){
        TestEntity t = session.get(TestEntity.class,4);
        System.out.println(t);

        session.clear();

        t = session.get(TestEntity.class,4);
        System.out.println(t);

    }
```

在打印第一条取出的数据后，进行了缓存清理，接着又进行取数据，看打印日志：

```
Hibernate:
    select
        testentity0_.id as id1_0_0_,
        testentity0_.name as name2_0_0_,
        testentity0_.age as age3_0_0_
    from
        test testentity0_
    where
        testentity0_.id=?
[4,im not test,16]
Hibernate:
    select
        testentity0_.id as id1_0_0_,
        testentity0_.name as name2_0_0_,
        testentity0_.age as age3_0_0_
    from
        test testentity0_
    where
        testentity0_.id=?
[4,im not test,16]
```

这就表明，clear()方法执行后，系统清理缓存，这个时候取数据就不会在缓存中取，而是重新到数据库中取。

# 持久化对象状态

临时对象：
什么是临时对象？
* 在使用代理主键的情况下，OID通常为null
* 不在session的缓存中
* 在数据库中没有对应的记录

其实说白了，就是不会一直存在的对象，只是临时使用

持久化对象：
* OID不为null
* 位于Session缓存中
* 若在数据库中已经有和其对应的记录，持久化对象和数据库中的相关记录对应
* session在flush缓存时，会根据持久化对象的属性变化，来同步更新数据库
* 在同一个session实例的缓存中，数据库表中的每条记录只对应唯一的持久化对象

删除对象：
* 在数据库中没有和其OID对应的记录
* 不再处于session缓存中
* 一般情况下，应用程序不该再使用被删除的对象

游离对象，也叫脱管
* OID不为null
* 不再处于session缓存中
* 一般情况下，游离对象是由持久化对象转变过来的，因此在数据库中可能还存在与它对应的记录。


如下简单例子：
```
    @Test
    public void Test(){
        TestEntity t = new TestEntity();
        t.setName("Test");
        t.setAge(17);

        System.out.println(t);

        session.save(t);
	//session.persist(t);

        System.out.println(t);
    }

```

首先，当使用new来创建对象的时候，该对象就是一个临时对象，其没有分配id，执行save之后，系统会为其分配一个id，这个时候便变为持久化对象，打印结果如下：

```
[0,Test,17]
Hibernate:
    insert
    into
        test
        (name, age)
    values
        (?, ?)
[6,Test,17]
```
session.save可以改变对象状态，session.persist(t）同样可以改变对象状态,两者的区别是，如果persist执行前，对象被设置了id，那么就会抛出异常，而save不会。


session.get()和load()方法可以取得对象，但是两者区别很大：

get会直接发送select语句来查询对象并取出，而load则是返回一个代理对象，在使用的时候才会进行查询，但是如果对象不存在，则会抛出异常，但是get不会抛异常，只是打印一个null值。说白了，get是立即加载，load是延时加载。

session.update()方法是将一个游离对象转换为持久化对象。

比如之前写的代码：
```
TestEntity t = session.get(TestEntity.class, 4);
t.setName("not test");
```
在事务commit之前就会调用session的flush方法，这个时候就会更新数据库，即发送update sql语句。

但是，我们也可以在代码后直接显示调用session.update(t)，这样的效果都是一样的。

什么时候要显示调用update方法呢？

当当前的游离对象不在当前的session中，就需要调用update。例如如下代码：在A的session中取出对象，在B的session中修改对象的某些值：

```
    @Test
    public void Test(){
        TestEntity t = new TestEntity();
        t.setName("Test");
        t.setAge(17);

        session.close();

        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        t.setAge(18);
    }
```

如果在第二个session中修改某些属性，这样修改后，在事务提交后，并不会执行update语句，打印日志没任何东西，因为session.close()后，事务也没有提交，在第二次提交的时候，t的修改并不在该session的管理范围。

session不仅可以使用update方法，同样有一个方法功能比较完善-> session.saveOrUpdate()看名字就知道，会根据对象的状态，执行save或update方法。


对于删除，使用session.delete()，该方法可以删除一个游离对象，也可以删除一个持久化对象。如：

```
TestEntity t = new TestEntity();
t.setId(4);
session.delete(t);
```

可以看到，只需要设置一个Id就可以进行删除。如果数据库中没有此Id的对象，就会抛出异常。但是注意，执行delete方法后不会立即删除，而是执行flush方法后才会执行删除。


说完以上方法后，就可以知道了对象间的转换关系。如：

起点[new]-> 临时状态
起点[get,load,Query.list(),Query.uniqueResult(),Query.iterator(),Query.scoll()]-> 持久化状态。

临时状态[save,saveOrUpdate,persist,merge]->持久化状态
持久化状态[delete]->删除状态
持久化状态[evict,close,clear]->游离状态
游离状态[update,saveOrUpdate,merge]->持久化状态

evict()方法是把某对象从缓存中移出，这样在调用commit方法时，移出的对象就不会在数据库中修改了。


# 多对一的关联关系

```
<many-to-one name="name1" class="XXXClass" column="Foreign_id"></many-to-one>
```

其中column就是指明外键，这样在执行session.save()方法后，在数据库中只是存储了外键的id。
name是多对一的多的那端的名字，class同样是多的那段的类。

再者，在类中，多的那端会有一的那端的引用，所以可以通过set来设置一的那端的值，比如以下代码：
```
Car car = new Car();

Person p1 = new Person();
Person p2 = new Person();

p1.setCar(car);
p2.setCar(car);

session.save(car);
session.save(p1);
session.save(p2);

```
这样就能把所有的数据保存在数据库中，注意先保存car，还是先保存person是有区别的。区别就在于，先保存car，就会执行3个insert，而先保存person，则会执行3个insert还会执行2个update，具体为什么，自行想象。


# 一对多的关联关系
一对多的关联关系，一的一端所引用的是多的一端的集合，所以对于一那一端写法是：
（1 car -- n person ）
```
<set name="persons" tabl=“PERSON”>
	<key column="CAR_ID"></key>
	<one-to-many class="com.xxx.Person"></one-to-many>
</set>
```
注意，该set对应的集合是Set<> ，一般使用HashSet。

# 一对一的关系映射

对于基于外键的1-1关联，其外键可以存放在任意一边，在需要存放外键的一端，增加<many-to-one>元素，并为<many-to-one>元素增加unique="true"来实现1-1关联。

```
<many-to-one name="manager" class="Manager" column="MANAGER_ID" unique="true" />
```

而另一端需要使用one-to-one元素，该元素使用property-ref属性制定使用被关联实体主键以外的字段作为关联字段
```
<one-to-one name="dept" class="Department" property-ref="manager"/>
```


OK,因为主要学习的东西并不是数据库，所以对于Hibernate就先了解到这。


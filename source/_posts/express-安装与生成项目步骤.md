---
title: express 安装与生成项目步骤
date: 2017-01-09 11:55:27
tags:
---

^ ^

<!--more-->

# 安装

首先去官网下载nodejs并安装。

之后依次执行以下cmd命令，其实该过程并不是安装`express`，安装的只是express生成器。

```
npm install -g express-generator@4 # 全局安装.
```
# 生成项目

依次在某一个项目包中执行以下CMD命令

```
express -e myproj  # myproj是项目名
cd myproj && npm install # 进入到项目包中并且安装依赖包
npm start # 执行
```
# 使用mysql

创建项目后，如果想要使用mysql，则需要安装module

安装方法是在项目目录下打开CMD命令行工具，执行npm install mysql，之后就可以在js文件中使用

使用方法如下：

```
var mysql = require('mysql');
var conn = mysql.createConnection({
    host:'localhost',
    user:'root',
    password:'root',
    database:'myapp'
});
conn.connect();
conn.query('SELECT * FROM person',function(err,rows,fields){
    if(err)throw err;
    console.log('mysql已经执行','rows的长度是',rows.length);
});
conn.end();
```
# 建立连接

```
var conn = mysql.createConnection({
    host:'localhost',
    user:'root',
    password:'root',
    database:'myapp'
});
conn.connect(function(err) {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  console.log('connected as id ' + connection.threadId);
});
```
如果直接使用query方法，则会默认进行连接，就不用调用connect方法了。
连接中常用的选项如下：

* host:默认localhost
* port:端口，默认3306
* user:用户名
* password:密码
* database:数据库名
* charset:字符集，默认UTF8_GENERAL_CI
* connectTimeout:超时，单位毫秒，默认10000
# 终止连接

```
conn.end(function(err){
});
```
或没有回调的终止连接:conn.destroy()

# 使用连接池

首先连接池的概念：(百度)像打开关闭数据库连接这种和数据库的交互可能是很费时的，尤其是当客户端数量增加的时候，会消耗大量的资源，成本是非常高的。可以在应用服务器启动的时候建立很多个数据库连接并维护在一个池中。连接请求由池中的连接提供。在连接使用完毕以后，把连接归还到池中，以用于满足将来更多的请求。

```
var mysql = require('mysql');
var pool  = mysql.createPool({
  connectionLimit : 10,
  user            : 'root',
  password        : 'root',
  database        : 'myapp'
});
pool.query('SELECT * FROM person', function(err, rows, fields) {
  if (err) throw err;
  console.log('The solution is: ', rows[0].solution);
});
```
## 获取连接

```
pool.getConnction(function(err,conn){
	if(err)throw err;
    
    //连接成功，就可以使用conn来进行操作
    conn.query(...);
    
    conn.release(); //注意这里不能使用end()，要使用release()来释放回连接池中。
    
    //如果你想要销毁该连接，需要使用conn.destroy()方法，之后连接池中会重新创建一个新的连接。
})
```
## 连接池事件

connection,当连接池创建一个新的连接后，会发送该事件
```
pool.on('connection',function(connection){
	...
})
```
## 终止连接池所有连接

``
pool.end(function(err){
	
});
```
# 更改用户

```
conn.changeUser({user:'root'},function(err){
	if(err)throw err;
});
```
# query方法

可以使用conn或pool对象来调用query方法。

第一种：query(sqlString,callback)
```
conn.query('SELECT * FROM person',function(err,rows,fields){
    if(err)throw err;
    console.log('mysql已经执行','rows的长度是',rows.length);
});
```
第二种:query(sqlString,value,callback)

```
conn.query('SELECT * FROM person where `name` = ?',['david'],function(err,rows,fields){
});
```
第三种:query(options,callback)
```
conn.query({
	sql:'SELECT * FROM person where `name` = ?',
    timeout:10000,
    values:['david']
},fucntion(err,rows,fields){
	...
});
```
注意，该方法中的sqlString不单单只是查询，也可以是插入，更新等sql操作,如插入操作：
```
conn.query('INSERT INTO person SET ?', {name: '秦晓光',age:22}, function(err, result) {
  if (err) throw err;
  console.log(result.insertId); //打印出插入成功后的id
});
```
如删除操作:
```
conn.query('DELETE FROM person WHERE age = 18', function (err, result) {
  if (err) throw err;
  console.log('deleted ' + result.affectedRows + ' rows'); //打印出受影响的行数，即删除了多少行
})
```
如更新操作：

```
connection.query('UPDATE posts SET ...', function (err, result) {
  if (err) throw err;
  console.log('changed ' + result.changedRows + ' rows'); //更新的行数
})
```
如果你使用select查询语句返回多行，并需要对每行都要执行一定的操作，那么可以使用以下方法：
```
var query = conn.query('select * from person');
query.on('result',function(row){
    conn.pause();            //暂停查询
    console.log(row);
    ...						//row是查询的当前行，在这里可以对row进行操作。
    conn.resume();			//恢复查询
});
```
当数据量比较大的时候，并且处理不过来，就可以使用pause方法来中断查询操作，处理完毕后就可以使用resume来恢复，这里的pause和resume在数据量少的时候并不是必须的。

当然query还有更多的事件

```
query
  .on('error', function(err) {
    // Handle error, an 'end' event will be emitted after this as well
  })
  .on('fields', function(fields) {
    // the field packets for the rows to follow
  })
  .on('result', function(row) {
    // Pausing the connnection is useful if your processing involves I/O
    connection.pause();
    processRow(row, function() {
      connection.resume();
    });
  })
  .on('end', function() {
    // all rows have been received
  });
```
# Escaping query values

这个术语不太明白，不过看解释说是防止sql注射。
使用方法如下：

```
var userId = 'some user provided value';
var sql    = 'SELECT * FROM users WHERE id = ' + connection.escape(userId);
connection.query(sql, function(err, results) {
  // ...
});
```
可以使用conn或pool等来调用escape()方法。

# 封装

一般为了简便，会把mysql进行封装，可以在项目根目录新建一个文件夹mysql，在mysql中创建pool.js
```
pool.js
-----
var mysql = require('mysql');
var pool = mysql.createPool({
    host:'localhost',
    user:'root',
    password:'root',
    database:'myapp',
    port:'3306'
});
module.exports = pool;
```
之后在app.js中的使用方法是：

```
var pool  = require('./mysql/pool');
pool.getConnection(function(err,coon){
	...
});
```
# 项目地址

这里只列出了常用的一些操作，如果想要查看其他详细操作，可到项目地址查看:https://github.com/mysqljs/mysql;



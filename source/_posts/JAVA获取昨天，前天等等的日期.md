---
title: JAVA获取昨天，前天等等的日期
date: 2017-02-20 20:21:44
tags:
---

有时候需求需要得到昨天，前天，大前天等的日期.

<!--more-->

方法如下：

昨天：

```
Calendar calendar = Calendar.getInstance();
calendar.add(Calendar.DATE, -1); //此时calendar就是昨天的

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
System.out.println(sdf.format(calendar.getTime()));
```


前天：

```
Calendar calendar = Calendar.getInstance();
calendar.add(Calendar.DATE, -2); 

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
System.out.println(sdf.format(calendar.getTime()));
```

主要方法就是`calendar.add(Calendar.DATE, -2); `
也就是当前日期的天数加上第二个参数，得到的就是所需要的日期

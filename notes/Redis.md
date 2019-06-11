## 缓存中间件-Memcache和Redis的区别：
Memcache：代码层类似Hash

* 支持简单数据类型
* 不支持数据持久化存储
* 不支持主从
* 不支持分片

Redis：

* 数据类型丰富
* 支持数据磁盘持久化存储
* 支持主从
* 支持分片

## 为什么Redis能这么快？
能达到10W+的QPS（即query per second，每秒内查询次数）

* 完全基于内存，绝大部分请求是纯粹的内存操作，不涉及磁盘IO，执行效率高
* 数据结构简单，对数据操作也简单
* 采用单线程，单线程也能处理高并发请求，想多核也可启动多实例
* 采用多路IO复用模型，即非阻塞IO


## Redis的数据类型

* String：最基本的数据类型，二进制安全。k-v键值对，值最大支持存储512M.V可以存任何东西。
    * set test "redis";  赋值
    * get test；  取值
* Hash：String元素组成的字典，适用于存储对象。跟实体类对象很相似。可以将结构化的信息在客户端序列化之后存储为json格式字符串，用hash来存储。
    * hmset test name "zhangsan" age 20;  赋值
    * hget test age;  获取test的age
* List：列表，按照String元素插入顺序排序。因为它跟栈很类似，后进先出结构，类似于栈的结构，所以它可以实现像最新消息排行榜的功能。
    * lpush testlist aaa;
    * lpush testlist bbb;
    * lpush testlist ccc;  赋值，存储进去三个
    * lrange mylist 10;  取值，取10个，但是目前只有三个，所以只能返回3个，且是后进先出的。
* Set：String元素组成的无序集合，通过哈希表实现，不允许重复。可以实现类似于微博的共同关注，共同喜好等功能。
    * sadd testset 111;
    * sadd testset 222;
    * sadd testset 333;  赋值
    * smembers testset;  取值
* Sorted Set：有序集合，通过分数来为集合中的成员进行从小到大的培训。可以实现一个班级按照分数进行排序，value为学号，score为得分。也可以实现权重队列。
    * zadd testset 3 abc;
    * zadd testset 1 abd;
    * zadd testset 2 abb; 
    * zadd testset 1 bgg;  赋值
    * zrangebyscore testset 0 10;  从第0个开始，取10个。显示结果为abd,bgg,abb,abc。1，2，3代表score，权重参数score。

上面五个是比较常见的数据类型，还有一些不常见的。例如：
用于计数的：HyperLogLog；
用于支持存储地理位置信息的：Geo;

## 从海量key里面查询出某一固定前缀的key
假如redis有一亿个key，其中有十万个key是以某固定前缀存储的，如何找出来？
（注意，这种问题要留意细节，即使不说海量数据，也得提前问号，数据量有多大。）

回答一：通过keys指令
dbsize：查询所有的key数量
keys k1*    将前缀为K1的统统打印出来。
但是缺点：KEYS命令一次性返回所有匹配的key，但是如果键的数量过大的话会使服务器卡顿。不适用于生产环境。

回答二：**使用SCAN指令**
SCAN cursor match 【pattertn】 count
举例：SCAN 0 match K1* 100
开始迭代返回前缀为K1的key；
count为100表示希望一次返回100个，但是并不保证确定是这些，只能是大概率符合；
cursor为0表示咱们刚开始迭代。
执行之后返回两个结果，上面是cursor游标的值，下面是具体数据。我们记录下上面返回的cursor值，下次迭代用这个。
这种做法不会造成堵塞。但是有可能造成获取到重复key，我们自己在程序里面去重就可以。

## 如何通过Redis实现分布式锁
分布式锁需要解决的问题：

1. 互斥性
2. 安全性
3. 死锁
4. 容错

SETNX key value：如果key不存在，则创建并赋值。
设置成功的话，返回1；key若存在，创建失败，返回0。

然后给该key设置一个过期时间
EXPIRE key seconds：给key设置一个seconds秒的过期时间。
设置成功，返回1。

如图：
![57a256daec595785f32225092214bddd](Redis.resources/818BEC46-3C73-49D9-B715-9E2C01B64E5B.png)

但是上面这段程序会有风险，就是假如我们刚setNX之后，程序就挂掉了，没执行expire方法。那么key就被一直占用。

<u>**优化方法：REDIS版本在2.6.12版本以上**</u>
通过set方法实现：
SET key value [EX seconds] [PX milliseconds] [NX|XX]
[EX seconds]：设置键的过期时间为seconds秒
[PX milliseconds]：设置键的过期时间为milliseconds毫秒
NX：只在键不存在时，才对键进行设置操作；
XX：只在键已经存在时，才对键进行设置操作。
操作成功完成时，返回OK，否则返回nil

## 大量的key同时过期的注意事项
集中过期，由于清除大量的key很耗时，会出现短暂的卡顿现象。

* 解决方案：在设置key的过期时间的时候，给每个key加上随机值，使得过期时间分散。

## 如何使用Redis做异步队列

使用lIST数据类型作为队列，RPUSH生产消息，LPOP消费消息。

* 缺点：没有等待队列里有值就直接消费
* 弥补：可以通过在应用层引入sleep机制去调用LPOP重试

如果不想用sleep的方式重试，还有别的方法：
BLPOP key [key...] timeout 
例如：blpop testlist 30
30秒内如果testlist里面没有值就返回nil，有值就返回。

* 缺点：只能供一个消费者消费
* 弥补：通过redis的pub/sub主题订阅者模式

## Redis的pub/sub主题订阅者模式

发送者pub发送消息，订阅者sub接收消息。
订阅者可以订阅任意数量的频道。

subscribe myTopic 订阅这个myTopic频道
publish myTopic "hello" 向myTopic频道发布消息

* 缺点：消息的发布是无状态的，无法保证可达。即发即达。

## Redis如何做持久化
#### 一.RDB（快照）持久化：保存某个时间点的全量数据快照。
进入redis.conf查看配置信息和持久化策略：
save 900 1：900秒之内如果有一条是写入指令，就触发产生一次快照。
save 300 10：在300秒之内，如果有10条写入，就会产生快照；如果变动数大于0，但是还没有到10条的话，就会等到900秒过后再去备份。
save 60 10000：在60秒内如果有10000条写入，就进行备份。

**为什么需要配置这么多规则？**
因为redis每个时段的读写请求是不均衡的，为了平衡性能与数据安全，我们根据redis自身写入情况。自由配置。

stop-writes-on-bgsave-error yes
当备份进程出错的时候，主进程停止写入操作。为了保护持久化数据一致性的问题。

rdbcompression yes
在备份的时候将rdb文件压缩后再进行保存。这个建议设置为no

save "" 
禁用rdb配置，加在save的后面


<u>**如何手动触发RDB持久化？**</u>

SAVE：阻塞Redis的服务器进程，直到RDB文件被创建完毕。一般不用这种。
**BGSAVE：Fork出一个子进程来创建RDB文件，不阻塞服务器进程。**

查看上一次快照的时间的指令：lastsave

我们可以通过java的定时器或者corn等其他方式，定期掉起redis的bgsave指定，备份RDB文件。并按照时间戳存储不同的RDB文件，作为Redis某段时间的全量备份脚本。


<u>**如何自动触发RDB持久化？**</u>

1. 根据redis.conf配置里的SAVE m n 定时触发（用的是BGSAVE）
2. 主从复制时，主节点自动触发
3. 执行Debug Reload
4. 执行Shutdown且没有开启AOF持久化

<u>**RDB持久化的缺点：**</u>

1. 内存数据的全量同步，数据量大会由于IO而严重影响性能
2. 可能会因为Redis挂掉而丢失从当前至最近一次快照期间的数据

#### 二.AOF持久化
AOF（Append-Only-File）持久化：保存写状态

* 记录下除了查询以外的所有变更数据库状态的指令
* 以append的形式追加保存到AOF文件中（增量）

AOF持久化默认是关闭的，可以设置redis.conf里面的设置：
appendonly 设置为 yes 即可生效。
appendfsync always/everysec/no 设置为everysec。
会自动生成appendonly.aof文件。

<u>**随着写操作不断增加，AOF文件会越来越大，如何解决？**</u>
日志重写解决。原理如下：

1. 调用fork（），创建一个子进程
2. 子进程把新的AOF写到一个临时文件中，不依赖原来的AOF文件
3. 主进程持续将新的变动同时写到内存和原来的AOF里
4. 主进程获取子进程重写AOF的完成信号，往新AOF同步增量变动
5. 使用新的AOF文件替换掉旧的AOF文件


#### RDB和AOF的优缺点
RDB
    优点：全量数据快照，文件小，恢复快；
    缺点：无法保存最近一次快照之后的数据
AOF
    优点：可读性高，适合保存增量数据，数据不易丢失。
    缺点：文件体积大，恢复时间长
    
改进方式：
Redis 4.0之后推出了混合持久化方式。也是默认方式。
RDB-AOF混合持久化方式：

* 用BGSAVE做镜像全量持久化，AOF做增量持久化



## Pipeline

* Pipeline和Linux的管道类似
* Redis基于请求/响应模型，单个请求处理需要一一应答
* Pipeline批量执行指令，节省多次IO往返的时间
* 有顺序依赖的指令，建议分批发送


## Redis的同步机制
主从同步原理：
一个Master来用于写操作，其它若干个Slave用于进行读操作。
Master代表主，Slave代表从。遵循数据的最终一致性，


全同步流程：

* slave发送sync命令到Master
* Master启动一个后台进程，将Redis中的数据快照保存到文件中
* Master将保存数据快照期间接收到的写命令缓存起来
* Master完成写文件操作后，将该文件发送给Slave
* 使用新的RDB文件替换掉旧的RDB文件
* Master将这期间收集的增量写命令发送给Slave端

增量同步流程

* Master接收到用户的操作指令，判断是否需要传播到Slave；一般涉及到增删改的话，就需要，查就不需要了
* 将操作记录追加到AOF文件
* 将该操作扩散到其它Slave
    * 对齐主从库
    * 往响应缓存写入指令
* 将缓存中的数据发送给Slave




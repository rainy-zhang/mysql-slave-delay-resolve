# mysql-slave-delay-resolve

使用：
```java
MysqlClient master;
MysqlClient[] slaves;
DelaySolver delaySolver = new DelaySolver(master, slaves);

// 通过second_behind_master判断主从是否存在延迟，如果不存在延迟返回slave节点，否则返回master节点
MysqlClient server = delaySolver.checkDelay();

// 等待主库位点，通过master_pos_wait命令判断主从是否存在延迟
MysqlClient server = delaySolver.waitingLoci();


// 等GTID方案
MysqlClient server = delaySolver.waitingGtid();


```

# homework
A simple http server for betting offer service.
# note
使用httpserver时，context没有通配符，使用"/"，并自己解析uri信息返回相应的内容

使用concurrenthashmap和priorityblockingqueue来存储相关的信息，并控制并发访问的线程安全
后续考虑持久化

每局游戏维护一个用优先队列实现的最小堆来处理金额前20的客户

concurrenthashmap只能保证put和get元素时线程安全，无法保证存储元素操作的线程安全
无法使用priorityblockingqueue保证线程安全因为需要控制当前size不超过20，还是需要加锁
先不加锁判断size和堆顶，然后在加锁判断并更新
使用priorityblockingqueue的toArray获取全部元素
在添加元素前先尝试删除


session重复获取是否要续期？
sessions是用customId当key还是sessionKey当key


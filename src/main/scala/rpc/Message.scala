package rpc

case class RegisterWorker(  workerID: String,
                            memory: Int,
                            cores: Int)
//Master返回给worker的消息
case object RegisteredWorker
//worker自己发送自己的消息
case object SendHeartBeat
//worker向master发送的消息
case class HeatBeat(workerID: String)



package rpc

class WorkerInfo (val workerId: String, var memory: Int, var cores: Int){
var lastHeartBeatTime: Long=0L
}

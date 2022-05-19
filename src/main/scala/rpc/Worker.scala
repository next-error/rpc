package rpc

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import java.util.UUID
//导入时间单位
import scala.concurrent.duration._
//Worker也是一个Actor,也可以收发消息
class Worker extends Actor{
  var masterRef: ActorSelection = _
  //生成Worker的唯一ID
  val WORKER_ID = UUID.randomUUID().toString
  //所有的Actor都有一个生命周期方法,即必须要调用的方法

  //在receive方法执行之前,在构造方法执行之后,一定会调用一次preStart方法
  override def preStart(): Unit = {
    //和Master建立连接
    //actorSelection选择和哪个ActorSystem下的Actor建立连接,返回代理对象
    masterRef= context.actorSelection("akka.tcp://MasterActorSystem@localhost:8888/user/MasterActor")
    //向maser发送消息
    //masterRef ! "register"
    masterRef ! RegisterWorker("001",1024,8)

    //println("preStart")
  }

  override def receive: Receive = {
  /*  case "yeah" => {
      println("you are right")}
    case "ok" => {
      println("ok from master")}*/
    case RegisteredWorker => {
      //1.启动定时器，定期发送心跳
      //schedule定期调用指定的方法
      //Worker应该把心跳信息发送给Master，但是目前没法直接发送
      //导入隐式转换
      import context.dispatcher
      //println("Worker 注册成功")
      //self返回当前自己
      //定时器每15秒自己给自己发送SendHeartBeat
      context.system.scheduler.schedule(0 millisecond,15000 millisecond,self,SendHeartBeat)
    }
    //worker向master发送心跳消息
    case SendHeartBeat=>{
      //在发送心跳之前，可以实现一些逻辑判断（省略...）
      //向Master发送心跳消息
      masterRef ! HeatBeat(WORKER_ID)
    }
  }


}
object Worker{
  def main(args: Array[String]): Unit = {
    //创建ActorSystem需要解析的参数
    val hostname = "localhost"
    val port = 9999
    val configStr =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = $hostname
         |akka.remote.netty.tcp.port = $port
         |""".stripMargin
         //解析参数
        val config = ConfigFactory.parseString(configStr)
    //创建ActorSystem
    val workerActorSystem = ActorSystem("WorkerActorSystem", config)
    //创建Actor
    val workerActor = workerActorSystem.actorOf(Props[Worker], "WorkerActor")
    //用创建好的Actor发送消息(自己给自己发消息)
   // workerActor ! "yeah"

  }
}

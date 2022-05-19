package rpc

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable

class Master extends Actor {
  //用于保存Worker信息的HashMap
  val idToWorkerInfo = new mutable.HashMap[String, WorkerInfo]()

  //receive 用于接收其它Actor (也包括字节)发送的消息
  //Receive是偏函数类型，即PartialFunction[Any, Unit]
  override def receive: Receive = {
/*    case "hello" => {
      println("hello")}
    case "hi" => {
      println("hi")}
    case "register" => {
      println("register")}*/
    case RegisterWorker(workerID,memory,cores) => {
      println(s"workerID: $workerID,memory: $memory,cores: $cores")
      //worker给master发送消息,即master接收到消息后,返回给worker消息
      //sender可以获取消息发送者的引用
      // master给worker发的消息
      //sender() ! "ok"
      //将Worker的信息封装起来然后保存起来
      val workerInfo = new WorkerInfo(workerID, memory, cores)
      //将Worker的信息保存起来(保证到可变的HashMap中)
      idToWorkerInfo(workerID) = workerInfo
      sender() ! RegisteredWorker
    }
    //Work发送个Master的消息消息
    case HeatBeat(workerId) => {
      //println(s"work : $workerId 发送了心跳消息！")
      //更新workerID的workerInfo的上一次心跳时间
      if(idToWorkerInfo.contains(workerId)){
        //
        val info = idToWorkerInfo(workerId)
        //更新最近的心跳时间
        

      }
    }
  }
}
object Master{
  def main(args: Array[String]): Unit = {
    //1.创建ActorSystem(用于创建并管理Actor,单例的,一个进程中有一个即可)


    val hostname = "localhost"
    val port = 8888
    //参数的含义
    //akka.actor.provider 远程通信的实现方式
    //akka.remote.netty.tcp.hostname 绑定的地址
    //akka.remote.netty.tcp.port 绑定的端口
    val configstr =
      s"""
        |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
        |akka.remote.netty.tcp.hostname = $hostname
        |akka.remote.netty.tcp.port = $port
        |""".stripMargin
    //创建ActorSystem前要解析参数
    val config = ConfigFactory.parseString(configstr)
    //调用ActorSystem的apply方法，传入ActorSystem的名称和配置信息
    val actorSystem: ActorSystem = ActorSystem.apply("MasterActorSystem", config)
    //2.创建Actor
        //调用actorSystem的actorOf方法创建Actor，传入Actor的类型，Actor的名称
        val masterActor: ActorRef = actorSystem.actorOf(Props[Master], "MasterActor")
    //3.发消息
    //给指定的Actor发消息，！是一个方法
    //masterActor ! "hi"
  }
}

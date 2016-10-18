package homework.withfsm

import akka.actor.{Actor, Props}
import akka.event.LoggingReceive

class AuctionService extends Actor {

  import AuctionService._
  import scala.concurrent.duration._

  override def receive: Receive = LoggingReceive {
    case Init =>
      val auctions = (1 to 10).map(i => this.context.system.actorOf(Props(Auction(i, 20 seconds, 10 seconds))))

      val buyers = (1 to 3).map(i => this.context.system.actorOf(Props(Buyer(i, auctions))))

      buyers.foreach(_ ! Buyer.Init)
  }

}
object AuctionService {
  case object Init
}
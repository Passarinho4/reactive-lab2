package homework.withoutfsm

import akka.actor.{Actor, Props}
import akka.event.LoggingReceive
import homework.withoutfsm.Auction.{BidTimer, DeleteTimer}

class AuctionService extends Actor {

  import AuctionService._
  import scala.concurrent.duration._

  override def receive: Receive = LoggingReceive {
    case Init => {
      val auctions = (1 to 10).map(i => this.context.system.actorOf(Props(Auction(i, BidTimer(20 seconds), DeleteTimer(10 seconds), BigDecimal(2)))))

      val buyers = (1 to 3).map(i => this.context.system.actorOf(Props(Buyer(i, auctions))))

      auctions.foreach(_ ! Auction.AuctionCreated)
      buyers.foreach(_ ! Buyer.Init)
    }
    case Finish => context.system.terminate()
  }

}
object AuctionService {
  case object Init
  case object Finish
}
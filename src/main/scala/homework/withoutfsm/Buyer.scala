package homework.withoutfsm

import java.util.Random

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive
import homework.withoutfsm.Buyer.{Bid, Init}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class Buyer(val id: Int, auctions: IndexedSeq[ActorRef]) extends Actor {

  val random = new Random()
  var bidCounter = 0

  override def receive: Receive = LoggingReceive {
    case Init =>
      auctions(random.nextInt(auctions.length)) ! Auction.BidEvent(random.nextInt(100))
      this.context.system.scheduler.scheduleOnce(random.nextInt(2) seconds, this.self, Bid)

    case Bid if bidCounter < 4 =>
      bidCounter = bidCounter + 1
      auctions(random.nextInt(auctions.length)) ! Auction.BidEvent(random.nextInt(100))
      this.context.system.scheduler.scheduleOnce(random.nextInt(2) seconds, this.self, Bid)

    case Bid if bidCounter >=4 =>

    case Auction.YouWinAuction(price) =>
      println("Hurra! I won auction")



  }

  override def toString: String = "Buyer $id"
}

object Buyer {

  def apply(id: Int, auctions: IndexedSeq[ActorRef]): Buyer = new Buyer(id, auctions)

  case object Init
  case object Bid

}

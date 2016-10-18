package homework.withfsm

import java.util.Random

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import Buyer._

class Buyer(val id: Int, auctions: IndexedSeq[ActorRef]) extends Actor {

  val random = new Random()
  var bidCounter = 0

  override def receive: Receive = LoggingReceive {
    case Init =>
      auctions(random.nextInt(auctions.length)) ! Auction.Bid(random.nextInt(100), self)
      this.context.system.scheduler.scheduleOnce(random.nextInt(2) seconds, this.self, Bid)

    case Bid if bidCounter < 4 =>
      bidCounter = bidCounter + 1
      auctions(random.nextInt(auctions.length)) ! Auction.Bid(random.nextInt(100), self)
      this.context.system.scheduler.scheduleOnce(random.nextInt(2) seconds, this.self, Bid)

    case Bid if bidCounter >=4 =>

    case Auction.YouWonTheAuction(price) =>
      println("Hurra! I won the auction")


  }

  override def toString: String = "Buyer $id"
}

object Buyer {

  def apply(id: Int, auctions: IndexedSeq[ActorRef]): Buyer = new Buyer(id, auctions)

  case object Init
  case object Bid

}

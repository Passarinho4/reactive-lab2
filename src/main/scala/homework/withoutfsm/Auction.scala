package homework.withoutfsm

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive
import homework.withoutfsm.Auction.{BidTimer, DeleteTimer}

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global

class Auction(val id:Int, bidTimer: BidTimer, deleteTimer: DeleteTimer, initialPrice: BigDecimal) extends Actor {

  import Auction._

  var price: BigDecimal = initialPrice
  var buyer: ActorRef = _

  override def receive: Receive = LoggingReceive {
    case AuctionCreated =>
      context.system.scheduler.scheduleOnce(bidTimer.time, this.self, BidTimerExpired)
      context become createdState
  }

  def createdState: Receive = LoggingReceive {
    case p @ BidEvent(value) if p.price > price =>
      price = p.price
      buyer = sender()
      context become activatedState
    case p @ BidEvent(value) if p.price <= price =>
    case BidTimerExpired =>
      context.system.scheduler.scheduleOnce(deleteTimer.time, this.self, DeleteTimerExpired)
      context become ignoredState
  }

  def activatedState: Receive = LoggingReceive {
    case p @ BidEvent(value) if p.price > price =>
      price = p.price
      buyer = sender()
    case p @ BidEvent(value) if p.price <= price =>
    case BidTimerExpired =>
      context.system.scheduler.scheduleOnce(deleteTimer.time, this.self, DeleteTimerExpired)
      buyer ! YouWinAuction(price)
      context become soldState
  }

  def soldState: Receive = LoggingReceive { case DeleteTimerExpired => context.stop(this.self) }

  def ignoredState: Receive = LoggingReceive { case DeleteTimerExpired =>
    println("Auction stopped without buyer")
    context.stop(this.self)
  }

  override def toString: String = s"Auction $id"
}

object Auction {

  def apply(id: Int, bidTimer: BidTimer, deleteTimer: DeleteTimer, initialPrice: BigDecimal): Auction =
    new Auction(id, bidTimer, deleteTimer, initialPrice)


  case object AuctionCreated
  case object BidTimerExpired
  case object DeleteTimerExpired
  case class BidEvent(price: BigDecimal)

  case class BidTimer(time: FiniteDuration)
  case class DeleteTimer(time: FiniteDuration)

  case class YouWinAuction(price: BigDecimal)

}

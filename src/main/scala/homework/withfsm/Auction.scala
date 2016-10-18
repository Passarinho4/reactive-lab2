package homework.withfsm

import akka.actor.{ActorRef, FSM, LoggingFSM}
import Auction._

import scala.concurrent.duration.FiniteDuration

class Auction(id: Int, bidTime: FiniteDuration, deleteTime: FiniteDuration) extends LoggingFSM[State, Data]{

  startWith(Created, AuctionData(0, None))
  setTimer("bidTimer", BidTimerExpired, bidTime)

  when(Created) {
    case Event(BidTimerExpired, auctionData: AuctionData) => {
      setTimer("deleteTimer", DeleteTimerExpired, deleteTime)
      goto(Ignored)
    }
    case Event(bid: Bid, auctionData:AuctionData) => {
      if(bid.price > auctionData.price) {
        goto(Activated) using AuctionData(bid.price, Some(bid.buyer))
      } else {
        stay()
      }
    }
  }

  when(Ignored) {
    case Event(DeleteTimerExpired, a:AuctionData) => {
      println("Auction stopped without buyer")
      stop()
    }
    case Event(Relist, a:AuctionData) => {
      cancelTimer("deleteTimer")
      goto(Created) using a
    }
  }

  when(Activated) {
    case Event(bid: Bid, auctionData: AuctionData) =>
      if(bid.price > auctionData.price) {
        goto(Activated) using AuctionData(bid.price, Some(bid.buyer))
      } else {
        stay()
      }
    case Event(BidTimerExpired, a:AuctionData) =>
      a.buyer.get ! YouWonTheAuction(a.price)
      setTimer("deleteTimer", DeleteTimerExpired, deleteTime)
      goto(Sold) using a
  }

  when(Sold) {
    case Event(DeleteTimerExpired, a:AuctionData) =>
      stop()
  }

}
object Auction {

  def apply(id: Int, bidTime: FiniteDuration, deleteTime: FiniteDuration): Auction = new Auction(id, bidTime, deleteTime)

  case object BidTimerExpired
  case object DeleteTimerExpired
  case object Relist
  case class Bid(price: BigDecimal, buyer: ActorRef)
  case class YouWonTheAuction(price: BigDecimal)


  sealed trait State

  case object Created extends State
  case object Ignored extends State
  case object Activated extends State
  case object Sold extends State


  sealed trait Data
  final case class AuctionData(price: BigDecimal, buyer: Option[ActorRef]) extends Data

}
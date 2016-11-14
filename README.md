# reactive-lab2

Dr Katarzyna Rycerz 40/40 pkt. 

(20 pkt) Prosze zaimplementować i uruchomic uproszczony system, w ktorym na poczatku tworzona jest pewna liczbe aukcji (aktorow Auction) oraz kilku aktorow Buyer, ktorzy licytuja w istniejacych aukcjach.
Prosze zaprojektować wiadomości jako Case Class'y
Proszę zaprojektować niezbędny wewnetrzny stan aktorow.
Prosze sprawdzac czy licytacja jest poprawna, tj. czy oferta w licytacji przebija aktualna cene.
Aktorzy Buyer moga dostac liste referencji (ActorRef) do aktorow aukcji przez konstruktor.

(10 pkt) Prosze zaimplementować i uruchomic pelna maszyne stanow.
Timery prosze zrealizowac przy pomocy Schedulera: aktor powinien zaplanowac wyslanie wiadomosci do samego siebie oznaczajacej uplyniecie okreslonego terminu (BidTimer: konca licytacji, DeleteTimer: usuniecia licytacji z systemu).

(10 pkt)
Prosze wykorzystac Akka FSM do implementacji maszyny stanow w Aktorze Auction.

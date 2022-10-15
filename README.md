# Laboratorium - monitory

Do laboratorium zostały dołączone przykłady:
- [BlockingQueue.java]()
- [BlockingQueueTest,java]()
- [Pair.java]()
- [PairTest.java]()
- [Swapper.java]()
- [SwapperTest.java]()

Tematem zajęć są prymitywne (”niskopoziomowe”) mechanizmy synchronizacji wątków w Javie. Są one uproszczoną formą monitorów.

Programowanie w Javie z użyciem monitorów jest podatne na błędy. By ułatwić pracę programistom, autorzy języka, za pomocą monitorów, zrealizowali “wysokopoziomowe” narzędzia z pakietu `java.util.concurrent`.

W praktyce zalecane jest programowanie współbieżne z użyciem mechanizmów, które przedstawiliśmy na poprzednich laboratoriach.

Monitory w Javie omawiamy po to, by pokazać, jak mechanizmy wysokopoziomowe są zrealizowane.

### Zamki

Każdy obiekt ma zamek (ang. *lock*), za pomocą którego wątek może zarezerwować dla siebie wyłączną własność tego obiektu.

Instrukcja `synchronized (obiekt) { instrukcje }` sprawdza, czy aktualny wątek założył zamek na obiekcie `obiekt`. Jeśli tak, to wykonywane są `instrukcje`.

Jeśli nie, wątek jest zawieszany do chwili, gdy na obiekcie `obiekt` nie będzie założonego zamka. Następnie na tym obiekcie zakładany jest zamek na czas wykonania instrukcji instrukcje.

Obiekt pamięta, który wątek założył na nim zamek. Zna też liczbę zagnieżdżonych żądań wyłączności zgłoszonych przez ten wątek. Wątek, który ma już wyłączną własność danego obiektu, nie zostanie zawieszony, gdy zażąda jej ponownie.

Alternatywnym dla instrukcji `synchronized` sposobem założenia zamka jest opatrzenie metody modyfikatorem `synchronized`. Daje to efekt taki, jak umieszczenie treści metody w instrukcji `synchronized` zakładającej zamek na obiekcie, dla którego ta metoda się wykonuje, czyli na `this`, lub na klasie, jeśli jest to metoda statyczna.

Zdjęcie zamka z obiektu, na którym czekają jakieś wątki, powoduje odwieszenie jednego z nich. To, który wątek z grupy oczekujących zostanie odwieszony, nie jest określone. W szczególności nie ma gwarancji, że będzie to ten wątek, który czekał najdłużej.

Dla definicji klasy [Pair.java]() program [PairTest.java]() demonstruje działanie zamków.

Zwracamy uwagę, że tylko instrukcje `synchronized` oraz metody z modyfikatorem synchronized respektują zamki. Usunięcie modyfikatora z metody `zamień()` lub `sąRówne()` w klasie `Para<T>` powoduje, że wynik może być niezerowy.

### Wstrzymywanie wątków

Oprócz grupy wątków oczekujących na założenie zamka, obiekt w Javie ma też grupę wątków czekających na zdarzenie z tym obiektem związane.

Mechanizm wstrzymywania wątków w oczekiwaniu na zdarzenie realizują, zdefiniowane w klasie `Object`, metody `wait()`, `notify()` i `notifyAll()`. Wątek może je wykonać na obiekcie tylko, jeśli założył na nim zamek.

Wykonanie na obiekcie metody `wait()` powoduje zawieszenie aktualnego wątku w oczekiwaniu na zdarzenie związane z tym obiektem. Jednocześnie z obiektu zdejmowany jest zamek. Jeśli czekały na to jakieś wątki, jeden z nich jest budzony.

Jeżeli na zdarzenie związane z obiektem nie czeka żaden wątek, to wykonanie metody `notify()` tego obiektu ma efekt pusty. W przeciwnym przypadku jeden z oczekujących na zdarzenie wątków jest przenoszony do grupy wątków oczekujących na założenie zamka na tym obiekcie. Nie jest określone, który z oczekujących wątków zostanie wybrany. Nie musi to być wątek czekający najdłużej.

Metoda `notifyAll()` działa podobnie do `notify()`, ale do grupy wątków oczekujących na zamek przenosi wszystkie wątki, które czekają na zdarzenie związane z tym obiektem.

Zwracamy uwagę na różnicę między opisanym mechanizmem a klasycznymi monitorami. W Javie budzony wątek musi konkurować z innymi wątkami o prawo do założenia zamka na obiekcie. Nie ma więc gwarancji, że w chwili, gdy uda mu się to zrobić, warunek, na który czekał, nadal będzie spełniony. Z tego powodu wywołania `wait()` umieszcza się zwykle w pętli sprawdzającej spełnienie warunku.

Umieszczenie wywołań `wait()` w pętli jest też wskazane ze względu na możliwość wystąpienia zjawiska spontanicznego budzenia wątków. Definicja maszyny wirtualnej dopuszcza sytuację, w której wątek zostanie obudzony z operacji `wait()` mimo, że nie wykonano ani metody `notify()` ani `notifyAll()`.

Nawet, jeżeli chcemy obudzić jeden wątek, często używamy do tego metody `notifyAll()`. Jest to konieczne, jeżeli wątki czekają na różne zdarzenia związane z tym samym obiektem. Wszystkie wątki są wówczas budzone i sprawdzają warunek w pętli. Jeżeli mogą kontynuować wykonanie, robią to, w przeciwnym przypadku ponownie wywołują `wait()`.

Użycie `notifyAll()` pozwala też uniknąć problemu zagubionego budzenia. W przypadku, gdy użycie `notify()` zbiegnie się w czasie z przerwaniem budzonego wątku, może się zdarzyć, że żaden wątek nie zostanie obudzony z wykonania `wait()`.

Definicja klasy [Swapper.java]() wraz z programem [SwapperTest.java]() demonstruje wstrzymywanie wątków.

### Ćwiczenie punktowane

Uzupełnij implementację kolejki blokującej `BlockingQueue.java`.

Wątki synchronizuj za pomocą mechanizmów z dzisiejszych zajęć.

Implementacja powinna przechodzić dwa pierwsze testy z pliku `BlockingQueueTest.java`.

Opcjonalnie (zadanie niepunktowane) zaimplementuj kolejki o pojemności 0, które pozwolą na realizację mechanizmu **rendezvous**. Chodzi o to, aby dwa wątki musiały się "spotkać", aby przekazać sobie obiekt, bez pośrednictwa bufora. Wątek, który chce coś włożyć do kolejki, musi poczekać, aż ktoś zgłosi chęć wyjęcia obiektu z kolejki. 

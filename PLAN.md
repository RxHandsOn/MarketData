# Exercice 0 - vérifier l'installation
Depuis votre IDE, lancer la classe Market puis la classe Application avant d'afficher la page [index.html](http://localhost:8000) dans un navigateur.  
Il n'y aura pas encore grand chose d'intéressant à l'écran, il faudra attendre l'exercice 6 pour voir des données à l'écran...

# Exercice 1 -  transformation simple
 Le but est de rendre opérationnel la classe **ForexServer** pour quelle propage les taux de change euro / dollar
 sous forme de **Double** provenant du service ForexProvider (via forexEventStreamClient.readServerSideEvents()).  
 La méthode **Quote::fromJson** pourra être utilisée pour parser les données brutes obtenues et créer des DTOs **Quote**.  
 Test d'acceptance: Test 1 dans **ForexServerTest**  
 Opérateurs Rx: map  

# Exercice 2 - on ne prend que le premier !
 Toujours dans la classe **ForexServer**, il faut maintenant modifier le code pour qu'à chaque souscription on ne
 renvoie qu'une seule valeur.
 Test d'acceptance: Test 2 dans **ForexServerTest**  
 Opérateurs Rx: take  

# Exercice 3 -  premier filtre
 Dans la classe **StockQuoteServer**, faire en sorte de prendre en compte le paramètre HTTP "STOCK" pour filtrer les
 cotations et ne pas tout envoyer au navigateur.  
 Test d'acceptance: Test 3 dans **StockQuoteServerTest**  
 Opérateurs Rx: filter  

# Exercice 4 -  premiers pas en Typescript
Dans le fichier  **Stock.ts**, modifier la méthode **parseRawStream** pour parser les messages json venant du server et renvoyer un flux d'objects **Quote**  
Test d'acceptance: Test 4 dans **StockTest.ts**  
Opérateurs Rx: map  

# Exercice 5 -  ça monte et ça baisse
Ici le but est de détecter quand le cours monte et quand il baisse. Dans le flux renvoyé par **detectTrends** il y aura un un événement "vert" lorsque le cours augmente, un événement "rouge" lorsqu'il baisse. On va donc avoir besoin de comparer chaque cotation avec la cotation précédente. On va utiliser l'opérateur zip pour combiner 2 flux:

  1. le flux de cotations tel quel
  2. le même flux de cotations mais décalé d'un événement grâce à l'opérateur skip

Ainsi nous allons pouvoir comparer les cotations 2 à 2 et renvoyer un flux de résultats en sorti de l'opérateur zip.

Test d'acceptance: Test 5 et 6 dans **StockTest.ts**  
Opérateurs Rx: skip et zip  

# Exercice 6 -  première combinaison avec flatmap
C'est bien de pouvoir filtrer, faut-il encore savoir ce que l'on a sous la main... L'idée de cet exercice est de récupérer
les informations sur les stocks qui sont actives, celles pour lesquelles il y a eu au moins une cotation depuis la souscription.  
Dans la classe **StockServer**, pour chaque cotation, venant de **quoteEventStreamClient.readServerSideEvents()**, demander à partir du code de la cotation les infos sur la stock correspondante à l'aide de **stockClient.request()**.  
**Quote::fromJson** et **Stock::fromJson** pourront être utilisées pour parser les données brutes et créer des DTOs.  
Test d'acceptance: Test 7 dans **StockServerTest**  
Opérateurs Rx: map & flatmap   

# Exercice 7 - pas de doublons
On reprend l'exercice précédent et cette fois-ci on utilise l'opérateur distinct pour ne pas envoyer plusieurs fois les informations sur une même stock.  
Test d'acceptance: Test 8 dans **StockServerTest**  
Opérateurs Rx: distinct & map

# Exercice 8 - on coupe au bout de 10 secondes
Le code écrit jusqu'à maintenant fonctionne mais on a un souci: la connection HTTP utilisée pour récupérer les descriptions des stocks n'est jamais coupée. Pour s'en convaincre vous pouvez ouvrir et fermer plusieurs fois l'application [WEB](http://localhost:8000) et regarder ce qui est loggé au niveau des process java.  
On va donc modifier la classe **StockServer** pour que le flux soit coupé au bout de 10 secondes.  
Test d'acceptance: Test 9 dans **StockServerTest**  
Opérateurs Rx: distinct & map  

# Exercice 8 -  gestion d'état et calcul d'un prix vwap
 On va maintenant consommer un flux de transactions pour calculer pour un titre, le volume d'actions échangées
 ainsi qu'un prix vwap, c'est à dire une moyenne pondérée du prix.  
 En gros si 10 actions google ont été vendu à 7000$ puis 20 actions à 15200$, alors le prix vwap est égale à
 (7000 + 15200) / (10 + 20) = 740$    
Comme on est gentil, ce petit calcul est déjà implémenté dans la classe **VWap**, il suffit d'utiliser la méthode **Vwap::addTrade**  
Test d'acceptance: Test 10 et Test 11 dans **VwapServerTest**   
Opérateurs Rx: map, filter, skip & scan  

# Exercice 9 -  échantillonage
 Dans la vraie vie, énormément de transactions sont réalisées sur les marchés. Pour éviter d'envoyer vers l'interface
 web plus de prix vwap que nécessaire, nous allons maintenant utiliser l'opérateur Rx "sample" pour limiter le nombre de
 messages envoyés sur le web.  
 Attention il y a un piège, pour que le test passe il faut penser au scheduler...  
 Test d'acceptance: Test 12 dans **VwapServerTest**  
 Opérateurs Rx: sample

# Exercice 10 -  combinaison cotations / taux de changes
 Le but maintenant est de faire en sorte que les cotations transmises par la classe **StockQuoteServer** soient exprimées
 en euros, et non en dollars.  
 A chaque cotation du flux stockEventStreamClient.readServerSideEvents(), il faut appliquer un taux de change venant du
 flux forexEventStreamClient.readServerSideEvents().  
 Attention, il ne faut pas générer plus de cotations sur une stock que ce que l'on a en entrée. En gros si le taux
 de change fluctue alors que le cours de l'action en dollar ne varie pas, il ne faut pas générer d'événement.
 Test d'acceptance: Test 13 dans **StockQuoteServerTest**  
 Opérateurs Rx: map, take & flatMap !!  

# Exercice 11 - Cache "last value" sur le forex
On va maintenant apporter une petite modification à la classe **StockQuoteServerTest**. Quand une cotation sur une stock arrive,
on veut maintenant que le dernier cours de change euros/dollars connu soit utilisé. Cela veut dire que quand une cotation sur une stock en dollar arrive, pas besoin d'attendre de recevoir une nouvelle cotation EUR/USD, il suffit d'utiliser la dernière valeur connu. Pour pouvoir répondre à ce nouveau besoin il est fortement recommandé d'utiliser la classe **BehaviorSubject**.   
Test d'acceptance: Test 14 dans **StockQuoteServerTest**

# Exercice 12 - Se désinscrire quand il faut...
Vous avez peut-être un soucis avec le code écrit précédemment: vous continuez peut-être d'écouter le flux forex lorsque plus personne n'écoute le flux stock. L'idée ici est donc d'arrèter les souscriptions au flux forex quand s'arrètent les souscriptions au flux sur les stocks.  
Test d'acceptance: Test 15 dans **StockQuoteServerTest**  
Opérateurs Rx: doOnUnsubscribe

# Exercice 13 - Ne pas attendre indéfiniment
Si jamais pour une raison ou un autre il y a un souci avec le flux forex, votre serveur va avoir un gros problème. Les cotations sur les stocks en dollars risquent de s'accumuler jusqu'à saturation de la mémoire de la JVM.
Pour résoudre ce problème on va limiter le temps d'attente d'une cotation forex à 5 secondes, temps au dela duquel un événement d'erreur sera lancé.  
Test d'acceptance: Test 16 dans **StockQuoteServerTest**  
Opérateurs Rx: timeout

# Exercice 14 - min/max glissants
Retour sur le code Typescript. On va maintenant implémenter les méthodes **minFromPrevious** et **maxFromPrevious** dans **Stock.ts**. L'idée est d'avoir un flux contenant la valeur minimum/maximum des n dernières cotations.  
Test d'acceptance: Tests 17, 18, 19 et 20 dans **StockTest.ts**  
Opérateurs Rx: windowCount, flatMap, map, min & max  

# Exercice 15 - des souscriptions en double
Si vous ouvrez plusieurs fois l'application WEB dans plusieurs onglets de votre navigateur, vous allez constater que la charge sur les serveurs va augmenter de manière significative (vous allez vite entendre le ventilo de votre portable).  
Côté "market" les messages vont être envoyé en double et c'est dommage... Dans la classe **Market** justement, passez le flag **flaky** à true histoire de générer des erreurs lorsque plusieurs clients essayent de soucrire au même flux.  
En fait le problème vient de la classe **MulticastEventStreamClient** qui est censée générer des flux "chauds"...
Test d'acceptance: Test 21 dans **MulticastEventStreamClientTest**  
Opérateurs Rx: publish & refcount  

# Exercice 16 - réessayer en cas d'erreur
Toujours dans la classe **MulticastEventStreamClient**, on va cette fois-ci mettre en place une politique de reconnection en cas d'erreur. Si jamais on reçoit un événement d'erreur, on attend 2 secondes puis on se reconnecte.
Test d'acceptance: Test 22 dans **MulticastEventStreamClientTest**  
Opérateurs Rx: retryWhen & delay  


TODO - idées pour la suite    
Typescript : plus grosse progression / baisse (combineLatest)
Typescript: gestion des reconnections avec retryWhen  

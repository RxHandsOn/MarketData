# Exercice 0 - vérifier l'installation
Depuis votre IDE, lancer la classe Market puis la classe Application avant d'afficher la page index.html dans un navigateur.

# Exercice 1 -  transformation simple
 Le but est de rendre opérationnel la classe **ForexServer** pour quelle propage les taux de change euro / dollar 
 sous forme de **Double** provenant du service ForexProvider (via forexEventStreamClient.readServerSideEvents())  
 Test d'acceptance: Test 1 dans **ForexServerTest**  
 Opérateurs Rx: map  
 Mentionner les classes utiles: Quote
 

# Exercice 2 -  premier filtre
 Dans la classe **StockServer**, faire en sorte de prendre en compte le paramètre HTTP "STOCK" pour filtrer les 
 cotations et ne pas tout envoyer au navigateur. 
 Test d'acceptance: Test 2 dans **StockServerTest**  
 Opérateurs Rx: filter  
 Quel eventStream client utiliser ? Expliquer les paramètres ?

# Exercice 3 -  combinaison cotations / taux de changes
 Le but maintenant est de faire en sorte que les cotations transmises par la classe **StockServer** soient exprimées 
 en euros, et non en dollars.  
 A chaque cotation du flux stockEventStreamClient.readServerSideEvents(), il faut appliquer un taux de change venant du 
 flux forexEventStreamClient.readServerSideEvents().  
 Test d'acceptance: Test 3 dans **StockServerTest**  
 Opérateurs Rx: map, take & flatMap !!  
 
 En fait, expliqué comme cela, sans tenir compte des opérateurs, j'ai utilisé un combineLatest, je pense qu'il faudrait 
 une petite phrase pour expliquer on ne s'attend pas à recevoir une nouvelle cotation, si le cours usd/eur a changé.
 
 Du coup, j'ai pensé à withLatestFrom : http://rxmarbles.com/#withLatestFrom, mais il n'est pas dispo en Java.
 Tu penses qu'il pourrait convenir ?
 
 Sinon, effectivement, on a besoin du flatMap, et du take(1) qui va nous donner le premier prix disponible sur le flux.
 Je trouve ça un poil compliqué, c'est pas forcément le cas d'usage le plus simple pour flatMap :(
 Sinon, je n'ai pas trouvé l'énoncé du test-7, j'ai ajouté un Test 7-bis qui fait planter ton implémentation 
 si l'on reçoit plusieurs valeurs.

# Exercice 4 -  gestion d'état et calcul d'un prix vwap
 On va maintenant consommer un flux de transactions pour calculer pour un titre, le volume d'actions échangées 
 ainsi qu'un prix vwap, c'est à dire une moyenne pondérée du prix.  
 En gros si 10 actions google ont été vendu à 7000$ puis 20 actions à 15200$, alors le prix vwap est égale à 
 (7000 + 15200) / (10 + 20) = 740$  
 Test d'acceptance: Test 4 et Test 5 dans **VwapServerTest**  
 Opérateurs Rx: map, filter, skip & scan 
 
 Mentionner les classes utiles: Trade (Trade.fromJson), Vwap.
 J'ai été gêné par le fait de devoir gérer une liste d'actions à filtre, j'ai vu que dans ton implémentation, tu ne 
 gères que la première. Si on veut gérer plusieurs actions, c'est pas aussi trivial, il va probablement falloir faire 
 un groupBy, un flatMap et dans le flatMap un scan.
 L'ajout d'un trade à une vwap n'est pas trivial à coder, et ça ne touche pas exactement au sujet du handson. 
 En venant à un handson, je n'ai pas spécialement envie de galérer sur des problèmes ne touchant pas au handson.
 On pourrait ajouter une méthode implémentée Vwap.addTrade() qui renvoie une nouvelle Vwap ou bien fournir une 
 implémentation en pseudo-code.

# Exercice 5 -  échantillonage
 Dans la vraie vie, énormément de transactions sont réalisées sur les marchés. Pour éviter d'envoyer vers l'interface 
 web plus de prix vwap que nécessaire, nous allons maintenant utiliser l'opérateur Rx "sample" pour limiter le nombre de 
 messages envoyés sur le web.  
 Attention il y a un piège, pour que le test passe il faut penser au scheduler...
 Test d'acceptance: Test 6  
 Opérateurs Rx: sample 

TODO - idées pour la suite    
Typescript : tendance rouge/vert si ça monte ou ça descend  (skip, zip)
Typescript : moyenne glissante (window + flatmap)  
Typescript : plus grosse progression / baisse (combineLatest)
Java: cache stock / forex  
Typescript: partage d'un flux sse avec publish/refCount    
Typescript: gestion des reconnections avec retryWhen  




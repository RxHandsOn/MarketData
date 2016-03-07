# Exercice 0 - vérifier l'installation
Depuis votre IDE, lancer la classe Market puis la classe Application avant d'afficher la page index.html dans un navigateur.

# Exercice 1 -  transformation simple
 Le but est de rendre opérationnel la classe **ForexServer** pour quelle propage les taux de change euro / dollar 
 sous forme de **Double** provenant du service ForexProvider (via forexEventStreamClient.readServerSideEvents()).  
 La méthode **Quote::fromJson** pourra être utilisée pour parser les données brutes obtenues et créer des DTOs **Quote**.  
 Test d'acceptance: Test 1 dans **ForexServerTest**  
 Opérateurs Rx: map  
 

# Exercice 2 -  premier filtre
 Dans la classe **StockServer**, faire en sorte de prendre en compte le paramètre HTTP "STOCK" pour filtrer les 
 cotations et ne pas tout envoyer au navigateur. 
 Test d'acceptance: Test 2 dans **StockServerTest**  
 Opérateurs Rx: filter  

# Exercice 3 -  combinaison cotations / taux de changes
 Le but maintenant est de faire en sorte que les cotations transmises par la classe **StockServer** soient exprimées 
 en euros, et non en dollars.  
 A chaque cotation du flux stockEventStreamClient.readServerSideEvents(), il faut appliquer un taux de change venant du 
 flux forexEventStreamClient.readServerSideEvents().  
 Test d'acceptance: Test 3 dans **StockServerTest**  
 Opérateurs Rx: map, take & flatMap !!  

# Exercice 4 -  gestion d'état et calcul d'un prix vwap
 On va maintenant consommer un flux de transactions pour calculer pour un titre, le volume d'actions échangées 
 ainsi qu'un prix vwap, c'est à dire une moyenne pondérée du prix.  
 En gros si 10 actions google ont été vendu à 7000$ puis 20 actions à 15200$, alors le prix vwap est égale à 
 (7000 + 15200) / (10 + 20) = 740$  
 Test d'acceptance: Test 4 et Test 5 dans **VwapServerTest**  
 Opérateurs Rx: map, filter, skip & scan 

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




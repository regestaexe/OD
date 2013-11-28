file di configurazione dell'ontologia generale
=============================================

```
	"id" : "/*/@rdf:about",
	"titolo" : "/*/dc:title/#text", 
	"rdfsLabel" : "/*/rdfs:label/#text",
	"twittedBy" : {
		"container" : "/*/ods:twittedBy",
		"valori" : "/@rdf:resource"
	}
```

* id è un campo singolo
* titolo è un campo singolo di tipo text
* twittedBy è un campo ripetibile

le chiavi della mappa sono quelle utilizzate dentro tutti file di configurazione di tipo HTML  
le impostazione possono essere sovrascritti nel pathjson.js che si trova nella cartella di configurazione di ogni singo archivio 
globalOpt.document.jsonMap = {
	"id" : "/*/@rdf:about",
	"titolo" : "/*/dc:title/#text",
	"subtitolo" : "/*/dcterms:title/#text",
	"rdfsLabel" : "/*/rdfs:label/#text",
	"biordfsLabel" : "/*/bio:Birth/rdfs:label/#text",
	"dateBirth" : "/*/bio:Birth/bio:date/#text",
	"placeBirth" : "/*/bio:Birth/bio:place/#text",
	"dateDeath" : "/*/bio:Death/bio:date/#text",
	"placeDeath" : "/*/bio:Death/bio:place/#text",
	"biography" : "/*/bio:biography/#text",
	"nome" : "/*/foaf:firstName/#text",
	"cognome" : "/*/foaf:surname/#text",
	"template" : "/*/ods:template/#text",
	"marginTemplate" : "/*/ods:marginTemplate/#text",
	"tagTemplate" : "/*/ods:tagTemplate/#text",
	"tag" : "/*/ods:tag/#text",
	"numero" : "/*/dc:identifier/#text",
	"descrizione" : "/*/dc:description/#text",
	"dataPubblicazione" : "/*/dc:date/dcterms:issued/#text",
	"note" : "/*/dc:note/#text",
	"data" : "/*/dc:date/#text",
	"dataPubblicazioneSimple" : "/*/dcterms:issued/#text",
	"autore" : "/*/dc:creator/#text",
	"autoreTerms" : "/*/dcterms:creator/#text",
	"editore" : "/*/dc:publisher/#text",
	"rivista" : "/*/dcterms:isPartOf/#text",
	"citazioneBibliografica" : "/*/dcterms:bibliographicCitation/#text",
	"destinatario" : "/*/dcterms:audience/@rdf:resource",
	"destinatarioText" : "/*/dcterms:audience/#text",
	"ente_produttore" : "/*/dcterms:provenance/#text",
	"audience" : "/*/dcterms:accessRights/#text",
	"type" : "/*/dc:type/#text",
	"mediator" : "/*/dcterms:mediator/#text",
	"wfStatus" : "/*/ods:status/#text",
	"wfChargeTo" : "/*/ods:chargeTo/#text",
	"dctermsFormat" : "/*/dcterms:format/@rdf:resource",
	"nfoFileName" : "/*/nfo:fileName/#text",
	"nfoRemoteDataObject" : "/*/nfo:remoteDataObject/@rdf:resource",
	"nfoHashValue" : "/*/nfo:hashValue/#text",
	"nfoFileSize" : "/*/nfo:fileSize/#text",
	"dctermsAvailable" : "/*/dcterms:available/#text",
	"nfoFileUrl" : "/*/nfo:fileUrl/@rdf:resource",
	"dctermsSpatial" : "/*/dcterms:spatial/#text",
	"odsFileMetadata" : "/*/ods:fileMetadata/#text",
	"odsOcr" : "/*/ods:ocr/#text",
	"skosNotation" : "/*/skos:notation/#text",
	"source" : "/*/dc:source/#text",
	"url" : "/*/dc:url/#text",
	"typeFine" : "/org:ChangeEvent[child::dc:type->#text='soppressione']/dc:type/#text",
	"attoFine" : "/org:ChangeEvent[child::dc:type->#text='soppressione']/dc:description/#text",
	"typeInizio" : "/org:ChangeEvent[child::dc:type->#text='istituzione']/dc:type/#text",
	"attoInizio" : "/org:ChangeEvent[child::dc:type->#text='istituzione']/dc:description/#text",
	"dataFine" : "/org:ChangeEvent[child::dc:type->#text='soppressione']/dc:date/#text",
	"dataInizio" : "/org:ChangeEvent[child::dc:type->#text='istituzione']/dc:date/#text",
	"hasBeginning" : "/*/time:hasBeginning/#text",
	"hasEnding" : "/*/time:hasEnding/#text",
	"subOrganizationOf" : "/*/org:subOrganizationOf/#text",
	
	//amministrazione//
	"originalOrganization" : "/org:ChangeEvent[child::dc:type->#text='soppressione']/org:originalOrganization/@rdf:resource",
	"resultingOrganization" : "/org:ChangeEvent[child::dc:type->#text='istituzione']/org:resultingOrganization/@rdf:resource",

	"originalOrganizationAbout" : "/org:ChangeEvent[child::dc:type->#text='soppressione']/@rdf:about",
	"resultingOrganizationAbout" : "/org:ChangeEvent[child::dc:type->#text='istituzione']/@rdf:about",
	
	
	
	"subOrganizationOf" : "/*/org:subOrganizationOf/@rdf:resource",	
	"role" : {
		"container" : "/*/org:role",
		"valori" : "/@rdf:resource"
	},	
	"member" : {
		"container" : "/*/org:member",
		"valori" : "/@rdf:resource"
	},
	"organization" : {
		"container" : "/*/org:organization",
		"valori" : "/@rdf:resource"
	},	
	"sourceMembership" : {
		"container" : "/*/dc:source",
		"valori" : "/#text"
	},

	//+++++++++++++++//
	
	
	"skosBroader" : {
		"container" : "/*/skos:broader",
		"valori" : "/@rdf:resource"
	},
	"odsPubblicato" : {
		"container" : "/*/ods:publishedOn",
		"valori" : "/@rdf:resource"
	},
	"owlSameAs" : {
		"container" : "/*/owl:sameAs",
		"valori" : "/@rdf:resource"
	},
	"autoreRip" : {
		"container" : "/*/dc:creator",
		"valori" : "/#text"
	},
	"autoreObjRip" : {
		"container" : "/*/dcterms:creator",
		"valori" : "/@rdf:resource"
	},
	"coverage" : {
		"container" : "/*/dc:coverage",
		"valori" : "/#text"
	},
	"spatial" : {
		"container" : "/*/dcterms:spatial",
		"valori" : "/#text"
	},
	"dipartimento" : {
		"container" : "/*/ods:rif_unitaOrganizzativa",
		"valori" : "/@rdf:resource"
	},
 
	"contributor" : {
		"container" : "/*/dc:contributor",
		"valori" : "/#text"
	},   
	"allegati" : {
		"container" : "/*/dc:relation",
		"valori" : "/@rdf:resource"
	},
	"dctermsReferences" : {
		"container" : "/*/dcterms:references",
		"valori" : "/@rdf:resource"
	}, 
	"lexPrincipale" : {
		"container" : "/*/dcterms:isReferencedBy",
		"valori" : "/@rdf:resource"
	},

	"gestione_utente" : {
		"container" : "/*/skos:changeNote",
		"valori" : "/dc:creator/#text"
	},
	"gestione_data" : {
		"container" : "/*/skos:changeNote",
		"valori" : "/dc:date/#text"
	},
	"gestione_azione" : {
		"container" : "/*/skos:changeNote",
		"valori" : "/rdf:value/#text"
	},  
	"isbdTitoloProprio" : "/*/isbd:P1004/#text",
	"isbdTitoloProprioPeriodico" : "/*/isbd:P1012/#text",
	"isbdSottotitolo" : "/*/isbd:P1006/#text",
	"isbdIndicazioneResp" : "/*/isbd:P1007/#text",
	"dctermsCitation" : "/*/dcterms:bibliographicCitation/#text",
	"isbdNumerazione" : "/*/isbd:P1015/#text",
	"isbdLuogo" : "/*/isbd:P1016/#text",
	"isbdEditore" : "/*/isbd:P1017/#text",
	"isbdRespEd" : "/*/isbd:P1010/#text",
	"isbdNserie" : "/*/isbd:P1031/#text",
	"isbdDescrizione" : "/*/isbd:P1022/#text",
	"isbdIndicazione" : "/*/isbd:P1026/#text",
	"lingua" : "/*/dc:language/#text",
	"biboVolume" : "/*/bibo:volume/#text",
	"biboEdition" : "/*/bibo:edition/#text",
	"biboIssue" : "/*/bibo:issue/#text",
	"biboPage" : "/*/bibo:pages/#text",
	"isbdAnno" : "/*/isbd:P1018/#text",
	"biboStatus" : "/*/bibo:status/#text",
	"biboAbstract" : "/*/bibo:abstract/#text",
	"dctermsModified" : "/*/dcterms:modified/#text",
	"dctermsExtent" : "/*/dcterms:extent/#text",
	"skosAltLabel" : "/*/skos:altLabel/#text",
	"skosNote" : "/*/skos:note/#text",
	"skosEditorialNote" : "/*/skos:editorialNote/#text",
	"foafDepiction" : "/*/foaf:depiction/@rdf:resource",
	"foafHomePage" : "/*/foaf:homepage/@rdf:resource",
	"dctermsAccessRights" : "/*/dcterms:accessRights/#text",

	"dctermsSource" : {
		"container" : "/*/dcterms:source",
		"valori" : "/@rdf:resource"
	},
	"ente_resource" : {
		"container" : "/*/dcterms:provenance",
		"valori" : "/@rdf:resource"
	}, 
	"dctermsIsPartOf" : {
		"container" : "/*/dcterms:isPartOf",
		"valori" : "/@rdf:resource"
	},

	"dctermsContributorString" : {
		"container" : "/*/dcterms:contributor",
		"valori" : "/#text"
	},

	"dctermsContributor" : {
		"container" : "/*/dcterms:contributor",
		"valori" : "/@rdf:resource"
	},
	"dctermsCreatorString" : {
		"container" : "/*/dcterms:creator",
		"valori" : "/#text"
	},
	"dctermsCreator" : {
		"container" : "/*/dcterms:creator",
		"valori" : "/@rdf:resource"
	},
	"dctermsSubject" : {
		"container" : "/*/dcterms:subject",
		"valori" : "/@rdf:resource"
	},
	"subject" : {
		"container" : "/*/dc:subject",
		"valori" : "/#text"
	},
	"destinatarioMulti" : {
		"container" : "/*/dcterms:audience",
		"valori" : "/@rdf:resource"
	},
	"destinatarioMultiText" : {
		"container" : "/*/dc:audience",
		"valori" : "/#text"
	},
	"twittedBy" : {
		"container" : "/*/ods:twittedBy",
		"valori" : "/@rdf:resource"
	},

};
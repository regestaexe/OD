var type = "monografia";

globalOpt.document.jsonMap.configurazioni = {
	"type" : type,
	"root" : "rdf:RDF",
	"idRecordType" : "4",
	"encoding" : "UTF-8",
	"xmlns" : "http://lod.xdams.org/ontologies/ods/" + type + ".rdf/",
	"xml:base" : "http://lod.xdams.org/ontologies/ods/" + type + ".rdf",
	"rdfName" : type + ".rdf",
	"fixedvaluesprefix" : "/rdf:RDF/bibo:Book",
	"fixedvalues" : [ {
		"/@rdf:about" : "."
	}, {
		"/skos:changeNote/dc:date/text()" : top.Application.today
	}, {
		"/skos:changeNote/rdf:value/text()" : "create"
	}, {
		"/skos:changeNote/dc:creator/text()" : top.Application.user.id
	} ],
	"imports" : [ {
		"xmlns:rdf" : "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	}, {
		"xmlns:rdfs" : "http://www.w3.org/2000/01/rdf-schema#"
	}, {
		"xmlns:bibo" : "http://purl.org/ontology/bibo/"
	}, {
		"xmlns:dc" : "http://purl.org/dc/elements/1.1/"
	}, {
		"xmlns:dcterms" : "http://purl.org/dc/terms/"
	}, {
		"xmlns:ods" : "http://lod.xdams.org/ontologies/ods/"
	}, {
		"xmlns:cdec" : "http://lod.xdams.org/ontologies/cdec/"
	}, {
		"xmlns:cdec" : "http://lod.xdams.org/ontologies/cdec/"
	}, {
		"xmlns:owl" : "http://www.w3.org/2002/07/owl#"
	}, {
		"xmlns:xsd" : "http://www.w3.org/2001/XMLSchema#"
	}, {
		"xmlns:skos" : "http://www.w3.org/2008/05/skos#"
	}, {
		"xmlns:foaf" : "http://xmlns.com/foaf/0.1/"
	}, {
		"xmlns:bio" : "http://purl.org/vocab/bio/0.1/"
	}, {
		"xmlns:xsi" : "http://www.w3.org/2001/XMLSchema-instance"
	}, {
		"xmlns:isbd" : "http://iflastandards.info/ns/isbd/elements/"
	} ]
};

globalOpt.document.jsonMap["dipartimento"] = {
		"container" : "/*/dcterms:provenance",
		"valori" : "/@rdf:resource"
	};

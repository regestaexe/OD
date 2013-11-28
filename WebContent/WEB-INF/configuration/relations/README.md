file di configurazione della relazioni
=============================================

```
<element name="file"
 		id_relation="22"
 		get_from="17"
		insert_to="1">
		/rdf:RDF/*[name()!='owl:Ontology']/@rdf:about,/rdf:RDF/*[name()!='owl:Ontology']/ods:rif_file[99]/@rdf:resource
</element>
```
* name = nome della relazione, utile solo agli humans
* id_relation = relations.idRelation del db SQL
* insert_to = record che verrà modificato con la scrittura della tripla di relazione (in questo caso rif_file/@rdf:resource
* get_from = archivio da cui sarà recuperato @rdf:about da inserire del @rdf:resource precedente

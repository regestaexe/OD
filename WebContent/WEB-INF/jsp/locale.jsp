<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>  
<%--LOGIN.HTML--%>
<spring:message code="login.user"      text="Utente" var="loginUser" htmlEscape="true" javaScriptEscape="false"/>
<spring:message code="login.password"  text="Password" var="loginPassword" htmlEscape="true" javaScriptEscape="false"/>
<spring:message code="login.company"   text="Società" var="loginCompany" htmlEscape="false" javaScriptEscape="false"/>
<spring:message code="login.remember"  text="Ricorda per 2 settimane" var="loginRemember" htmlEscape="true" javaScriptEscape="false"/>
<spring:message code="login.message"   text="Impossibile eseguire l'accesso, riprova" var="loginMessage" htmlEscape="true" javaScriptEscape="false"/>
<%--DESKTOP.HTML--%>
<spring:message code="tab.archive"       text="Archivi" var="tabArchive" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="tab.admin"         text="Funzioni di Gestione" var="tabAdmin" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="tab.searchBuilder" text="Ricerca" var="tabSearchBuilder" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="tab.showcase"      text="Bacheca" var="tabShowcase" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="footer.message"    text="Benvenuto" var="footerMessage" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="footer.logout"     text="Esci" var="footerLogout" htmlEscape="true" javaScriptEscape="true"/>
<%--BUTTONS--%>
<spring:message code="button.cancel"          text="Annulla"  var="buttonCancel" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="button.create"          text="Crea"     var="buttonCreate" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="button.edit"            text="Modifica" var="buttonEdit"   htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="button.delete"          text="Cancella" var="buttonDelete" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="button.reset"           text="Ripristina documento"  var="buttonReset"  htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="button.save"            text="Salva"    var="buttonSave"   htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="button.search"          text="Cerca"    var="buttonSearch" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="button.searchreset"     text="Ripristina"    var="buttonSearchReset" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="button.version"         text="Gestione Versioni"  var="buttonVersion" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="button.version.new"     text="Crea nuova Versione"  var="buttonVersionNew" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="button.version.restore" text="Ripristina questa Versione"  var="buttonVersionRestore" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="button.version.delete"  text="Cancella Versione"  var="buttonVersionDelete" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="button.adv.search.button"   text="Avvia la ricerca"  var="buttonAdvSearchButton" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="message.status.empty.field" text="Inserire almeno un parametro di ricerca"  var="messageStatusEmptyField" htmlEscape="true" javaScriptEscape="true"/>
<%--WORKSPACE.HTML--%>
<spring:message code="skos.addrelation.button.label"             text="aggiungi alla relazione selezionata"  var="skosAddrelationButtonLabel" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skos.westpanel.title"                      text="Navigazione & Ricerche"  var="skosWestpanelTitle" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skos.westpanel.tree.title"                 text="Albero"  var="skosWestpanelTreeTitle" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skos.westpanel.search.title"               text="Ricerca semplice"  var="skosWestpanelSearchTitle" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skos.westpanel.search.concept.input.value" text="Seleziona un Concetto ..."  var="skosWestpanelSearchConceptInputValue" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skos.westpanel.search.footer"              text="Risultati {0} - {1} di {2}"  var="skosWestpanelSearchFooter" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skos.westpanel.search.noresults"           text="Nessuna occorrenza trovata"  var="skosWestpanelSearchNoresults" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skos.westpanel.relation.title"     		 text="Gestione relazioni"  var="skosWestpanelRelationTitle" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skos.center.skos.tab"                      text="Modifica"  var="skosCenterSkosTab" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skos.center.relations.tab"                 text="Gestisci Relazioni"  var="skosCenterRelationsTab" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skos.center.visual.tab"                    text="Navigazione Visuale"  var="skosCenterVisualTab" htmlEscape="true" javaScriptEscape="true"/>

<spring:message code="message.status"                            text="Stato"  var="messageStatus" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="message.status.text.operation"             text="Operazione eseguita con successo."  var="messageStatusTextOperation" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="message.status.error"                      text="Errore inaspettato!Prova di nuovo."  var="messageStatusError" htmlEscape="true" javaScriptEscape="true"/>

 
<spring:message code="skos.message.deleting.concept"             text="Elimina tutti Concetti narrower/hasTopConcept prima di eseguire la cancellazione!"  var="skosMessageDeletingConcept" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="center.openDams.short.tab"                 text="Scheda Breve"  var="centeropenDamsShortTab" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="center.xml.tab"                            text="XML"  var="centerXmlTab" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="center.adv.search.tab"                     text="Ricerca Avanzata"  var="centerAdvSearchTab" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="center.unlinked.tab"                       text="Scollegati"  var="centerUnlinkedTab" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="center.duplicate.tab"                      text="Duplicati"  var="centerDuplicateTab" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="center.inscheme.tab"                       text="Orfani"  var="centerOrphanTab" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="message.status.title"                      text="Attendere prego"  var="messageStatusTitle" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="message.status.text"                       text="Configurazione richiesta in corso..."  var="messageStatusText" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="message.status.loading"                    text="Caricamento..."  var="messageStatusLoading" htmlEscape="true" javaScriptEscape="true"/>
<%--SKOS.HTML--%>
<spring:message code="arrow.previos" text="pagina precedente"  var="arrowPrevios" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="arrow.first"   text="prima pagina"       var="arrowFirst" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="arrow.next"    text="pagina successiva"  var="arrowNext" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="arrow.last"    text="ultima pagina"      var="arrowLast" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="arrow.page"    text="PAGINA"             var="arrowPage" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="arrow.of"      text="DI"                 var="arrowOf" htmlEscape="true" javaScriptEscape="true"/>
<%--SKOS.HTML TREE.HTML contextMenu--%>
<spring:message code="contextMenu.new"       text="Crea nuovo"     var="contextMenuNew"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="contextMenu.modify"    text="Modifica"       var="contextMenuModify" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="contextMenu.copy"      text="Copia"          var="contextMenuCopy" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="contextMenu.cut"       text="Taglia"         var="contextMenuCut" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="contextMenu.paste"     text="Incolla"        var="contextMenuPaste" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="contextMenu.delete"    text="Cancella"       var="contextMenuDelete" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="contextMenu.reset"     text="Annulla"        var="contextMenuReset" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="contextMenu.viewxml"   text="Visualizza XML" var="contextMenuViewxml" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="contextMenu.selecton"  text="Seleziona ON"   var="contextMenuSelecton" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="contextMenu.selectoff" text="Seleziona OFF"  var="contextMenuSelectoff" htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="contextMenu.importxml" text="Importa XML"    var="contextMenuImportxml" htmlEscape="true" javaScriptEscape="true"/>
<%--SKOS_TAB.HTML SKOS_ADV_SEARCH.HTML UNLINKED.html contextMenu--%>
<spring:message code="skosTab.confirm.title"       text="Conferma"     var="skosTabconfirmtitle"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skosTab.confirm.message"       text="Sei sicuro di voler cancellare questa relazione?"     var="skosTabconfirmmessage"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skosTab.search.concept.input.value"       text="Seleziona un Concetto "     var="skosTabsearchconceptinputvalue"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skosTab.search.concept.title"       text="Ricerca Concept"     var="skosTabsearchconcepttitle"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skosTab.search.concept.button.add"       text="Aggiungi"     var="skosTabsearchconceptbuttonadd"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skosTab.search.concept.button.search"       text="Cerca"     var="skosTabsearchconceptbuttonsearch"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skosTab.createAdd.title"       text="Nuovo Concept"     var="skosTabcreateAddtitle"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skosTab.createAdd.message"       text="inserire il nuovo concept:"     var="skosTabcreateAddmessage"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skosTab.createAdd.message.trim"       text="Inserire il nuovo Concept prima di salvare!"     var="skosTabcreateAddmessagetrim"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skosTab.tab.concept"       text="Dati del Concept"     var="skosTabtabconcept"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skosTab.tab.semantic"       text="Dati Semantici"     var="skosTabtabsemantic"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skosTab.tab.semantic.relations"       text="Relazioni Semantiche"     var="skosTabtabsemanticrelations"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skosTab.tab.mapping.relations"       text="Relazioni di mapping"     var="skosTabtabmappingrelations"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.label"       text="Denominazione"     var="skostabtablabellabel"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.notation"       text="Codice della Griglia"     var="skostabtablabelnotation"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.preflabel"       text="PrefLabel"     var="skostabtablabelpreflabel"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.type"       text="Type"     var="skostabtablabeltype"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.status"       text="Status"     var="skostabtablabelstatus"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.changenote"       text="Change Note"     var="skostabtablabelchangenote"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.date"       text="date"     var="skostabtablabeldate"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.alternativelabel"       text="Label alternativa"     var="skostabtablabelalternativelabel"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.scopenote"       text="Descrizione"     var="skostabtablabelscopenote"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.images"       text="Images"     var="skostabtablabelimages"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.annotations"       text="Note"     var="skostabtablabelannotations"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.id"       text="Id"     var="skostabtablabelid"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.full.text"       text="Libera"     var="skostabtablabelfulltext"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.search.fields"       text="Campi di ricerca"     var="skostabtablabelsearchfields"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.search.results"       text="Risultati"     var="skostabtablabelsearchresults"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.search.tot.results"       text="Totale Risultati"     var="skostabtablabelsearchtotresults"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.search.order.by"       text="ordina risultati per"     var="skostabtablabelsearchorderby"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.search.order.by.relevance"       text="rilevanza"     var="skostabtablabelsearchorderbyrelevance"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.search.order.by.label"       text="label"     var="skostabtablabelsearchorderbylabel"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.unlinked.elements"       text="Elementi scollegati"     var="skostabtablabelunlinkedelements"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.button.edit"       text="modifica"     var="skostabtabbuttonedit"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.button.empty"       text="svuota"     var="skostabtabbuttonempty"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.button.remove"       text="rimuovi"     var="skostabtabbuttonremove"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.button.add"       text="aggiungi"     var="skostabtabbuttonadd"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.button.moveup"       text="sposta su"     var="skostabtabbuttonmoveup"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.button.movedown"       text="sposta giù"     var="skostabtabbuttonmovedown"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.button.disabled"       text="disabled"     var="skostabtabbuttondisabled"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.delete"       text="cancella"     var="skostabtablabeldelete"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.relation"       text="relazione"     var="skostabtablabelrelation"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.create"       text="crea"     var="skostabtablabelcreate"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.broader"       text="Broader"     var="skostabtablabelbroader"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.topconceptof"       text="Top Concept Of"     var="skostabtablabeltopconceptof"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.narrower"       text="Narrower"     var="skostabtablabelnarrower"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.hastopconcept"       text="Has Top Concept"     var="skostabtablabelhastopconcept"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.related"       text="Related"     var="skostabtablabelrelated"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.inscheme"       text="In Scheme"     var="skostabtablabelinscheme"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.broadmatch"       text="Broad Match"     var="skostabtablabelbroadmatch"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.narrowmatch"       text="Narrow Match"     var="skostabtablabelnarrowmatch"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.relatedmatch"       text="Related Match"     var="skostabtablabelrelatedmatch"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="skostab.tab.label.closematch"       text="Close Match"     var="skostabtablabelclosematch"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="visualtab.table.name"       text="nome"     var="visualtabtablename"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="visualtab.table.value"       text="valore"     var="visualtabtablevalue"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="visualtab.tab.concept"       text="Dati del Concept"     var="visualtabtabconcept"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="visualtab.tab.triple"       text="Triple"     var="visualtabtabtriple"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="visualtab.tab.more.concepts"       text="Altri concetti"     var="visualtabtabmoreconcepts"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="visualtab.tab.first.previous.more.concepts" text="torna ai primi narrower concepts"     var="visualtabTabFirstPreviousMoreConcepts"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="visualtab.tab.previous.more.concepts" text="narrower concepts precedenti"     var="visualtabTabPreviousMoreConcepts"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="visualtab.tab.last.next.more.concepts" text="vai agli ultimi narrower concepts"     var="visualtabTabLastNextMoreConcepts"    htmlEscape="true" javaScriptEscape="true"/>
<spring:message code="visualtab.tab.next.more.concepts" text="narrower concepts successivi"     var="visualtabTabNextMreConcepts"    htmlEscape="true" javaScriptEscape="true"/>

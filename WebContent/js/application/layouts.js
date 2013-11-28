layout.storeMulti = new Ext.data.GroupingStore({
	url : 'ajax/documental_adv_search.html',
	reader : new Ext.data.JsonReader({
		totalProperty : 'totalCount',
		root : 'data',
		id : 'id',
		fields : [ 'id', 'title', 'date', 'description', 'datenormal', 'preview', 'myDip' ]
	}),
	groupDir : 'DESC',
	sortInfo : {
		field : 'date',
		direction : 'DESC'
	},
	groupField : 'datenormal',
	baseParams : {
		outputMode : 'multisearch',
		action : 'search',
		sort : 'DATE',
		query : '{a TO Z}',
		limit : 20
	},
	listeners : {
		'load' : function() {
			highlightRecord();
		}
	}
});

layout.gridMulti = new Ext.grid.GridPanel({
	ddGroup : 'gridDDGroup',
	store : layout.storeMulti,
	id : 'multiArchive',
	enableDragDrop : true,
	disableSelection : false,

	trackMouseOver : true,
	disableSelection : true,
	// grid columns
	border : false,
	collapsible : false,
	headerAsText : false,
	enableColumnResize : false,
	enableColumnMove : false,
	enableColumnHide : false,
	animCollapse : false,
	hideHeaders : true,
	header : false,
	iconCls : 'icon-grid',
	autoHeight : true,
	stateful : true,
	autoScroll : false,
	enableHdMenu : false,
	autoSizeColumns : true,
	autoSizeGrid : true,
	autoExpandColumn : 'title',
	columns : [ {
		id : 'preview',
		header : '',
		width : 20,
		flex : 0,
		fixed : true,
		dataIndex : 'preview'
	}, {
		id : 'title',
		flex : 1,
		header : "Titolo",
		dataIndex : 'title'
	}, {
		id : 'myDip',
		header : '',
		width : 23,
		flex : 0,
		fixed : true,
		dataIndex : 'myDip'
	}, {
		id : 'laData',
		dataIndex : 'date',
		hidden : true,
		hideable : false
	}, {
		id : 'datenormal',
		dataIndex : 'datenormal',
		hidden : true,
		hideable : false
	} ],
	view : new Ext.grid.GroupingView({
		forceFit : true,
		groupTextTpl : '{[ values.rs[0].data["date"] ]} ({[values.rs.length]} {[values.rs.length > 1 ? "elementi" : "elemento"]})'
	}),
	selModel : new Ext.grid.RowSelectionModel({
		singleSelect : true
	}),

	loadMask : {
		msg : 'caricamento in corso...',
		enable : true
	}
});

layout.regionWestMulti = {
	id : 'Multi_accordion',
	title : 'relazioni',
	border : false,
	autoScroll : true,
	iconCls : 'navigation_img',
	layout : 'fit',
	items : [ layout.gridMulti ],
	tbar : [ 'termine: ', new Ext.ux.form.SearchField({
		store : layout.storeMulti,
		id : 'query_fieldCrono',
		width : 180,
		emptyText : 'inserisci termine',
		listeners : {
			'afterrender' : function(elemento) {
				$("#" + $(Ext.getDom(elemento)).attr("id")).parent('div').attr("style", "");
			}
		}
	}), '->', generateArchiveFilters('multiArchive'), generateFilters('multiArchive') ],
	bbar : new Ext.PagingToolbar({
		id : 'paging_barCrono',
		store : layout.storeMulti,
		pageSize : 20,
		displayInfo : true,
		displayMsg : 'Risultati {0} - {1} di {2}',
		emptyMsg : "Nessuna occorrenza trovata"
	})

};
function generateMyDropTarget(elemento) {

	return new Ext.dd.DropTarget(elemento.body.dom, {
		ddGroup : globalOpt.editOn ? 'gridDDGroup' : 'dummyGroup',
		notifyEnter : function(ddSource, e, data) {
			elemento.body.stopFx();
			elemento.body.highlight();
		},
		notifyDrop : function(dropSource, e, data) {
			var selectedRecord = dropSource.dragData.selections[0];
			if (dropSource.grid.id == 'multiArchive') {
				new Ext.Window({
					id : 'choiseWindow',
					title : 'attenzione',
					height : 300,
					width : 350,
					html : '<br /><br /><center><img src="css/resources/images/default/window/icon-warning.gif" align="absmiddle"/> confermare la relazione <br /></center>',
					modal : true,
					bbar : [ new Ext.Button({
						text : 'conferma',
						handler : function() {
							(addRelationFromGrid(selectedRecord.data.id, globalOpt.document.id, selectedRecord.data.title, function() {
								(addRelationFromGrid(globalOpt.document.id, selectedRecord.data.id, selectedRecord.data.title, function() {
									top.Ext.ods.msg('Relazione creata con succeso!');
									reloadRecord('scheda');
								}, 21));
							}, 21));
							Ext.getCmp('choiseWindow').destroy();
						}
					}), '->', new Ext.Button({
						text : 'annulla',
						handler : function() {
							Ext.getCmp('choiseWindow').destroy();
						}
					}) ]
				}).show();
			} else {
				return eval(dropSource.grid.id + '(dropSource)');

			}

		}
	});
}
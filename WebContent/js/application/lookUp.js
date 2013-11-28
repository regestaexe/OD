var fieldToFill;
var winLookUp;
Ext.ux.LookUpField = Ext.extend(Ext.form.TwinTriggerField, {
    initComponent : function(){
		Ext.ux.LookUpField.superclass.initComponent.call(this);
        this.on('specialkey', function(f, e){
            if(e.getKey() == e.ENTER){
                this.onTrigger2Click();
            }
        }, this);
    },
    validationEvent:false,
    validateOnBlur:false,
    trigger1Class:'x-form-clear-trigger',
    trigger2Class:'x-form-lookup-trigger',
    hideTrigger1:true,
    width:180,
    hasSearch : false,
    paramName : 'query',
    onTrigger1Click : function(){
        if(this.hasSearch){
        	this.setValue('');
        	Ext.getCmp(this.idRef).setValue('');
            this.triggers[0].hide();
            this.hasSearch = false;
        }
    },

    onTrigger2Click : function(){
    	var idArchive = this.idArchive;
    	var sort_type = this.sort_type;
    	var order_by = this.order_by;
    	var field = this.name;
    	var idRef = this.idRef;
    	var keyId = this.keyId;
    	var query = '{a TO Z}';
    	var lookUpDs = new Ext.data.JsonStore({
    		url : 'lookUp.html',
    		root : 'data',
    		idProperty : 'id',
    		totalProperty : 'totalCount',
    		baseParams : {
    			idArchive : idArchive,
    			page_size : 40,
    			query:query,
    			field:field,
    			sort_type:sort_type,
	            order_by:order_by,
	            idRef:idRef,
	            keyId:keyId
    		},
    		fields : [ 'id', 'title','preview' ]
    	});
    	var lookUpGrid = new Ext.grid.GridPanel({
    		store : lookUpDs,
    		trackMouseOver : true,
    		disableSelection : true,
    		loadMask : {
    			msg : 'caricamento in corso...',
    			enable : true
    		},
    		// grid columns
    		border : false,
    		collapsible : false,
    		enableColumnResize : false,
    		enableColumnMove : false,
    		headerAsText : false,
    		enableColumnHide : false,
    		animCollapse : false,
    		header : false,
    		hideHeaders : true,
    		iconCls : 'icon-grid',
    		autoHeight : true,
    		stateful : true,
    		hideHeaders : true,
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
    			header : "Titolo",
    			dataIndex : 'title'
    		}],
    		viewConfig : {
    			forceFit : true
    		},
            listeners:{
		         'afterrender': function(el){
		         		this.getStore().load();				         		
		         		
		         }
		     }

    	});
    	var lookUp= { 
            	layout:'fit', 
                border: false, 
                fullscreen:true,
                autoScroll: true,
                iconCls: 'results_img',
                items:[lookUpGrid],
                 tbar: [
                    'termine: ', 
                    new Ext.ux.form.SearchField({
					    store: lookUpDs,
					    width:180, 
					    emptyText:'inserisci termine'			    	    
				      })],
                  bbar: new Ext.PagingToolbar({
                  id:'paging_bar',
                  store: lookUpDs, 
                  pageSize: 20,
                  displayInfo: true,
                  displayMsg: 'Risultati {0} - {1} di {2}',
                  emptyMsg: "Nessuna occorrenza trovata"
                  })
            };
    	fieldToFill = this;
    	winLookUp = new Ext.Window({
            layout:'fit',
            width:500,
            height:400,
            closeAction:'destroy',
            header:true,
            title:'Look Up',
            iconCls: 'voc-icon',
            modal:true,                
            y:10,
            items:[lookUp]
        });
    	winLookUp.show();
    }
});
Ext.reg('lookUpField', Ext.ux.LookUpField);
function setLookUpValue(idRef,title,valueToInsert){
    var value=valueToInsert;
	if(valueToInsert.indexOf('/')!=-1){
		value= valueToInsert.substring(valueToInsert.lastIndexOf("/")+1);
	}       
	Ext.getCmp(idRef).setValue(Ext.getCmp(idRef).getValue()+' '+value);
	if(fieldToFill.getValue()!=''){
	   fieldToFill.setValue(fieldToFill.getValue()+','+title);
	}else{
	   fieldToFill.setValue(title);	
	}
	fieldToFill.hasSearch = true;
	fieldToFill.triggers[0].show();
	winLookUp.destroy();
}
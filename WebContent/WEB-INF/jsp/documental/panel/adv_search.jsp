<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Archives"%>
<%@include file="../../locale.jsp"%>
<script>
var checkGroupDipartimenti = {
        xtype: 'fieldset',
        title: 'Dipartimenti',
        autoHeight: true,
        layout: 'form',
        collapsed: true,   // initially collapse the group
        collapsible: true,
        items: [{
            xtype: 'radiogroup',
            columns: 2,
            items: [
                {boxLabel: 'Tutti i dipartimenti', name: 'dipartimento',inputValue: '',checked: true},
				{boxLabel: 'Comitato per la Legislazione-Osservatorio', name: 'dipartimento',inputValue: 'd1'},
				{boxLabel: 'Dip. Affari Sociali', name: 'dipartimento',inputValue: 'd2'},
				{boxLabel: 'Dip. Affari UE', name: 'dipartimento',inputValue: 'd3'},
				{boxLabel: 'Dip. Agricoltura', name: 'dipartimento',inputValue: 'd4'},
				{boxLabel: 'Dip. Ambiente', name: 'dipartimento',inputValue: 'DA'},
				{boxLabel: 'Dip. AttProduttive', name: 'dipartimento',inputValue: 'd5'},
				{boxLabel: 'Dip. Cultura', name: 'dipartimento',inputValue: 'd6'},
				{boxLabel: 'Dip. Esteri', name: 'dipartimento',inputValue: 'd7'},
				{boxLabel: 'Dip. Finanze', name: 'dipartimento',inputValue: 'd8'},
				{boxLabel: 'Dip. Giustizia', name: 'dipartimento',inputValue: 'd9'},
				{boxLabel: 'Dip. Istituzioni', name: 'dipartimento',inputValue: 'd10'},
				{boxLabel: 'Dip. Lavoro', name: 'dipartimento',inputValue: 'd11'},
				{boxLabel: 'Dip. Trasporti', name: 'dipartimento',inputValue: 'd12'},
				{boxLabel: 'Sez. Affari regionali', name: 'dipartimento',inputValue: 'd13'}
            ]
        }]
    };
var checkGroupDocumenti = {
        xtype: 'fieldset',
        title: 'Restringi per Utente',
        autoHeight: true,
        layout: 'form',
        collapsed: true,
        collapsible: true,
        items: [
         {
            xtype: 'radiogroup',
            columns: 2,
            items: [
                {boxLabel: 'Documenti di tutti gli utenti', name: 'documenti',inputValue:'',checked: true},
				{boxLabel: 'I miei documenti', name: 'documenti',inputValue: 'my'}
            ]
        }]
    };
var fsf = new Ext.FormPanel({
        labelWidth: 75,
        frame:true,
        bodyStyle:'padding:5px 5px 0',
        layout: 'fit',
        items: [{
            xtype:'fieldset',
            title: 'Campi di ricerca',
            autoHeight:true,
            defaults: {width: 400},
            defaultType: 'textfield',
            collapsed: false,
            collapsible: false,
            allowBlank:false,
            
            items :[{
                    fieldLabel: 'Libera',
                    name: 'contents'
                },{
                    fieldLabel: 'Titolo',
                    name: 'title'
                },{
                    fieldLabel: 'Descrizione',
                    name: 'description'
                }
            ]
	        },
	        checkGroupDipartimenti,
	        checkGroupDocumenti
        ],
        buttons: [
                   {
                      text:'${buttonAdvSearchButton}',
                      handler: function(){
                        var query = '';
                      	if(fsf.getForm().isValid()){
                    		var field;
                    		var values = fsf.getForm().getValues();
                    		var name;        
                    		for (name in values) { 
                        		   if($.trim(values[name])!=''){
                        			   query+=" +"+name+":"+escape(values[name]);
                                   }      
              		        }
  		                }
  		                if($.trim(query)!=''){
	                	    Ext.getCmp('results_accordion').expand();
	                 		ds.baseParams.query=query.substring(2);	
	                 		ds.load({params:{start:0}});
  		                }else{
  		                	Ext.MessageBox.alert('Status', 'Compilare almeno un campo di ricerca!');
  	  	  		        }
                   	}
                   },{
                      text: '${buttonReset}',
                      handler: function(){
                    	  fsf.getForm().reset();
                      }
                  }]
    });
    fsf.render('advSearch_tab');
</script>
Ext.ux.VocField = Ext.extend(Ext.form.TriggerField,{
	triggerClass: 'x-form-voc-trigger',
    onTriggerClick: function(){
	    var cks = Ext.select("input[name^=ck-multi-search-archive_]").elements;
	    var requestArchives='';
	    var countArchives = 0;
	    var field = this;
		for (var x = 0 ; x < cks.length; x ++){
			if(Ext.getCmp(Ext.get(cks[x]).id).getValue()){
				requestArchives+=Ext.getCmp(Ext.get(cks[x]).id).inputValue+";";	
				countArchives++;
			}						
		}
		var value = $.trim(this.getValue());
		value = value.replace(/\+/g,'').replace(/:/g,'');
		if(requestArchives!=''){
			$("input[name='selected_terms']").attr("value","");
			var win = new Ext.Window({
                renderTo:'multiSearchVoc',
                layout:'fit',
                width:500,
                height:400,
                closeAction:'destroy',
                header:true,
                title:'Vocabolario di campo',
                iconCls: 'voc-icon',
                modal:true,                
                y:10,
                autoLoad:{
			         		  url:"vocabulary.html?voc_field="+this.name+"&last_term="+value+"&requestArchives="+requestArchives,
			         		  discardUrl: false,
			         		  nocache: true,
			         		  text: 'caricamento in corso...',
			         		  timeout: 30,
			         		  scripts: true
				     	},   	
                buttons: [
							{
							    text:'precedente',
							    id:'button_prev',
							    disabled:true,
							    handler: function(){
								var one_field = false;
								if($("input[name='one_field']")!=undefined){
									one_field = $("input[name='one_field']").attr("checked");
								}
						        $(".voc_contents").load("vocabulary.html?voc_field="+$("input[name='voc_field']").attr("value")+"&one_field="+one_field+"&last_term="+term_previous+"&requestArchives="+requestArchives+"&action=voc_page&selected_terms="+$("input[name='selected_terms']").attr("value"),function(){
						        	if(isFirstPage){
						        		Ext.getCmp('button_prev').setDisabled(true);			
								    }else{
								    	Ext.getCmp('button_prev').setDisabled(false);		
									}
						        	if(isLastPage){
						        		Ext.getCmp('button_next').setDisabled(true);
								    }else{
								    	Ext.getCmp('button_next').setDisabled(false);		
									}
							    });
	                    	}
							},
							{
							    text:'successivo',
							    id:'button_next',
							    handler: function(){
									var one_field = false;
									if($("input[name='one_field']")!=undefined){
										one_field = $("input[name='one_field']").attr("checked");
									}									
						        	$(".voc_contents").load("vocabulary.html?voc_field="+$("input[name='voc_field']").attr("value")+"&one_field="+one_field+"&last_term="+last_term+"&requestArchives="+requestArchives+"&action=voc_page&selected_terms="+$("input[name='selected_terms']").attr("value"),function(){
						        		if(isFirstPage){
							        		Ext.getCmp('button_prev').setDisabled(true);			
									    }else{
									    	Ext.getCmp('button_prev').setDisabled(false);		
										}
							        	if(isLastPage){
							        		Ext.getCmp('button_next').setDisabled(true);
									    }else{
									    	Ext.getCmp('button_next').setDisabled(false);		
										}
								    });
		                    	}
							},
			                {
			                    text:'inserisci',
			                    handler: function(){
		                        	var values = $("input[name='selected_terms']").attr("value").split('|');
		                        	var voc_field = $("input[name='voc_field']").attr("value");
		                        	var toSearch = '';
		                        	for (var x = 0 ; x < values.length; x ++){
			                        	if(values[x]!='')
		                        			toSearch+=values[x]+' ';
				                    }
		                        	field.setValue(toSearch);
		                        	win.destroy();
		                    	}
			                },{
			                    text: 'chiudi',
			                    handler: function(){
			                        win.destroy();
			                    }
			                }
                		]
            });
			if(countArchives>5){
				Ext.MessageBox.buttonText.yes = "Sí"; 
				Ext.MessageBox.buttonText.cancel = "No"; 
				Ext.MessageBox.confirm('Attenzione', 'Il numero di archivi selezionato potrebbe causare un rallentamento nell\'esecuzione del vocabolario, continuare?',function(btn){
					if(btn=="yes"){
						win.show();
					}
				});
			}else{			
				win.show();
			}			
		}else{
			Ext.Msg.alert('Attenzione','Selezionare almeno un\'archivio prima richiedere il vocabolario di campo!');
		}
       
    },
    initComponent:function() {
        Ext.ux.VocField.superclass.initComponent.call(this);
        
    }
});
 
// register xtype
Ext.reg('vocfield', Ext.ux.VocField);


Ext.ux.SingleVocField = Ext.extend(Ext.form.TriggerField,{
	triggerClass: 'x-form-voc-trigger',
    onTriggerClick: function(){
	    var requestArchives=this.archives;
		var value = $.trim(this.getValue());
		value = value.replace(/\+/g,'').replace(/:/g,'');
		$("input[name='selected_terms']").attr("value","");
		var field = this;
		var win = new Ext.Window({
            renderTo:'multiSearchVoc',
            layout:'fit',
            width:500,
            height:400,
            closeAction:'destroy',
            header:true,
            title:'Vocabolario di campo',
            iconCls: 'voc-icon',
            modal:true,                
            y:10,
            autoLoad:{
		         		  url:"vocabulary.html?voc_field="+this.name+"&last_term="+value+"&requestArchives="+requestArchives,
		         		  discardUrl: false,
		         		  nocache: true,
		         		  text: 'caricamento in corso...',
		         		  timeout: 30,
		         		  scripts: true
			     	},   	
            buttons: [
						{
						    text:'precedente',
						    id:'button_prev',
						    disabled:true,
						    handler: function(){
							var one_field = false;
							if($("input[name='one_field']")!=undefined){
								one_field = $("input[name='one_field']").attr("checked");
							}
					        $(".voc_contents").load("vocabulary.html?voc_field="+$("input[name='voc_field']").attr("value")+"&one_field="+one_field+"&last_term="+term_previous+"&requestArchives="+requestArchives+"&action=voc_page&selected_terms="+$("input[name='selected_terms']").attr("value"),function(){
					        	if(isFirstPage){
					        		Ext.getCmp('button_prev').setDisabled(true);			
							    }else{
							    	Ext.getCmp('button_prev').setDisabled(false);		
								}
					        	if(isLastPage){
					        		Ext.getCmp('button_next').setDisabled(true);
							    }else{
							    	Ext.getCmp('button_next').setDisabled(false);		
								}
						    });
                    	}
						},
						{
						    text:'successivo',
						    id:'button_next',
						    handler: function(){
								var one_field = false;
								if($("input[name='one_field']")!=undefined){
									one_field = $("input[name='one_field']").attr("checked");
								}									
					        	$(".voc_contents").load("vocabulary.html?voc_field="+$("input[name='voc_field']").attr("value")+"&one_field="+one_field+"&last_term="+last_term+"&requestArchives="+requestArchives+"&action=voc_page&selected_terms="+$("input[name='selected_terms']").attr("value"),function(){
					        		if(isFirstPage){
						        		Ext.getCmp('button_prev').setDisabled(true);			
								    }else{
								    	Ext.getCmp('button_prev').setDisabled(false);		
									}
						        	if(isLastPage){
						        		Ext.getCmp('button_next').setDisabled(true);
								    }else{
								    	Ext.getCmp('button_next').setDisabled(false);		
									}
							    });
	                    	}
						},
		                {
		                    text:'inserisci',
		                    handler: function(){
	                        	var values = $("input[name='selected_terms']").attr("value").split('|');
	                        	var voc_field = $("input[name='voc_field']").attr("value");
	                        	var toSearch = '';
	                        	for (var x = 0 ; x < values.length; x ++){
		                        	if(values[x]!='')
	                        			toSearch+=values[x]+' ';
			                    }
	                        	field.setValue(toSearch);
	                        	win.destroy();
	                    	}
		                },{
		                    text: 'chiudi',
		                    handler: function(){
		                        win.destroy();
		                    }
		                }
            		]
        });		
		if(requestArchives.split(';').length>5){
			Ext.MessageBox.buttonText.yes = "Sí"; 
			Ext.MessageBox.buttonText.cancel = "No"; 
			Ext.MessageBox.confirm('Attenzione', 'La richiesta di vocabolario per tutti gli archivi potrebbe impiegare del tempo, continuare?',function(btn){
				if(btn=="yes"){
					win.show();
				}
			});
		}else{			
			win.show();
		}
    },
    initComponent:function() {
    	Ext.ux.SingleVocField.superclass.initComponent.call(this);
        
    }
});
Ext.reg('singleVocfield', Ext.ux.SingleVocField);
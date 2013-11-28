function stampaBtt(comp) {
	Ext.ux.Printer.PanelRenderer = Ext.extend(Ext.ux.Printer.BaseRenderer, {
		generateBody : function(panel) {
			return String.format("<div class='x-panel-print'>{0}</div>", panel.body.dom.innerHTML);
		}
	});
	Ext.ux.Printer.registerRenderer("panel", Ext.ux.Printer.PanelRenderer);
	return new Ext.Toolbar({
		items : [ '->', new Ext.Button({
			text : 'stampa',
			handler : function(btn) {
				Ext.ux.Printer.print(Ext.getCmp(comp));
			}
		}) ]
	})
}
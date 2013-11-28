function initSpecificLayout() {

	// if (globalOpt.idArchivio == 2) {
	// loadScripts({
	// base : "application/layouts",
	// scripts : [ 'dottrina' ]
	// });
	// Ext.getCmp('leftColumnAccordion').insert(1, layout.regionWestDottrina);
	// } else if (globalOpt.idArchivio == 8) {
	// ....
	// }

}
function getImg(arg) {
	arg = arg + '';
	if (arg != '') {
		arg = '&nbsp;&nbsp;<img src="img/coverage_img/' + arg + '.jpg" title="'
				+ arg + '" alt="' + arg + '" border="0"/>';
	}
	return arg;
}
function getLuogo(arg) {
	var dati;
	var input = "";
	dati = getDataFromRDF('countries.html?action=all');
	dati = dati.countries;
	for ( var i = 0; i < dati.length; i++) {
		if (arg == dati[i].about)
			input = ('<img src=\'img/countries/' + dati[i].img + '\'/><span> '
					+ dati[i].title + '</span>');
	}
	return input;
}

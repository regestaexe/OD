function connect(channel) {
	// callback di connessione con il topic desiderato

	function callback(response) {
		if (response.transport != 'polling' && response.state != 'connected' && response.state != 'closed') {
			if (response.status == 200) {
				var data = response.responseBody;
				if (data.length > 0) {
					// TODO: bisogna separare bene i log relativi ai due canali
					// distinti di notifica
					// TODO: sostituire l'html con un json?
					// $(idLogBox).prepend($('<li></li>').text(" message
					// received: " + data));
					alert("data: " + data);
				} else {
					alert("sono connesso ma data=0 " + data);
				}
			}
		} else {
			alert("response.transport " + response.transport);
		}
	}
	// TODO: bisognerebbe associare ad ogni oggetto che legga o scriva dalla
	// connessione
	// i parametri request, response, topic
	$.atmosphere.subscribe(channel, callback, $.atmosphere.request = {
		transport : "websocket"
	});
}

function publish(channel, msg) {
	$.atmosphere.response.push(channel, null, $.atmosphere.request = {
		data : 'message=' + msg
	});
}

function connect1(channel) {
	// callback di connessione con il topic desiderato

	function callback(response) {
		if (response.transport != 'polling' && response.state != 'connected' && response.state != 'closed') {
			if (response.status == 200) {
				var data = response.responseBody;
				if (data.length > 0) {
					// TODO: bisogna separare bene i log relativi ai due canali
					// distinti di notifica
					// TODO: sostituire l'html con un json?
					// $(idLogBox).prepend($('<li></li>').text(" message
					// received: " + data));
					alert("data: " + data);
				} else {
					alert("sono connesso ma data=0 " + data);
				}
			}
		} else {
			alert("response.transport " + response.transport);
		}
	}
	// TODO: bisognerebbe associare ad ogni oggetto che legga o scriva dalla
	// connessione
	// i parametri request, response, topic
	$.atmosphere1.subscribe(channel, callback, $.atmosphere1.request = {
		transport : "websocket"
	});
}

function publish1(channel, msg) {
	$.atmosphere1.response.push(channel, null, $.atmosphere1.request = {
		data : 'message=' + msg
	});
}
var wsUri = "ws://localhost:8080/Kojak-Web-Streamer/KojakSocketManager";


function init() {
	kojakWebSocket();
	$('#btnConfirmar').hide();
	$('.btnProcess').hide();
	
}

function kojakWebSocket() {
	websocket = new WebSocket(wsUri);
	websocket.onopen = function(evt) {
		onOpen(evt)
	};
	websocket.onclose = function(evt) {
		onClose(evt)
	};
	websocket.onmessage = function(evt) {
		onMessage(evt)
	};
	websocket.onerror = function(evt) {
		onError(evt)
	};
}

function doSend(message) {
	console.log("SENT: " + message);
	websocket.send(message);
}

function onOpen(evt) {
	$('#connectionStatus').css({
		'color' : 'green'
	});
	console.log("CONNECTED");
}

function onClose(evt) {
	$('#connectionStatus').css({
		'color' : 'yellow'
	});
	console.log("DISCONNECTED");
}

function onMessage(evt) {
	if (typeof evt.data === 'string') {
		console.log('RESPONSE: ' + evt.data);
		if(evt.data=='action:snapshot'){
			$('.btnIniciar').show();
			
			$('#btnConfirmar').show();
		}
		
	} else {
		
		var streamedImage = URL.createObjectURL(evt.data);
		var oldImage = $('#myCanvas').attr("src");
		$('#myCanvas').attr("src",streamedImage );
		URL.revokeObjectURL(oldImage);

	}

}


function onError(evt) {
	$('#connectionStatus').css({
		'color' : 'red'
	});
	console.log(evt);
	console.log(evt.data);
}


window.addEventListener("load", init, false);


$('#btnConfirmar').click(function() {
	$('.btnIniciar').hide();
	$('.btnProcess').show();
	
	$('#btnConfirmar').hide();
});




$('#btnIniciarFoot').click(function() {
	$('#myCanvas').attr("src","./img/loading.gif" );
	doSend('action:start:footPrint');
	$('.btnIniciar').hide();
	$('#btnConfirmar').hide();
});

$('#btnIniciarThumb').click(function() {
	$('#myCanvas').attr("src","./img/loading.gif" );
	doSend('action:start:oneThumb');
	$('.btnIniciar').hide();
	$('#btnConfirmar').hide();
});


$('#btnIniciarTwoThumbs').click(function() {
	$('#myCanvas').attr("src","./img/loading.gif" );
	doSend('action:start:twoThumbs');
	$('.btnIniciar').hide();
	$('#btnConfirmar').hide();
});

$('#btnIniciar4Left').click(function() {
	$('#myCanvas').attr("src","./img/loading.gif" );
	doSend('action:start:leftFour');
	$('.btnIniciar').hide();
	$('#btnConfirmar').hide();
});
$('#btnIniciar4Right').click(function() {
	$('#myCanvas').attr("src","./img/loading.gif" );
	doSend('action:start:rightFour');
	$('.btnIniciar').hide();
	$('#btnConfirmar').hide();
});



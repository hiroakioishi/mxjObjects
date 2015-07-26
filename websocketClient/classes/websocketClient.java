
import java.io.IOException;

/**
 * max.jar
 */
import com.cycling74.max.*;

/**
 *  webSocket client library
 *  https://github.com/TakahikoKawasaki/nv-websocket-client/tree/master/src/main/java/com/neovisionaries/ws/client
 */
import com.neovisionaries.ws.client.*;

public class websocketClient extends MaxObject {

	private String uri = "ws://127.0.0.1:3000";
	
	WebSocket ws;
	
	public websocketClient(Atom[] args) {
		declareIO(1,3);
		declareInlets(new int[]{DataTypes.ALL});
		declareOutlets(new int[]{DataTypes.MESSAGE, DataTypes.MESSAGE, DataTypes.MESSAGE});
		
		setInletAssist(0, "");
		setOutletAssist(new String[] {
			"Received message",
			"Server URI",
			"isOpen"
		});
		
		declareAttribute("connect", null, "_connect");
		declareAttribute("disconnect", null, "_disconnect");
		declareAttribute("uri", null, "_setServerURI");
		declareAttribute("send", null, "_sendMessage");
	}
	
	public void bang() {		
		if (ws != null) {
			outlet(1, ws.getURI().toString());
			outlet(2, ws.isOpen() ? "true" : "false");
		} else {
			outlet(1, "null");
			outlet(2, "null");
		}
	}
	
	private void _sendMessage (String mes) {
		if (ws != null) {
			ws.sendText(mes);
			post ("send : " + mes);
		}
	}
	
	private void _setServerURI (String uri) {
		this.uri = uri;
		post("set serverURI : " + this.uri);
	}
	
	private void _connect(){
		
		if (ws != null) return;
		
		WebSocketFactory factory = new WebSocketFactory ();
	
		try {
			ws = factory.createSocket(uri);
			ws.addListener(new WebSocketAdapter (){
				public void onTextMessage(WebSocket websocket, String message) {
					outlet(0, message.toString());
					post("receive : " + message);
				}
			});
		} catch (IOException e) {
			error(e.toString());
		}
		
		try {
			ws.connect();
			post ("connect");
		} catch (WebSocketException e) {
			post (e.toString());
		}
	
	}
	
	private void _disconnect() {
		if(ws != null) {
			ws.disconnect();
			ws = null;
			post("disconnect");
		}
	}
}
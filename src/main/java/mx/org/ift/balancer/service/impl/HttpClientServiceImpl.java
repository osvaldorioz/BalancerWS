package mx.org.ift.balancer.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.UUID;

import mx.org.ift.balancer.Listener;
import mx.org.ift.balancer.constants.Const;
import mx.org.ift.balancer.service.HttpClientService;
import mx.org.ift.balancer.variables.Vars;

public class HttpClientServiceImpl implements HttpClientService{
	
	private Vars v = Vars.getInstance();
	
	public String sendMessage(String message, String endpoint){
		String resp = null;
	
		try {
			URL url = new URL(endpoint);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    
			conn.setDoOutput(true);
			//conn.setRequestProperty("Authorization", "Bearer " + bearer);
			conn.setRequestMethod("POST");

			//conn.setConnectTimeout(300);
			//conn.setReadTimeout(300);
            //conn.setRequestProperty("Accept", "text/plain");
            //conn.addRequestProperty("User-Agent", "Mozilla/4.0");
            conn.setRequestProperty("Content-Type", "text/plain");
            //conn.setRequestProperty("charset", "UTF-8");
            
    		
            OutputStream os = conn.getOutputStream();
    		os.write(message.getBytes());
    		os.flush();
    		
    		if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				resp = output;
			}
			conn.disconnect();
    		
    		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return resp;
	}
	
	public String getNtpTime(){
		String time = null;
		String bearer = "07bfda62-2aef-30b7-83b4-2440687a865e";
		try {
			URL url = new URL(Const.WS_TIME);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    //Bearer 07bfda62-2aef-30b7-83b4-2440687a865e
			
			conn.setRequestProperty("Authorization", "Bearer " + bearer);
			conn.setRequestMethod("GET");

            conn.setRequestProperty("Accept", "text/plain");
            conn.addRequestProperty("User-Agent", "Mozilla/4.0");
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("charset", "UTF-8");
            
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				time = output;
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return time;
	}
	
	public String failOver(String message){
		String resp = null;
		String endpoint = v.getServers().get(v.getPrincipal());
			
		resp = sendMessage(message, endpoint);
		System.out.println("---------->" + resp);
		if(resp == null){
			if(v.getPrincipal() < v.getServers().size()){
				v.setPrincipal(v.getPrincipal() + 1);
				endpoint = v.getServers().get(v.getPrincipal());
				System.out.println("---FAIL---> " + endpoint);
			} else {
				v.setPrincipal(1);
				endpoint = v.getServers().get(v.getPrincipal());
				System.out.println("--ORIGIN--> " + endpoint);
			}
		}
		
		return resp;
	}
	
	public static void main(String...strings ){
		Vars v = Vars.getInstance();
		
		System.out.println(v.getVersion());
		v.setPrincipal(1);
		v.setTop(10);
		v.setStatus(true);
		v.getServers().clear();
		v.getServers().put(1, "http://172.17.42.20:9768/RegistroBitacoraWS/1.0.0/services/registro_evento/registro/evento");
		//v.getServers().put(1, "http://172.18.34.124:9766/RegistroBitacoraWS/1.0.0/services/registro_evento/registro/evento");
		v.getServers().put(2, "http://172.17.42.71:9763/RegistroBitacoraWS/1.0.0/services/registro_evento/registro/evento");
		
		v.setCounter(0);
		//String endpoint = v.getServers().get(v.getPrincipal());
		String message = 
				"{" +
			            "	ip:\"192.168.1.22\"," +
			            "	username:\"dgticexterno.140\"," +
			            "	idAplicacion:1, " +
			            "	idTipoEvento:22, " +
			            "	tablename: \"USUARIO_B\", " +
			            "	modulename: \"ASDF\", " +
			            "	rowAffected: 2345, " +
			            "	campos: { " +	
			            "		\"campo_1\": \"Alpha\", " + 
			            " 		\"campo_2\": 234.67, " + 
			            "		\"campo_3\": \"12565\", " + 
			            "		\"campo_4\": \"07/07/2018\", " + 
			            "		\"campo_5\": \"Delta\", " + 
			            "		\"campo_6\": 1, " +
			            "		\"campo_7\": \"23464\", " + 
			            "		\"campo_8\": \"13/07/18 09:32:52.298000000 -05:00\" " +
			            "	} "+
			            "}";
		
		final Listener l = new Listener();
		for(int i = 0; i < 100; i++){
			String resp = "";//l.failOver(message);
			
			System.out.println(resp);
		}
	}
	
	public String Uuid(){
		String uuid = "";
		Long milis = System.currentTimeMillis();
		uuid = UUID.randomUUID().toString();
		String last = uuid.substring(23,uuid.length());
		Boolean rand = new Random().nextBoolean();
		if(rand){
			last = last + "a";
		} else {
			last = last + "f";
		}
		uuid = uuid.substring(0,9) + milis + last;
		return uuid;
	}
}

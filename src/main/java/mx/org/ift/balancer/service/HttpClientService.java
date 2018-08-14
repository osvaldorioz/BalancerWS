package mx.org.ift.balancer.service;

public interface HttpClientService {
	String sendMessage(String message, String url);
	String getNtpTime();
	String failOver(String message);
	String Uuid();
}

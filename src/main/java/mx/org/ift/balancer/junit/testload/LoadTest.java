package mx.org.ift.balancer.junit.testload;

import mx.org.ift.balancer.service.HttpClientService;
import mx.org.ift.balancer.service.impl.HttpClientServiceImpl;

import org.junit.Test;

public class LoadTest {

	@Test
	public void tester(){
		HttpClientService hcp = new HttpClientServiceImpl();
		String time = hcp.getNtpTime();
		
		
	}
}

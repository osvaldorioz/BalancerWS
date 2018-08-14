package mx.org.ift.balancer.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import mx.org.ift.balancer.service.HttpClientService;
import mx.org.ift.balancer.service.impl.HttpClientServiceImpl;
import mx.org.ift.balancer.variables.Vars;
import mx.org.ift.balancer.vo.MessageVO;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Processor implements Job{
	private Vars v = Vars.getInstance();
	private HttpClientService hcs = new HttpClientServiceImpl();
	
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println("Procesamiento de lote: " +v.getMp().size());
		
		String superMessage = "";
		if(v.getPeak() > v.getMp().size()){
			return;
		}
		
		for(int i=0; i < v.getPeak(); i++){
			String uuid = v.getMp().lastKey();
			MessageVO mm = v.getMp().remove(uuid);
			mm.setUuid(uuid);
			v.getBuffer().add(mm);
		}
		
		for(MessageVO ss: v.getBuffer()){
			superMessage += ss.getContent() + "#$" +
		                    ss.getTime()    + "$#" +
					        ss.getUuid()    + "||";
		}
		
		superMessage = superMessage.substring(0, superMessage.length()-2);
		
		String resp = hcs.failOver(superMessage);
		
		if(resp == null){
			System.out.println("Fallo de envio de mensajes!!!");
			System.out.println("Size: " + v.getMp().size());
			for(MessageVO mm: v.getBuffer()){
				String uuid = mm.getUuid();
				mm.setUuid("");
				v.getMp().put(uuid, mm);
				System.out.println("Se regresa mensaje a la cola: " + uuid);				
			}
			System.out.println("Size: " + v.getMp().size());
		}
		v.getBuffer().clear();
	}
	
	public static void main(String ...strings ){
		List<String> list = new ArrayList<String>();
		String a = "{aadfsdfs}#$werwqrweqrewqrqwe$#312312-123213-1232321-123213||" +
	               "{dfcsdfsd}#$werwqrweqrewqrqwe$#312312-123213-1232321-123213||" +
				   "{dfsfsdfs}#$werwqrweqrewqrqwe$#312312-123213-1232321-123213||" +
	               "{fsdfsedf}#$werwqrweqrewqrqwe$#312312-123213-1232321-123213||" + 
				   "{dsfsdddf}#$werwqrweqrewqrqwe$#312312-123213-1232321-123213||" +
	               "{sdffsfsd}#$werwqrweqrewqrqwe$#312312-123213-1232321-123213||";
		a = a.substring(0,a.length()-2);
		
		System.out.println(a);
		
		StringTokenizer st2 = new StringTokenizer(a, "||");
		while (st2.hasMoreElements()) {
			list.add((String)st2.nextElement());
		}
		
		System.out.print('\n');
		
		for(String ss: list){
			int pos1 = ss.indexOf("#$");
			int pos2 = ss.indexOf("$#");
			String content = ss.substring(0, pos1);
			String fx = ss.substring(pos1 + 2, pos2);
			String uuid = ss.substring(pos2 + 2, ss.length());
			System.out.print(content+ " " + fx + " " + uuid + "\n");
		}
		System.out.print('\n');
	}
}

package mx.org.ift.balancer;

import java.util.UUID;

import mx.org.ift.balancer.variables.Vars;
import mx.org.ift.balancer.vo.MessageVO;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import mx.org.ift.balancer.service.HttpClientService;
import mx.org.ift.balancer.service.impl.HttpClientServiceImpl;
import mx.org.ift.balancer.pool.Processor;

@Path("/bitacora")
public class Listener {
	private Scheduler scheduler;
	private Vars v = Vars.getInstance();
	private final HttpClientService hcs = new HttpClientServiceImpl();
	
	@POST
	@Path("/fo")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.TEXT_PLAIN)
	public String trailingMessage(String message){
		String time = hcs.getNtpTime();
		MessageVO mm = new MessageVO();
		mm.setTime(time);
		String uuid = hcs.Uuid();
		mm.setContent(message);
		v.getMp().put(uuid, mm);
		
		return "Mensaje encarrilado...";
	}
	
	@POST
	@Path("/lb")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.TEXT_PLAIN)
	public String loadBalancer(String message){
		String resp = "";
		
			String endpoint = "";
			if(v.getStatus()){
				endpoint = v.getServers().get(1);
				resp = hcs.sendMessage(message, endpoint);
			} else {
				endpoint = v.getServers().get(2);
				resp = hcs.sendMessage(message, endpoint);
			}
			v.setStatus(!v.getStatus());
			
		return resp;
	}
	
	@POST
	@Path("/server")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.TEXT_PLAIN)
	public String crearServer(String server){
		int size = v.getServers().size();
		v.getServers().put(size+1, server);
		return "Server agregado";
	}
	
	@GET
	@Path("/initlb/{Estatus}")
	@Produces(MediaType.TEXT_HTML)
	public String setStatus(@PathParam("Estatus")Boolean st){
		v.setStatus(st);
		return "Estatus: " + v.getStatus();
	}
	
	@GET
	@Path("/principal/{Numero}")
	@Produces(MediaType.TEXT_HTML)
	public String setPrincipal(@PathParam("Numero")Integer principal){
		v.setPrincipal(principal);
		return "Server principal: " + v.getServers().get(principal);
	}
	
	@GET
	@Path("/peak/{Numero}")
	@Produces(MediaType.TEXT_HTML)
	public String setPeak(@PathParam("Numero")Integer peak){
		v.setPeak(peak);
		return "Peak: " + v.getPeak();
	}
	
	@GET
	@Path("/borrarservers")
	@Produces(MediaType.TEXT_HTML)
	public String eraseServer(){
		v.getServers().clear();
		
		return "Servers eliminados " ;
	}
	
	@GET
	@Path("/mostrarprincipal")
	@Produces(MediaType.TEXT_HTML)
	public String showPrincipal(){
		
		return "Server principal: " + v.getServers().get(v.getPrincipal());
	}
	
	@GET
	@Path("/version")
	@Produces(MediaType.TEXT_HTML)
	public String showVersion(){
		
		return v.getVersion();
	}
	
	@GET
	@Path("/processor/{StartStop}/{Interval}")
	@Produces(MediaType.TEXT_HTML)
	public String startstop(@PathParam("StartStop") String ss,
			@PathParam("Interval") Integer interval){
		
		String msg = "";
		
		if(interval==null || interval < 1){
			interval = 2000;
		}
		JobDetail job = JobBuilder.newJob(Processor.class)
				.withIdentity("Processor", "group1").build();

		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("Processor", "group1")
				.withSchedule(
			SimpleScheduleBuilder.simpleSchedule().
				withIntervalInMilliseconds(interval).repeatForever())
				.build();

		try{
			if(ss.equals("start")){
				scheduler = new StdSchedulerFactory().
						getScheduler();
				scheduler.start();
				scheduler.scheduleJob(job, trigger);
				msg = "Procesador iniciado";
			} else if(ss.equals("stop")){
				if(scheduler != null && !scheduler.isShutdown()){
					scheduler.shutdown();
					msg = "Procesador detenido";
				} else {
					msg = "Procesador no está en ejecución";
				}
			}
		}catch(SchedulerException err){
			
		}
	
		return msg;
	}
}
package com.openDams.listeners;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.impl.StdScheduler;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.openDams.vertx.server.VertXServerProvider;

public class ShutDownHook implements ServletContextListener {
	ServletContextEvent servletContextEvent = null;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("INIZIO CHIUSURA Thread DI QUARTZ " + servletContextEvent);
		try {
			// Get a reference to the Scheduler and shut it down
			WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
			if(context.getBean("quartzSchedulerFactory")!=null){
				StdScheduler stdScheduler = (StdScheduler) context.getBean("quartzSchedulerFactory");
				System.out.println("ATTUALMENTE SONO IN ESECUZIONE N = "+stdScheduler.getCurrentlyExecutingJobs().size());
				String[] triggerGroupNames = stdScheduler.getTriggerGroupNames();
				for (int i = 0; i < triggerGroupNames.length; i++) {
					String[] triggerNames = stdScheduler.getTriggerNames(triggerGroupNames[i]);
					System.out.println("GROUPNAME "+triggerGroupNames[i]);
					for (int j = 0; j < triggerNames.length; j++) {
						System.out.println("TRIGGER "+triggerNames[j]);
						stdScheduler.deleteJob(triggerNames[j], triggerGroupNames[i]);
					}
				}
				stdScheduler.shutdown(true);	
				Thread.sleep(1000);
			}
			// Sleep for a bit so that we don't get any errors
			//Thread.sleep(1000);
		} catch (Exception e) {
			System.out.println("Thread DI QUARTZ: ERRORE!!!! " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("CHIUSI TUTTI I Thread DI QUARTZ");
		/*System.out.println("INIZIO CHIUSURA driver");
		Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                System.out.println(String.format("deregistering jdbc driver: %s", driver));
            } catch (SQLException e) {
                System.out.println(String.format("Error deregistering driver %s", driver));
            }

        }
        System.out.println("FINE CHIUSURA driver");*/
       /* System.out.println("INIZIO CHIUSURA Thread");
        try {
			ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
			ThreadGroup parentGroup;
			while ( ( parentGroup = rootGroup.getParent() ) != null ) {
			    rootGroup = parentGroup;
			}
			Thread[] threads = new Thread[rootGroup.activeCount()];
			while ( rootGroup.enumerate( threads, true ) == threads.length ) {
			    threads = new Thread[ threads.length * 2 ];
			}
			for (int i = 0; i < threads.length; i++) {
				Thread thread = threads[i];
				if(thread.getName().toLowerCase().indexOf("timer-")!=-1 || thread.getName().toLowerCase().indexOf("com.mchange.v2.async.threadpoolasynchronousrunner$poolthread-#")!=-1){
					//thread.setContextClassLoader(null);
					System.out.println(thread.getContextClassLoader());
					if(thread.isAlive()){
						thread.interrupt();
						System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<Thread>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+thread.getName()+" interrupted!!!");
					}else{
						System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<Thread>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+thread.getName()+" already stopped!!!");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		 System.out.println("FINE CHIUSURA Thread");*/
		 
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		this.servletContextEvent = arg0;
	}

}
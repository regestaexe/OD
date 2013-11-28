package com.openDams.admin.tasks;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
public class EndPointDePublisher implements Tasklet {
     private SQLEndPointPublisher sqlEndPointPublisher = null;
	 public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		 String idArchive = (String)arg1.getStepContext().getJobParameters().get("idArchive");
		 String endPointManager = (String)arg1.getStepContext().getJobParameters().get("endPointManager");
		 String userName = (String)arg1.getStepContext().getJobParameters().get("userName");
		 System.out.println("Pubblicazione su EndPoint per archivio "+idArchive); 
		 sqlEndPointPublisher.dePublishArchive(Integer.parseInt(idArchive),endPointManager,userName);
		 return RepeatStatus.FINISHED;
	 }
	public void setSqlEndPointPublisher(SQLEndPointPublisher sqlEndPointPublisher) {
		this.sqlEndPointPublisher = sqlEndPointPublisher;
	}
	
}

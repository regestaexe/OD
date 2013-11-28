package com.openDams.admin.tasks;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
public class EndPointPublisher implements Tasklet {
     private SQLEndPointPublisher sqlEndPointPublisher = null;
	 public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		 String idArchive = (String)arg1.getStepContext().getJobParameters().get("idArchive");
		 String endPointManager = (String)arg1.getStepContext().getJobParameters().get("endPointManager");
		 String userName = (String)arg1.getStepContext().getJobParameters().get("userName");
		
		 System.out.println("Pubblicazione su EndPoint per archivio "+idArchive); 
		 
		 if(arg1.getStepContext().getJobParameters().get("republish")!=null){
			 sqlEndPointPublisher.rePublishRecords(Integer.parseInt(idArchive),endPointManager,userName);
		 }
		 else if(arg1.getStepContext().getJobParameters().get("publishEndPoint")!=null){
			 sqlEndPointPublisher.publishArchive(Integer.parseInt(idArchive),endPointManager,userName);
		 }
		 else if(arg1.getStepContext().getJobParameters().get("modified")!=null){
			 sqlEndPointPublisher.publishRecordsModified(Integer.parseInt(idArchive),endPointManager,userName);
		 }
		 else if(arg1.getStepContext().getJobParameters().get("notPublished")!=null){
			 sqlEndPointPublisher.publishRecordsNotPublished(Integer.parseInt(idArchive),endPointManager,userName);
		 }
		 else if(arg1.getStepContext().getJobParameters().get("errors")!=null){
			 sqlEndPointPublisher.publishErrorResults(Integer.parseInt(idArchive),endPointManager,userName);
		 }
		 return RepeatStatus.FINISHED;
	 }
	public void setSqlEndPointPublisher(SQLEndPointPublisher sqlEndPointPublisher) {
		this.sqlEndPointPublisher = sqlEndPointPublisher;
	}
	
}

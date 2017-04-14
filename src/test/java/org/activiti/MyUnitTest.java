package org.activiti;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyUnitTest {
	
	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();
	
	@Test
	@Deployment(resources = {"org/activiti/test/my-process.bpmn20.xml"})
	public void test() {
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
		assertNotNull(processInstance);
		
		List<Task> tasks = activitiRule.getTaskService().createTaskQuery().list();
		for(Task task:tasks){
		assertNotEquals("Activiti is awesome1!", task.getName());
		}
	}
	
	@Test
	public void testUserguideCode(){
		ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
		RepositoryService repositoryService=processEngine.getRepositoryService();
		repositoryService.createDeployment().addClasspathResource("org/activiti/test/VacationRequest.bpmn20.xml").deploy();
		System.out.println("Number of process definitions: "+repositoryService.createProcessDefinitionQuery().count());
		
		Map<String,Object> variables=new  HashMap<String,Object> ();
		variables.put("employeeName", "Kermit");
		variables.put("numberOfDays", new Integer(4));
		variables.put("vacationMotivation", "I'm really tired!");
		
		RuntimeService runtimeService=processEngine.getRuntimeService();
		ProcessInstance processInstance=runtimeService.startProcessInstanceByKey("vacationRequest",variables);
		System.out.println("Number of process instances: "+runtimeService.createProcessInstanceQuery().count());
	
		TaskService taskService=processEngine.getTaskService();
		List<Task> tasks=taskService.createTaskQuery().taskCandidateGroup("management").list();
//		int t=tasks.size();
		for(Task task:tasks){
			System.out.println("Task available:	"+task.getName());
		}
		
		Task task=tasks.get(0);
		Map<String,Object> taskVariables=new HashMap<String,Object>();
		taskVariables.put("vacationApproved", "false");
		taskVariables.put("managerMotivation","We have a tight deadline!");
		taskService.complete(task.getId(),taskVariables);
	}

}

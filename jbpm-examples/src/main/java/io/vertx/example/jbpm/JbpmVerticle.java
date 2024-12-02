package io.vertx.example.jbpm;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;


public class JbpmVerticle extends AbstractVerticle {

	@Override
	public void start() {
		System.out.println("Starting JbpmVerticle");

		EventBus eb = vertx.eventBus();

		/* Initializing kie-session to start a process */
		KieContainer container = KieServices.Factory.get().getKieClasspathContainer();
		KieSession ksession = container.newKieSession("sampleSession");

		eb.consumer("process-message", message -> {
			System.out.println("Received message on consumer " + message.body());
			Map<String, Object> input = new HashMap<String, Object>();
			input.put("message", message.body());

			// Start a process
			WorkflowProcessInstance p = (WorkflowProcessInstance) ksession.startProcess("com.sample.bpmn.hello", input);
			System.out.println("Generated Process ID: " + p.getId());
		});
	}

}

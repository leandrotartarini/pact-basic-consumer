package br.ce.wcaquino.consumer.tasks.pact;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import br.ce.wcaquino.consumer.tasks.model.Task;
import br.ce.wcaquino.consumer.tasks.service.TasksConsumer;
import org.apache.http.client.ClientProtocolException;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SaveTaskTest {

    @Rule
    public PactProviderRule mockProvider = new PactProviderRule("Tasks", this);

    @Pact(consumer = "BasicConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {

        DslPart requestBody = new PactDslJsonBody()
                .stringType("task", "Task With String")
                .date("dueDate", "yyyy-MM-dd", new Date());

        DslPart responseBody = new PactDslJsonBody()
                .numberType("id")
                .stringType("task")
                .stringType("dueDate");

        return builder
                .uponReceiving("Save a task with string")
                .path("/todo")
                .method("POST")
                .body(requestBody)
                .willRespondWith()
                .status(201)
                .body(responseBody)
                .toPact();
    }

    @Test
    @PactVerification
    public void test() throws ClientProtocolException, IOException {
        TasksConsumer consumer = new TasksConsumer(mockProvider.getUrl());
        DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

        Task task = consumer.saveTask("Task With String", dFormat.format(new Date()));

        assertThat(task.getId(), is(notNullValue()));
        assertThat(task.getTask(), is(notNullValue()));
    }

}
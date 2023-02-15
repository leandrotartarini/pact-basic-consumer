package br.ce.wcaquino.consumer.tasks.pact;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import br.ce.wcaquino.consumer.tasks.model.Task;
import br.ce.wcaquino.consumer.tasks.service.TasksConsumer;
import org.apache.http.client.ClientProtocolException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;

import au.com.dius.pact.consumer.junit.PactProviderRule;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class GetTaskByIdTest {

    @Rule
    public PactProviderRule mockProvider = new PactProviderRule("Tasks", this);

    @Pact(consumer = "BasicConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        DslPart body = new PactDslJsonBody()
                .numberType("id", 1L)
                .stringType("task")
                .stringType("dueDate");

        return builder
                .given("There is a task with id = 1")
                .uponReceiving("Retrieve Task #1")
                .path("/todo/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(body)
                //.body("{\"id\": 1, \"task\": \"Task from pact\", \"dueDate\": \"2020-01-01\"}")
                .toPact();
    }

    @Test
    @PactVerification
    public void test() throws ClientProtocolException, IOException {
        TasksConsumer consumer = new TasksConsumer(mockProvider.getUrl());
        System.out.println(mockProvider.getUrl());

        Task task = consumer.getTask(1L);
        System.out.println(task);

        assertThat(task.getId(), is(1L));
        assertThat(task.getTask(), is(notNullValue()));
    }

}
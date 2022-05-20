package com.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class Consumer {

    // KafkaListener is used as consumer on a given topic and inside a given groupID

    @KafkaListener(topics = "test-topic", groupId = "test-group", containerFactory = "listenerFactory")
    public void trainingGroupListen(ConsumerRecord<String, String> record) {
        System.out.printf("Received Message from partiton %s and offset %s with key %s and value %s%n", record.partition(), record.offset(), record.key(), record.value());
        if (record.key().equals("startProcess")) {
            startProcess(record.value());
        }
    }

    private static void startProcess(String proess_id)
    {
        final String uri = String.format("http://camunda-server:8080/engine-rest/process-definition/key/%s/start", proess_id);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String bodyAsJsonString = "{\"variables\":{\"aVariable\":{\"value\":\"aStringValue\",\"type\":\"String\"},\"anotherVariable\":{\"value\":true,\"type\":\"Boolean\"}},\"businessKey\":\"myBusinessKey\"}";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(uri, new HttpEntity<String>(bodyAsJsonString, headers), String.class);
            System.out.println(response.getStatusCode());
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
            System.out.printf("### Please first deploy a process with process ID: \"%s\"%n", proess_id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

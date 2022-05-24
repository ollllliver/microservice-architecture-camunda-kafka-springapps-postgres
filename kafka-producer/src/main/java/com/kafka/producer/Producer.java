package com.kafka.producer;

import java.util.Scanner;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class Producer {

    // periodically request data from a rest endpoint and produce it into a kafka topic

    // properties
    private final KafkaTemplate<String, Object> jsonMessageKafkaTemplate;

    // constructor
    public Producer(KafkaTemplate<String, Object> jsonMessageKafkaTemplate) {
        this.jsonMessageKafkaTemplate = jsonMessageKafkaTemplate;
    }

    @Scheduled(fixedRate = 30000)
    public void doSomethingAfterStartup() {
        ListenableFuture<SendResult<String, Object>> send = jsonMessageKafkaTemplate.send(new ProducerRecord<>("test-topic", "startProcess", "process_id"));
        send.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onSuccess(SendResult<String, Object> sendResult) {
                RecordMetadata recordMetadata = sendResult.getRecordMetadata();
                System.out.printf("Record produced to partition %s and offset %s, with key %s and value %s%n", recordMetadata.partition(), recordMetadata.offset(), "startProcess", "process_id");
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.err.println("Record could not be produced!");
                throwable.printStackTrace();
            }
        });
    }
}
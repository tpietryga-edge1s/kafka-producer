package org.tobiaszpietryga.kafka_producer.sevice;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final KafkaTemplate kafkaTemplate;

	@Value("${orders.topic.name}")
	private String ordersTopicName;
	public void sendOrder(String order) {
		kafkaTemplate.send("orders", order);
	}
}

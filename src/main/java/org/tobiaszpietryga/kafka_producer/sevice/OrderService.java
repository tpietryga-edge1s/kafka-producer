package org.tobiaszpietryga.kafka_producer.sevice;

import java.util.concurrent.atomic.AtomicLong;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.tobiaszpietryga.kafka_producer.model.Order;
import org.tobiaszpietryga.kafka_producer.model.Status;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final KafkaTemplate<Long, Order> kafkaTemplate;
	private final AtomicLong idGenerator = new AtomicLong();

	@Value("${orders.topic.name}")
	private String ordersTopicName;
	public void sendOrder(String orderName) {
		Order newOrder = Order.builder().id(idGenerator.incrementAndGet()).name(orderName).status(Status.NEW).build();
		kafkaTemplate.send("orders", newOrder.getId(), newOrder);
	}
}

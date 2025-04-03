package org.tobiaszpietryga.order.sevice;

import java.util.concurrent.atomic.AtomicLong;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.tobiaszpietryga.order.common.model.Order;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final KafkaTemplate<Long, Order> kafkaTemplate;
	private final AtomicLong idGenerator = new AtomicLong();
	@Value("${orders.topic.name}")
	private String topicName;

	public void sendOrder(Order order) {
		order.setId(idGenerator.incrementAndGet());
		kafkaTemplate.send(topicName, order.getId(), order);
	}
}

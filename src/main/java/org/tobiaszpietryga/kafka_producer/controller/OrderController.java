package org.tobiaszpietryga.kafka_producer.controller;

import java.util.concurrent.atomic.AtomicLong;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tobiaszpietryga.kafka_producer.model.Order;
import org.tobiaszpietryga.kafka_producer.model.Status;
import org.tobiaszpietryga.kafka_producer.sevice.OrderService;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {
	Logger logger = LoggerFactory.getLogger(OrderController.class);
	private final OrderService orderService;

	@PostMapping
	public void makeOrder(@RequestBody String orderName) {
		logger.info("Received an orderName {}", orderName);
		orderService.sendOrder(orderName);
	}
}

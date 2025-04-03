package org.tobiaszpietryga.order.kafka;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.tobiaszpietryga.order.common.model.Order;
import org.tobiaszpietryga.order.common.model.Status;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcessingListener {
	private final KafkaTemplate<Long, Order> kafkaTemplate;

	@Data
	@NoArgsConstructor
	private static class OrderProcessingStatus {
		private ConfirmationStatus paymentStatus = ConfirmationStatus.UNKNOWN;
		private ConfirmationStatus stockStatus = ConfirmationStatus.UNKNOWN;
	}

	private enum ConfirmationStatus {
		UNKNOWN, CONFIRMED, REJECTED;
	}

	@Value("${orders.topic.name}")
	private String ordersTopicName;

	private Map<Long, OrderProcessingStatus> orderStatus = new ConcurrentHashMap<>();

	@KafkaListener(id = "order-service-payment-listener", topics = "${payment-orders.topic.name}", groupId = "order-service-payment-listener")
	public void processPayment(Order paymentOrder) {
		log.info("Payment: Received: {}", paymentOrder);
		OrderProcessingStatus orderProcessingStatus = orderStatus.computeIfAbsent(paymentOrder.getId(), id -> new OrderProcessingStatus());
		if (paymentOrder.getStatus().equals(Status.PARTIALLY_REJECTED)) {
			if (!orderProcessingStatus.getStockStatus().equals(ConfirmationStatus.UNKNOWN)) {
				finalizeProcessing(paymentOrder, Status.ROLLBACK);
				log.info("Payment: ROLLBACK: {}", paymentOrder);
			} else {
				orderProcessingStatus.setPaymentStatus(ConfirmationStatus.REJECTED);
				log.info("Payment: Stored: {}, {}", paymentOrder, orderProcessingStatus);
			}
		} else if (paymentOrder.getStatus().equals(Status.PARTIALLY_CONFIRMED)) {
			if (orderProcessingStatus.getStockStatus().equals(ConfirmationStatus.CONFIRMED)) {
				finalizeProcessing(paymentOrder, Status.CONFIRMED);
				log.info("Payment: CONFIRMED: {}", paymentOrder);
			} else if (orderProcessingStatus.getStockStatus().equals(ConfirmationStatus.REJECTED)) {
				finalizeProcessing(paymentOrder, Status.ROLLBACK);
				log.info("Payment: ROLLBACK: {}", paymentOrder);
			} else {
				orderProcessingStatus.setPaymentStatus(ConfirmationStatus.CONFIRMED);
				log.info("Payment: Stored: {}", orderProcessingStatus);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@KafkaListener(id = "order-service-stock-listener", topics = "${stock-orders.topic.name}", groupId = "order-service-stock-listener")
	public void processStock(Order stockOrder) {
		log.info("Stock: Received: {}", stockOrder);
		OrderProcessingStatus orderProcessingStatus = orderStatus.computeIfAbsent(stockOrder.getId(), id -> new OrderProcessingStatus());
		if (stockOrder.getStatus().equals(Status.PARTIALLY_REJECTED)) {
			if (!orderProcessingStatus.getPaymentStatus().equals(ConfirmationStatus.UNKNOWN)) {
				finalizeProcessing(stockOrder, Status.ROLLBACK);
				log.info("Stock: ROLLBACK: {}", stockOrder);
			} else {
				orderProcessingStatus.setStockStatus(ConfirmationStatus.REJECTED);
				log.info("Stock: Stored: {}, {}", stockOrder, orderProcessingStatus);
			}
		} else if (stockOrder.getStatus().equals(Status.PARTIALLY_CONFIRMED)) {
			if (orderProcessingStatus.getPaymentStatus().equals(ConfirmationStatus.CONFIRMED)) {
				finalizeProcessing(stockOrder, Status.CONFIRMED);
				log.info("Stock: CONFIRMED: {}", stockOrder);
			} else if (orderProcessingStatus.getPaymentStatus().equals(ConfirmationStatus.REJECTED)) {
				finalizeProcessing(stockOrder, Status.ROLLBACK);
				log.info("Stock: ROLLBACK: {}", stockOrder);
			} else {
				orderProcessingStatus.setStockStatus(ConfirmationStatus.CONFIRMED);
				log.info("Stock: Stored: {}, {}", stockOrder, orderProcessingStatus);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void finalizeProcessing(Order paymentOrder, Status status) {
		paymentOrder.setStatus(status);
		orderStatus.put(paymentOrder.getId(), new OrderProcessingStatus());
		kafkaTemplate.send(ordersTopicName, paymentOrder.getId(), paymentOrder);
	}
}

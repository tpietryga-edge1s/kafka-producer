package org.tobiaszpietryga.kafka_producer.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaConfiguration {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrapAddress;

	@Bean
	public NewTopic orders() {
		return new NewTopic("orders", 1, (short) 1);
	}

	@Bean
	public NewTopic paymentTopic() {
		return new NewTopic("payment-orders", 1, (short) 1);
	}

	@Bean
	public NewTopic stockTopic() {
		return new NewTopic("stock-orders", 1, (short) 1);
	}

}

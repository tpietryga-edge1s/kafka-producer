package org.tobiaszpietryga.order.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.tobiaszpietryga.order.common.model.Order;

@Configuration
@EnableKafka
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

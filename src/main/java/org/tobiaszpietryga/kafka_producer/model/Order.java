package org.tobiaszpietryga.kafka_producer.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;


@Builder
@Value
public class Order {
	Long id;
	String name;
	Status status;
}

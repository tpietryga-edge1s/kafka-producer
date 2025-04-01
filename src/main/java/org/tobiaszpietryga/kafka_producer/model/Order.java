package org.tobiaszpietryga.kafka_producer.model;

import lombok.Data;

@Data
public class Order {
	private Long id;
	private String name;
	private Status status;
}

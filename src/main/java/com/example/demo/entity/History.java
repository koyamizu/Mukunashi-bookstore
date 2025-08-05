package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class History {

	private Integer orderId;
	private Customer customer;
	private List<Item> items;
	private LocalDateTime orderDateTime;
}

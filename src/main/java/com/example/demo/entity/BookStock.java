package com.example.demo.entity;

import com.example.demo.exception.IlligalActionException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookStock {

	private Book book;
	private int stockNum;

	public void updateStockNum(int addNum) throws IlligalActionException {
		if (stockNum - addNum < 0) {
			throw new IlligalActionException("在庫がありません");
		}
		stockNum -= addNum;
	}
}

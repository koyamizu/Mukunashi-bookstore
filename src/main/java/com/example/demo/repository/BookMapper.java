package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.Book;
import com.example.demo.entity.BookStock;
import com.example.demo.entity.Customer;
import com.example.demo.entity.History;

@Mapper
public interface BookMapper {
	
	List<BookStock> selectAll();
	
	Book select(String isbn);
	
	Integer selectStockNum(String isbn);
	
	void update(@Param("isbn")String isbn, @Param("updatedStockNum") Integer updatedStockNum);

	void insertCustomer(Customer customer);

	Integer selectCustomerId(Customer customer);

	void insertOrder(Integer customerId);

	Integer selectOrderId(@Param("customerId") Integer customerId,@Param("orderDate") LocalDate orderDate);

	void insertItems(@Param("orderId") Integer orderId, @Param("itemSet") Set<Entry<Book, Integer>> itemSet);

	History selectOrderHistory(Integer orderId);

	void deleteOrder(Integer orderId);
}

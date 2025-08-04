package com.example.demo.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.demo.entity.Book;
import com.example.demo.entity.Customer;

@MybatisTest
public class BookMapperTest {

	@Autowired
	private BookMapper mapper;
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Test
	void test_update() {
		mapper.update("978-4-8222-5391-2", 10);
	}
	
	@Test
	void test_insert() {
		Customer customer=new Customer(
				null
				,"田中あさひ"
				,"030-1234-5678"
				,"101-1234"
				,"東京都西区椋梨2-1-4 ミルプレス椋梨203号室"
				);
		mapper.insertCustomer(customer);
	}
	
	@Test
	void test_selectCustomerId() {
		jdbcTemplate.update("INSERT INTO\n"
				+ "		  customers(id,name,tel,post_num,address)\n"
				+ "		VALUES\n"
				+ "		  (?,?,?,?,?)\n"
				+ "		;"
				,2
				,"田中あさひ"
				,"030-1234-5678"
				,"101-1234"
				,"東京都西区椋梨2-1-4 ミルプレス椋梨203号室");
		Customer customer=new Customer(
				null
				,"田中あさひ"
				,"030-1234-5678"
				,"101-1234"
				,"東京都西区椋梨2-1-4 ミルプレス椋梨203号室"
				);
		Integer actual=mapper.selectCustomerId(customer);
		assertThat(actual).isEqualTo(2);
	}

	@Test
	void test_insertOrder() {
		jdbcTemplate.update("INSERT INTO\n"
				+ "		  customers(id,name,tel,post_num,address)\n"
				+ "		VALUES\n"
				+ "		  (?,?,?,?,?)\n"
				+ "		;"
				,2
				,"田中あさひ"
				,"030-1234-5678"
				,"101-1234"
				,"東京都西区椋梨2-1-4 ミルプレス椋梨203号室");
		mapper.insertOrder(2);
	}
	
	@Test
	void test_selectOrderId() {
		jdbcTemplate.update("INSERT INTO\n"
				+ "		  customers(id,name,tel,post_num,address)\n"
				+ "		VALUES\n"
				+ "		  (?,?,?,?,?)\n"
				+ "		;"
				,2
				,"田中あさひ"
				,"030-1234-5678"
				,"101-1234"
				,"東京都西区椋梨2-1-4 ミルプレス椋梨203号室");
		jdbcTemplate.update("INSERT INTO\n"
				+ "		  order_histories(customer_id)\n"
				+ "		VALUES\n"
				+ "		  (?)\n"
				+ "		;"
				,2);
		Integer actual=mapper.selectOrderId(2, LocalDate.now());
		assertThat(actual).isEqualTo(1);
	}
	
	@Test
	void test_insertItems() {
		jdbcTemplate.update("INSERT INTO\n"
				+ "		  customers(id,name,tel,post_num,address)\n"
				+ "		VALUES\n"
				+ "		  (?,?,?,?,?)\n"
				+ "		;"
				,2
				,"田中あさひ"
				,"030-1234-5678"
				,"101-1234"
				,"東京都西区椋梨2-1-4 ミルプレス椋梨203号室");
		jdbcTemplate.update("INSERT INTO\n"
				+ "		  order_histories(customer_id)\n"
				+ "		VALUES\n"
				+ "		  (?)\n"
				+ "		;"
				,2);
		Integer orderId=jdbcTemplate.queryForObject("SELECT\n"
				+ "		  id\n"
				+ "		FROM\n"
				+ "		  order_histories\n"
				+ "		WHERE\n"
				+ "		  customer_id = ?\n"
				+ "		  AND\n"
				+ "		  CAST(created_at AS DATE) = ?\n"
				+ "		;"
				,Integer.class
				,2
				,LocalDate.now()
				);
		Book dummy1=new Book("978-4-8222-5391-2", "iPhoneアプリ超入門", 2200, "日経BP", LocalDate.of(2020, 2, 28));
		Book dummy2=new Book("978-4-7981-6365-9", "独習ASP.NET Webフォーム", 3800, "翔泳社", LocalDate.of(2020, 2, 17));
		Map<Book,Integer> items=Map.of(dummy1,2,dummy2,1);
		mapper.insertItems(orderId,items.entrySet());
	}
}

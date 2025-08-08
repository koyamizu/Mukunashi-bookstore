package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.Book;
import com.example.demo.entity.Customer;
import com.example.demo.entity.History;

//ここに適切なアノテーションを記述してください
public interface BookMapper {
	
//	戻り値がBookStockリストで、「全ての書籍の在庫を抽出するメソッド」を記述
	
//	戻り値がBookで、「isbnコードをもとに、書籍を抽出するメソッド」を記述
	
//	戻り値がIntegerで、「isbnコードをもとに、在庫数を抽出するメソッド」を記述
	
	Integer selectCustomerId(Customer customer);
	
	List<Integer> selectOrderId(@Param("customerId") Integer customerId,@Param("orderDate") LocalDate orderDate);
	
	History selectOrderHistory(Integer orderId);
	
//	引数が二つあります。以下のクエリ文やselectOrderIdメソッドを参考に、引数を記述してみてください
	void update(/*引数1,引数2*/);
	
//	<update id="update">
//	UPDATE
//	  book_stocks
//	SET
//	  stock_num = #{updatedStockNum}
//	WHERE
//	  isbn = #{isbn}
//	;
//	</update>
	
//	Customerクラスのオブジェクトを挿入するメソッドを記述

	void insertOrder(Integer customerId);

	void insertItems(@Param("orderId") Integer orderId, @Param("itemSet") Set<Entry<Book, Integer>> itemSet);

	void deleteOrder(Integer orderId);
}

package com.example.demo.helper;

import com.example.demo.entity.Customer;
import com.example.demo.form.CustomerForm;

public class CustomerConverter {

	public static Customer convertCustomer(CustomerForm customerForm) {
		Customer customer = new Customer();
		customer.setAddress(customerForm.getAddress());
		customer.setName(customerForm.getName());
		customer.setPostNum(customerForm.getPostNum());
		customer.setTel(customerForm.getTel());
		return customer;
	}
}

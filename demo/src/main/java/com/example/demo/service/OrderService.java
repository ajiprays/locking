package com.example.demo.service;

import com.example.demo.dto.request.PurchaseRequest;

public interface OrderService {

	void purchase(PurchaseRequest request);
	void purchase2(PurchaseRequest request);
	
}
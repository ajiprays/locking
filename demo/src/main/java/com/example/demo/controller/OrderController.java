package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.PurchaseRequest;
import com.example.demo.service.OrderService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping({"orders"})
public class OrderController {

	private final OrderService orderService;
	
	@PostMapping(value = "/purchase")
	public ResponseEntity<String> purchase(@RequestBody PurchaseRequest request) {
		try {
			orderService.purchase(request);
			return ResponseEntity.ok("success");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@PostMapping(value = "/purchase2")
	public ResponseEntity<String> purchase2(@RequestBody PurchaseRequest request) {
		try {
			orderService.purchase2(request);
			return ResponseEntity.ok("success");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@PostMapping(value = "/purchase/test")
	public ResponseEntity<String> purchaseTest() {
		try {
			AtomicReference<Exception> exception = new AtomicReference<>();
			ExecutorService executor = Executors.newFixedThreadPool(4);
			List<CompletableFuture<Void>> futures = new ArrayList<>();
			futures.add(CompletableFuture.runAsync(() -> {
				orderService.purchase(new PurchaseRequest(1, 1, 3));
			}, executor));
			futures.add(CompletableFuture.runAsync(() -> {
				orderService.purchase(new PurchaseRequest(2, 1, 3));
			}, executor));
			
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();  
			executor.shutdown();
			return ResponseEntity.ok("success");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@PostMapping(value = "/purchase2/test")
	public ResponseEntity<String> purchase2Test() {
		try {
			AtomicReference<Exception> exception = new AtomicReference<>();
			ExecutorService executor = Executors.newFixedThreadPool(4);
			List<CompletableFuture<Void>> futures = new ArrayList<>();
			futures.add(CompletableFuture.runAsync(() -> {
				orderService.purchase2(new PurchaseRequest(1, 1, 3));
			}, executor));
			futures.add(CompletableFuture.runAsync(() -> {
				orderService.purchase2(new PurchaseRequest(2, 1, 3));
			}, executor));
			
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();  
			executor.shutdown();
			return ResponseEntity.ok("success");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
}
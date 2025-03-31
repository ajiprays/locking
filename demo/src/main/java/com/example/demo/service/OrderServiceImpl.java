package com.example.demo.service;

import java.time.ZonedDateTime;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.demo.dto.request.PurchaseRequest;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final TransactionTemplate transactionTemplate;
	
	
	@Transactional
	@PostConstruct
	public void initData() {
		userRepository.saveAndFlush(new User("user1", "password123", "user1@yopmail.com"));
		userRepository.saveAndFlush(new User("user2", "password123", "user2@yopmail.com"));
		productRepository.saveAndFlush(new Product("001", "Baju Koko", 3));
	}
	
	@Override
	public void purchase(PurchaseRequest request) {
		transactionTemplate.executeWithoutResult((tx) -> {
			try {
				validateUser(request);
				validateProduct(request);
				validateStock(request);
				Order order = mapToEntity(request);
				orderRepository.saveAndFlush(order);
				updateStock(order);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}catch (ObjectOptimisticLockingFailureException ex) {
				throw new RuntimeException("version is not update");
			}catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		});
	}

	@Transactional
	@Override
	public void purchase2(PurchaseRequest request) {
		validateUser(request);
		validateProduct(request);
		validateStock(request);
		Order order = mapToEntity(request);
		orderRepository.saveAndFlush(order);
		updateStock(order);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void updateStock(Order order) {
		Product product = order.getProduct();
		product.setStock(product.getStock() - order.getCount());
		productRepository.saveAndFlush(product);
	}
	
	private void validateProduct(PurchaseRequest request){
		if(productRepository.findWithLockingById(request.getProductId()).isEmpty()) {
			throw new RuntimeException("product not exists");
		}
//		if(productRepository.findById(request.getProductId()).isEmpty()) {
//			throw new RuntimeException("product not exists");
//		}
	}
	
	private void validateUser(PurchaseRequest request){
		if(userRepository.findById(request.getUserId()).isEmpty()) {
			throw new RuntimeException("user not exists");
		}
	}

	private void validateStock(PurchaseRequest request) {
		productRepository.findWithLockingById(request.getProductId()).ifPresent(product -> {
			if((product.getStock() - request.getCount()) < 0) {
				log.error(String.format("user : %d,stock is empty", request.getUserId()));
				throw new RuntimeException("stock is empty");
			}			
		});
	}
	
	private Order mapToEntity(PurchaseRequest request) {
		Order entity = new Order();
		entity.setUser(userRepository.findById(request.getUserId()).orElseThrow());
		entity.setProduct(productRepository.findById(request.getProductId()).orElseThrow());
		entity.setCount(request.getCount());
		entity.setOrderDate(ZonedDateTime.now());
		return entity;
	}
}
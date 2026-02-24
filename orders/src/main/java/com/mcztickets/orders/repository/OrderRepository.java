package com.mcztickets.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mcztickets.orders.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> { }

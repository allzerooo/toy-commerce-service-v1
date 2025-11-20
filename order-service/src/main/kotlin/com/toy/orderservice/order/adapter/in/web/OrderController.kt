package com.toy.orderservice.order.adapter.`in`.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController {

    @GetMapping("/welcome")
    fun welcome(): String {
        return "Welcome to Order Service"
    }
}

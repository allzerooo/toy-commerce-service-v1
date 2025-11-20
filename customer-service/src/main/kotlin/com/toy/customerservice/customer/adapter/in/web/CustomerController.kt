package com.toy.customerservice.customer.adapter.`in`.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/customers")
class CustomerController {

    @GetMapping("/welcome")
    fun welcome(): String {
        return "Welcome to Customer Service"
    }
}

package com.toy.userservice.account.domain.model

/**
 * 사용자 역할
 * - BUYER: 구매자
 * - SELLER: 판매자
 * - ADMIN: 관리자
 */
enum class UserRole() {
    BUYER,
    SELLER,
    ADMIN;
}

package ir.university.technicalservice

import org.springframework.stereotype.Component

@Component
class UiText {
    fun t(lang: String, key: String): String = text[key]?.get(lang) ?: text[key]?.get("fa") ?: key

    private val text = mapOf(
        "appName" to mapOf("fa" to "سامانه شبکه ارتباطی بین افراد فنی", "en" to "Technical Service Network"),
        "home" to mapOf("fa" to "خانه", "en" to "Home"),
        "technicians" to mapOf("fa" to "متخصصان", "en" to "Technicians"),
        "dashboard" to mapOf("fa" to "داشبورد متخصص", "en" to "Technician Dashboard"),
        "adminOwner" to mapOf("fa" to "مدیریت و مالک", "en" to "Admin and Owner"),
        "search" to mapOf("fa" to "جستجو و فیلتر زنده", "en" to "Live search and filters"),
        "featured" to mapOf("fa" to "متخصصان پیشنهادی", "en" to "Featured technicians"),
        "book" to mapOf("fa" to "ثبت درخواست", "en" to "Book service"),
        "reviews" to mapOf("fa" to "نظرها", "en" to "Reviews"),
        "verified" to mapOf("fa" to "تایید شده", "en" to "Verified"),
        "available" to mapOf("fa" to "فقط زمان دار", "en" to "Available only"),
        "role" to mapOf("fa" to "نقش", "en" to "Role"),
        "customer" to mapOf("fa" to "کاربر خدمات فنی", "en" to "Customer"),
        "technicianRole" to mapOf("fa" to "متخصص فنی", "en" to "Technician"),
        "admin" to mapOf("fa" to "مدیر سیستم", "en" to "Admin"),
        "owner" to mapOf("fa" to "مالک سیستم", "en" to "Owner")
    )
}

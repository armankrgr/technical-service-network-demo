package ir.university.technicalservice

import java.time.LocalDateTime

enum class Role { CUSTOMER, TECHNICIAN, ADMIN, OWNER }
enum class RequestStatus { SUBMITTED, ACCEPTED, REJECTED }

data class ServiceCategory(
    val key: String,
    val icon: String,
    val fa: String,
    val en: String,
    val descriptionFa: String,
    val descriptionEn: String
) {
    fun label(lang: String) = if (lang == "en") en else fa
    fun description(lang: String) = if (lang == "en") descriptionEn else descriptionFa
}

data class TimeSlot(
    val id: String,
    val start: LocalDateTime,
    var booked: Boolean = false
)

data class Review(
    val author: String,
    val stars: Int,
    val comment: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class Technician(
    val id: Long,
    val nameFa: String,
    val nameEn: String,
    val titleFa: String,
    val titleEn: String,
    val cityFa: String,
    val cityEn: String,
    val categoryFa: String,
    val categoryEn: String,
    val skillsFa: List<String>,
    val skillsEn: List<String>,
    val bioFa: String,
    val bioEn: String,
    val completedJobs: Int,
    val verified: Boolean,
    val distanceKm: Double,
    val responseTimeFa: String,
    val responseTimeEn: String,
    val priceFromToman: Int,
    val approximateFeeFa: String,
    val approximateFeeEn: String,
    val slots: MutableList<TimeSlot>,
    val reviews: MutableList<Review>
) {
    val ratingAverage: Double
        get() = if (reviews.isEmpty()) 0.0 else reviews.map { it.stars }.average()

    val availableSlotCount: Int
        get() = slots.count { !it.booked }

    val reviewCount: Int
        get() = reviews.size

    fun title(lang: String) = if (lang == "en") titleEn else titleFa
    fun displayName(lang: String) = if (lang == "en") nameEn else nameFa
    fun city(lang: String) = if (lang == "en") cityEn else cityFa
    fun category(lang: String) = if (lang == "en") categoryEn else categoryFa
    fun skills(lang: String) = if (lang == "en") skillsEn else skillsFa
    fun bio(lang: String) = if (lang == "en") bioEn else bioFa
    fun responseTime(lang: String) = if (lang == "en") responseTimeEn else responseTimeFa
    fun approximateFee(lang: String) = if (lang == "en") approximateFeeEn else approximateFeeFa
    fun avatarText(lang: String) = displayName(lang).trim().take(1).ifBlank { "?" }
}

data class ServiceRequest(
    val id: String,
    val technicianId: Long,
    val technicianName: String,
    val customerName: String,
    val title: String,
    val locationNote: String,
    val slotId: String,
    val slotStart: LocalDateTime,
    var status: RequestStatus = RequestStatus.SUBMITTED
)

data class SearchCriteria(
    val q: String? = null,
    val category: String? = null,
    val city: String? = null,
    val minRating: Double? = null,
    val availableOnly: Boolean = false,
    val sort: String? = "rating"
)

data class DemoStats(
    val technicianCount: Int,
    val categoryCount: Int,
    val completedJobs: Int,
    val averageRating: Double,
    val cityCount: Int,
    val openSlots: Int
)

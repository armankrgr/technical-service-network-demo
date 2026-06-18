package ir.university.technicalservice

import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.roundToInt

@Repository
class DemoDataRepo {
    private val base = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusDays(1)

    val categories = listOf(
        ServiceCategory("electrical", "⚡", "برق کاری", "Electrical"),
        ServiceCategory("plumbing", "🔧", "لوله کشی", "Plumbing"),
        ServiceCategory("plastering", "🏗", "گچ کاری", "Plastering"),
        ServiceCategory("painting", "🎨", "نقاشی ساختمان", "Painting"),
        ServiceCategory("computer", "💻", "تعمیر کامپیوتر", "Computer Repair"),
        ServiceCategory("cctv", "📹", "نصب دوربین", "CCTV Install"),
        ServiceCategory("ac", "❄", "تعمیر کولر", "AC Repair")
    )

    val categoriesFa: List<String>
        get() = categories.map { it.fa }

    val categoriesEn: List<String>
        get() = categories.map { it.en }

    val technicians: MutableList<Technician> = mutableListOf(
        technician(1, "رضا احمدی", "Reza Ahmadi", "تهران", "Tehran", "برق کاری", "Electrical", listOf("سیم کشی", "تابلو برق", "عیب یابی"), listOf("wiring", "panels", "diagnostics"), 4.8, 132, true, 3.2, "از ۴۵۰ هزار تومان", "from 450,000 toman"),
        technician(2, "مریم کریمی", "Maryam Karimi", "کرج", "Karaj", "تعمیر کامپیوتر", "Computer Repair", listOf("ویندوز", "شبکه", "ارتقا سخت افزار"), listOf("Windows", "network", "hardware upgrade"), 4.7, 88, true, 8.5, "از ۳۵۰ هزار تومان", "from 350,000 toman"),
        technician(3, "علی صادقی", "Ali Sadeghi", "اصفهان", "Isfahan", "لوله کشی", "Plumbing", listOf("رفع نشتی", "پکیج", "لوله کشی آشپزخانه"), listOf("leak repair", "boiler", "kitchen plumbing"), 4.5, 101, true, 5.1, "از ۵۰۰ هزار تومان", "from 500,000 toman"),
        technician(4, "سارا محمدی", "Sara Mohammadi", "شیراز", "Shiraz", "نقاشی ساختمان", "Painting", listOf("رنگ روغنی", "رنگ پلاستیک", "بازسازی"), listOf("oil paint", "latex paint", "renovation"), 4.9, 76, true, 2.4, "متری توافقی", "price per meter"),
        technician(5, "حسین رستمی", "Hossein Rostami", "مشهد", "Mashhad", "نصب دوربین", "CCTV Install", listOf("دوربین IP", "DVR", "کابل کشی"), listOf("IP camera", "DVR", "cabling"), 4.4, 59, false, 6.7, "از ۷۵۰ هزار تومان", "from 750,000 toman"),
        technician(6, "نرگس مرادی", "Narges Moradi", "تبریز", "Tabriz", "گچ کاری", "Plastering", listOf("گچ بری", "سقف کاذب", "ترمیم"), listOf("decorative plaster", "false ceiling", "repair"), 4.6, 64, true, 4.3, "بازدید رایگان", "free visit"),
        technician(7, "امیر جعفری", "Amir Jafari", "رشت", "Rasht", "تعمیر کولر", "AC Repair", listOf("سرویس کولر", "تعویض پمپ", "عیب یابی"), listOf("service", "pump replacement", "diagnostics"), 4.3, 47, false, 9.0, "از ۴۰۰ هزار تومان", "from 400,000 toman")
    )

    private val requests = linkedMapOf<String, ServiceRequest>()

    fun featured() = technicians.sortedByDescending { it.ratingAverage }.take(3)
    fun findTechnician(id: Long) = technicians.firstOrNull { it.id == id }
    fun allRequests() = requests.values.sortedByDescending { it.slotStart }
    fun findRequest(id: String) = requests[id]
    fun stats() = DemoStats(
        technicianCount = technicians.size,
        categoryCount = categories.size,
        completedJobs = technicians.sumOf { it.completedJobs },
        averageRating = technicians.map { it.ratingAverage }.average()
    )

    fun search(criteria: SearchCriteria): List<Technician> = technicians.filter { tech ->
        val query = criteria.q.orEmpty().trim().lowercase()
        val haystack = listOf(
            tech.nameFa, tech.nameEn, tech.cityFa, tech.cityEn, tech.categoryFa, tech.categoryEn,
            tech.skillsFa.joinToString(" "), tech.skillsEn.joinToString(" ")
        ).joinToString(" ").lowercase()
        val queryOk = query.isBlank() || haystack.contains(query)
        val categoryOk = criteria.category.isNullOrBlank() || tech.categoryFa == criteria.category || tech.categoryEn == criteria.category
        val cityOk = criteria.city.isNullOrBlank() || tech.cityFa == criteria.city || tech.cityEn == criteria.city
        val ratingOk = criteria.minRating == null || tech.ratingAverage >= criteria.minRating
        val availableOk = !criteria.availableOnly || tech.slots.any { !it.booked }
        queryOk && categoryOk && cityOk && ratingOk && availableOk
    }.sortedWith(compareByDescending<Technician> { it.ratingAverage }.thenBy { it.distanceKm })

    @Synchronized
    fun book(technicianId: Long, slotId: String, customerName: String, title: String, locationNote: String): ServiceRequest {
        val technician = findTechnician(technicianId) ?: error("Technician not found")
        val cleanCustomerName = customerName.trim()
        val cleanTitle = title.trim()
        val cleanLocationNote = locationNote.trim()
        require(cleanCustomerName.isNotBlank()) { "Customer name is required" }
        require(cleanTitle.isNotBlank()) { "Request title is required" }
        require(cleanLocationNote.isNotBlank()) { "Address or location note is required" }
        val slot = technician.slots.firstOrNull { it.id == slotId && !it.booked } ?: error("Selected slot is no longer available")
        slot.booked = true
        val request = ServiceRequest(
            id = UUID.randomUUID().toString().substring(0, 8).uppercase(),
            technicianId = technician.id,
            technicianName = technician.nameFa,
            customerName = cleanCustomerName,
            title = cleanTitle,
            locationNote = cleanLocationNote,
            slotId = slot.id,
            slotStart = slot.start
        )
        requests[request.id] = request
        return request
    }

    @Synchronized
    fun addReview(technicianId: Long, author: String, stars: Int, comment: String): Technician {
        val technician = findTechnician(technicianId) ?: error("Technician not found")
        val cleanAuthor = author.trim().ifBlank { "کاربر مهمان" }
        val cleanComment = comment.trim()
        require(cleanComment.isNotBlank()) { "Comment is required" }
        technician.reviews.add(0, Review(cleanAuthor, stars.coerceIn(1, 5), cleanComment))
        return technician
    }

    @Synchronized
    fun updateRequestStatus(requestId: String, status: RequestStatus) {
        requests[requestId]?.status = status
    }

    private fun technician(
        id: Long,
        nameFa: String,
        nameEn: String,
        cityFa: String,
        cityEn: String,
        categoryFa: String,
        categoryEn: String,
        skillsFa: List<String>,
        skillsEn: List<String>,
        ratingSeed: Double,
        jobs: Int,
        verified: Boolean,
        distance: Double,
        feeFa: String,
        feeEn: String
    ) = Technician(
        id = id,
        nameFa = nameFa,
        nameEn = nameEn,
        cityFa = cityFa,
        cityEn = cityEn,
        categoryFa = categoryFa,
        categoryEn = categoryEn,
        skillsFa = skillsFa,
        skillsEn = skillsEn,
        bioFa = "متخصص ${categoryFa} با تجربه عملی در پروژه های خانگی و اداری. زمان بندی شفاف، توضیح مرحله به مرحله کار و ثبت بازخورد مشتریان در سامانه.",
        bioEn = "Experienced ${categoryEn.lowercase()} specialist for home and office jobs with clear scheduling, transparent workflow, and customer feedback tracking.",
        completedJobs = jobs,
        verified = verified,
        distanceKm = distance,
        approximateFeeFa = feeFa,
        approximateFeeEn = feeEn,
        slots = MutableList(4) { index -> TimeSlot("T$id-${index + 1}", base.plusDays(index.toLong()).plusHours((id + index) % 5)) },
        reviews = sampleReviews(ratingSeed)
    )

    private fun sampleReviews(ratingSeed: Double): MutableList<Review> {
        val top = ratingSeed.roundToInt().coerceIn(1, 5)
        val base = ratingSeed.toInt().coerceIn(1, 5)
        return mutableListOf(
            Review("کاربر نمونه", top, "پاسخگویی خوب، توضیح شفاف و انجام کار در زمان مقرر."),
            Review("مشتری محلی", base, "قیمت تقریبی قبل از شروع کار مشخص شد و نتیجه برای دمو قابل اعتماد بود."),
            Review("Sample customer", top, "Reliable service, tidy work, and clear explanation.")
        )
    }
}

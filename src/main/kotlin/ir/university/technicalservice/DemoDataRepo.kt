package ir.university.technicalservice

import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
class DemoDataRepo {
    private val base = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusDays(1)

    val categoriesFa = listOf("برق کاری", "لوله کشی", "گچ کاری", "نقاشی ساختمان", "تعمیر کامپیوتر", "نصب دوربین", "تعمیر کولر")
    val categoriesEn = listOf("Electrical", "Plumbing", "Plastering", "Painting", "Computer Repair", "CCTV Install", "AC Repair")

    val technicians: MutableList<Technician> = mutableListOf(
        technician(1, "رضا احمدی", "Reza Ahmadi", "تهران", "Tehran", "برق کاری", "Electrical", listOf("سیم کشی", "تابلو برق", "عیب یابی"), listOf("wiring", "panels", "diagnostics"), 4.8, 132, true, 3.2, "از ۴۵۰ هزار تومان"),
        technician(2, "مریم کریمی", "Maryam Karimi", "کرج", "Karaj", "تعمیر کامپیوتر", "Computer Repair", listOf("ویندوز", "شبکه", "ارتقا سخت افزار"), listOf("Windows", "network", "hardware upgrade"), 4.7, 88, true, 8.5, "از ۳۵۰ هزار تومان"),
        technician(3, "علی صادقی", "Ali Sadeghi", "اصفهان", "Isfahan", "لوله کشی", "Plumbing", listOf("رفع نشتی", "پکیج", "لوله کشی آشپزخانه"), listOf("leak repair", "boiler", "kitchen plumbing"), 4.5, 101, true, 5.1, "از ۵۰۰ هزار تومان"),
        technician(4, "سارا محمدی", "Sara Mohammadi", "شیراز", "Shiraz", "نقاشی ساختمان", "Painting", listOf("رنگ روغنی", "رنگ پلاستیک", "بازسازی"), listOf("oil paint", "latex paint", "renovation"), 4.9, 76, true, 2.4, "متری توافقی"),
        technician(5, "حسین رستمی", "Hossein Rostami", "مشهد", "Mashhad", "نصب دوربین", "CCTV Install", listOf("دوربین IP", "DVR", "کابل کشی"), listOf("IP camera", "DVR", "cabling"), 4.4, 59, false, 6.7, "از ۷۵۰ هزار تومان"),
        technician(6, "نرگس مرادی", "Narges Moradi", "تبریز", "Tabriz", "گچ کاری", "Plastering", listOf("گچ بری", "سقف کاذب", "ترمیم"), listOf("decorative plaster", "false ceiling", "repair"), 4.6, 64, true, 4.3, "بازدید رایگان"),
        technician(7, "امیر جعفری", "Amir Jafari", "رشت", "Rasht", "تعمیر کولر", "AC Repair", listOf("سرویس کولر", "تعویض پمپ", "عیب یابی"), listOf("service", "pump replacement", "diagnostics"), 4.3, 47, false, 9.0, "از ۴۰۰ هزار تومان")
    )

    private val requests = linkedMapOf<String, ServiceRequest>()

    fun featured() = technicians.sortedByDescending { it.ratingAverage }.take(3)
    fun findTechnician(id: Long) = technicians.firstOrNull { it.id == id }
    fun allRequests() = requests.values.sortedByDescending { it.slotStart }
    fun findRequest(id: String) = requests[id]

    fun search(criteria: SearchCriteria): List<Technician> = technicians.filter { tech ->
        val haystack = listOf(
            tech.nameFa, tech.nameEn, tech.cityFa, tech.cityEn, tech.categoryFa, tech.categoryEn,
            tech.skillsFa.joinToString(" "), tech.skillsEn.joinToString(" ")
        ).joinToString(" ").lowercase()
        val queryOk = criteria.q.isNullOrBlank() || haystack.contains(criteria.q.lowercase())
        val categoryOk = criteria.category.isNullOrBlank() || tech.categoryFa == criteria.category || tech.categoryEn == criteria.category
        val cityOk = criteria.city.isNullOrBlank() || tech.cityFa == criteria.city || tech.cityEn == criteria.city
        val ratingOk = criteria.minRating == null || tech.ratingAverage >= criteria.minRating
        val availableOk = !criteria.availableOnly || tech.slots.any { !it.booked }
        queryOk && categoryOk && cityOk && ratingOk && availableOk
    }.sortedWith(compareByDescending<Technician> { it.ratingAverage }.thenBy { it.distanceKm })

    @Synchronized
    fun book(technicianId: Long, slotId: String, customerName: String, title: String, locationNote: String): ServiceRequest {
        val technician = findTechnician(technicianId) ?: error("Technician not found")
        val slot = technician.slots.firstOrNull { it.id == slotId && !it.booked } ?: error("Selected slot is no longer available")
        slot.booked = true
        val request = ServiceRequest(
            id = UUID.randomUUID().toString().substring(0, 8).uppercase(),
            technicianId = technician.id,
            technicianName = technician.nameFa,
            customerName = customerName,
            title = title,
            locationNote = locationNote,
            slotId = slot.id,
            slotStart = slot.start
        )
        requests[request.id] = request
        return request
    }

    @Synchronized
    fun addReview(technicianId: Long, author: String, stars: Int, comment: String): Technician {
        val technician = findTechnician(technicianId) ?: error("Technician not found")
        technician.reviews.add(0, Review(author, stars.coerceIn(1, 5), comment))
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
        fee: String
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
        approximateFee = fee,
        slots = MutableList(4) { index -> TimeSlot("T$id-${index + 1}", base.plusDays(index.toLong()).plusHours((id + index) % 5)) },
        reviews = mutableListOf(
            Review("کاربر نمونه", ratingSeed.toInt(), "پاسخگویی خوب و انجام کار در زمان مقرر."),
            Review("Sample customer", (ratingSeed - 0.2).toInt().coerceAtLeast(1), "Reliable service and clear explanation.")
        )
    )
}

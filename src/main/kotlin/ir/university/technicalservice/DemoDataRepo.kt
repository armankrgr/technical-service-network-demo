package ir.university.technicalservice

import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt

@Repository
class DemoDataRepo {
    private val base = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusDays(1)

    val categories = listOf(
        ServiceCategory("electrical", "⚡", "برق کاری", "Electrical", "عیب یابی، سیم کشی، تابلو برق و ایمنی ساختمان.", "Troubleshooting, wiring, panels, and building safety."),
        ServiceCategory("plumbing", "🔧", "لوله کشی", "Plumbing", "رفع نشتی، نصب شیرآلات، پکیج و مسیر آب.", "Leak repair, fixtures, boilers, and water lines."),
        ServiceCategory("plastering", "▣", "گچ کاری", "Plastering", "ترمیم دیوار، گچ بری، سقف کاذب و آماده سازی رنگ.", "Wall repair, decorative plaster, ceilings, and paint prep."),
        ServiceCategory("painting", "🎨", "نقاشی ساختمان", "Painting", "رنگ روغنی، پلاستیک، پتینه و بازسازی سبک.", "Oil paint, latex, texture work, and light renovation."),
        ServiceCategory("computer", "💻", "تعمیر کامپیوتر", "Computer Repair", "نرم افزار، سخت افزار، ارتقا و پشتیبانی حضوری.", "Software, hardware, upgrades, and on-site support."),
        ServiceCategory("cctv", "◉", "نصب دوربین", "CCTV Install", "طراحی نقطه دوربین، کابل کشی، DVR و کنترل از راه دور.", "Camera placement, cabling, DVR, and remote access."),
        ServiceCategory("ac", "❄", "تعمیر کولر", "AC Repair", "سرویس کولر آبی و گازی، عیب یابی و تعویض قطعه.", "Evaporative and split AC service, diagnostics, and parts."),
        ServiceCategory("appliance", "▣", "تعمیر لوازم خانگی", "Appliance Repair", "یخچال، ماشین لباسشویی، ظرفشویی و اجاق گاز.", "Fridges, washing machines, dishwashers, and ovens."),
        ServiceCategory("faucets", "🚿", "نصب شیرآلات", "Fixture Install", "تعویض شیر، سیفون، دوش، روشویی و آب بندی.", "Faucets, siphons, showers, basins, and sealing."),
        ServiceCategory("carpentry", "🪚", "کابینت و نجاری", "Cabinetry", "تنظیم درب، کابینت، قفسه و کارهای چوبی سبک.", "Cabinet doors, shelves, fittings, and light woodwork."),
        ServiceCategory("tiling", "▥", "کاشی و سرامیک", "Tile and Ceramic", "ترمیم کاشی، بندکشی، نصب سرامیک و آب بندی.", "Tile repair, grouting, ceramic install, and sealing."),
        ServiceCategory("network", "🌐", "خدمات شبکه و اینترنت", "Network and Internet", "مودم، وای فای، کابل شبکه و عیب یابی اتصال.", "Routers, Wi-Fi, network cabling, and connectivity fixes."),
        ServiceCategory("mobile", "📱", "تعمیر موبایل", "Mobile Repair", "تعویض باتری، نمایشگر، نرم افزار و بازیابی اطلاعات.", "Battery, display, software, and data recovery."),
        ServiceCategory("cleaning", "✦", "نظافت و خدمات عمومی", "Cleaning Services", "نظافت پس از تعمیر، جابجایی سبک و آماده سازی فضا.", "Post-repair cleaning, light moving, and space prep."),
        ServiceCategory("renovation", "🏠", "بازسازی و تعمیرات جزئی", "Minor Renovation", "هماهنگی چند خدمت برای تعمیرات کوچک خانه و دفتر.", "Coordinated small repairs for homes and offices.")
    )

    val categoriesFa: List<String>
        get() = categories.map { it.fa }

    val categoriesEn: List<String>
        get() = categories.map { it.en }

    val technicians: MutableList<Technician> = mutableListOf(
        technician(1, "رضا احمدی", "Reza Ahmadi", "تکنسین ارشد برق ساختمان", "Senior building electrician", "تهران", "Tehran", "برق کاری", "Electrical", listOf("سیم کشی", "تابلو برق", "عیب یابی", "ارتینگ"), listOf("wiring", "panels", "diagnostics", "grounding"), 4.8, 132, true, 3.2, "کمتر از ۲ ساعت", "under 2 hours", 450000, "از ۴۵۰ هزار تومان", "from 450,000 toman"),
        technician(2, "لاله پورصادق", "Laleh Poursadegh", "برق کار واحدهای اداری", "Office electrical specialist", "کرج", "Karaj", "برق کاری", "Electrical", listOf("نورپردازی", "کلید و پریز", "ایمنی", "عیب یابی سریع"), listOf("lighting", "switches", "safety", "fast diagnostics"), 4.6, 91, true, 6.4, "امروز عصر", "today evening", 390000, "از ۳۹۰ هزار تومان", "from 390,000 toman"),
        technician(3, "علی صادقی", "Ali Sadeghi", "متخصص رفع نشتی و پکیج", "Leak and boiler specialist", "اصفهان", "Isfahan", "لوله کشی", "Plumbing", listOf("رفع نشتی", "پکیج", "لوله کشی آشپزخانه", "فشار آب"), listOf("leak repair", "boiler", "kitchen plumbing", "water pressure"), 4.5, 101, true, 5.1, "۳ تا ۴ ساعت", "3 to 4 hours", 500000, "از ۵۰۰ هزار تومان", "from 500,000 toman"),
        technician(4, "کیان مهر", "Kian Mehr", "نصاب و تعمیرکار لوله", "Pipe installer and repairer", "تهران", "Tehran", "لوله کشی", "Plumbing", listOf("لوله پنج لایه", "سیفون", "آب بندی", "شیرآلات"), listOf("PEX pipe", "siphon", "sealing", "fixtures"), 4.7, 118, true, 2.8, "کمتر از ۲ ساعت", "under 2 hours", 520000, "از ۵۲۰ هزار تومان", "from 520,000 toman"),
        technician(5, "نرگس مرادی", "Narges Moradi", "گچ کار و ترمیم کار داخلی", "Interior plaster repair specialist", "تبریز", "Tabriz", "گچ کاری", "Plastering", listOf("گچ بری", "سقف کاذب", "ترمیم ترک", "زیرسازی"), listOf("decorative plaster", "false ceiling", "crack repair", "surface prep"), 4.6, 64, true, 4.3, "فردا صبح", "tomorrow morning", 620000, "بازدید رایگان", "free visit"),
        technician(6, "آرش فرهادی", "Arash Farhadi", "مجری گچ و سقف سبک", "Plaster and light ceiling installer", "یزد", "Yazd", "گچ کاری", "Plastering", listOf("ترمیم دیوار", "ابزار ساده", "سقف کاذب", "آماده سازی رنگ"), listOf("wall repair", "simple molding", "false ceiling", "paint prep"), 4.3, 53, false, 7.2, "تا ۲۴ ساعت", "within 24 hours", 570000, "از ۵۷۰ هزار تومان", "from 570,000 toman"),
        technician(7, "سارا محمدی", "Sara Mohammadi", "نقاش ساختمان و بازسازی سبک", "Painter and light renovation lead", "شیراز", "Shiraz", "نقاشی ساختمان", "Painting", listOf("رنگ روغنی", "رنگ پلاستیک", "پتینه", "بازسازی"), listOf("oil paint", "latex paint", "texture", "renovation"), 4.9, 76, true, 2.4, "امروز", "today", 650000, "متری توافقی", "price per meter"),
        technician(8, "حمید نریمانی", "Hamid Narimani", "نقاش سریع واحدهای اجاره ای", "Fast painter for rental units", "تهران", "Tehran", "نقاشی ساختمان", "Painting", listOf("رنگ فوری", "لکه گیری", "بتونه", "هماهنگی مصالح"), listOf("quick paint", "spot repair", "putty", "material planning"), 4.4, 82, false, 4.9, "فردا عصر", "tomorrow evening", 480000, "از ۴۸۰ هزار تومان", "from 480,000 toman"),
        technician(9, "مریم کریمی", "Maryam Karimi", "پشتیبان کامپیوتر و شبکه", "Computer and network support", "کرج", "Karaj", "تعمیر کامپیوتر", "Computer Repair", listOf("ویندوز", "شبکه", "ارتقا سخت افزار", "بکاپ"), listOf("Windows", "network", "hardware upgrade", "backup"), 4.7, 88, true, 8.5, "کمتر از ۳ ساعت", "under 3 hours", 350000, "از ۳۵۰ هزار تومان", "from 350,000 toman"),
        technician(10, "سینا یزدانی", "Sina Yazdani", "تعمیرکار لپ تاپ و سیستم", "Laptop and PC repairer", "یزد", "Yazd", "تعمیر کامپیوتر", "Computer Repair", listOf("نصب نرم افزار", "تعویض SSD", "پاکسازی ویروس", "عیب یابی"), listOf("software install", "SSD upgrade", "virus cleanup", "diagnostics"), 4.5, 73, true, 3.7, "امروز عصر", "today evening", 320000, "از ۳۲۰ هزار تومان", "from 320,000 toman"),
        technician(11, "حسین رستمی", "Hossein Rostami", "نصاب دوربین و امنیت", "CCTV and security installer", "مشهد", "Mashhad", "نصب دوربین", "CCTV Install", listOf("دوربین IP", "DVR", "کابل کشی", "انتقال تصویر"), listOf("IP camera", "DVR", "cabling", "remote view"), 4.4, 59, false, 6.7, "تا ۲۴ ساعت", "within 24 hours", 750000, "از ۷۵۰ هزار تومان", "from 750,000 toman"),
        technician(12, "پریسا اسدی", "Parisa Asadi", "طراح پوشش دوربین فروشگاه", "Retail camera coverage designer", "تهران", "Tehran", "نصب دوربین", "CCTV Install", listOf("جانمایی", "دوربین فروشگاهی", "NVR", "آموزش کاربر"), listOf("placement", "retail cameras", "NVR", "user training"), 4.8, 96, true, 5.4, "فردا صبح", "tomorrow morning", 840000, "از ۸۴۰ هزار تومان", "from 840,000 toman"),
        technician(13, "امیر جعفری", "Amir Jafari", "سرویس کار کولر آبی و گازی", "Evaporative and split AC service", "رشت", "Rasht", "تعمیر کولر", "AC Repair", listOf("سرویس کولر", "تعویض پمپ", "عیب یابی", "شارژ گاز"), listOf("service", "pump replacement", "diagnostics", "gas charge"), 4.3, 47, false, 9.0, "۲۴ تا ۴۸ ساعت", "24 to 48 hours", 400000, "از ۴۰۰ هزار تومان", "from 400,000 toman"),
        technician(14, "شیما داوری", "Shima Davari", "تعمیرکار سیستم سرمایش", "Cooling system technician", "اصفهان", "Isfahan", "تعمیر کولر", "AC Repair", listOf("کولر گازی", "ترموستات", "سرویس فصلی", "رسوب گیری"), listOf("split AC", "thermostat", "seasonal service", "descaling"), 4.7, 70, true, 4.4, "امروز", "today", 530000, "از ۵۳۰ هزار تومان", "from 530,000 toman"),
        technician(15, "بهزاد توکلی", "Behzad Tavakoli", "تعمیرکار لوازم خانگی آشپزخانه", "Kitchen appliance technician", "تهران", "Tehran", "تعمیر لوازم خانگی", "Appliance Repair", listOf("یخچال", "لباسشویی", "ظرفشویی", "تشخیص برد"), listOf("fridge", "washer", "dishwasher", "board diagnostics"), 4.6, 109, true, 3.8, "کمتر از ۴ ساعت", "under 4 hours", 600000, "از ۶۰۰ هزار تومان", "from 600,000 toman"),
        technician(16, "مینا ابراهیمی", "Mina Ebrahimi", "کارشناس سرویس لوازم خانگی", "Home appliance service specialist", "شیراز", "Shiraz", "تعمیر لوازم خانگی", "Appliance Repair", listOf("ماشین لباسشویی", "اجاق گاز", "نشتی آب", "سرویس دوره ای"), listOf("washer", "oven", "water leak", "periodic service"), 4.5, 62, true, 5.9, "فردا", "tomorrow", 560000, "از ۵۶۰ هزار تومان", "from 560,000 toman"),
        technician(17, "فرهاد قاسمی", "Farhad Ghasemi", "نصاب شیرآلات و سرویس بهداشتی", "Fixture and bathroom installer", "یزد", "Yazd", "نصب شیرآلات", "Fixture Install", listOf("شیرآلات", "روشویی", "دوش", "آب بندی"), listOf("fixtures", "basin", "shower", "sealing"), 4.6, 57, true, 2.2, "امروز عصر", "today evening", 420000, "از ۴۲۰ هزار تومان", "from 420,000 toman"),
        technician(18, "الهه نصیری", "Elaheh Nasiri", "کابینت کار و تنظیم درب", "Cabinet fitter and door adjuster", "تهران", "Tehran", "کابینت و نجاری", "Cabinetry", listOf("ریگلاژ درب", "تعویض لولا", "قفسه", "اندازه گیری"), listOf("door adjustment", "hinge replacement", "shelves", "measurement"), 4.8, 84, true, 3.5, "فردا صبح", "tomorrow morning", 680000, "از ۶۸۰ هزار تومان", "from 680,000 toman"),
        technician(19, "مهسا رحیمی", "Mahsa Rahimi", "نجار کارهای کوچک خانه", "Small home carpentry specialist", "رشت", "Rasht", "کابینت و نجاری", "Cabinetry", listOf("نصب طبقه", "تعمیر کشو", "پارتیشن سبک", "ابزارآلات کامل"), listOf("shelf install", "drawer repair", "light partition", "complete tools"), 4.4, 49, false, 6.1, "تا ۲۴ ساعت", "within 24 hours", 540000, "از ۵۴۰ هزار تومان", "from 540,000 toman"),
        technician(20, "امید کاظمی", "Omid Kazemi", "کاشی کار ترمیمی", "Repair tile installer", "مشهد", "Mashhad", "کاشی و سرامیک", "Tile and Ceramic", listOf("بندکشی", "ترمیم کاشی", "سرامیک کف", "آب بندی"), listOf("grouting", "tile repair", "floor ceramic", "sealing"), 4.5, 68, true, 7.4, "۲۴ ساعت آینده", "next 24 hours", 720000, "از ۷۲۰ هزار تومان", "from 720,000 toman"),
        technician(21, "نگار شفیعی", "Negar Shafiei", "کارشناس شبکه و وای فای", "Network and Wi-Fi specialist", "تبریز", "Tabriz", "خدمات شبکه و اینترنت", "Network and Internet", listOf("مودم", "وای فای", "کابل شبکه", "عیب یابی اینترنت"), listOf("router", "Wi-Fi", "network cable", "internet diagnostics"), 4.9, 93, true, 4.1, "کمتر از ۳ ساعت", "under 3 hours", 380000, "از ۳۸۰ هزار تومان", "from 380,000 toman"),
        technician(22, "پیمان راد", "Peyman Rad", "تعمیرکار موبایل در محل", "On-site mobile repairer", "تهران", "Tehran", "تعمیر موبایل", "Mobile Repair", listOf("تعویض باتری", "نمایشگر", "نرم افزار", "بازیابی اطلاعات"), listOf("battery", "display", "software", "data recovery"), 4.4, 66, true, 5.6, "امروز", "today", 450000, "از ۴۵۰ هزار تومان", "from 450,000 toman"),
        technician(23, "زهرا معینی", "Zahra Moini", "هماهنگ کننده نظافت پس از تعمیر", "Post-repair cleaning coordinator", "کرج", "Karaj", "نظافت و خدمات عمومی", "Cleaning Services", listOf("نظافت نهایی", "جمع آوری نخاله سبک", "آماده سازی واحد", "زمان بندی تیم"), listOf("final cleaning", "light debris", "unit prep", "team scheduling"), 4.7, 112, true, 6.9, "فردا", "tomorrow", 300000, "از ۳۰۰ هزار تومان", "from 300,000 toman"),
        technician(24, "داوود اکبری", "Davood Akbari", "هماهنگ کننده تعمیرات جزئی", "Minor repair coordinator", "اصفهان", "Isfahan", "بازسازی و تعمیرات جزئی", "Minor Renovation", listOf("هماهنگی چند خدمت", "برآورد اولیه", "ترمیم دیوار", "زمان بندی"), listOf("multi-service coordination", "initial estimate", "wall repair", "scheduling"), 4.6, 74, true, 4.8, "۲۴ تا ۴۸ ساعت", "24 to 48 hours", 900000, "بازدید و برآورد", "visit and estimate")
    )

    private val requests = linkedMapOf<String, ServiceRequest>()

    fun featured() = technicians.sortedWith(compareByDescending<Technician> { it.verified }.thenByDescending { it.ratingAverage }.thenByDescending { it.completedJobs }).take(4)
    fun findTechnician(id: Long) = technicians.firstOrNull { it.id == id }
    fun allRequests() = requests.values.sortedByDescending { it.slotStart }
    fun findRequest(id: String) = requests[id]

    fun citiesFa() = technicians.map { it.cityFa }.distinct().sorted()

    fun categoryCounts(): Map<String, Int> = categories.associate { category ->
        category.fa to technicians.count { it.categoryFa == category.fa }
    }

    fun similarTechnicians(tech: Technician, limit: Int = 3): List<Technician> = technicians
        .filter { it.id != tech.id }
        .sortedWith(
            compareByDescending<Technician> { it.categoryFa == tech.categoryFa }
                .thenBy { abs(it.distanceKm - tech.distanceKm) }
                .thenByDescending { it.ratingAverage }
        )
        .take(limit)

    fun stats() = DemoStats(
        technicianCount = technicians.size,
        categoryCount = categories.size,
        completedJobs = technicians.sumOf { it.completedJobs },
        averageRating = technicians.map { it.ratingAverage }.average(),
        cityCount = technicians.map { it.cityFa }.distinct().size,
        openSlots = technicians.sumOf { it.availableSlotCount }
    )

    fun search(criteria: SearchCriteria): List<Technician> {
        val filtered = technicians.filter { tech ->
            val query = criteria.q.orEmpty().trim().lowercase()
            val haystack = listOf(
                tech.nameFa, tech.nameEn, tech.titleFa, tech.titleEn, tech.cityFa, tech.cityEn,
                tech.categoryFa, tech.categoryEn, tech.bioFa, tech.bioEn,
                tech.skillsFa.joinToString(" "), tech.skillsEn.joinToString(" ")
            ).joinToString(" ").lowercase()
            val queryOk = query.isBlank() || haystack.contains(query)
            val categoryOk = matchesCategory(tech, criteria.category)
            val cityOk = criteria.city.isNullOrBlank() || tech.cityFa == criteria.city || tech.cityEn.equals(criteria.city, ignoreCase = true)
            val ratingOk = criteria.minRating == null || tech.ratingAverage >= criteria.minRating
            val availableOk = !criteria.availableOnly || tech.slots.any { !it.booked }
            queryOk && categoryOk && cityOk && ratingOk && availableOk
        }

        return when (criteria.sort.orEmpty()) {
            "distance" -> filtered.sortedWith(compareBy<Technician> { it.distanceKm }.thenByDescending { it.ratingAverage })
            "price" -> filtered.sortedWith(compareBy<Technician> { it.priceFromToman }.thenByDescending { it.ratingAverage })
            "jobs" -> filtered.sortedWith(compareByDescending<Technician> { it.completedJobs }.thenByDescending { it.ratingAverage })
            else -> filtered.sortedWith(compareByDescending<Technician> { it.ratingAverage }.thenBy { it.distanceKm })
        }
    }

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

    private fun matchesCategory(tech: Technician, selected: String?): Boolean {
        val clean = selected.orEmpty().trim()
        if (clean.isBlank()) return true
        val category = categories.firstOrNull {
            it.key.equals(clean, ignoreCase = true) || it.fa == clean || it.en.equals(clean, ignoreCase = true)
        }
        return if (category == null) {
            tech.categoryFa == clean || tech.categoryEn.equals(clean, ignoreCase = true)
        } else {
            tech.categoryFa == category.fa || tech.categoryEn == category.en
        }
    }

    private fun technician(
        id: Long,
        nameFa: String,
        nameEn: String,
        titleFa: String,
        titleEn: String,
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
        responseFa: String,
        responseEn: String,
        priceFromToman: Int,
        feeFa: String,
        feeEn: String
    ): Technician {
        val generatedSlots = MutableList(5) { index ->
            TimeSlot("T$id-${index + 1}", base.plusDays(index.toLong()).plusHours(((id + index) % 6).toLong()))
        }
        if (id % 4L == 0L) generatedSlots[0].booked = true
        if (id % 7L == 0L) generatedSlots[1].booked = true

        return Technician(
            id = id,
            nameFa = nameFa,
            nameEn = nameEn,
            titleFa = titleFa,
            titleEn = titleEn,
            cityFa = cityFa,
            cityEn = cityEn,
            categoryFa = categoryFa,
            categoryEn = categoryEn,
            skillsFa = skillsFa,
            skillsEn = skillsEn,
            bioFa = "$titleFa با تجربه عملی در پروژه های خانگی و اداری. زمان بندی شفاف، توضیح مرحله به مرحله کار، برآورد اولیه و ثبت بازخورد مشتریان در سامانه.",
            bioEn = "$titleEn with practical home and office experience, clear scheduling, step-by-step explanation, initial estimates, and customer feedback tracking.",
            completedJobs = jobs,
            verified = verified,
            distanceKm = distance,
            responseTimeFa = responseFa,
            responseTimeEn = responseEn,
            priceFromToman = priceFromToman,
            approximateFeeFa = feeFa,
            approximateFeeEn = feeEn,
            slots = generatedSlots,
            reviews = sampleReviews(ratingSeed, categoryFa)
        )
    }

    private fun sampleReviews(ratingSeed: Double, categoryFa: String): MutableList<Review> {
        val top = ratingSeed.roundToInt().coerceIn(1, 5)
        val support = if (ratingSeed >= 4.7) 5 else 4
        val baseStars = ratingSeed.toInt().coerceIn(1, 5)
        val now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0)
        return mutableListOf(
            Review("کاربر نمونه", top, "پاسخگویی خوب، توضیح شفاف و انجام کار در زمان مقرر.", now.minusDays(4)),
            Review("مشتری محلی", support, "قبل از شروع، هزینه تقریبی و مراحل انجام کار کاملا مشخص شد.", now.minusDays(12)),
            Review("مدیر ساختمان", baseStars, "برای ${categoryFa} گزینه قابل اعتمادی بود و هماهنگی از داخل سامانه راحت انجام شد.", now.minusDays(21))
        )
    }
}

package ir.university.technicalservice

import jakarta.servlet.http.HttpSession
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class WebController(private val repo: DemoDataRepo, private val ui: UiText) {
    @ModelAttribute
    fun common(model: Model, session: HttpSession, @RequestParam(required = false) lang: String?, @RequestParam(required = false) role: String?) {
        if (lang == "fa" || lang == "en") session.setAttribute("lang", lang)
        role?.let { runCatching { Role.valueOf(it) }.onSuccess { selected -> session.setAttribute("role", selected.name) } }
        val currentLang = session.getAttribute("lang") as? String ?: "fa"
        val currentRole = session.getAttribute("role") as? String ?: Role.CUSTOMER.name
        model.addAttribute("lang", currentLang)
        model.addAttribute("dir", if (currentLang == "en") "ltr" else "rtl")
        model.addAttribute("role", currentRole)
        model.addAttribute("roles", Role.entries)
        model.addAttribute("ui", ui)
    }

    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("categories", repo.categories)
        model.addAttribute("categoryCounts", repo.categoryCounts())
        model.addAttribute("featured", repo.featured())
        model.addAttribute("stats", repo.stats())
        return "home"
    }

    @GetMapping("/technicians")
    fun technicians(model: Model, criteria: SearchCriteria): String {
        searchModel(model, criteria)
        return "technicians"
    }

    @GetMapping("/technicians/results")
    fun results(model: Model, criteria: SearchCriteria): String {
        searchModel(model, criteria)
        return "fragments/technician-results :: results"
    }

    @GetMapping("/technicians/{id}")
    fun profile(@PathVariable id: Long, model: Model): String {
        val tech = repo.findTechnician(id) ?: return "redirect:/technicians"
        model.addAttribute("tech", tech)
        model.addAttribute("similarTechnicians", repo.similarTechnicians(tech))
        model.addAttribute("ratingStars", stars(tech.ratingAverage))
        return "profile"
    }

    @PostMapping("/technicians/{id}/book")
    fun book(@PathVariable id: Long, @RequestParam customerName: String, @RequestParam title: String, @RequestParam locationNote: String, @RequestParam slotId: String, model: Model): String {
        val tech = repo.findTechnician(id) ?: return "redirect:/technicians"
        model.addAttribute("tech", tech)
        runCatching {
            repo.book(id, slotId, customerName, title, locationNote)
        }.onSuccess { request ->
            model.addAttribute("request", request)
            model.addAttribute("googleUrl", googleCalendarUrl(request))
        }.onFailure { error ->
            model.addAttribute("bookingError", bookingError(error.message, currentLang(model)))
        }
        return "fragments/booking-panel :: bookingPanel"
    }

    @PostMapping("/technicians/{id}/reviews")
    fun review(@PathVariable id: Long, @RequestParam author: String, @RequestParam stars: String, @RequestParam comment: String, model: Model): String {
        val tech = repo.findTechnician(id) ?: return "redirect:/technicians"
        val rating = stars.toIntOrNull()
        if (rating == null || rating !in 1..5) {
            model.addAttribute("reviewError", if ((model.asMap()["lang"] as? String) == "en") "Choose a rating from 1 to 5." else "لطفا یک امتیاز معتبر بین ۱ تا ۵ انتخاب کنید.")
        } else {
            runCatching {
                repo.addReview(id, author, rating, comment)
            }.onSuccess {
                model.addAttribute("reviewSaved", true)
            }.onFailure { error ->
                model.addAttribute("reviewError", reviewError(error.message, currentLang(model)))
            }
        }
        model.addAttribute("tech", tech)
        model.addAttribute("ratingStars", ir.university.technicalservice.stars(tech.ratingAverage))
        return "fragments/reviews :: reviews"
    }

    @GetMapping("/technician/dashboard")
    fun dashboard(model: Model): String {
        addRequestsModel(model)
        return "dashboard"
    }

    @GetMapping("/technician/onboarding")
    fun technicianOnboarding(model: Model): String {
        model.addAttribute("categories", repo.categories)
        model.addAttribute("cities", repo.cities(currentLang(model)))
        return "technician-onboarding"
    }

    @PostMapping("/technician/onboarding/submit")
    fun submitTechnicianOnboarding(): String = "fragments/onboarding-success :: onboardingSuccess"

    @PostMapping("/requests/{id}/status")
    fun status(@PathVariable id: String, @RequestParam status: RequestStatus, model: Model): String {
        repo.updateRequestStatus(id, status)
        addRequestsModel(model)
        return "dashboard :: dashboardLive"
    }

    @GetMapping("/calendar/{requestId}.ics")
    fun calendar(@PathVariable requestId: String): ResponseEntity<String> {
        val request = repo.findRequest(requestId) ?: return ResponseEntity.notFound().build()
        val headers = HttpHeaders()
        headers.contentDisposition = ContentDisposition.attachment().filename("service-request-$requestId.ics").build()
        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("text/calendar; charset=UTF-8")).body(calendarFile(request))
    }

    private fun searchModel(model: Model, criteria: SearchCriteria) {
        val results = repo.search(criteria)
        val lang = currentLang(model)
        model.addAttribute("criteria", criteria)
        model.addAttribute("technicians", results)
        model.addAttribute("categories", repo.categories)
        model.addAttribute("categoryCounts", repo.categoryCounts())
        model.addAttribute("cities", repo.cities(lang))
        model.addAttribute("totalTechnicians", repo.technicians.size)
    }

    private fun addRequestsModel(model: Model) {
        val requests = repo.allRequests()
        model.addAttribute("requests", requests)
        model.addAttribute("totalRequests", requests.size)
        model.addAttribute("submittedCount", requests.count { it.status == RequestStatus.SUBMITTED })
        model.addAttribute("acceptedCount", requests.count { it.status == RequestStatus.ACCEPTED })
        model.addAttribute("rejectedCount", requests.count { it.status == RequestStatus.REJECTED })
        model.addAttribute("todayCount", requests.count { it.slotStart.toLocalDate() == java.time.LocalDate.now() })
    }

    private fun currentLang(model: Model) = model.asMap()["lang"] as? String ?: "fa"

    private fun bookingError(message: String?, lang: String): String = when (message) {
        "Customer name is required" -> if (lang == "en") "Customer name is required." else "نام مشتری الزامی است."
        "Request title is required" -> if (lang == "en") "Request title is required." else "عنوان درخواست الزامی است."
        "Address or location note is required" -> if (lang == "en") "Address or location note is required." else "آدرس یا توضیح مکان الزامی است."
        "Selected slot is no longer available" -> if (lang == "en") "Selected slot is no longer available." else "این زمان قبلا رزرو شده یا دیگر در دسترس نیست."
        else -> if (lang == "en") "Booking was not saved." else "ثبت درخواست انجام نشد."
    }

    private fun reviewError(message: String?, lang: String): String = when (message) {
        "Comment is required" -> if (lang == "en") "Comment is required." else "متن نظر الزامی است."
        else -> if (lang == "en") "Review was not saved." else "ثبت نظر انجام نشد."
    }
}

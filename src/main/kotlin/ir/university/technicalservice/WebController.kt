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
        model.addAttribute("categoriesFa", repo.categoriesFa)
        model.addAttribute("featured", repo.featured())
        return "home"
    }

    @GetMapping("/technicians")
    fun technicians(model: Model): String {
        searchModel(model, SearchCriteria())
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
        model.addAttribute("ratingStars", stars(tech.ratingAverage))
        return "profile"
    }

    @PostMapping("/technicians/{id}/book")
    fun book(@PathVariable id: Long, @RequestParam customerName: String, @RequestParam title: String, @RequestParam locationNote: String, @RequestParam slotId: String, model: Model): String {
        val request = repo.book(id, slotId, customerName, title, locationNote)
        model.addAttribute("request", request)
        model.addAttribute("googleUrl", googleCalendarUrl(request))
        return "fragments/booking-result :: bookingResult"
    }

    @PostMapping("/technicians/{id}/reviews")
    fun review(@PathVariable id: Long, @RequestParam author: String, @RequestParam stars: Int, @RequestParam comment: String, model: Model): String {
        val tech = repo.addReview(id, author, stars, comment)
        model.addAttribute("tech", tech)
        model.addAttribute("ratingStars", stars(tech.ratingAverage))
        return "fragments/reviews :: reviews"
    }

    @GetMapping("/technician/dashboard")
    fun dashboard(model: Model): String {
        model.addAttribute("requests", repo.allRequests())
        return "dashboard"
    }

    @PostMapping("/requests/{id}/status")
    fun status(@PathVariable id: String, @RequestParam status: RequestStatus, model: Model): String {
        repo.updateRequestStatus(id, status)
        model.addAttribute("requests", repo.allRequests())
        return "fragments/request-table :: requestTable"
    }

    @GetMapping("/calendar/{requestId}.ics")
    fun calendar(@PathVariable requestId: String): ResponseEntity<String> {
        val request = repo.findRequest(requestId) ?: return ResponseEntity.notFound().build()
        val headers = HttpHeaders()
        headers.contentDisposition = ContentDisposition.attachment().filename("service-request-$requestId.ics").build()
        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("text/calendar; charset=UTF-8")).body(calendarFile(request))
    }

    private fun searchModel(model: Model, criteria: SearchCriteria) {
        model.addAttribute("criteria", criteria)
        model.addAttribute("technicians", repo.search(criteria))
        model.addAttribute("categoriesFa", repo.categoriesFa)
        model.addAttribute("citiesFa", repo.technicians.map { it.cityFa }.distinct())
    }
}

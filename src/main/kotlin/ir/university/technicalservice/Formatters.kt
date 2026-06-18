package ir.university.technicalservice

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val uiDate = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
private val icsDate = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")

fun LocalDateTime.ui() = format(uiDate)

fun stars(rating: Double): String {
    val full = rating.toInt().coerceIn(0, 5)
    return "★".repeat(full) + "☆".repeat(5 - full)
}

fun stars(rating: Int): String = stars(rating.toDouble())

fun ratingCount(tech: Technician, stars: Int): Int = tech.reviews.count { it.stars == stars }

fun ratingPercent(tech: Technician, stars: Int): Int {
    if (tech.reviews.isEmpty()) return 0
    return ((ratingCount(tech, stars).toDouble() / tech.reviews.size) * 100).toInt().coerceIn(0, 100)
}

fun statusLabel(status: RequestStatus, lang: String): String = when (status) {
    RequestStatus.SUBMITTED -> if (lang == "en") "Pending" else "در انتظار بررسی"
    RequestStatus.ACCEPTED -> if (lang == "en") "Accepted" else "قبول شده"
    RequestStatus.REJECTED -> if (lang == "en") "Rejected" else "رد شده"
}

fun statusCss(status: RequestStatus): String = status.name.lowercase()

fun googleCalendarUrl(request: ServiceRequest): String {
    val zone = ZoneId.of("Asia/Tehran")
    val start = request.slotStart.atZone(zone).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().format(icsDate) + "Z"
    val end = request.slotStart.plusHours(1).atZone(zone).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().format(icsDate) + "Z"
    val details = "درخواست ${request.id} برای ${request.technicianName}"
    return "https://calendar.google.com/calendar/render?action=TEMPLATE" +
        "&text=${enc(request.title)}" +
        "&dates=$start/$end" +
        "&details=${enc(details)}" +
        "&location=${enc(request.locationNote)}"
}

fun calendarFile(request: ServiceRequest): String {
    val zone = ZoneId.of("Asia/Tehran")
    val start = request.slotStart.atZone(zone).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().format(icsDate) + "Z"
    val end = request.slotStart.plusHours(1).atZone(zone).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().format(icsDate) + "Z"
    return """
        BEGIN:VCALENDAR
        VERSION:2.0
        PRODID:-//Technical Service Network Demo//University Project//FA
        BEGIN:VEVENT
        UID:${request.id}@technical-service-network-demo
        DTSTAMP:$start
        DTSTART:$start
        DTEND:$end
        SUMMARY:${escapeIcs(request.title)}
        DESCRIPTION:${escapeIcs("درخواست ${request.id} برای ${request.technicianName}")}
        LOCATION:${escapeIcs(request.locationNote)}
        END:VEVENT
        END:VCALENDAR
    """.trimIndent().replace("\n", "\r\n")
}

private fun enc(value: String) = URLEncoder.encode(value, StandardCharsets.UTF_8)
private fun escapeIcs(value: String) = value.replace("\\", "\\\\").replace("\n", "\\n").replace(",", "\\,").replace(";", "\\;")

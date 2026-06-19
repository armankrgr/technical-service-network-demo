package ir.university.technicalservice

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.nio.charset.StandardCharsets

@SpringBootTest
@AutoConfigureMockMvc
class WebControllerSmokeTests(@Autowired private val mockMvc: MockMvc) {
    @Test
    fun `home page returns 200`() {
        mockMvc.get("/")
            .andExpect {
                status { isOk() }
                content { string(containsString("FA")) }
                content { string(containsString("EN")) }
            }
    }

    @Test
    fun `technicians page returns 200`() {
        mockMvc.get("/technicians")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `valid technician profile returns 200`() {
        mockMvc.get("/technicians/1")
            .andExpect {
                status { isOk() }
                content { string(containsString("booking-panel")) }
            }
    }

    @Test
    fun `technician onboarding page returns 200`() {
        mockMvc.get("/technician/onboarding")
            .andExpect {
                status { isOk() }
                content { string(containsString("onboarding-flow")) }
            }

        mockMvc.post("/technician/onboarding/submit")
            .andExpect {
                status { isOk() }
                content { string(containsString("onboarding-submit-success")) }
            }
    }

    @Test
    fun `calendar route works after booking`() {
        val booking = mockMvc.post("/technicians/1/book") {
            param("customerName", "Demo Student")
            param("title", "Calendar test")
            param("locationNote", "Tehran demo address")
            param("slotId", "T1-1")
        }.andExpect {
            status { isOk() }
            content { string(containsString(".ics")) }
        }.andReturn()

        val html = booking.response.getContentAsString(StandardCharsets.UTF_8)
        val requestId = Regex("<b>([A-Z0-9]{8})</b>").find(html)?.groupValues?.get(1)
        assertNotNull(requestId)

        mockMvc.get("/calendar/$requestId.ics")
            .andExpect {
                status { isOk() }
                content { string(containsString("BEGIN:VCALENDAR")) }
                content { string(containsString("SUMMARY:Calendar test")) }
            }

        mockMvc.post("/requests/$requestId/status") {
            param("status", "ACCEPTED")
        }.andExpect {
            status { isOk() }
            content { string(containsString("dashboard-live")) }
            content { string(containsString("قبول شده")) }
        }
    }
}

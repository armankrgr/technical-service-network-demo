package ir.university.technicalservice

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DemoDataRepoTests {
    @Test
    fun `search filters by category city rating and availability`() {
        val repo = DemoDataRepo()

        val results = repo.search(
            SearchCriteria(
                category = "برق کاری",
                city = "تهران",
                minRating = 4.0,
                availableOnly = true
            )
        )

        assertEquals(1, results.size)
        assertEquals("Reza Ahmadi", results.first().nameEn)
        assertTrue(results.first().availableSlotCount > 0)
    }

    @Test
    fun `booking marks selected slot as unavailable`() {
        val repo = DemoDataRepo()
        val before = repo.findTechnician(1)!!.availableSlotCount

        repo.book(
            technicianId = 1,
            slotId = "T1-1",
            customerName = "Demo Student",
            title = "Wiring check",
            locationNote = "Tehran demo address"
        )

        val after = repo.findTechnician(1)!!.availableSlotCount
        assertEquals(before - 1, after)
        assertTrue(repo.findTechnician(1)!!.slots.first { it.id == "T1-1" }.booked)
    }
}

package at.apf.gstochabock.service

import at.apf.gstochabock.model.*
import org.junit.Assert
import org.junit.jupiter.api.Test

class JassBaseServiceCardsToWeisTest {

    val svc = JassBaseService()

    @Test
    fun dreiBlattInDreiCardHand() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("E6"), Card("E7"), Card("E8")))
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Drei, CardValue.Acht, CardColor.Eichel), weises.first())
    }

    @Test
    fun dreiBlattIn4CardHand() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("E6"), Card("E7"), Card("E8"), Card("EX")))
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Drei, CardValue.Acht, CardColor.Eichel), weises.first())
    }

    @Test
    fun twoTimesdreiBlattInEichel() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("E6"), Card("E7"), Card("E8"), Card("EX"), Card("EU"), Card("EO")))
        Assert.assertEquals(2, weises.size)
        Assert.assertEquals(Weis(WeisRank.Drei, CardValue.Acht, CardColor.Eichel), weises.first())
        Assert.assertEquals(Weis(WeisRank.Drei, CardValue.Ober, CardColor.Eichel), weises.last())
    }

    @Test
    fun twoTimesdreiBlattInDifferentColors() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("E6"), Card("E7"), Card("E8"), Card("H6"), Card("H7"), Card("H8")))
        Assert.assertEquals(2, weises.size)
        Assert.assertTrue(weises.contains(Weis(WeisRank.Drei, CardValue.Acht, CardColor.Eichel)))
        Assert.assertTrue(weises.contains(Weis(WeisRank.Drei, CardValue.Acht, CardColor.Herz)))
    }

    @Test
    fun vierBlattIn4CardHand() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("E6"), Card("E7"), Card("E8"), Card("E9")))
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Vier, CardValue.Nell, CardColor.Eichel), weises.first())
    }

    @Test
    fun quartett() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("EX"), Card("LX"), Card("HX"), Card("SX")))
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Quartett, CardValue.Zehn), weises.first())
    }

    @Test
    fun quartettOverVierBlattOnSameCard() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("EX"), Card("LX"), Card("HX"), Card("SX"), Card("E9"), Card("EU"), Card("EO")))
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Quartett, CardValue.Zehn), weises.first())
    }

    @Test
    fun FuenfBlattOverQuartettOnSameCard() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("EX"), Card("LX"), Card("HX"), Card("SX"), Card("E9"), Card("EU"), Card("EO"), Card("EK")))
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Fuenf, CardValue.Koenig, CardColor.Eichel), weises.first())
    }

    @Test
    fun BauerQuartettOver5Blatt() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("EU"), Card("LU"), Card("HU"), Card("SU"), Card("E9"), Card("EX"), Card("EO"), Card("EK")))
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Quartett, CardValue.Bauer), weises.first())
    }

    @Test
    fun NellrQuartettOver5Blatt() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("E9"), Card("L9"), Card("H9"),
                Card("S9"), Card("EU"), Card("EX"), Card("E7"), Card("E6")))
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Quartett, CardValue.Nell), weises.first())
    }

    @Test
    fun QuartettAndDreiBlattOverVierBlatt() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("EX"), Card("LX"), Card("HX"),
                Card("SX"), Card("EU"), Card("EO"), Card("EK"), Card("E6")))
        Assert.assertEquals(2, weises.size)
        Assert.assertTrue(weises.contains(Weis(WeisRank.Quartett, CardValue.Zehn)))
        Assert.assertTrue(weises.contains(Weis(WeisRank.Drei, CardValue.Koenig, CardColor.Eichel)))
    }

}
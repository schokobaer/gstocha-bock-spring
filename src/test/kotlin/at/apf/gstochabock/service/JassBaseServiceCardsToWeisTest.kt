package at.apf.gstochabock.service

import at.apf.gstochabock.gamelogic.BaseJassLogic
import at.apf.gstochabock.model.*
import org.junit.Assert
import org.junit.Test

class JassBaseServiceCardsToWeisTest {

    val svc = BaseJassLogic()

    @Test
    fun dreiBlattInDreiCardHand() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("E6"), Card("E7"), Card("E8")), Trumpf.Schell)
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Drei, CardValue.Acht, CardColor.Eichel), weises.first())
    }

    @Test
    fun dreiBlattIn4CardHand() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("E6"), Card("E7"), Card("E8"), Card("EX")), Trumpf.Schell)
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Drei, CardValue.Acht, CardColor.Eichel), weises.first())
    }

    @Test
    fun twoTimesdreiBlattInEichel() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("E6"), Card("E7"), Card("E8"), Card("EX"), Card("EU"), Card("EO")), Trumpf.Schell)
        Assert.assertEquals(2, weises.size)
        Assert.assertEquals(Weis(WeisRank.Drei, CardValue.Acht, CardColor.Eichel), weises.first())
        Assert.assertEquals(Weis(WeisRank.Drei, CardValue.Ober, CardColor.Eichel), weises.last())
    }

    @Test
    fun twoTimesdreiBlattInDifferentColors() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("E6"), Card("E7"), Card("E8"), Card("H6"), Card("H7"), Card("H8")), Trumpf.Schell)
        Assert.assertEquals(2, weises.size)
        Assert.assertTrue(weises.contains(Weis(WeisRank.Drei, CardValue.Acht, CardColor.Eichel)))
        Assert.assertTrue(weises.contains(Weis(WeisRank.Drei, CardValue.Acht, CardColor.Herz)))
    }

    @Test
    fun vierBlattIn4CardHand() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("E6"), Card("E7"), Card("E8"), Card("E9")), Trumpf.Schell)
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Vier, CardValue.Nell, CardColor.Eichel), weises.first())
    }

    @Test
    fun quartett() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("EX"), Card("LX"), Card("HX"), Card("SX")), Trumpf.Schell)
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Quartett, CardValue.Zehn), weises.first())
    }

    @Test
    fun quartettOverVierBlattOnSameCard() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("EX"), Card("LX"), Card("HX"), Card("SX"), Card("E9"), Card("EU"), Card("EO")), Trumpf.Schell)
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Quartett, CardValue.Zehn), weises.first())
    }

    @Test
    fun FuenfBlattOverQuartettOnSameCard() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("EX"), Card("LX"), Card("HX"), Card("SX"), Card("E9"), Card("EU"), Card("EO"), Card("EK")), Trumpf.Schell)
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Fuenf, CardValue.Koenig, CardColor.Eichel), weises.first())
    }

    @Test
    fun BauerQuartettOver5Blatt() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("EU"), Card("LU"), Card("HU"), Card("SU"), Card("E9"), Card("EX"), Card("EO"), Card("EK")), Trumpf.Schell)
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Quartett, CardValue.Bauer), weises.first())
    }

    @Test
    fun NellrQuartettOver5Blatt() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("E9"), Card("L9"), Card("H9"),
                Card("S9"), Card("EU"), Card("EX"), Card("E7"), Card("E6")), Trumpf.Schell)
        Assert.assertEquals(1, weises.size)
        Assert.assertEquals(Weis(WeisRank.Quartett, CardValue.Nell), weises.first())
    }

    @Test
    fun QuartettAndDreiBlattOverVierBlatt() {
        val weises: List<Weis> = svc.cardsToWeis(listOf(Card("EX"), Card("LX"), Card("HX"),
                Card("SX"), Card("EU"), Card("EO"), Card("EK"), Card("E6")), Trumpf.Schell)
        Assert.assertEquals(2, weises.size)
        Assert.assertTrue(weises.contains(Weis(WeisRank.Quartett, CardValue.Zehn)))
        Assert.assertTrue(weises.contains(Weis(WeisRank.Drei, CardValue.Koenig, CardColor.Eichel)))
    }

    @Test
    fun SechsSiebenAchtQuartettNotPossibleInEichelLaubHerzSchell() {
        val sechsCards = listOf(Card("E6"),Card("L6"),Card("H6"),Card("S6"))
        Assert.assertTrue(svc.cardsToWeis(sechsCards, Trumpf.Eichel).isEmpty())
        Assert.assertTrue(svc.cardsToWeis(sechsCards, Trumpf.Laub).isEmpty())
        Assert.assertTrue(svc.cardsToWeis(sechsCards, Trumpf.Herz).isEmpty())
        Assert.assertTrue(svc.cardsToWeis(sechsCards, Trumpf.Schell).isEmpty())
        val siebenCards = listOf(Card("E7"),Card("L7"),Card("H7"),Card("S7"))
        Assert.assertTrue(svc.cardsToWeis(siebenCards, Trumpf.Eichel).isEmpty())
        Assert.assertTrue(svc.cardsToWeis(siebenCards, Trumpf.Laub).isEmpty())
        Assert.assertTrue(svc.cardsToWeis(siebenCards, Trumpf.Herz).isEmpty())
        Assert.assertTrue(svc.cardsToWeis(siebenCards, Trumpf.Schell).isEmpty())
        val achtCards = listOf(Card("E8"),Card("L8"),Card("H8"),Card("S8"))
        Assert.assertTrue(svc.cardsToWeis(achtCards, Trumpf.Eichel).isEmpty())
        Assert.assertTrue(svc.cardsToWeis(achtCards, Trumpf.Laub).isEmpty())
        Assert.assertTrue(svc.cardsToWeis(achtCards, Trumpf.Herz).isEmpty())
        Assert.assertTrue(svc.cardsToWeis(achtCards, Trumpf.Schell).isEmpty())
    }

    @Test
    fun SechsSiebenAchtQuartettPossibleInGeisBockKulmi() {
        val sechsCards = listOf(Card("E6"),Card("L6"),Card("H6"),Card("S6"))
        Assert.assertEquals(1, svc.cardsToWeis(sechsCards, Trumpf.Geiss).size)
        Assert.assertEquals(1, svc.cardsToWeis(sechsCards, Trumpf.Bock).size)
        Assert.assertEquals(1, svc.cardsToWeis(sechsCards, Trumpf.KulmiUnten).size)
        Assert.assertEquals(1, svc.cardsToWeis(sechsCards, Trumpf.KulmiOben).size)
        val siebenCards = listOf(Card("E7"),Card("L7"),Card("H7"),Card("S7"))
        Assert.assertEquals(1, svc.cardsToWeis(siebenCards, Trumpf.Geiss).size)
        Assert.assertEquals(1, svc.cardsToWeis(siebenCards, Trumpf.Bock).size)
        Assert.assertEquals(1, svc.cardsToWeis(siebenCards, Trumpf.KulmiUnten).size)
        Assert.assertEquals(1, svc.cardsToWeis(siebenCards, Trumpf.KulmiOben).size)
        val achtCards = listOf(Card("E8"),Card("L8"),Card("H8"),Card("S8"))
        Assert.assertEquals(1, svc.cardsToWeis(achtCards, Trumpf.Geiss).size)
        Assert.assertEquals(1, svc.cardsToWeis(achtCards, Trumpf.Bock).size)
        Assert.assertEquals(1, svc.cardsToWeis(achtCards, Trumpf.KulmiUnten).size)
        Assert.assertEquals(1, svc.cardsToWeis(achtCards, Trumpf.KulmiOben).size)
    }

    @Test
    fun Weis3AchtIsBetterThan3NellInGeis() {
        Assert.assertTrue(svc.compareWeis(Weis("3S8"), Weis("3E9"), Trumpf.Geiss) > 0)
    }

    @Test
    fun Weis3AchtIsWorseThan3NellInNotGeis() {
        Assert.assertTrue(svc.compareWeis(Weis("3S8"), Weis("3E9"), Trumpf.Eichel) < 0)
        Assert.assertTrue(svc.compareWeis(Weis("3S8"), Weis("3E9"), Trumpf.Laub) < 0)
        Assert.assertTrue(svc.compareWeis(Weis("3S8"), Weis("3E9"), Trumpf.Herz) < 0)
        Assert.assertTrue(svc.compareWeis(Weis("3S8"), Weis("3E9"), Trumpf.Schell) < 0)
        Assert.assertTrue(svc.compareWeis(Weis("3S8"), Weis("3E9"), Trumpf.Bock) < 0)
        Assert.assertTrue(svc.compareWeis(Weis("3S8"), Weis("3E9"), Trumpf.KulmiUnten) < 0)
        Assert.assertTrue(svc.compareWeis(Weis("3S8"), Weis("3E9"), Trumpf.KulmiOben) < 0)
    }

}
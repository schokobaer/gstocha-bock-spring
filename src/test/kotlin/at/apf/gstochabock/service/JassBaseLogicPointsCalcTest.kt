package at.apf.gstochabock.service

import at.apf.gstochabock.gamelogic.BaseJassLogic
import at.apf.gstochabock.model.Card
import at.apf.gstochabock.model.Trumpf
import at.apf.gstochabock.model.Weis
import org.junit.Assert
import org.junit.Test

class JassBaseLogicPointsCalcTest {

    val svc = BaseJassLogic()

    @Test
    fun zero() {
        val points = svc.calcPoints(listOf(Card("E6"), Card("E7"), Card("E8"), Card("E9")), Trumpf.Herz)
        Assert.assertEquals(0, points)
    }

    @Test
    fun countNell() {
        val points = svc.calcPoints(listOf(Card("E6"), Card("E7"), Card("E8"), Card("E9")), Trumpf.Eichel)
        Assert.assertEquals(14, points)
    }

    @Test
    fun countBauer() {
        val points = svc.calcPoints(listOf(Card("E6"), Card("E7"), Card("E8"), Card("EU")), Trumpf.Eichel)
        Assert.assertEquals(20, points)
    }

    @Test
    fun sumPoints() {
        val points = svc.calcPoints(listOf(Card("E6"), Card("EX"), Card("HU"), Card("E7")), Trumpf.Eichel)
        Assert.assertEquals(12, points)
    }

    @Test
    fun count8OnGeiss() {
        val points = svc.calcPoints(listOf(Card("E6"), Card("E7"), Card("E8"), Card("E9")), Trumpf.Geiss)
        Assert.assertEquals(8, points)
    }

    @Test
    fun count8OnBock() {
        val points = svc.calcPoints(listOf(Card("E6"), Card("E7"), Card("E8"), Card("E9")), Trumpf.Bock)
        Assert.assertEquals(8, points)
    }

    @Test
    fun count8OnKulmi() {
        val points = svc.calcPoints(listOf(Card("E6"), Card("E7"), Card("E8"), Card("E9")), Trumpf.KulmiUnten)
        Assert.assertEquals(8, points)
        val points2 = svc.calcPoints(listOf(Card("E6"), Card("E7"), Card("E8"), Card("E9")), Trumpf.KulmiOben)
        Assert.assertEquals(8, points2)
    }

    @Test
    fun calcWeisPoints1() {
        val points = svc.calcWeissPoints(listOf(Weis("3HU")))
        Assert.assertEquals(20, points)
    }

    @Test
    fun calcWeisPoints2() {
        val points = svc.calcWeissPoints(listOf(Weis("3HU"), Weis("4SO")))
        Assert.assertEquals(70, points)
    }

}
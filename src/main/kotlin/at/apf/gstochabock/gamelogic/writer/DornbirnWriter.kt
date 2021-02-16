package at.apf.gstochabock.gamelogic.writer

class DornbirnWriter : BaseWriter() {

    override fun serializationCode(): String = "ESLH"

    override fun trumpfOrder(): List<WriterTrumpf> = listOf(
            WriterTrumpf.Eichel,
            WriterTrumpf.Schell,
            WriterTrumpf.Laub,
            WriterTrumpf.Herz,
            WriterTrumpf.Geiss,
            WriterTrumpf.Bock,
            WriterTrumpf.Sieben,
            WriterTrumpf.Acht,
            WriterTrumpf.Kulmi
    )
}
package com.eam.investire.helper

import java.text.SimpleDateFormat
import java.util.*

object DateCustom {
    fun dataAtual(): String {
        val data = System.currentTimeMillis()
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return simpleDateFormat.format(data)
    }

    @JvmStatic
    fun mesAnoDataEscolhida(data: String): String {
        val retornoData = data.split("/").toTypedArray()
        val mes = retornoData[1]
        val ano = retornoData[2]
        return mes + ano
    }
}
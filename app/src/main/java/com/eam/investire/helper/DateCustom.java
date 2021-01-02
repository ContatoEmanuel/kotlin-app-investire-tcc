package com.eam.investire.helper;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateCustom {
    public static String dataAtual() {
        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return simpleDateFormat.format(data);
    }

    public static String mesAnoDataEscolhida(String data) {
        String[] retornoData = data.split("/");
        String mes = retornoData[1];
        String ano = retornoData[2];
        return mes + ano;
    }
}

package br.com.mateus.sugarme.Builder;

import android.util.Log;
import android.util.TimeFormatException;

import java.sql.Timestamp;

public abstract class CoverterFactory {

    public static Integer tryParseInt (Object obj){
        Integer retVal;
        try{
            retVal = Integer.parseInt((String) obj);
        }catch(NumberFormatException nfe){
            retVal = -1;
        }
        return retVal;
    }

    public static String tryParseDatetoTimeStamp (String data, String hora){
        Timestamp timestamp;

        String dia = data.substring(0,2);
        String mes = data.substring(3,5);
        String ano = data.substring(6,10);

        StringBuilder converter = new StringBuilder(ano);
        converter.append ("-");
        converter.append(mes);
        converter.append("-");
        converter.append(dia);
        converter.append(" ");
        converter.append(hora);
        converter.append(":00");

        return converter.toString();
    }

}

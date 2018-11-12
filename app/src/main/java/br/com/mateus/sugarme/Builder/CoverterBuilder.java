package br.com.mateus.sugarme.Builder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TimeFormatException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Timestamp;

public abstract class CoverterBuilder {

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

    public static byte [] toByteArray (Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0,byteArrayOutputStream );
        return byteArrayOutputStream.toByteArray();
    }
    public static Bitmap toBitmap (byte [] bytes){
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return BitmapFactory.decodeStream(byteArrayInputStream);
    }



    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }



}

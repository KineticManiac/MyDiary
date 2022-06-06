package com.example.mydiary.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.example.mydiary.register.Registry;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentRegisterer {
    private static final String TEXT_REGISTER_SUFFIX = ".abc";
    private final Registry registry;
    private final Context context;

    public ContentRegisterer(Context context, Registry registry){
        this.registry = registry;
        this.context = context;
    }

    private static String getTextRegister(String contentName){
        return contentName + TEXT_REGISTER_SUFFIX;
    }

    public Spanned load(String contentName) throws IOException {
        SpannableString text = new SpannableString(new String(
                registry.getInputStream(getTextRegister(contentName)).readAll(),
                StandardCharsets.UTF_8
        ));

        StringBuffer buffer = new StringBuffer();
        Matcher matcher = Pattern.compile("<\\w*>").matcher(text);
        while(matcher.find()){
            String str = matcher.group();
            if(str.equals("<lt>")){
                matcher.appendReplacement(buffer, "<");
            }
            else if(str.equals("<gt>")){
                matcher.appendReplacement(buffer, ">");
            }
            else{
                String suffix = "." + str.substring(1, str.length() - 1);
                String rName = contentName + suffix;

                matcher.appendReplacement(buffer, "@");

                Bitmap bmp = BitmapFactory.decodeStream(registry.getInputStream(rName));

                text.setSpan(new ImageSpan(context, bmp), matcher.start(), matcher.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        matcher.appendTail(buffer);
        return text;
    }

    public void store(String contentName, Spanned spanned) throws IOException{
        String text = spanned.toString();

        text = text.replaceAll("<", "<lt>").replaceAll(">", "<gt>");

        int i = 0;
        for (ImageSpan is : spanned.getSpans(0, spanned.length(), ImageSpan.class)) {
            Bitmap bmp = ((BitmapDrawable) is.getDrawable()).getBitmap(); //Çirkin bir kod :(
            bmp.compress(Bitmap.CompressFormat.PNG, 100, registry.getOutputStream(contentName + "." + i));

            text = text.substring(0, spanned.getSpanStart(is)) + "<" + i + ">" + text.substring(spanned.getSpanEnd(is));

            i++;
        }

        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        OutputStream os = registry.getOutputStream(getTextRegister(contentName));
        os.write(bytes);
        os.close();
    }
}

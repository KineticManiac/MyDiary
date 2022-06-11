package com.example.mydiary.data.other;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.example.mydiary.register.Registry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
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
        String text = new String(
                registry.getInputStream(getTextRegister(contentName)).readAll(),
                StandardCharsets.UTF_8
        );
        class Image{
            Bitmap bmp;
            int pos;
        }
        ArrayList<Image> images = new ArrayList<>();

        StringBuffer buffer = new StringBuffer();
        Matcher matcher = Pattern.compile("<\\w*>").matcher(text);
        int lastEnd = 0;
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

                InputStream is = registry.getInputStream(rName);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                is.close();

                Image image = new Image();
                image.bmp = bmp;
                image.pos = buffer.length() + matcher.start() - lastEnd;

                images.add(image);

                matcher.appendReplacement(buffer, "@");
            }
            lastEnd = matcher.end();
        }
        matcher.appendTail(buffer);

        SpannableString content = new SpannableString(buffer);
        for(Image image: images){
            content.setSpan(new ImageSpan(context, image.bmp), image.pos, image.pos + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return content;
    }

    public void store(String contentName, Spanned spanned) throws IOException{

        HashMap<Integer, String> images = new HashMap<>();

        int i = 0;
        for (ImageSpan is : spanned.getSpans(0, spanned.length(), ImageSpan.class)) {
            Bitmap bmp = ((BitmapDrawable) is.getDrawable()).getBitmap(); //Çirkin bir kod :(

            OutputStream os = registry.getOutputStream(contentName + "." + i);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();

            images.put(spanned.getSpanStart(is), String.valueOf(i));

            i++;
        }

        StringBuffer buffer = new StringBuffer();
        Matcher matcher = Pattern.compile("[<>@]").matcher(spanned);
        while(matcher.find()){
            String str = matcher.group();
            switch (str) {
                case "<":
                    matcher.appendReplacement(buffer, "<lt>");
                    break;
                case ">":
                    matcher.appendReplacement(buffer, "<gt>");
                    break;
                case "@":
                    String id = images.get(matcher.start());
                    if (id != null) {
                        matcher.appendReplacement(buffer, "<" + id + ">");
                    }
                    break;
                default:
                    //Yukarının bütün ihtimalleri içermesi gerekir.
                    throw new RuntimeException("Unreachable code!");
            }
        }
        matcher.appendTail(buffer);

        byte[] bytes = buffer.toString().getBytes(StandardCharsets.UTF_8);
        OutputStream os = registry.getOutputStream(getTextRegister(contentName));
        os.write(bytes);
        os.close();
    }

    public void remove(String contentName) throws IOException{
        String text = new String(
                registry.getInputStream(getTextRegister(contentName)).readAll(),
                StandardCharsets.UTF_8
        );

        Matcher matcher = Pattern.compile("<\\w>").matcher(text);
        while(matcher.find()){
            String str = matcher.group();
            if(!str.equals("<lt>") && !str.equals("<gt>")){
                String rName = contentName + "." + str.substring(1, str.length() - 1);
                registry.removeRegister(rName);
            }
        }

        registry.removeRegister(getTextRegister(contentName));
    }
}

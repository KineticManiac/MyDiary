package com.example.mydiary;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

public class FileRef<T extends Serializable> implements Serializable {

    private final FileRefUnsafe<T> dataFile;
    private final FileRefUnsafe<Integer> refFile;
    private final UUID uuid;

    public FileRef(Context context){
        uuid = UUID.randomUUID();
        dataFile = new FileRefUnsafe<>(context,
                new File(context.getFilesDir(), uuid.toString() + ".dat"));
        refFile = new FileRefUnsafe<>(context,
                new File(context.getFilesDir(), uuid.toString() + ".ref"));
    }

    static private class FileRefUnsafe<V extends Serializable> implements Serializable {
        final Context context;
        final File file;

        FileRefUnsafe(Context context, File file){
            this.context = context;
            this.file = file;
        }

        V read(){
            //Bu fonksiyonda oluşacak hata fataldir.

            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);

                return (V) ois.readObject();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            finally {
                try {
                    fis.close();
                    ois.close();
                }
                catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }

        void write(V obj){
            //Bu fonksiyonda oluşacak hata fataldir.

            FileOutputStream fos = null;
            ObjectOutputStream oos = null;
            try {
                fos = new FileOutputStream(file);
                oos = new ObjectOutputStream(fos);

                oos.writeObject(obj);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            finally {
                try {
                    fos.close();
                    oos.close();
                }
                catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

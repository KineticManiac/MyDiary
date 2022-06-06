package com.example.mydiary.register;


import com.example.mydiary.other.SizedInputStream;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

//Test amaçlı. RAM'e kaydediyor
public class MemoryRegistry implements Registry {
    public static final MemoryRegistry SINGLETON = new MemoryRegistry();
    private final HashMap<String, byte[]> map;

    private MemoryRegistry(){
        map = new HashMap<>();
    }

    @Override
    public SizedInputStream getInputStream(String register) {
        final byte[] buffer = map.getOrDefault(register, null);
        if(buffer != null){
            final ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            return new SizedInputStream() {
                @Override
                public int size() {
                    return buffer.length;
                }

                @Override
                public int read() {
                    return bais.read();
                }
            };
        }
        else{
            return null;
        }
    }

    @Override
    public OutputStream getOutputStream(String register) {
        ArrayList<Byte> bytes = new ArrayList<>(4096);
        return new OutputStream() {
            @Override
            public void write(int i) {
                bytes.add((byte) i);
            }

            @Override
            public void close(){
                byte[] buffer = new byte[bytes.size()];
                for(int i = 0; i < buffer.length; i++)
                    buffer[i] = bytes.get(i);
                map.put(register, buffer);
            }
        };
    }
}

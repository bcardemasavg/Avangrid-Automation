package com.nttdata.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class JsonDataReader {
    protected final ConfigFileReader configFileReader = new ConfigFileReader();

    public JsonDataReader() {
    }

    protected BufferedReader getBufferReaderFromPath(String path) {
        BufferedReader bufferReader = null;

        try {
            bufferReader = new BufferedReader(new FileReader(path));
            return bufferReader;
        } catch (FileNotFoundException var4) {
            throw new RuntimeException("Json file not found at path : " + path);
        }
    }
}

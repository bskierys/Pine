package com.github.bskierys.pine.sample;

import com.github.bskierys.pine.LogAction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogger implements LogAction {

    private FileWriter fileWriter;
    private DateFormat dateFormat;

    public FileLogger(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dateFormat = SimpleDateFormat.getDateTimeInstance();
    }

    @Override public void action(int priority, String tag, String message, Throwable t) {
        Date currentDate = new Date();
        String actionMessage = String.format("%s %s: %s\n", dateFormat.format(currentDate), tag, message);
        try {
            if (fileWriter != null) {
                fileWriter.write(actionMessage);
                fileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

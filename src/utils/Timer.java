package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.System;

public class Timer {

    private static class CSVWriter {

        private static final char SEPARATOR = ',';
        
        private static String followCVSformat(String value) {

            String result = value;
            if (result.contains("\"")) {
                result = result.replace("\"", "\"\"");
            }
            return result;

        }

        private Writer _writer;
        private String _filename;
        
        public CSVWriter(String filename) {
            
            try {
                _writer     = new FileWriter(filename);
                _filename   = filename;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(String.format("Could not open '%s' for writing", filename));
            }
        }
        
        public void writeLine(String... values) {
            boolean first = true;
            
            StringBuilder sb = new StringBuilder();
            for (String value : values) {
                if (!first) {
                    sb.append(SEPARATOR);
                }
                sb.append(followCVSformat(value));

                first = false;
            }
            sb.append("\n");
            try {
                _writer.append(sb.toString());
                _writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(String.format("Could not write to '%s'", _filename));
            }
        }

    }

    
    private long        _timeElapsed;
    private long        _lastStartTime;
    private boolean     _isRunning;
    
    private CSVWriter   _csvWriter;

    public Timer(String resultFileName) {
        this._timeElapsed   = 0;
        this._lastStartTime = 0;
        this._isRunning     = false;
        
        this._csvWriter     = new CSVWriter(resultFileName);
    }

    public void start() {
        if (!this._isRunning) {
            this._lastStartTime = System.nanoTime();
            this._isRunning = true;
        }
    }

    public void stop() {
        if (this._isRunning) {
            long difference = System.nanoTime() - this._lastStartTime;

            this._isRunning = false;
            this._timeElapsed += difference;
            
            _csvWriter.writeLine(Long.toString(difference));
        }
    }

    public long timeElapsed() {
        if (this._isRunning) {
            return this._timeElapsed + (System.nanoTime() - this._lastStartTime);
        }
        return this._timeElapsed;
    }
}
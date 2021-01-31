/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.log;

public class DkFileLogger {
    private String logFilePath;

    public DkFileLogger(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }
}

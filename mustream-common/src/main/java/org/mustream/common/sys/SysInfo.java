package org.mustream.common.sys;

import java.io.File;

public class SysInfo {
    public static final int MUSTREAMER_PORT = 4400;
    public static final File SERVER_FOLDER = new File(System.getProperty("user.home"), "mustreammer");
    public static final File SERVER_TEMP_FOLDER = new File(SERVER_FOLDER, "tmp");

    static {
        if (!SERVER_FOLDER.exists()) {
            SERVER_FOLDER.mkdirs();
        }
        if (!SERVER_TEMP_FOLDER.exists()) {
            SERVER_TEMP_FOLDER.mkdirs();
        }
    }
}

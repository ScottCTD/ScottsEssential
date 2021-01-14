package xyz.scottc.scessential.utils;

import java.io.File;

public class FIleUtils {

    public static File createSubFile(String fileName, File parrentFolder) {
        return new File(parrentFolder.getAbsolutePath() + "/" + fileName);
    }

}

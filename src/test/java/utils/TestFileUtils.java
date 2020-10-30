package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestFileUtils {

    public static boolean isFileExist(String path) {
        return Files.exists(Paths.get(path));
    }

    public static List<String> readLinesFromFile(String path) {
        List<String> result = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
            String strLine;
            while ((strLine = bufferedReader.readLine()) != null)   {
                // Print the content on the console
                result.add(strLine);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void deleteFile(String path) {
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

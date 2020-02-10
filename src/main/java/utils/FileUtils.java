package utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class FileUtils {

    public static Properties propertiesFromFile(String pathToFile) {
        try {
            return propertiesFrom(new FileInputStream(new File(pathToFile)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Properties();
    }

    public static Properties propertiesFromClasspath(String pathToResource) {
        Properties properties = new Properties();
        try {
            Resource resource = new ClassPathResource(pathToResource);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            properties.load(reader);
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static Properties propertiesFromResource(String pathToResource) {
        Properties properties = new Properties();
        try {
            return propertiesFrom(new FileInputStream(Objects.requireNonNull(
                    FileUtils.class.getClassLoader().getResource(pathToResource)).getFile()
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static List<String> linesFromResource(String pathToResource) {
        List<String> result = new ArrayList<>();
        try {
            FileInputStream fileInputStream =  new FileInputStream(Objects.requireNonNull(
                    FileUtils.class.getClassLoader().getResource(pathToResource)).getFile()
            );
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String strLine;
            while ((strLine = bufferedReader.readLine()) != null)   {
                // Print the content on the console
                result.add(strLine);
            }
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Properties propertiesFrom(InputStream inputStream) throws Exception {
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }
}

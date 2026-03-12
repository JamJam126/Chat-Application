package utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader {

    public static Map<String,String> load() {

        Map<String,String> env = new HashMap<>();

        try {
            for(String line : Files.readAllLines(Path.of(".env"))) {

                if(line.isBlank() || line.startsWith("#"))
                    continue;

                String[] parts = line.split("=",2);
                env.put(parts[0].trim(), parts[1].trim());
            }

        } catch (Exception e) {
            System.out.println(".env file not found");
        }

        return env;
    }
}
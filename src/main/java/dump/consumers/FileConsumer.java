package dump.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import objects.AbstractObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;


public class FileConsumer<T extends AbstractObject> extends AbstractObjectConsumer<T> {

    String filePath;
    int successfullyConsumed, failedToConsume;
    private final ObjectMapper mapper;
    private BufferedOutputStream out;
    boolean successfullyInit;

    public FileConsumer(String filePath) {
        this.filePath = filePath;
        this.successfullyConsumed = 0;
        this.failedToConsume = 0;
        this.mapper = new ObjectMapper();
        this.successfullyInit = false;
        init();
    }

    private void init() {
        try {
            out = new BufferedOutputStream(
                    Files.newOutputStream(Paths.get(filePath), CREATE, APPEND));
            successfullyInit = true;
        } catch (IOException e) {
            e.printStackTrace();
            successfullyInit = false;
        }
    }

    @Override
    public void accept(AbstractObject abstractObject) {
        if (successfullyInit) {
            try {
                out.write(mapper.writeValueAsString(abstractObject).getBytes());
                out.write('\n');
            } catch (IOException e) {
                e.printStackTrace();
                failedToConsume++;
            }
            successfullyConsumed++;
        }
    }

    @Override
    public void finalHook() {
        if (out != null) {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

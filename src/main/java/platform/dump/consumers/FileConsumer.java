package platform.dump.consumers;

import objects.AbstractObject;

import java.util.function.Consumer;

public class FileConsumer implements Consumer<AbstractObject> {

    String filePath;

    public FileConsumer(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void accept(AbstractObject abstractObject) {

    }

}

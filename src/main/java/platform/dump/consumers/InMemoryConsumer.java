package platform.dump.consumers;

import objects.AbstractObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InMemoryConsumer implements Consumer<AbstractObject> {

    List<AbstractObject> buffer;

    public InMemoryConsumer(List<AbstractObject> buffer) {
        this.buffer = buffer;
    }

    @Override
    public void accept(AbstractObject abstractObject) {
        buffer.add(abstractObject);
    }

}

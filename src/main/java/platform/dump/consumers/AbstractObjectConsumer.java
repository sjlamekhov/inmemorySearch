package platform.dump.consumers;

import objects.AbstractObject;

import java.util.function.Consumer;

public abstract class AbstractObjectConsumer<T extends AbstractObject> implements Consumer<T> {

    public void finalHook() {}

}

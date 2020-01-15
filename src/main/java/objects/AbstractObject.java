package objects;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractObject {

    protected final Map<String, String> attributes;

    protected AbstractObjectUri uri;
    protected Long createTimestamp = new Long(0L);
    protected Long updateTimestamp = new Long(0L);

    public AbstractObject(AbstractObjectUri uri) {
        this.uri = uri;
        this.attributes = new HashMap<>();
    }

    public AbstractObject(AbstractObjectUri uri, Map<String, String> attributes) {
        this.uri = uri;
        this.attributes = new HashMap<>(attributes);
    }

    public AbstractObjectUri getUri() {
        return uri;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public Long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUri(DocumentUri uri) {
        this.uri = uri;
    }

    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

}

package objects;

import java.util.Map;

public class Document extends AbstractObject {

    public Document() {
        super(null);
    }

    public Document(DocumentUri documentUri) {
        super(documentUri);
    }

    public Document(DocumentUri documentUri, Map<String, String> attributes) {
        super(documentUri, attributes);
    }

    public DocumentUri getUri() {
        return (DocumentUri) uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractObject that = (AbstractObject) o;

        if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null) return false;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
        if (createTimestamp != null ? !createTimestamp.equals(that.createTimestamp) : that.createTimestamp != null)
            return false;
        return updateTimestamp != null ? updateTimestamp.equals(that.updateTimestamp) : that.updateTimestamp == null;
    }

    @Override
    public int hashCode() {
        int result = attributes != null ? attributes.hashCode() : 0;
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (createTimestamp != null ? createTimestamp.hashCode() : 0);
        result = 31 * result + (updateTimestamp != null ? updateTimestamp.hashCode() : 0);
        return result;
    }

}
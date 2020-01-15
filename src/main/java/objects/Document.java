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

}
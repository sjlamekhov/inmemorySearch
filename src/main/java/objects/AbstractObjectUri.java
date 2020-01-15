package objects;

import dao.UriGenerator;

public abstract class AbstractObjectUri {

    private final String id;
    private final String tenantId;
    private final boolean isNew;

    public AbstractObjectUri(String tenantId) {
        this.id = UriGenerator.generateId();
        this.tenantId = tenantId;
        this.isNew = true;
    }

    public AbstractObjectUri(String id, String tenantId) {
        this.id = id;
        this.tenantId = tenantId;
        this.isNew = false;
    }

    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public boolean isNew() {
        return isNew;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractObjectUri that = (AbstractObjectUri) o;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return tenantId != null ? tenantId.equals(that.tenantId) : that.tenantId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (tenantId != null ? tenantId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", tenantId='" + tenantId + '\'' +
                '}';
    }
}

package objects;

import dao.UriGenerator;

public abstract class AbstractObjectUri {

    protected String id;
    protected boolean isNew;
    protected String tenantId;

    public AbstractObjectUri() {
    }

    public AbstractObjectUri(String tenantId) {
        this.id = null;
        this.isNew = true;
        this.tenantId = tenantId;
    }

    public AbstractObjectUri(String id, String tenantId) {
        this.id = id;
        this.isNew = false;
        this.tenantId = tenantId;
    }

    public String getId() {
        return id;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public String getTenantId() {
        return tenantId;
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

package uk.gov.hmcts.ccd;

import java.util.Map;

public class CCDCollectionEntry {
    public String id;
    public Object value;
    CCDCollectionEntry() {
        
    }
    public CCDCollectionEntry(String id, Object value) {
        this.id = id;
        this.value = value;
    }
}

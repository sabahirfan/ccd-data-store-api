package uk.gov.hmcts.ccd.domain.model.aggregated;

import uk.gov.hmcts.ccd.domain.model.definition.CaseField;

public class CaseViewTab {
    private String id;
    private String label;
    private Integer order;
    private CaseField[] fields;

    public CaseViewTab() {
        // default constructor
    }

    public CaseViewTab(String id, String label, Integer order, CaseField[] fields) {
        this.id = id;
        this.label = label;
        this.order = order;
        this.fields = fields;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public CaseField[] getFields() {
        return fields;
    }

    public void setFields(CaseField[] fields) {
        this.fields = fields;
    }
}

package uk.gov.hmcts.ccd.domain.model.search;

public class SearchInput {

    private String label;
    private Integer order;
    private Field field;

    public SearchInput() {
        // Default constructor for JSON mapper
    }

    public SearchInput(
            final Field field,
            final String label,
            final Integer order
    ) {
        this.field = field;
        this.label = label;
        this.order = order;
    }

    public Field getField() {
        return field;
    }

    public int getOrder() {
        return order;
    }

    public String getLabel() {
        return label;
    }

}

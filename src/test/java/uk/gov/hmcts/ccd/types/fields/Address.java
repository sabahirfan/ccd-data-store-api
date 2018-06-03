package uk.gov.hmcts.ccd.types.fields;

import uk.gov.hmcts.ccd.definition.CaseListField;
import uk.gov.hmcts.ccd.definition.FieldLabel;

public class Address {
    @CaseListField(label = "Nested field")
    @FieldLabel(value = "Line 1")
    private String line1 = "test line 1";
    @FieldLabel(value = "Line 2")
    private String line2 = "line 2";
}

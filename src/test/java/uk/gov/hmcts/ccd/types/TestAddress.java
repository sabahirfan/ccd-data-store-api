package uk.gov.hmcts.ccd.types;

import uk.gov.hmcts.ccd.definition.FieldLabel;

public class TestAddress {
    @FieldLabel(value = "test")
    private String line1 = "foo";
    private String line2;
    private String postcode;
}

package uk.gov.hmcts.ccd.types.fields;

import uk.gov.hmcts.ccd.definition.FieldLabel;

public class TestAddress {
    private static String ignored = "ignore me!";
    @FieldLabel(value = "test")
    private String line1 = "foo";
    private String line2 = "bar";
    private String postcode = "BAZ";
}

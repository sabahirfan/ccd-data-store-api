package uk.gov.hmcts.ccd.types;

import uk.gov.hmcts.ccd.definition.ComplexType;
import uk.gov.hmcts.ccd.definition.FieldLabel;

public class Party {
    @FieldLabel(value = "Party Name")
    String name = "test";
    @FieldLabel(value = "Party Address")
    @ComplexType
    Address address = new Address();
}

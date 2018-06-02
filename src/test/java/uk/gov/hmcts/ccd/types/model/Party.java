package uk.gov.hmcts.ccd.types.model;

import uk.gov.hmcts.ccd.definition.ComplexType;
import uk.gov.hmcts.ccd.definition.FieldLabel;
import uk.gov.hmcts.ccd.types.Address;

public class Party {
    @FieldLabel(value = "Party Name")
    String name = "test";
    @FieldLabel(value = "Party Address")
    @ComplexType
    Address address = new Address();
}

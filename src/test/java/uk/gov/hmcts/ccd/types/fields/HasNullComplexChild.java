package uk.gov.hmcts.ccd.types.fields;

import uk.gov.hmcts.ccd.definition.FieldLabel;

@FieldLabel(value = "nothing")
public class HasNullComplexChild {
    Address address = null;
}

package uk.gov.hmcts.ccd.types.nested;

import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.ccd.definition.CaseListField;
import uk.gov.hmcts.ccd.definition.ComplexType;
import uk.gov.hmcts.ccd.types.fields.Address;

@Getter
@Setter
public class WithSubClass {
    @CaseListField(label = "Foo Bar")
    public String fooBar = "foo";
    @ComplexType
    public Address subClass = new Address();
}

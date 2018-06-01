package uk.gov.hmcts.ccd.types;

import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.ccd.definition.CaseListField;
import uk.gov.hmcts.ccd.definition.ComplexType;

@Getter
@Setter
public class WithSubClass {
    @CaseListField(label = "Foo Bar")
    public String fooBar = "foo";
    @ComplexType
    public SubClass subClass;
}

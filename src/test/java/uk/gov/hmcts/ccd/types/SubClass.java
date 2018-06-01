package uk.gov.hmcts.ccd.types;

import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.ccd.definition.CaseListField;

@Getter
@Setter
public class SubClass {
    @CaseListField(label = "Sub!")
    private String sub = "bar";
}


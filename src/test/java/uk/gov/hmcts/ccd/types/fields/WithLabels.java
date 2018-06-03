package uk.gov.hmcts.ccd.types.fields;


import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.ccd.definition.FieldLabel;

@Getter @Setter
@FieldLabel(value = "parent")
public class WithLabels {
    @FieldLabel(value = "child")
    private String aString = "value";
}

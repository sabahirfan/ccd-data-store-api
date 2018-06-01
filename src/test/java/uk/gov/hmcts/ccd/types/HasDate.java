package uk.gov.hmcts.ccd.types;

import uk.gov.hmcts.ccd.definition.CaseListField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class HasDate {
    @CaseListField(label = "foo")
    private LocalDate date;
    @CaseListField(label = "bar")
    private LocalDateTime dateTime;
    @CaseListField(label = "baz")
    private Date third;
}

package uk.gov.hmcts.ccd.definition;

import java.util.function.Supplier;

public interface FieldRenderer {
    void render(Supplier<Object> getter);
}

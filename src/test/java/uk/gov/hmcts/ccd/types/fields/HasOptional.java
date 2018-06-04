package uk.gov.hmcts.ccd.types.fields;

import lombok.Getter;

import java.util.Optional;

@Getter
public class HasOptional {
    Optional<String> value = Optional.empty();
    String foo = "a";
}

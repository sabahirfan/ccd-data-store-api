package uk.gov.hmcts.ccd.types.cmc;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;


public class Offer {

    @NotBlank
    private final String content;

    @NotNull
    private final LocalDate completionDate;

    public Offer(String content, LocalDate completionDate) {
        this.content = content;
        this.completionDate = completionDate;
    }

    public String getContent() {
        return content;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    @Override
    public boolean equals(Object input) {
        if (this == input) {
            return true;
        }
        if (input == null || getClass() != input.getClass()) {
            return false;
        }

        Offer other = (Offer) input;
        return Objects.equals(content, other.content)
            && Objects.equals(completionDate, other.completionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, completionDate);
    }


}

package uk.gov.hmcts.ccd.types.cmc;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Objects;
import java.util.Optional;


public class PartyStatement {

    private StatementType type;
    private MadeBy madeBy;
    private Offer offer;

    @JsonCreator
    public PartyStatement(StatementType type, MadeBy madeBy, Offer offer) {
        this.type = type;
        this.madeBy = madeBy;
        this.offer = offer;
    }

    public PartyStatement(StatementType type, MadeBy madeBy) {
        this.type = type;
        this.madeBy = madeBy;
    }

    public StatementType getType() {
        return type;
    }

    public MadeBy getMadeBy() {
        return madeBy;
    }

    public Optional<Offer> getOffer() {
        return Optional.ofNullable(offer);
    }

    @Override
    public boolean equals(Object input) {
        if (this == input) {
            return true;
        }

        if (input == null || getClass() != input.getClass()) {
            return false;
        }

        PartyStatement that = (PartyStatement) input;

        return type == that.type
            && madeBy == that.madeBy
            && Objects.equals(offer, that.offer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, madeBy, offer);
    }


}

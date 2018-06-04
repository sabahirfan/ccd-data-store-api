package uk.gov.hmcts.ccd.types.cmc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

public class Settlement {

    private final List<PartyStatement> partyStatements = new ArrayList<>();

    public void makeOffer(Offer offer, MadeBy party) {
        partyStatements.add(new PartyStatement(StatementType.OFFER, party, offer));
    }

    public void accept(MadeBy party) {
        partyStatements.add(new PartyStatement(StatementType.ACCEPTATION, party));
    }

    public void reject(MadeBy party) {
        partyStatements.add(new PartyStatement(StatementType.REJECTION, party));
    }

    public void countersign(MadeBy party) {
        partyStatements.add(new PartyStatement(StatementType.COUNTERSIGNATURE, party));
    }

    @JsonIgnore
    PartyStatement getLastStatement() {
        return partyStatements.get(partyStatements.size() - 1);
    }

    public List<PartyStatement> getPartyStatements() {
        return Collections.unmodifiableList(partyStatements);
    }

    private boolean lastStatementIsAnOfferMadeBy(MadeBy madeBy) {
        return lastStatementIsOffer() && getLastStatement().getMadeBy().equals(madeBy);
    }

    private boolean lastStatementIsOffer() {
        return getLastStatement().getType().equals(StatementType.OFFER);
    }

    private boolean lastStatementIsAcceptationNotBy(MadeBy madeBy) {
        PartyStatement lastStatement = getLastStatement();
        return lastStatement.getType().equals(StatementType.ACCEPTATION)
            && !lastStatement.getMadeBy().equals(madeBy);
    }

    @Override
    public boolean equals(Object input) {
        if (this == input) {
            return true;
        }
        if (input == null || getClass() != input.getClass()) {
            return false;
        }
        Settlement that = (Settlement) input;
        return Objects.equals(partyStatements, that.partyStatements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyStatements);
    }
}

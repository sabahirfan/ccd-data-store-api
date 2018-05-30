package uk.gov.hmcts.ccd;

public interface ICase {
    String getCaseId();
    <T extends Enum<T>> T getState();
}

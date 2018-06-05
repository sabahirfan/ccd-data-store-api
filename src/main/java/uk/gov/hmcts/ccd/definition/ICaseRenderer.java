package uk.gov.hmcts.ccd.definition;

public interface ICaseRenderer {
    void render(Object o);
    void render(Object o, String label);
}

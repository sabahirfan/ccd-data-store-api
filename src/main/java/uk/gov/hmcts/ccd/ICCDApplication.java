package uk.gov.hmcts.ccd;

import com.google.common.collect.ImmutableSet;

import java.util.List;

public interface ICCDApplication<T extends ICase> {
    Class<T> getCaseClass();
    List<T> getCases();
    void saveCase(T c);

    ImmutableSet<String> getEvents();
}

package uk.gov.hmcts.ccd;

import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Map;

public interface ICCDApplication<T extends ICase> {
    List<T> getCases(Map<String, String> searchCriteria);
    void saveCase(T c);

    ImmutableSet<String> getEvents();
}

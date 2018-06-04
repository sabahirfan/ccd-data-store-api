package uk.gov.hmcts.ccd.types.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import uk.gov.hmcts.ccd.ICCDApplication;
import uk.gov.hmcts.ccd.definition.EventConfig;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewTrigger;
import uk.gov.hmcts.ccd.domain.model.aggregated.ProfileCaseState;
import uk.gov.hmcts.ccd.types.model.FakeCase;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FakeCCDImplementation implements ICCDApplication<FakeCase> {
    public static FakeCase fakeCase = new FakeCase("Defendant", "foo");

    @Override
    public List<FakeCase> getCases(Map<String, String> searchCriteria) {
        return Lists.newArrayList(
                fakeCase,
                fakeCase
        );
    }

    @Override
    public String saveCase(FakeCase c) {
        return "fake id";
    }

    @Override
    public FakeCase getCase(String id) {
        return fakeCase;
    }

    @Override
    public ImmutableSet<String> getEvents() {
        return ImmutableSet.of();
    }

    @Override
    public List<CaseViewTrigger> getTriggers(String caseId) {
        return Arrays.asList(
            new CaseViewTrigger("hello1", "Hello 1", "Hello 1 desc", 0),
            new CaseViewTrigger("more-time-request-paper", "More time request paper", "desc", 1)
        );
    }

    @Override
    public ProfileCaseState getCaseState(String caseId) {
        return new ProfileCaseState();
    }

    @Override
    public Map<String, EventConfig> eventsMapping() {
        return Maps.newHashMap();
    }
}

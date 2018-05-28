package uk.gov.hmcts.ccd;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ccd.domain.model.definition.*;

@Configuration
@Service
public class CoreCaseService {
    private final ICCDApplication application;
    private final CCDAppConfig config;

    public CoreCaseService(CCDAppConfig config, ICCDApplication application) {
        this.config = config;
        this.application = application;
    }

    public CaseType getCaseType() {
        CaseType result = new CaseType();
        result.setId(config.getCaseTypeId());
        result.setCaseFields(FieldGenerator.generateFields(application.getCaseClass()));
        return result;
    }
}

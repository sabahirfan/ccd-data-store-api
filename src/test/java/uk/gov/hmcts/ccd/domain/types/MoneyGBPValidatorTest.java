package uk.gov.hmcts.ccd.domain.types;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.ccd.data.definition.CaseDefinitionRepository;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.test.CaseFieldBuilder;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class MoneyGBPValidatorTest {
    private static final String FIELD_ID = "TEST_FIELD_ID";
    private static final JsonNodeFactory NODE_FACTORY = JsonNodeFactory.instance;

    @Mock
    private BaseType moneyGbpBaseType;

    @Mock
    private CaseDefinitionRepository definitionRepository;

    private MoneyGBPValidator validator;
    private CaseField caseField;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        doReturn(Collections.emptyList()).when(definitionRepository).getBaseTypes();
        BaseType.setCaseDefinitionRepository(definitionRepository);
        BaseType.initialise();

        doReturn(MoneyGBPValidator.TYPE_ID).when(moneyGbpBaseType).getType();
        BaseType.register(moneyGbpBaseType);

        validator = new MoneyGBPValidator();

        caseField = caseField().build();
    }

    @Test
    public void validMoney() {
        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("177978989700"),
                                                                   caseField);
        assertEquals(0, result01.size());

        final List<ValidationResult> result02 = validator.validate(FIELD_ID, NODE_FACTORY.textNode("-100"), caseField);
        assertEquals(0, result02.size());
    }

    @Test
    public void nullMoney() {
        final List<ValidationResult> result01 = validator.validate(FIELD_ID, null, caseField);
        assertEquals("Did not catch null", 0, result01.size());
    }

    @Test
    public void invalidMoney() {
        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("3321M1 1AA"),
                                                                   caseField);
        assertEquals(result01.toString(), 1, result01.size());

        final List<ValidationResult> result02 = validator.validate(FIELD_ID, NODE_FACTORY.textNode("100.1"), caseField);
        assertEquals(result02.toString(), 1, result01.size());
    }

    @Test
    public void checkMaxMin_BothBelow1GBP() {
        final CaseField minMaxCaseField = caseField().withMin(5)
                                                     .withMax(10)
                                                     .build();

        // Test valid max min
        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("5"),
                                                                   minMaxCaseField);
        assertEquals(0, result01.size());

        final List<ValidationResult> result02 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("10"),
                                                                   minMaxCaseField);
        assertEquals(0, result02.size());

        final List<ValidationResult> result03 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("7"),
                                                                   minMaxCaseField);
        assertEquals(0, result03.size());

        // Test invalid max min
        final List<ValidationResult> result04 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("4"),
                                                                   minMaxCaseField);
        assertEquals(1, result04.size());
        assertEquals("Should be more than or equal to £0.05", result04.get(0).getErrorMessage());

        final List<ValidationResult> result05 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("11"),
                                                                   minMaxCaseField);
        assertEquals(1, result05.size());
        assertEquals("Should be less than or equal to £0.10", result05.get(0).getErrorMessage());
    }

    @Test
    public void checkMaxMin_BothAbove1GBP() {
        final CaseField minMaxCaseField = caseField().withMin(123)
                                                     .withMax(123456)
                                                     .build();

        // Test invalid max min
        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("123457"),
                                                                   minMaxCaseField);
        assertEquals(1, result01.size());
        assertEquals("Should be less than or equal to £1,234.56", result01.get(0).getErrorMessage());

        final List<ValidationResult> result02 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("122"),
                                                                   minMaxCaseField);
        assertEquals(1, result02.size());
        assertEquals("Should be more than or equal to £1.23", result02.get(0).getErrorMessage());
    }

    @Test
    public void getType() {
        assertEquals("Type is incorrect", validator.getType(), BaseType.get("MoneyGBP"));
    }

    private CaseFieldBuilder caseField() {
        return new CaseFieldBuilder(FIELD_ID).withType(MoneyGBPValidator.TYPE_ID);
    }
}

package uk.gov.hmcts.ccd.domain.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.ccd.data.definition.CaseDefinitionRepository;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.test.CaseFieldBuilder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class EmailValidatorTest {
    private static final JsonNodeFactory NODE_FACTORY = JsonNodeFactory.instance;
    private static final String FIELD_ID = "TEST_FIELD_ID";

    @Mock
    private BaseType emailBaseType;

    @Mock
    private CaseDefinitionRepository definitionRepository;

    private EmailValidator validator;

    private CaseField caseField;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        doReturn(Collections.emptyList()).when(definitionRepository).getBaseTypes();
        BaseType.setCaseDefinitionRepository(definitionRepository);
        BaseType.initialise();

        doReturn(EmailValidator.TYPE_ID).when(emailBaseType).getType();
        doReturn(null).when(emailBaseType).getRegularExpression();
        BaseType.register(emailBaseType);

        validator = new EmailValidator();

        caseField = caseField().build();
    }

    @Test
    public void validEmail() {
        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("test@test.com"),
                                                                   caseField);
        assertEquals(result01.toString(), 0, result01.size());

        final List<ValidationResult> result02 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("test@test"),
                                                                   caseField);
        assertEquals(result02.toString(), 0, result02.size());

        final List<ValidationResult> result03 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("test@test.org"),
                                                                   caseField);
        assertEquals(result01.toString(), 0, result03.size());

        final List<ValidationResult> result04 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("test@test.org.uk"),
                                                                   caseField);
        assertEquals(result04.toString(), 0, result04.size());

        final List<ValidationResult> result05 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("test.test@test.com"),
                                                                   caseField);
        assertEquals(result05.toString(), 0, result05.size());

        final List<ValidationResult> result06 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("test_test@test.xxx"),
                                                                   caseField);
        assertEquals(result06.toString(), 0, result06.size());
    }

    @Test
    public void invalidEmail() {
        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("test.test.com"),
                                                                   caseField);
        assertEquals(result01.toString(), 1, result01.size());

        final List<ValidationResult> result02 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("test.com"),
                                                                   caseField);
        assertEquals(result02.toString(), 1, result01.size());

        final List<ValidationResult> result03 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("test@test@test"),
                                                                   caseField);
        assertEquals(result03.toString(), 1, result03.size());
    }

    @Test
    public void fieldTypeRegEx() {
        final CaseField regexCaseField = caseField().withRegExp("^[a-z]\\w*@hmcts.net$").build();
        final JsonNode validValue = NODE_FACTORY.textNode("k9@hmcts.net");
        final List<ValidationResult> validResult = validator.validate(FIELD_ID, validValue, regexCaseField);
        assertEquals(validResult.toString(), 0, validResult.size());

        final JsonNode invalidValue = NODE_FACTORY.textNode("9k@hmcts.net");
        final List<ValidationResult> invalidResult = validator.validate(FIELD_ID, invalidValue, regexCaseField);
        assertEquals(invalidResult.toString(), 1, invalidResult.size());
        assertEquals("9k@hmcts.net field type Regex Failed: ^[a-z]\\w*@hmcts.net$",
                     invalidResult.get(0).getErrorMessage());
    }

    @Test
    public void baseTypeRegEx() {
        doReturn("\\\\w*@hmcts.net").when(emailBaseType).getRegularExpression();

        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("9k@hmcts.net"), caseField);
        assertEquals(1, result01.size());
        assertEquals("9k@hmcts.net base type Regex Failed: \\\\w*@hmcts.net", result01.get(0).getErrorMessage());
    }

    @Test
    public void checkMin() {
        final CaseField caseField = caseField().withMin(new BigDecimal(13)).build();
        final JsonNode validValue = NODE_FACTORY.textNode("k99@hmcts.net");
        final List<ValidationResult> validResult = validator.validate(FIELD_ID, validValue, caseField);
        assertEquals(validResult.toString(), 0, validResult.size());

        final JsonNode invalidValue = NODE_FACTORY.textNode("k9@hmcts.net");
        final List<ValidationResult> invalidResult = validator.validate(FIELD_ID, invalidValue, caseField);
        assertEquals(invalidResult.toString(), 1, invalidResult.size());
        assertEquals("Email 'k9@hmcts.net' requires minimum length 13", invalidResult.get(0).getErrorMessage());
    }

    @Test
    public void checkMax() {
        final CaseField caseField = caseField().withMax(new BigDecimal(12)).build();
        final JsonNode validValue = NODE_FACTORY.textNode("k9@hmcts.net");
        final List<ValidationResult> validResult = validator.validate(FIELD_ID, validValue, caseField);
        assertEquals(validResult.toString(), 0, validResult.size());

        final JsonNode invalidValue = NODE_FACTORY.textNode("k99@hmcts.net");
        final List<ValidationResult> invalidResult = validator.validate(FIELD_ID, invalidValue, caseField);
        assertEquals(invalidResult.toString(), 1, invalidResult.size());
        assertEquals("Email 'k99@hmcts.net' exceeds maximum length 12", invalidResult.get(0).getErrorMessage());
    }

    @Test
    public void nullValue() {
        assertEquals("Did not catch NULL", 0, validator.validate(FIELD_ID, null, caseField).size());
    }

    @Test
    public void getType() {
        assertEquals("Type is incorrect", validator.getType(), BaseType.get("Email"));
    }

    private CaseFieldBuilder caseField() {
        return new CaseFieldBuilder(FIELD_ID).withType(EmailValidator.TYPE_ID);
    }
}

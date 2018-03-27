package uk.gov.hmcts.ccd.domain.types;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.util.RawValue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.ccd.BaseTest;
import uk.gov.hmcts.ccd.data.definition.CaseDefinitionRepository;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.test.CaseFieldBuilder;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class NumberValidatorTest {
    private static final JsonNodeFactory NODE_FACTORY = JsonNodeFactory.instance;
    private static final String FIELD_ID = "TEST_FIELD_ID";

    @Mock
    private BaseType numberBaseType;

    @Mock
    private CaseDefinitionRepository definitionRepository;

    private NumberValidator validator;
    private CaseField caseField;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        doReturn(Collections.emptyList()).when(definitionRepository).getBaseTypes();
        BaseType.setCaseDefinitionRepository(definitionRepository);
        BaseType.initialise();

        doReturn(NumberValidator.TYPE_ID).when(numberBaseType).getType();
        BaseType.register(numberBaseType);

        validator = new NumberValidator();

        caseField = caseField().build();
    }

    private CaseFieldBuilder caseField() {
        return new CaseFieldBuilder(FIELD_ID).withType(NumberValidator.TYPE_ID);
    }

    @Test
    public void noValueOrMaxOrMin() {
        final JsonNode data = NODE_FACTORY.textNode("");
        assertEquals(validator.validate("TEST_FIELD_ID", data, caseField).toString(),
                     0,
                     validator.validate("TEST_FIELD_ID", data, caseField).size());
    }

    @Test
    public void noValueWithMaxOrMin() {
        final CaseField caseField = caseField().withMin(5)
                                               .withMax(10)
                                               .build();
        final JsonNode data = NODE_FACTORY.textNode("");
        assertEquals(0, validator.validate("TEST_FIELD_ID", data, caseField).size());
    }

    @Test
    public void valueWithMaxMin() {
        final CaseField caseField = caseField().withMin(5)
                                               .withMax(10)
                                               .build();

        assertEquals("5 should be with in range of between 5 and 10", 0,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.textNode("5"), caseField).size());

        assertEquals("5 should be with in range of between 5 and 10", 0,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.numberNode(5), caseField).size());

        assertEquals("5.001 should be with in range of between 5 and 10", 0,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.textNode("5.001"), caseField).size());

        assertEquals("5.001 should be with in range of between 5 and 10", 0,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.numberNode(5.001), caseField).size());

        assertEquals("9.999999 should be with in range of between 5 and 10", 0,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.textNode("9.999999"), caseField).size());

        assertEquals("9.999999 should be with in range of between 5 and 10", 0,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.numberNode(9.999999), caseField).size());

        List<ValidationResult> textNodeBelowMin = validator.validate("TEST_FIELD_ID",
                                                                     NODE_FACTORY.textNode("4.9"),
                                                                     caseField);
        assertEquals("4.9 should not be with in range of between 5 and 10", 1, textNodeBelowMin.size());
        assertEquals("Should be more than or equal to 5", textNodeBelowMin.get(0).getErrorMessage());

        List<ValidationResult> numberNodeBelowMin = validator.validate("TEST_FIELD_ID",
                                                                       NODE_FACTORY.numberNode(4.9),
                                                                       caseField);
        assertEquals("4.9 should not be with in range of between 5 and 10", 1, numberNodeBelowMin.size());
        assertEquals("Should be more than or equal to 5", numberNodeBelowMin.get(0).getErrorMessage());

        List<ValidationResult> textNodeAboveMin = validator.validate("TEST_FIELD_ID",
                                                                     NODE_FACTORY.textNode("10.1"),
                                                                     caseField);
        assertEquals("10.1 should not be with in range of between 5 and 10", 1, textNodeAboveMin.size());
        assertEquals("Should be less than or equal to 10", textNodeAboveMin.get(0).getErrorMessage());

        List<ValidationResult> numberNodeAboveMin = validator.validate("TEST_FIELD_ID",
                                                                       NODE_FACTORY.numberNode(10.1),
                                                                       caseField);
        assertEquals("10.1 should not be with in range of between 5 and 10", 1,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.numberNode(10.1), caseField).size());
        assertEquals("Should be less than or equal to 10", numberNodeAboveMin.get(0).getErrorMessage());
    }

    @Test
    public void valueWithSameMaxMin() {
        final CaseField caseField = caseField().withMin(0)
                                               .withMax(0)
                                               .build();

        assertEquals("0 should be with in range of between 0 and 0", 0,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.textNode("0"), caseField).size());

        assertEquals("0 should be with in range of between 0 and 0", 0,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.numberNode(0), caseField).size());

        assertEquals("-1 should not be with in range of between 0 and 0", 1,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.textNode("-1"), caseField).size());

        assertEquals("-1 should not be with in range of between 0 and 0", 1,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.numberNode(-1), caseField).size());

        assertEquals("0.0000000000 should be with in range of between 0 and 0", 0,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.textNode("0.0000000000"), caseField).size());

        assertEquals("0.0000000000 should be with in range of between 0 and 0", 0,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.numberNode(0.0000000000), caseField).size());
    }

    @Test
    public void valueWithSameDecimalMaxMin() {
        final CaseField caseField = caseField().withMin(0.0f)
                                               .withMax(0.0f)
                                               .build();

        assertEquals("0 should be with in range of between 0.00 and 0.00", 0,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.textNode("0"), caseField).size());

        assertEquals("0 should be with in range of between 0.00 and 0.00", 0,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.numberNode(0), caseField).size());
    }

    @Test
    public void fieldTypeRegEx() {
        final CaseField caseField = caseField().withRegExp("^\\d\\.\\d\\d$").build();

        assertEquals("regular expression check", 0,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.textNode("8.20"), caseField).size());

        List<ValidationResult> results = validator.validate("TEST_FIELD_ID", NODE_FACTORY.textNode("8.2"), caseField);
        assertEquals("regular expression check", 1, results.size());
        assertEquals("8.2 fails regex: ^\\d\\.\\d\\d$", results.get(0).getErrorMessage());
    }

    @Test
    public void invalidBaseTypeRegEx() {
        doReturn("\\d").when(numberBaseType).getRegularExpression();

        final List<ValidationResult> result = validator.validate("TEST_FIELD_ID",
                                                                 NODE_FACTORY.numberNode(12), caseField);
        assertEquals("RegEx validation failed", 1, result.size());
        assertEquals("'12' failed number Type Regex check: \\d", result.get(0).getErrorMessage());
        assertEquals("TEST_FIELD_ID", result.get(0).getFieldId());
    }

    @Test
    public void incorrectFormat() {
        final CaseField caseField = caseField().withMin(5)
                                               .withMax(10)
                                               .build();

        assertEquals("Did not catch invalid 10.1xxxx", 1,
                     validator.validate("TEST_FIELD_ID", NODE_FACTORY.textNode("10.1xxxx"), caseField).size());
    }

    @Test
    public void nullValue() {
        assertEquals("Did not catch NULL", 0, validator.validate("TEST_FIELD_ID", null, null).size());
    }

    @Test
    public void getType() {
        assertEquals("Type is incorrect", validator.getType(), BaseType.get("NUMBER"));
    }
}

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

public class FixedListValidatorTest {
    private static final String FIELD_ID = "TEST_FIELD_ID";
    private static final JsonNodeFactory NODE_FACTORY = JsonNodeFactory.instance;

    @Mock
    private BaseType fixedListBaseType;

    @Mock
    private CaseDefinitionRepository definitionRepository;

    private FixedListValidator validator;
    private CaseField caseField;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        doReturn(Collections.emptyList()).when(definitionRepository).getBaseTypes();
        BaseType.setCaseDefinitionRepository(definitionRepository);
        BaseType.initialise();

        doReturn(FixedListValidator.TYPE_ID).when(fixedListBaseType).getType();
        doReturn(null).when(fixedListBaseType).getRegularExpression();
        BaseType.register(fixedListBaseType);

        validator = new FixedListValidator();

        caseField = caseField().build();
    }

    @Test
    public void validValue() {
        final List<ValidationResult> result01 = validator.validate("TEST_FIELD_ID",
                                                                   NODE_FACTORY.textNode("AAAAAA"),
                                                                   caseField);
        assertEquals(0, result01.size());
    }

    @Test
    public void invalidValue() {
        final List<ValidationResult> result01 = validator.validate("TEST_FIELD_ID",
                                                                   NODE_FACTORY.textNode("DDDD"),
                                                                   caseField);
        assertEquals(result01.toString(), 1, result01.size());
    }

    @Test
    public void nullValue() {
        assertEquals("Did not catch NULL", 0, validator.validate("TEST_FIELD_ID", null, null).size());
    }

    @Test
    public void getType() {
        assertEquals("Type is incorrect", validator.getType(), BaseType.get("FixedList"));
    }

    @Test
    public void fieldTypeRegEx() {
        final CaseField caseFieldWithRegEx = caseField().withRegExp("AAAAAA").build();
        final List<ValidationResult> result01 = validator.validate("TEST_FIELD_ID", NODE_FACTORY.textNode("AAAAAA"),
                                                                   caseFieldWithRegEx);
        assertEquals(0, result01.size());

        final List<ValidationResult> result02 = validator.validate("TEST_FIELD_ID", NODE_FACTORY.textNode("BBBBBB"),
                                                                   caseFieldWithRegEx);
        assertEquals("BBBBBB failed regular expression check", 1, result02.size());
        assertEquals("BBBBBB fails regex: AAAAAA", result02.get(0).getErrorMessage());
        assertEquals("TEST_FIELD_ID", result02.get(0).getFieldId());
    }

    @Test
    public void baseTypeRegEx() {
        doReturn("InvalidRegEx").when(fixedListBaseType).getRegularExpression();
        final List<ValidationResult> result = validator.validate("TEST_FIELD_ID",
                                                                 NODE_FACTORY.textNode("AA"), caseField);
        assertEquals("RegEx validation failed", 1, result.size());
        assertEquals("'AA' failed FixedList Type Regex check: InvalidRegEx", result.get(0).getErrorMessage());
        assertEquals("TEST_FIELD_ID", result.get(0).getFieldId());
    }

    private CaseFieldBuilder caseField() {
        return new CaseFieldBuilder(FIELD_ID).withType(FixedListValidator.TYPE_ID)
                                             .withFixedListItem("AAAAAA")
                                             .withFixedListItem("BBBBBB")
                                             .withFixedListItem("CCCCCC");
    }
}

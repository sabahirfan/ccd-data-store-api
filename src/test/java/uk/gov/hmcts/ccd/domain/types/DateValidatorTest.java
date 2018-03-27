package uk.gov.hmcts.ccd.domain.types;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.ccd.data.definition.CaseDefinitionRepository;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldType;
import uk.gov.hmcts.ccd.test.CaseFieldBuilder;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class DateValidatorTest {
    private static final JsonNodeFactory NODE_FACTORY = JsonNodeFactory.instance;
    private static final String DATE_REGEX = "^(\\d{4})\\D?(0[1-9]|1[0-2])\\D?([12]\\d|0[1-9]|3[01])([zZ]|([\\+-])([01]\\d|2[0-3])\\D?([0-5]\\d)?)?$";
    private static final String LIMITED_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";
    private static final String FIELD_ID = "TEST_FIELD_ID";

    @Mock
    private FieldType dateFieldType;

    @Mock
    private CaseDefinitionRepository definitionRepository;

    private DateValidator validator;
    private CaseField caseField;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        doReturn(Collections.emptyList()).when(definitionRepository).getBaseTypes();
        BaseType.setCaseDefinitionRepository(definitionRepository);
        BaseType.initialise();

        doReturn(DateValidator.TYPE_ID).when(dateFieldType).getType();
        doReturn(DATE_REGEX).when(dateFieldType).getRegularExpression();
        BaseType.register(new BaseType(dateFieldType));

        validator = new DateValidator();

        caseField = caseField().build();
    }

    @Test
    public void validDate() {
        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("2012-04-21"), caseField);
        assertEquals(0, result01.size());

        final List<ValidationResult> result02 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("2012-04-21Z"), caseField);
        assertEquals(0, result02.size());

        final List<ValidationResult> result03 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("2012-04-21+01:00"),
                                                                   caseField);
        assertEquals(0, result03.size());
    }

    @Test
    public void invalidDate() {
        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("3321M1 1AA"), caseField);
        assertEquals("Did not catch invalid date 3321M1 1AA", 1, result01.size());
        assertEquals("\"3321M1 1AA\" is not a valid ISO 8601 date", result01.get(0).getErrorMessage());

        final List<ValidationResult> result02 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("1800-14-14"), caseField);
        assertEquals("Did not catch invalid date 1800-14-14 ", 1, result02.size());
        assertEquals("\"1800-14-14\" is not a valid ISO 8601 date", result02.get(0).getErrorMessage());

        final List<ValidationResult> result03 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("2001-11-31"), caseField);
        assertEquals("Did not catch invalid date 2001-11-31", 1, result03.size());
        assertEquals("\"2001-11-31\" is not a valid ISO 8601 date", result03.get(0).getErrorMessage());

        // checks that ISO DATE TIME is not accepted by DateValidator
        final List<ValidationResult> result04 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("2001-01-01T00:00:00.000Z"),
                                                                   caseField);
        assertEquals("Did not catch invalid date 2001-01-01T00:00:00.000Z", 1, result04.size());
        assertEquals("\"2001-01-01T00:00:00.000Z\" is not a valid ISO 8601 date", result04.get(0).getErrorMessage());

        final List<ValidationResult> result05 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("2000-02-29Z"), caseField);
        assertEquals("Year 2000 is a leap year", 0, result05.size());

        final List<ValidationResult> result06 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("2100-02-29Z"), caseField);
        assertEquals("Did not catch invalid date 2100-02-29Z", 1, result06.size());
        assertEquals("\"2100-02-29Z\" is not a valid ISO 8601 date", result06.get(0).getErrorMessage());
    }

    @Test
    public void getType() {
        assertEquals("Type is incorrect", validator.getType(), BaseType.get("DATE"));
    }

    @Test
    public void nullValue() {
        assertEquals("Did not catch NULL", 0, validator.validate(FIELD_ID, null, null).size());
    }

    @Test
    public void checkMax() {
        final String validDate = "2001-01-01Z";
        final String invalidDate = "2002-01-01Z";
        final String maxDate = "2001-12-31+01:00";
        final CaseField caseField = caseField().withMax(date(maxDate)).build();

        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode(validDate), caseField);
        assertEquals(result01.toString(), 0, result01.size());

        final List<ValidationResult> result02 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode(maxDate), caseField);
        assertEquals(result02.toString(), 0, result02.size());

        final List<ValidationResult> result03 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode(invalidDate), caseField);
        assertEquals("Did not catch invalid max-date", 1, result03.size());
        assertEquals("Validation message", "The date should be earlier than 31-12-2001",
                     result03.get(0).getErrorMessage());
    }

    @Test
    public void checkMin() {
        final String validDate = "2001-12-31Z";
        final String invalidDate = "2000-01-01Z";
        final String minDate = "2001-01-01Z";
        final CaseField caseField = caseField().withMin(date(minDate)).build();

        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode(validDate), caseField);
        assertEquals(result01.toString(), 0, result01.size());

        final List<ValidationResult> result02 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode(minDate), caseField);
        assertEquals(result02.toString(), 0, result02.size());

        final List<ValidationResult> result03 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode(invalidDate), caseField);
        assertEquals("Did not catch invalid max-date", 1, result03.size());
        assertEquals("Validation message", "The date should be later than 01-01-2001",
                     result03.get(0).getErrorMessage());
    }

    @Test
    public void checkMaxMinWithoutRegEx() {
        final String validDate = "2001-12-10Z";
        final String invalidMinDate = "1999-12-31Z";
        final String invalidMaxDate = "2002-01-01Z";
        final String minDate = "2001-01-01Z";
        final String maxDate = "2001-12-31Z";
        final CaseField caseField = caseField().withMin(date(minDate))
                                               .withMax(date(maxDate))
                                               .build();

        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode(validDate), caseField);
        assertEquals(result01.toString(), 0, result01.size());

        final List<ValidationResult> result02 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode(minDate), caseField);
        assertEquals(result02.toString(), 0, result02.size());

        final List<ValidationResult> result03 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode(maxDate), caseField);
        assertEquals(result03.toString(), 0, result03.size());

        final List<ValidationResult> result04 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode(invalidMinDate), caseField);
        assertEquals("Did not catch invalid min-date", 1, result04.size());

        final List<ValidationResult> result05 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode(invalidMaxDate), caseField);
        assertEquals("Did not catch invalid max-date", 1, result05.size());
    }

    @Test
    public void invalidFieldTypeRegEx() {
        final String validDate = "2001-12-10Z";

        final CaseField caseField = caseField().withMin(date("2001-01-01"))
                                               .withMax(date("2001-12-10Z"))
                                               .withRegExp("InvalidRegEx")
                                               .build();
        final List<ValidationResult> result = validator.validate(FIELD_ID,
                                                                 NODE_FACTORY.textNode(validDate), caseField);
        assertEquals("RegEx validation failed", 1, result.size());
        assertEquals("2001-12-10Z Field Type Regex Failed:InvalidRegEx", result.get(0).getErrorMessage());
        assertEquals(FIELD_ID, result.get(0).getFieldId());
    }

    @Test
    public void invalidBaseTypeRegEx() {
        ReflectionTestUtils.setField(validator.getType(), "regularExpression", "InvalidRegEx");

        final CaseField caseField = caseField().build();
        final List<ValidationResult> result = validator.validate(FIELD_ID,
                                                                 NODE_FACTORY.textNode("2001-12-10"), caseField);
        assertEquals("RegEx validation failed", 1, result.size());
        assertEquals("2001-12-10 Date Type Regex Failed:InvalidRegEx", result.get(0).getErrorMessage());
        assertEquals(FIELD_ID, result.get(0).getFieldId());
    }

    @Test
    public void validRegEx() {
        final String validDate = "2001-12-10";
        final CaseField caseField = caseField().withRegExp(LIMITED_REGEX).build();

        final List<ValidationResult> result = validator.validate(FIELD_ID,
                                                                 NODE_FACTORY.textNode(validDate), caseField);
        assertEquals("RegEx validation failed", 0, result.size());
    }

    private BigDecimal date(final String dateString) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return new BigDecimal(df.parse(dateString).getTime());
        } catch (ParseException e) {
            throw new AssertionError(e);
        }
    }

    private CaseFieldBuilder caseField() {
        return new CaseFieldBuilder(FIELD_ID).withType(DateValidator.TYPE_ID);
    }
}

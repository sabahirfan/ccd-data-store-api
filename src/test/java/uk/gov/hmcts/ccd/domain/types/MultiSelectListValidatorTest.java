package uk.gov.hmcts.ccd.domain.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;

public class MultiSelectListValidatorTest {

    private static final JsonNodeFactory NODE_FACTORY = new JsonNodeFactory(Boolean.FALSE);
    private static final String FIELD_ID = "MultiSelectList1";
    private static final String OPTION_1 = "Option1";
    private static final String OPTION_2 = "Option2";
    private static final String OPTION_3 = "Option3";
    private static final String OPTION_UNKNOWN = "OptionUnknown";

    @Mock
    private BaseType multiSelectBaseType;

    @Mock
    private CaseDefinitionRepository definitionRepository;

    private CaseField caseField;

    private MultiSelectListValidator validator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        doReturn(Collections.emptyList()).when(definitionRepository).getBaseTypes();
        BaseType.setCaseDefinitionRepository(definitionRepository);
        BaseType.initialise();

        doReturn(MultiSelectListValidator.TYPE_ID).when(multiSelectBaseType).getType();
        BaseType.register(multiSelectBaseType);

        validator = new MultiSelectListValidator();

        caseField = caseField().build();
    }

    @Test
    public void getType() {
        assertThat(validator.getType(), is(BaseType.get("MultiSelectList")));
    }

    @Test
    public void validate_shouldBeValidWhenNull() {
        final List<ValidationResult> results = validator.validate(FIELD_ID, null, caseField);

        assertThat(results, is(emptyCollectionOf(ValidationResult.class)));
    }

    @Test
    public void validate_shouldBeValidWhenNullNode() {
        final List<ValidationResult> results = validator.validate(FIELD_ID, NODE_FACTORY.nullNode(), caseField);

        assertThat(results, is(emptyCollectionOf(ValidationResult.class)));
    }

    @Test
    public void validate_shouldBeValidWhenArrayOfValidValues() {
        final ArrayNode values = NODE_FACTORY.arrayNode()
                                             .add(OPTION_1)
                                             .add(OPTION_2);

        final List<ValidationResult> results = validator.validate(FIELD_ID, values, caseField);

        assertThat(results, is(emptyCollectionOf(ValidationResult.class)));
    }

    @Test
    public void validate_shouldNOTBeValidWhenValueIsNotAnArray() {
        final JsonNode value = NODE_FACTORY.textNode("Nayab was here, 24/07/2017");

        final List<ValidationResult> results = validator.validate(FIELD_ID, value, caseField);

        assertThat(results, hasSize(1));
    }

    @Test
    public void validate_shouldNOTBeValidWhenContainsUnknownValue() {
        final ArrayNode values = NODE_FACTORY.arrayNode()
                                             .add(OPTION_1)
                                             .add(OPTION_UNKNOWN);

        final List<ValidationResult> results = validator.validate(FIELD_ID, values, caseField);

        assertThat(results, hasSize(1));
    }

    @Test
    public void validate_shouldNOTBeValidWhenContainsDuplicateValues() {
        final ArrayNode values = NODE_FACTORY.arrayNode()
                                             .add(OPTION_1)
                                             .add(OPTION_1);

        final List<ValidationResult> results = validator.validate(FIELD_ID, values, caseField);

        assertThat(results, hasSize(1));
    }

    @Test
    public void validate_shouldNOTBeValidWhenBelowMin() {
        final CaseField caseField = caseField().withMin(2).build();

        final ArrayNode values = NODE_FACTORY.arrayNode()
                                             .add(OPTION_1);

        final List<ValidationResult> results = validator.validate(FIELD_ID, values, caseField);

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getErrorMessage(), equalTo("Select at least 2 options"));
    }

    @Test
    public void validate_shouldNOTBeValidWhenAboveMax() {
        final CaseField caseField = caseField().withMax(1).build();

        final ArrayNode values = NODE_FACTORY.arrayNode()
                                             .add(OPTION_1)
                                             .add(OPTION_2);

        final List<ValidationResult> results = validator.validate(FIELD_ID, values, caseField);

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getErrorMessage(), equalTo("Cannot select more than 1 option"));
    }

    private CaseFieldBuilder caseField() {
        return new CaseFieldBuilder(FIELD_ID).withType(MultiSelectListValidator.TYPE_ID)
                                             .withFixedListItem(OPTION_1)
                                             .withFixedListItem(OPTION_2)
                                             .withFixedListItem(OPTION_3);
    }
}

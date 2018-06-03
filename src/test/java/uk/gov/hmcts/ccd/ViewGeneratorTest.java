package uk.gov.hmcts.ccd;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import org.apache.tomcat.jni.Local;
import org.junit.Test;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldType;
import uk.gov.hmcts.ccd.types.cmc.MadeBy;
import uk.gov.hmcts.ccd.types.cmc.Offer;
import uk.gov.hmcts.ccd.types.cmc.Settlement;
import uk.gov.hmcts.ccd.types.fields.*;
import uk.gov.hmcts.ccd.types.model.FakeCase;
import uk.gov.hmcts.ccd.types.model.FakeView;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ViewGeneratorTest {
    @Test
    public void extractsFields() {
        FakeView view = new FakeView();
        FakeCase fakeCase = new FakeCase("A", "B");
        List<Object> fields = view.render(fakeCase);
        assertThat(fields.size()).isEqualTo(2);
    }

    @Test
    public void convertsString() {
        CaseField field = ReflectionUtils.convert("hello");
        assertThat(field.getFieldType().getType()).isEqualTo("Text");
        assertThat(field.getValue().isTextual()).isTrue();
    }

    @Test
    public void convertsLabels() {
        CaseField field = ReflectionUtils.convert(new WithLabels());
        assertThat(field.getLabel()).isEqualTo("parent");
        assertThat(field.getFieldType().getComplexFields().get(0).getLabel()).isEqualTo("child");
    }

    @Test
    public void convertsComplex() {
        CaseField field = ReflectionUtils.convert(new Address());
        assertThat(field.getFieldType().getType()).isEqualTo("Complex");
        assertThat(field.getFieldType().getComplexFields().get(0).getValue().toString()).contains("test line 1");
    }

    @Test
    public void convertsStringList() throws JsonProcessingException {
        CaseField field = ReflectionUtils.convert((Object) Lists.newArrayList("Hello", "World"));
        assertThat(field.getFieldType().getType()).isEqualTo("Collection");
        CCDCollectionEntry[] values = new ObjectMapper().treeToValue(field.getValue(), CCDCollectionEntry[].class);
        assertThat(values.length).isEqualTo(2);
    }

    @Test
    public void convertsHasStringList() throws JsonProcessingException {
        CaseField field = ReflectionUtils.convert(new HasStringList());
        assertThat(field.getFieldType().getType()).isEqualTo("Complex");
        CaseField collection = field.getFieldType().getComplexFields().get(0);
        assertThat(collection.getFieldType().getType()).isEqualTo("Collection");
        CCDCollectionEntry[] values = new ObjectMapper().treeToValue(collection.getValue(), CCDCollectionEntry[].class);
        assertThat(values.length).isEqualTo(2);
    }

    @Test
    public void convertsComplexList() throws JsonProcessingException {
        CaseField field = ReflectionUtils.convert((Object)Lists.newArrayList(new Address(), new Address()));
        FieldType type = field.getFieldType();
        assertThat(type.getType()).isEqualTo("Collection");
        FieldType collectionType = type.getCollectionFieldType();
        assertThat(collectionType.getType()).isEqualTo("Complex");
        CaseField leaf = collectionType.getComplexFields().get(0);
        assertThat(leaf.getFieldType().getType()).isEqualTo("Text");
        CCDCollectionEntry[] values = new ObjectMapper().treeToValue(field.getValue(), CCDCollectionEntry[].class);
        assertThat(values.length).isEqualTo(2);
        LinkedHashMap l = (LinkedHashMap) values[0].value;
        assertThat(l.get("line1")).isEqualTo("test line 1");
    }

    @Test
    public void handlesEnum() {
        HasEnum h = new HasEnum();
        CaseField s = ReflectionUtils.convert(h);
        assertThat(s.getFieldType().getType()).isEqualTo("Complex");
        assertThat(s.getFieldType().getComplexFields().get(0).getFieldType().getType()).isEqualTo("Text");
    }

    @Test
    public void handlesInt() {
        WithInt h = new WithInt();
        CaseField s = ReflectionUtils.convert(h);
        assertThat(s.getFieldType().getType()).isEqualTo("Complex");
        assertThat(s.getFieldType().getComplexFields().get(0).getFieldType().getType()).isEqualTo("Number");
    }

    @Test
    public void handlesCMCType() {
        Settlement s = new Settlement();
        Offer offer = new Offer("Some offer details", LocalDate.now());
        s.makeOffer(offer, MadeBy.CLAIMANT);
        CaseField field = ReflectionUtils.convert(s);
    }

    @Test
    public void serialisesDatesCorrectly() {
        LocalDate local = LocalDate.of(2009, 1, 1);
        CaseField field = ReflectionUtils.convert(local);
        assertThat(field.getValue().toString()).contains("2009-01-01");
    }
}

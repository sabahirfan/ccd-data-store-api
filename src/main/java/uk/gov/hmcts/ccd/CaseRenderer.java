package uk.gov.hmcts.ccd;

import com.google.common.collect.Lists;
import uk.gov.hmcts.ccd.definition.ICaseRenderer;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;

import java.util.List;

public class CaseRenderer implements ICaseRenderer {
    private List<CaseField> fields = Lists.newArrayList();

    @Override
    public void render(Object o) {
        render(o, "");
    }

    @Override
    public void render(Object o, String label) {
        if (null == o) {
            return;
        }
        CaseField field = ReflectionUtils.convert(o);
        if (null != field) {
            if (label != "") {
                field.setLabel(label);
            }
            fields.add(field);
            field.setOrder(0);
        }
    }

    public CaseField[] getFields() {
        return fields.toArray(new CaseField[0]);
    }
}

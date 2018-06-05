package uk.gov.hmcts.ccd.definition;

import com.google.common.collect.Maps;
import uk.gov.hmcts.ccd.ICase;

import java.util.List;
import java.util.Map;

public abstract class BaseCaseView<T extends ICase> implements ICaseView<T> {

    Map<Object, String> result = Maps.newHashMap();

    @Override
    public final Map<Object, String> render(T theCase) {
        result.clear();
        if (null != theCase) {
            onRender(theCase);
        }
        return result;
    }

    protected abstract void onRender(T theCase);

    protected void render(Object o, String label) {
        result.put(o, label);
    }

    protected void render(Object o) {
        result.put(o, "");
    }
}

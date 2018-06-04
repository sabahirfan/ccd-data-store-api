package uk.gov.hmcts.ccd.definition;

import com.google.common.collect.Lists;
import uk.gov.hmcts.ccd.ICase;

import java.util.List;

public abstract class BaseCaseView<T extends ICase> implements ICaseView<T> {

    List<Object> result = Lists.newArrayList();

    @Override
    public final List<Object> render(T theCase) {
        result.clear();
        onRender(theCase);
        return result;
    }

    protected abstract void onRender(T theCase);

    protected void render(Object o) {
        result.add(o);
    }
}

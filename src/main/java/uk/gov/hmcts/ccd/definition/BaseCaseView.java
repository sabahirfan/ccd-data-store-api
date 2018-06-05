package uk.gov.hmcts.ccd.definition;

import com.google.common.collect.Maps;
import uk.gov.hmcts.ccd.ICase;

import java.util.List;
import java.util.Map;

public abstract class BaseCaseView<T extends ICase> implements ICaseView<T> {

    private ICaseRenderer renderer;

    @Override
    public final void render(ICaseRenderer renderer, T theCase) {
        this.renderer = renderer;
        onRender(theCase);
    }

    protected abstract void onRender(T theCase);

    protected void render(Object o, String label) {
        renderer.render(o, label);
    }

    protected void render(Object o) {
        renderer.render(o);
    }
}

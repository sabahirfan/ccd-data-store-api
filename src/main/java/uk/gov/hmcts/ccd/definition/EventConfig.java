package uk.gov.hmcts.ccd.definition;

import java.util.function.BiConsumer;

public class EventConfig<T extends IEvent> {

    public final Class<T> objType;
    public final BiConsumer<String, T> handler;

    public EventConfig(Class<T> objType, BiConsumer<String, T> handler) {
        this.objType = objType;
        this.handler = handler;
    }
}

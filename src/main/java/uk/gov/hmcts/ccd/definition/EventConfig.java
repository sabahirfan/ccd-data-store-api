package uk.gov.hmcts.ccd.definition;

import java.util.function.BiConsumer;

public class EventConfig {

    public final Class objType;
    public final BiConsumer<String, Object> handler;

    public EventConfig(Class objType, BiConsumer<String, Object> handler) {
        this.objType = objType;
        this.handler = handler;
    }

    public static EventConfig of(Class objType, BiConsumer<String, Object> handler) {
        return new EventConfig(objType, handler);
    }
}

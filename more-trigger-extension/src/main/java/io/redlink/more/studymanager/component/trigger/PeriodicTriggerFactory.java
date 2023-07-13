package io.redlink.more.studymanager.component.trigger;

import io.redlink.more.studymanager.core.exception.ConfigurationValidationException;
import io.redlink.more.studymanager.core.factory.TriggerFactory;
import io.redlink.more.studymanager.core.properties.TriggerProperties;
import io.redlink.more.studymanager.core.properties.model.IntegerValue;
import io.redlink.more.studymanager.core.properties.model.Value;
import io.redlink.more.studymanager.core.sdk.MoreTriggerSDK;

import java.util.List;

public class PeriodicTriggerFactory extends TriggerFactory<PeriodicTrigger, TriggerProperties> {

    static final String PERIOD_IN_SECONDS = "periodInSec";

    private static final List<Value> PROPERTIES = List.of(
            new IntegerValue(PERIOD_IN_SECONDS)
                    .setMax(59)
                    .setMin(1)
                    .setName("Period (s)")
                    .setDescription("Period in seconds, between 1 and 59")
                    .setDefaultValue(10)
                    .setRequired(true)
    );

    @Override
    public String getId() {
        return "more-blueprint-periodic-trigger";
    }

    @Override
    public String getTitle() {
        return "Periodic Trigger";
    }

    @Override
    public String getDescription() {
        return "Triggers periodically and chooses a participant randomly";
    }

    @Override
    public List<Value> getProperties() {
        return PROPERTIES;
    }

    @Override
    public PeriodicTrigger create(MoreTriggerSDK sdk, TriggerProperties properties) throws ConfigurationValidationException {
        return new PeriodicTrigger(sdk, validate(properties));
    }
}

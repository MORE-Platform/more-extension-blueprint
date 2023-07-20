package io.redlink.more.studymanager.component.observation;

import io.redlink.more.studymanager.core.exception.ConfigurationValidationException;
import io.redlink.more.studymanager.core.factory.ObservationFactory;
import io.redlink.more.studymanager.core.measurement.Measurement;
import io.redlink.more.studymanager.core.measurement.MeasurementSet;
import io.redlink.more.studymanager.core.properties.ObservationProperties;
import io.redlink.more.studymanager.core.properties.model.StringValue;
import io.redlink.more.studymanager.core.properties.model.Value;
import io.redlink.more.studymanager.core.sdk.MoreObservationSDK;

import java.util.List;
import java.util.Set;

public class PushButtonObservationFactory extends ObservationFactory<PushButtonObservation, ObservationProperties> {

    private static final List<Value> PROPERTIES = List.of(
            new StringValue("buttonText")
                    .setDefaultValue("Click Me!")
                    .setRequired(true)
                    .setName("Button Text")
                    .setDescription("Value is displayed on the app button.")
    );
    @Override
    public PushButtonObservation create(MoreObservationSDK sdk, ObservationProperties properties) throws ConfigurationValidationException {
        return new PushButtonObservation(sdk, validate(properties));
    }

    @Override
    public MeasurementSet getMeasurementSet() {
        return new MeasurementSet("RANDOM_INT", Set.of(
                new Measurement("i", Measurement.Type.INTEGER)
        ));
    }

    @Override
    public String getId() {
        return "push-button-observation";
    }

    @Override
    public String getTitle() {
        return "Push Button";
    }

    @Override
    public String getDescription() {
        return "Enables manually send random integer values on button click.";
    }

    @Override
    public List<Value> getProperties() {
        return PROPERTIES;
    }

    @Override
    public Boolean getHidden() {
        return false;
    }
}

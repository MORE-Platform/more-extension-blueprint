package io.redlink.more.studymanager.component.observation;

import io.redlink.more.studymanager.core.component.Observation;
import io.redlink.more.studymanager.core.exception.ConfigurationValidationException;
import io.redlink.more.studymanager.core.properties.ObservationProperties;
import io.redlink.more.studymanager.core.sdk.MoreObservationSDK;

public class PushButtonObservation extends Observation<ObservationProperties> {
    protected PushButtonObservation(MoreObservationSDK sdk, ObservationProperties properties) throws ConfigurationValidationException {
        super(sdk, properties);
    }
}

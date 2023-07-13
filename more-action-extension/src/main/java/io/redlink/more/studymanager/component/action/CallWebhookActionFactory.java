// package of extension components must start with `io.redlink.more.studymanager.component`
package io.redlink.more.studymanager.component.action;

import io.redlink.more.studymanager.core.exception.ConfigurationValidationException;
import io.redlink.more.studymanager.core.factory.ActionFactory;
import io.redlink.more.studymanager.core.properties.ActionProperties;
import io.redlink.more.studymanager.core.properties.model.StringValue;
import io.redlink.more.studymanager.core.properties.model.Value;
import io.redlink.more.studymanager.core.sdk.MoreActionSDK;

import java.util.List;

public class CallWebhookActionFactory extends ActionFactory<CallWebhookAction, ActionProperties> {

    static final String WEBHOOK_URL_IDENTIFIER = "webhookUrl";

    //There is a list of properties available (StringValue, IntegerValue etc.) that you can use.
    //The values describe, how properties are named and validated. They also determine how properties
    //can be set in the FE.
    //In this case, the web url allows user to define the webhook endpoint
    private static final List<Value> PROPERTIES = List.of(
            new StringValue(WEBHOOK_URL_IDENTIFIER)
                    .setName("Webhook URL")
                    .setDescription("A URL that is called with an HTTP Post.")
                    .setRequired(true)
    );

    //The id of the component, must be unique within the Studymanager Backend
    public String getId() {
        return "more-blueprint-action-call-webhook";
    }

    //Title and description are displayed on the FE
    public String getTitle() {
        return "Call Webhook";
    }

    public String getDescription() {
        return "Sends a post to an http endpoint with json body {\"sid\":<studyId>,\"pid\":<participantId}.";
    }

    @Override
    public List<Value> getProperties() {
        return PROPERTIES;
    }

    // The factory is a long living, singleton component. The component itself is short living and only created on demand
    public CallWebhookAction create(MoreActionSDK sdk, ActionProperties properties) throws ConfigurationValidationException {
        return new CallWebhookAction(sdk, validate(properties));
    }
}

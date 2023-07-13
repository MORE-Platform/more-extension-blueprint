package io.redlink.more.studymanager.component.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.redlink.more.studymanager.core.component.Action;
import io.redlink.more.studymanager.core.exception.ConfigurationValidationException;
import io.redlink.more.studymanager.core.io.ActionParameter;
import io.redlink.more.studymanager.core.properties.ActionProperties;
import io.redlink.more.studymanager.core.sdk.MoreActionSDK;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CallWebhookAction extends Action<ActionProperties> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallWebhookAction.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    //Components are short living, so storing in local fields gets lost! Nevertless the sdk provides you some functionalities
    //to persist and retrieve values. In addition, the sdk provides, based on the type of the component useful functions, like
    //send push notifications to participants, schedule things, etc.
    protected CallWebhookAction(MoreActionSDK sdk, ActionProperties properties) throws ConfigurationValidationException {
        super(sdk, properties);
    }

    //Execution of an action is called, when the trigger of the corresponding intervention emits trigger results
    //Action parameters contain at least studyId and participantId
    public void execute(ActionParameter parameter) {

        //in this case, the action takes the data from the action params and sends it as POST to a defined web url.
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost(properties.getString(CallWebhookActionFactory.WEBHOOK_URL_IDENTIFIER));

            final Map<String, Object> jsonMap =
                    Map.of("pid", parameter.getParticipantId(), "sid", parameter.getStudyId());

            final StringEntity entity = new StringEntity(MAPPER.writeValueAsString(jsonMap));
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", "application/json");

            client.execute(httpPost);

            LOGGER.info("Calling webhook was successful");
        } catch (Exception e) {
            LOGGER.error("Calling webhook failed", e);
        }
    }
}

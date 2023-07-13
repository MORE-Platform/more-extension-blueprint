package io.redlink.more.studymanager.component.trigger;

import io.redlink.more.studymanager.core.component.Trigger;
import io.redlink.more.studymanager.core.exception.ConfigurationValidationException;
import io.redlink.more.studymanager.core.io.ActionParameter;
import io.redlink.more.studymanager.core.io.Parameters;
import io.redlink.more.studymanager.core.io.TriggerResult;
import io.redlink.more.studymanager.core.properties.TriggerProperties;
import io.redlink.more.studymanager.core.sdk.MorePlatformSDK;
import io.redlink.more.studymanager.core.sdk.MoreTriggerSDK;
import io.redlink.more.studymanager.core.sdk.schedule.CronSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class PeriodicTrigger extends Trigger<TriggerProperties> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicTrigger.class);

    protected PeriodicTrigger(MoreTriggerSDK sdk, TriggerProperties properties) throws ConfigurationValidationException {
        super(sdk, properties);
    }

    //when the study is started or restarted, the activation is called once per configured instance
    @Override
    public void activate() {
        String cron = "0/"+properties.getInt(PeriodicTriggerFactory.PERIOD_IN_SECONDS)+" * * * * ?";

        //the sdk allows to set a periodic schedule that calls the execute method
        String schedule = sdk.addSchedule(new CronSchedule(cron));

        //the sdk allows also to store data persistent; each component instance has its own namespace
        sdk.setValue("scheduleId", schedule);
        LOGGER.info("Activated periodic trigger for study {}", sdk.getStudyId());
    }

    //when the study is stopped or paused, the activation is called once per configured instance
    @Override
    public void deactivate() {
        sdk.getValue("scheduleId", String.class).ifPresent(sdk::removeSchedule);
        LOGGER.info("Deactivated periodic trigger for study {}", sdk.getStudyId());
    }

    @Override
    public TriggerResult execute(Parameters parameters) {
        //the sdk is used to list the study participants
        Set<Integer> participants = sdk.participantIds(MorePlatformSDK.ParticipantFilter.ALL);

        Integer pid = participants.stream()
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .findAny()
                .orElse(null);

        LOGGER.info("Execute periodic trigger on study {} - call action with participant {}", sdk.getStudyId(), pid);

        //of no participant is selected, no action will be called
        if(pid == null) {
            return TriggerResult.NOOP;
        } else {
            //the trigger can trigger 0 to n action executions
            return TriggerResult.withParams(
                    Set.of(new ActionParameter(sdk.getStudyId(), pid))
            );
        }
    }
}

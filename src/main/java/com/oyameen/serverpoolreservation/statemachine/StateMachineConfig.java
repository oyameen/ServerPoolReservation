package com.oyameen.serverpoolreservation.statemachine;


import com.oyameen.serverpoolreservation.model.Server;
import com.oyameen.serverpoolreservation.model.ServerStatus;
import com.oyameen.serverpoolreservation.repositories.ServerPoolRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import static com.oyameen.serverpoolreservation.constants.ServerPoolConstants.timeToActivateServerInMills;
import static com.oyameen.serverpoolreservation.statemachine.LoggingUtils.LOGGER;
import static com.oyameen.serverpoolreservation.statemachine.LoggingUtils.getStateInfo;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<ServerStatus, ServerEvent> {
    final ServerPoolRepository serverPoolRepository;

    public StateMachineConfig(ServerPoolRepository serverPoolRepository) {
        this.serverPoolRepository = serverPoolRepository;
    }

    @Override
    public void configure(StateMachineStateConfigurer<ServerStatus, ServerEvent> states) throws Exception {
        states.withStates().initial(ServerStatus.CREATING, entryAction())
                .state(ServerStatus.ACTIVE, entryAction())
                .end(ServerStatus.ACTIVE);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ServerStatus, ServerEvent> transitions) throws Exception {

        transitions.withInternal()
                .source(ServerStatus.CREATING)
                .action(myAction())
                .timerOnce(timeToActivateServerInMills);
    }


    private Action<ServerStatus, ServerEvent> myAction() {
        return ctx -> {
            Long id = Long.valueOf((ctx.getStateMachine().getId()));
            Server value = serverPoolRepository.findById(id).get();
            value.setServerStatus(ServerStatus.ACTIVE);
            serverPoolRepository.activate(value);
        };
    }


    @Bean
    public Action<ServerStatus, ServerEvent> entryAction() {
        return ctx -> LOGGER.info("Entry action {} to get from {} to {}",
                ctx.getEvent(),
                getStateInfo(ctx.getSource()),
                getStateInfo(ctx.getTarget()));
    }

}

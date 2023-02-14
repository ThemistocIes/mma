package org.home.mma.worker.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.home.mma.store.entities.SendingEmailTaskEntity;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Component
public class SendingEmailTaskListener {

    ObjectMapper mapper = new ObjectMapper();
    public static final String SENDING_EMAIL_TASKS_QUEUE = "org.home.mma.sending.email.tasks.queue";
    public static final String SENDING_EMAIL_TASKS_EXCHANGE = "org.home.mma.sending.email.tasks.exchange";

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = SENDING_EMAIL_TASKS_QUEUE),
            exchange = @Exchange(value = SENDING_EMAIL_TASKS_EXCHANGE)))
    public void handleSendingEmailTask(Long sendingEmailTaskId) {
        log.info(mapper.writeValueAsString("RabbitListener: sendingEmailTaskId: " + sendingEmailTaskId));
    }
}

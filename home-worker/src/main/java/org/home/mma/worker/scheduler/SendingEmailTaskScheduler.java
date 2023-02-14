package org.home.mma.worker.scheduler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.home.mma.store.dao.SendingEmailTaskDao;
import org.home.mma.store.entities.SendingEmailTaskEntity;
import org.home.mma.worker.rabbitmq.SendingEmailTaskListener;
import org.home.mma.worker.service.SendingEmailTaskClientApi;
import org.home.mma.worker.service.RedisLockWrapper;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class SendingEmailTaskScheduler {

    private static final String SENDING_EMAIL_TASK_KEY_FORMAT = "org:home:mma:worker:scheduler:sending:email:task:%s";
    SendingEmailTaskDao sendingEmailTaskDao;
    SendingEmailTaskClientApi sendingEmailTaskClientApi;
    RedisLockWrapper redisLockWrapper;
    RabbitMessagingTemplate rabbitMessagingTemplate;

    @Scheduled(cron = "*/5 * * * * *")
    public void executeSendingEmailTasks() {

        log.debug("Checking for the presence of unprocessed tasks...");

        List<Long> sendingEmailTaskIds = sendingEmailTaskDao.findNotProcessedIds();

        for (Long sendingEmailTaskId : sendingEmailTaskIds) {
            rabbitMessagingTemplate.convertAndSend(SendingEmailTaskListener.SENDING_EMAIL_TASKS_EXCHANGE,
                            null,
                            sendingEmailTaskId);
            String sendingEmailTaskKey = getSendingEmailTaskKey(sendingEmailTaskId);
            redisLockWrapper.lockAndExecuteTask(sendingEmailTaskKey, Duration.ofSeconds(5),
                    () -> sendEmail(sendingEmailTaskId));
        }
    }

    private void sendEmail(Long sendingEmailTaskId) {

        Optional<SendingEmailTaskEntity> optionalSendingEmailTask = sendingEmailTaskDao.findNotProcessedById(sendingEmailTaskId);

        if (optionalSendingEmailTask.isEmpty()) {
            log.info(String.format("Task %s is already processed.", sendingEmailTaskId));
            return;
        }

        SendingEmailTaskEntity sendingEmailTask = optionalSendingEmailTask.get();

        String destination = sendingEmailTask.getDestination();
        String message = sendingEmailTask.getMessage();

        boolean isDelivered = sendingEmailTaskClientApi.isEmailDelivered(destination, message);

        if (isDelivered) {
            log.debug(String.format("Task %d has been processed", sendingEmailTask.getId()));
            sendingEmailTaskDao.markAsProcessed(sendingEmailTask);
            return;
        }

        log.warn(String.format("Task %d has been returned for processing", sendingEmailTask.getId()));
        sendingEmailTaskDao.updateLatestTryAt(sendingEmailTask);
    }

    private static String getSendingEmailTaskKey(Long taskId) {
        return String.format(SENDING_EMAIL_TASK_KEY_FORMAT, taskId);
    }
}

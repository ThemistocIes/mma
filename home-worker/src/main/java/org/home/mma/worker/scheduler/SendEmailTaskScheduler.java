package org.home.mma.worker.scheduler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.home.mma.store.dao.SendEmailTaskDao;
import org.home.mma.store.entities.SendEmailTaskEntity;
import org.home.mma.worker.service.EmailClientApi;
import org.home.mma.worker.service.RedisLockWrapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class SendEmailTaskScheduler {

    private static final String SEND_EMAIL_TASK_KEY_FORMAT = "home:send:email:task:%s";
    SendEmailTaskDao sendEmailTaskDao;
    EmailClientApi emailClientApi;
    RedisLockWrapper redisLockWrapper;

    @Scheduled(cron = "*/5 * * * * *")
    public void executeSendEmailTasks() {

        List<Long> sendEmailTaskIds = sendEmailTaskDao.findNotProcessedIds();

        for (Long sendEmailTaskId : sendEmailTaskIds) {
            String sendEmailTaskKey = getSendEmailTaskKey(sendEmailTaskId);
            redisLockWrapper.lockAndExecuteTask(sendEmailTaskKey, Duration.ofSeconds(5), () -> sendEmail(sendEmailTaskId));
        }
    }

    private void sendEmail(Long sendEmailTaskId) {

        Optional<SendEmailTaskEntity> optionalSendEmailTask = sendEmailTaskDao.findNotProcessedById(sendEmailTaskId);

        if (optionalSendEmailTask.isEmpty()) {
            log.info(String.format("Task %s is already processed.", sendEmailTaskId));
            return;
        }

        SendEmailTaskEntity sendEmailTask = optionalSendEmailTask.get();

        String destination = sendEmailTask.getDestination();
        String message = sendEmailTask.getMessage();

        boolean isDelivered = emailClientApi.isEmailDelivered(destination, message);

        if (isDelivered) {
            log.debug(String.format("Task %d is processed.", sendEmailTask.getId()));
            sendEmailTaskDao.markAsProcessed(sendEmailTask);
            return;
        }

        log.warn(String.format("Task %d has been returned to process.", sendEmailTask.getId()));
        sendEmailTaskDao.updateLatestTryAt(sendEmailTask);
    }

    private static String getSendEmailTaskKey(Long taskId) {
        return String.format(SEND_EMAIL_TASK_KEY_FORMAT, taskId);
    }
}

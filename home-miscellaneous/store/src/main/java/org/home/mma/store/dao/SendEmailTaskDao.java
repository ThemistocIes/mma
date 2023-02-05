package org.home.mma.store.dao;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.home.mma.store.entities.SendEmailTaskEntity;
import org.home.mma.store.repositories.SendEmailTaskRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class SendEmailTaskDao {
    SendEmailTaskRepository sendEmailTaskRepository;
    private static final Duration TASK_DURATION_EXECUTION = Duration.ofSeconds(10);
    @Transactional
    public SendEmailTaskEntity save(SendEmailTaskEntity entity) {
        return sendEmailTaskRepository.save(entity);
    }

    public List<Long> findNotProcessedIds() {

        Instant latestTryAtGTE = Instant.now().minus(TASK_DURATION_EXECUTION);

        return sendEmailTaskRepository.findAllNotProcessed(latestTryAtGTE);
    }

    public Optional<SendEmailTaskEntity> findNotProcessedById(Long sendEmailTaskId) {

        Instant latestTryAtLTE = Instant.now().minus(TASK_DURATION_EXECUTION);

        return sendEmailTaskRepository.findNotProcessedById(sendEmailTaskId, latestTryAtLTE);
    }

    @Transactional
    public void markAsProcessed(SendEmailTaskEntity sendEmailTask) {
        sendEmailTaskRepository.markAsProcessed(sendEmailTask.getId());
    }

    @Transactional
    public void updateLatestTryAt(SendEmailTaskEntity sendEmailTask) {
        sendEmailTaskRepository.updateLatestTryAt(sendEmailTask.getId());
    }
}

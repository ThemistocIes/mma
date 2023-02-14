package org.home.mma.store.dao;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.home.mma.store.entities.SendingEmailTaskEntity;
import org.home.mma.store.repositories.SendingEmailTaskRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class SendingEmailTaskDao {
    SendingEmailTaskRepository sendingEmailTaskRepository;
    private static final Duration TASK_DURATION_EXECUTION = Duration.ofSeconds(10);
    @Transactional
    public SendingEmailTaskEntity save(SendingEmailTaskEntity entity) {
        return sendingEmailTaskRepository.save(entity);
    }

    public List<Long> findNotProcessedIds() {

        Instant latestTryAtGTE = Instant.now().minus(TASK_DURATION_EXECUTION);

        return sendingEmailTaskRepository.findAllNotProcessed(latestTryAtGTE);
    }

    public Optional<SendingEmailTaskEntity> findNotProcessedById(Long sendingEmailTaskId) {

        Instant latestTryAtLTE = Instant.now().minus(TASK_DURATION_EXECUTION);

        return sendingEmailTaskRepository.findNotProcessedById(sendingEmailTaskId, latestTryAtLTE);
    }

    @Transactional
    public void markAsProcessed(SendingEmailTaskEntity sendingEmailTask) {
        sendingEmailTaskRepository.markAsProcessed(sendingEmailTask.getId());
    }

    @Transactional
    public void updateLatestTryAt(SendingEmailTaskEntity sendingEmailTask) {
        sendingEmailTaskRepository.updateLatestTryAt(sendingEmailTask.getId());
    }
}

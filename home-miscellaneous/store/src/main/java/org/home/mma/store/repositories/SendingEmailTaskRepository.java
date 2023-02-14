package org.home.mma.store.repositories;

import org.home.mma.store.entities.SendingEmailTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SendingEmailTaskRepository extends JpaRepository<SendingEmailTaskEntity, Long> {

    @Query("" +
            "SELECT task.id " +
            "FROM SendingEmailTaskEntity task " +
            "WHERE task.processedAt IS NULL " +
            "AND (task.latestTryAt IS NULL OR task.latestTryAt <= ?1) " +
            "ORDER BY task.createdAt" +
            "")
    List<Long> findAllNotProcessed(Instant latestTryAtLTE);

    @Query("" +
            "SELECT task " +
            "FROM SendingEmailTaskEntity task " +
            "WHERE task.id = ?1 " +
            "AND task.processedAt IS NULL " +
            "AND (task.latestTryAt IS NULL OR task.latestTryAt <= ?2)" +
            "")
    Optional<SendingEmailTaskEntity> findNotProcessedById(Long id, Instant latestTryAtLTE);

    @Modifying
    @Query("" +
            "UPDATE SendingEmailTaskEntity task " +
            "SET task.processedAt = current_timestamp " +
            "WHERE task.id = ?1" +
            "")
    void markAsProcessed(Long id);

    @Modifying
    @Query("" +
            "UPDATE SendingEmailTaskEntity task " +
            "SET task.latestTryAt = current_timestamp " +
            "WHERE task.id = ?1" +
            "")
    void updateLatestTryAt(Long id);
}

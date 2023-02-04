package org.home.mma.store.repositories;

import org.home.mma.store.entities.SendEmailTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SendEmailTaskRepository extends JpaRepository<SendEmailTaskEntity, Long> {

    @Query("" +
            "SELECT task.id " +
            "FROM SendEmailTaskEntity task " +
            "WHERE task.processedAt IS NULL " +
            "ORDER BY task.createdAt" +
            "")
    List<Long> findAllNotProcessed();

    @Query("" +
            "SELECT task " +
            "FROM SendEmailTaskEntity task " +
            "WHERE task.id = ?1 " +
            "AND task.processedAt IS NULL" +
            "")
    Optional<SendEmailTaskEntity> findNotProcessedById(Long id);

    @Modifying
    @Query("" +
            "UPDATE SendEmailTaskEntity task " +
            "SET task.processedAt = current_timestamp " +
            "WHERE task.id = ?1" +
            "")
    void markAsProcessed(Long id);

    @Modifying
    @Query("" +
            "UPDATE SendEmailTaskEntity task " +
            "SET task.latestTryAt = current_timestamp " +
            "WHERE task.id = ?1" +
            "")
    void updateLatestTryAt(Long id);
}
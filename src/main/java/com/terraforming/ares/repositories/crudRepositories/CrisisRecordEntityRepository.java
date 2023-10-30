package com.terraforming.ares.repositories.crudRepositories;

import com.terraforming.ares.entity.CrisisRecordEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by oleksii.nikitin
 * Creation date 17.05.2022
 */
public interface CrisisRecordEntityRepository extends CrudRepository<CrisisRecordEntity, Long> {

    CrisisRecordEntity findByUuid(String uuid);

    @Query(value = "SELECT * FROM crisis_record_entity m WHERE player_count = ? " +
            "ORDER BY difficulty DESC, (m.victory_points + m.terraforming_points) DESC, date, turns_left DESC LIMIT 20", nativeQuery = true)
    List<CrisisRecordEntity> findTopTwentyRecordsByPoints(int playerCount);

    @Query(value = "SELECT * FROM crisis_record_entity m WHERE player_count = ? " +
            "ORDER BY difficulty DESC, turns_left DESC, (m.victory_points + m.terraforming_points) DESC, date LIMIT 20", nativeQuery = true)
    List<CrisisRecordEntity> findTopTwentyRecordsByTurns(int playerCount);


    @Modifying
    @Query(value = "DELETE FROM crisis_record_entity m " +
            "WHERE uuid NOT IN (" +
            "    SELECT uuid" +
            "    FROM (" +
            "        SELECT uuid" +
            "        FROM crisis_record_entity" +
            "        ORDER BY difficulty DESC, turns_left DESC, (m.victory_points + m.terraforming_points) DESC, date LIMIT 20" +
            "    ) AS top_turns" +
            "    UNION" +
            "    SELECT uuid" +
            "    FROM (" +
            "        SELECT uuid" +
            "        FROM crisis_record_entity" +
            "        ORDER BY difficulty DESC, (m.victory_points + m.terraforming_points) DESC, date, turns_left DESC LIMIT 20" +
            "    ) AS top_victory_points)",
            nativeQuery = true)
    void clearCrisisRecordMemory();
}

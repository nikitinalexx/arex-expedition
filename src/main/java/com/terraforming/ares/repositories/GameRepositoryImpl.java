package com.terraforming.ares.repositories;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terraforming.ares.entity.GameEntity;
import com.terraforming.ares.entity.PlayerEntity;
import com.terraforming.ares.mars.MarsGame;
import com.terraforming.ares.repositories.crudRepositories.GameEntityRepository;
import com.terraforming.ares.repositories.crudRepositories.PlayerEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by oleksii.nikitin
 * Creation date 26.04.2022
 */
@Service
@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepository {
    private final ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    private final GameEntityRepository gameRepository;
    private final PlayerEntityRepository playerRepository;

    @Override
    @Transactional
    public long save(MarsGame game) {
        System.out.println("Save by id");
        boolean newGame = (game.getId() == null);
        List<PlayerEntity> players = game.getPlayerUuidToPlayer().keySet()
                .stream()
                .map(PlayerEntity::new)
                .collect(Collectors.toList());

        GameEntity oldGame = new GameEntity(safeSerialize(game));
        oldGame.setId(game.getId());
        GameEntity savedGame = gameRepository.save(oldGame);

        if (newGame) {
            players.forEach(player -> {
                player.setGame(savedGame);
                playerRepository.save(player);
            });
        }

        game.setId(savedGame.getId());
        return savedGame.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public MarsGame getGameById(long id) {
        System.out.println("Get by id");
        GameEntity entity = gameRepository.findById(id);
        MarsGame marsGame = safeDeserialize(entity.getGameJson());
        marsGame.setId(entity.getId());
        return marsGame;
    }

    @Override
    @Transactional(readOnly = true)
    public long getGameIdByPlayerUuid(String playerUuid) {
        System.out.println("Get player by id");
        return playerRepository.findByUuid(playerUuid).getGame().getId();
    }

    private String safeSerialize(MarsGame marsGame) {
        try {
            return objectMapper.writeValueAsString(marsGame);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializing the game");
        }
    }

    private MarsGame safeDeserialize(String gameJson) {
        try {
            return objectMapper.readValue(gameJson, MarsGame.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error deserializing the game");
        }
    }


}

package com.terraforming.ares.services;

import com.terraforming.ares.factories.StateFactory;
import com.terraforming.ares.mars.MarsGame;
import com.terraforming.ares.model.GameUpdateResult;
import com.terraforming.ares.model.Player;
import com.terraforming.ares.model.TurnResponse;
import com.terraforming.ares.model.turn.Turn;
import com.terraforming.ares.model.turn.TurnType;
import com.terraforming.ares.processors.turn.TurnProcessor;
import com.terraforming.ares.repositories.caching.CachingGameRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by oleksii.nikitin
 * Creation date 25.04.2022
 */
@Service
public class GameProcessorService {
    private final CachingGameRepository gameRepository;
    private final StateFactory stateFactory;
    private final Map<TurnType, TurnProcessor<?>> turnProcessors;
    private final Queue<Long> gamesToProcess = new ArrayBlockingQueue<>(100);

    public GameProcessorService(List<TurnProcessor<?>> turnProcessor,
                                CachingGameRepository gameRepository,
                                StateFactory stateFactory) {
        this.gameRepository = gameRepository;
        this.stateFactory = stateFactory;

        turnProcessors = turnProcessor.stream().collect(Collectors.toMap(
                TurnProcessor::getType, Function.identity()
        ));
    }

    @Scheduled(fixedRate = 1000)
    public void asyncUpdate() {
        if (gamesToProcess.isEmpty()) {
            return;
        }

        Long gameId = gamesToProcess.poll();

        gameRepository.updateMarsGame(gameId, game -> null, game -> {
            if (processFinalTurns(game)) {
                stateFactory.getCurrentState(game).updateState();
            }

            return null;
        });
    }

    public GameUpdateResult<TurnResponse> syncPlayerUpdate(long gameId, Turn turn, Function<MarsGame, String> stateChecker) {
        return gameRepository.updateMarsGame(gameId, stateChecker, game -> processTurn(turn, game));
    }

    private boolean processFinalTurns(MarsGame game) {
        boolean allTurnsReadyAndAllTerminal = game.getPlayerUuidToPlayer()
                .values()
                .stream()
                .allMatch(player -> player.getNextTurn() != null && player.getNextTurn().getType().isTerminal()
                        || player.getNextTurn() == null && stateFactory.getCurrentState(game).getPossibleTurns(player.getUuid()).isEmpty()
                );

        if (!allTurnsReadyAndAllTerminal) {
            return false;
        }

        game.getPlayerUuidToPlayer().values().forEach(player -> processNextTurn(player, game));

        return true;
    }

    private void processNextTurn(Player player, MarsGame game) {
        Turn turnToProcess = player.getNextTurn();
        processTurn(turnToProcess, game);

        if (turnToProcess == player.getNextTurn()) {
            player.setNextTurn(null);
        }
    }

    @SuppressWarnings("unchecked")
    private TurnResponse processTurn(Turn turn, MarsGame game) {
        if (turn == null) {
            return null;
        }

        TurnProcessor<Turn> turnProcessor = (TurnProcessor<Turn>) turnProcessors.get(turn.getType());

        return turnProcessor.processTurn(turn, game);
    }

    public void registerAsyncGameUpdate(long gameId) {
        gamesToProcess.add(gameId);
    }

}

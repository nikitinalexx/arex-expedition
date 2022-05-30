package com.terraforming.ares.services;

import com.terraforming.ares.cards.CardMetadata;
import com.terraforming.ares.factories.StateFactory;
import com.terraforming.ares.mars.MarsGame;
import com.terraforming.ares.model.*;
import com.terraforming.ares.model.payments.Payment;
import com.terraforming.ares.model.request.ChooseCorporationRequest;
import com.terraforming.ares.model.turn.*;
import com.terraforming.ares.repositories.caching.CachingGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by oleksii.nikitin
 * Creation date 25.04.2022
 */
@Service
@RequiredArgsConstructor
public class TurnService {
    private static final Predicate<MarsGame> ASYNC_TURN = game -> false;
    private static final Predicate<MarsGame> SYNC_TURN = game -> true;

    private final StateFactory stateFactory;
    private final CachingGameRepository gameRepository;
    private final GameProcessorService gameProcessorService;
    private final CardValidationService cardValidationService;
    private final TerraformingService terraformingService;
    private final StandardProjectService standardProjectService;
    private final CardService cardService;

    public void chooseCorporationTurn(ChooseCorporationRequest chooseCorporationRequest) {
        String playerUuid = chooseCorporationRequest.getPlayerUuid();
        Integer corporationCardId = chooseCorporationRequest.getCorporationId();
        performTurn(
                new CorporationChoiceTurn(playerUuid, corporationCardId),
                playerUuid,
                game -> {
                    if (!game.getPlayerByUuid(playerUuid).getCorporations().containsCard(corporationCardId)) {
                        return "Can't pick corporation that is not in your choice deck";
                    }
                    return null;
                },
                ASYNC_TURN
        );
    }

    public void choosePhaseTurn(String playerUuid, int phaseId) {
        performTurn(
                new PhaseChoiceTurn(playerUuid, phaseId),
                playerUuid,
                game -> {
                    if (phaseId < 1 || phaseId > 5) {
                        return "Phase is not within [1..5] range";
                    }

                    Player player = game.getPlayerByUuid(playerUuid);
                    if (player.getPreviousChosenPhase() != null && player.getPreviousChosenPhase() == phaseId) {
                        return "This phase already picked in previous round";
                    }

                    return null;
                },
                ASYNC_TURN
        );
    }

    public void collectIncomeTurn(String playerUuid) {
        performTurn(new CollectIncomeTurn(playerUuid), playerUuid, game -> null, ASYNC_TURN);
    }

    public void skipTurn(String playerUuid) {
        performTurn(new SkipTurn(playerUuid), playerUuid, game -> null, ASYNC_TURN);
    }

    public void confirmGameEnd(String playerUuid) {
        performTurn(new GameEndConfirmTurn(playerUuid), playerUuid, game -> null, ASYNC_TURN);
    }

    public void pickExtraCardTurn(String playerUuid) {
        performTurn(new PickExtraCardTurn(playerUuid), playerUuid, game -> null, ASYNC_TURN);
    }

    public void draftCards(String playerUuid) {
        performTurn(
                new DraftCardsTurn(playerUuid),
                playerUuid,
                game -> null,
                SYNC_TURN
        );
    }

    public void plantForest(String playerUuid) {
        performTurn(
                new PlantForestTurn(playerUuid),
                playerUuid,
                game -> {
                    Player player = game.getPlayerByUuid(playerUuid);

                    if (player.getPlants() < Constants.FOREST_PLANT_COST) {
                        return "Not enough plants to create a forest";
                    }

                    return null;
                },
                SYNC_TURN
        );
    }

    public void increaseTemperature(String playerUuid) {
        performTurn(
                new IncreaseTemperatureTurn(playerUuid),
                playerUuid,
                game -> {
                    Player player = game.getPlayerByUuid(playerUuid);

                    if (player.getHeat() < Constants.TEMPERATURE_HEAT_COST) {
                        return "Not enough heat to raise temperature";
                    }

                    if (!terraformingService.canIncreaseTemperature(game)) {
                        return "Can't increase temperature anymore, already max";
                    }

                    return null;
                },
                SYNC_TURN
        );
    }

    public void standardProjectTurn(String playerUuid, StandardProjectType type) {
        performTurn(
                new StandardProjectTurn(playerUuid, type),
                playerUuid,
                game -> {
                    Player player = game.getPlayerByUuid(playerUuid);

                    return standardProjectService.validateStandardProject(game, player, type);
                },
                SYNC_TURN
        );
    }

    public void exchangeHeatRequest(String playerUuid, int value) {
        performTurn(
                new ExchangeHeatTurn(playerUuid, value),
                playerUuid,
                game -> {
                    Player player = game.getPlayerByUuid(playerUuid);

                    if (value <= 0) {
                        return "Incorrect heat value provided";
                    }

                    if (player.getHeat() < value) {
                        return "Not enough heat to perform exchange";
                    }

                    if (player.getPlayed()
                            .getCards()
                            .stream()
                            .map(cardService::getCard)
                            .map(Card::getCardMetadata)
                            .filter(Objects::nonNull)
                            .map(CardMetadata::getCardAction)
                            .filter(Objects::nonNull)
                            .noneMatch(CardAction.HELION_CORPORATION::equals)
                    ) {
                        return "Only Helion may perform heat exchange";
                    }

                    return null;
                },
                SYNC_TURN
        );
    }

    public TurnResponse sellCards(String playerUuid, List<Integer> cards) {
        return performTurn(
                new SellCardsTurn(playerUuid, cards),
                playerUuid,
                game -> {
                    if (!game.getPlayerByUuid(playerUuid).getHand().getCards().containsAll(cards)) {
                        return "Can't sell cards that you don't have";
                    }

                    return null;
                },
                SYNC_TURN
        );
    }

    public void sellCardsLastRoundTurn(String playerUuid, List<Integer> cards) {
        performTurn(
                new SellCardsLastRoundTurn(playerUuid, cards),
                playerUuid,
                game -> {
                    Player player = game.getPlayerByUuid(playerUuid);

                    if (!player.getHand().getCards().containsAll(cards)) {
                        return "Can't sell cards that you don't have";
                    }

                    if (player.getHand().size() - cards.size() > Constants.MAX_HAND_SIZE_LAST_ROUND) {
                        return "You need to discard at least "
                                + (player.getHand().size() - Constants.MAX_HAND_SIZE_LAST_ROUND)
                                + " cards";
                    }

                    return null;
                },
                ASYNC_TURN
        );
    }

    public TurnResponse discardCards(Turn turn, String playerUuid, List<Integer> cards, boolean sync) {
        Function<MarsGame, String> verifier = game -> {
            Player player = game.getPlayerByUuid(playerUuid);

            if (player.getNextTurn().getType() != TurnType.DISCARD_CARDS) {
                return "Invalid next turn. Expected " + player.getNextTurn().getType();
            }

            DiscardCardsTurn expectedTurn = (DiscardCardsTurn) player.getNextTurn();
            if (expectedTurn.getSize() != cards.size()) {
                return "Incorrect number of cards to discard, expected: " + expectedTurn.getSize();
            }

            if (!player.getHand().getCards().containsAll(cards)) {
                return "Can't discard cards that you don't have";
            }

            if (expectedTurn.isOnlyFromSelectedCards()) {
                List<Integer> expectedCardsToBeRemovedFrom = expectedTurn.getCards();
                for (Integer card : cards) {
                    if (!expectedCardsToBeRemovedFrom.contains(card)) {
                        return "You can't discard cards other than from those that you received";
                    }
                }
            }

            return null;
        };

        return performTurn(turn, playerUuid, verifier, game -> sync);
    }

    public TurnResponse buildGreenProjectCard(String playerUuid, int projectId, List<Payment> payments, Map<Integer, List<Integer>> inputParams) {
        return performTurn(
                new BuildGreenProjectTurn(playerUuid, projectId, payments, inputParams),
                playerUuid,
                game -> {
                    Player player = game.getPlayerByUuid(playerUuid);

                    return cardValidationService.validateCard(player, game, projectId, payments, inputParams);
                },
                game -> game.getCurrentPhase() == 3
        );
    }

    public TurnResponse buildBlueRedProjectCard(String playerUuid, int projectId, List<Payment> payments, Map<Integer, List<Integer>> inputParams) {
        return performTurn(
                new BuildBlueRedProjectTurn(playerUuid, projectId, payments, inputParams),
                playerUuid,
                game -> {
                    Player player = game.getPlayerByUuid(playerUuid);

                    return cardValidationService.validateCard(player, game, projectId, payments, inputParams);
                },
                game -> game.getCurrentPhase() == 3
        );
    }

    public TurnResponse performBlueAction(String playerUuid, int projectId, List<Integer> inputParams) {
        return performTurn(
                new PerformBlueActionTurn(playerUuid, projectId, inputParams),
                playerUuid,
                game -> {
                    Player player = game.getPlayerByUuid(playerUuid);

                    return cardValidationService.validateBlueAction(player, game, projectId, inputParams);
                },
                SYNC_TURN
        );
    }

    private TurnResponse performTurn(Turn turn,
                                     String playerUuid,
                                     Function<MarsGame, String> turnSpecificValidations,
                                     Predicate<MarsGame> syncTurnDecider) {
        long gameId = gameRepository.getGameIdByPlayerUuid(playerUuid);

        GameUpdateResult<TurnResponse> updateResult = gameProcessorService.performTurn(
                gameId,
                turn,
                playerUuid,
                game -> {
                    if (!stateFactory.getCurrentState(game).getPossibleTurns(playerUuid).contains(turn.getType())) {
                        return "Incorrect game state for a turn " + turn.getType();
                    }

                    return turnSpecificValidations.apply(game);
                },
                syncTurnDecider
        );

        if (updateResult.finishedWithError()) {
            throw new IllegalStateException(updateResult.getError());
        }

        return updateResult.getResult();
    }

}

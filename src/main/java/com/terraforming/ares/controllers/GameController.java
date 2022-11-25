package com.terraforming.ares.controllers;

import com.terraforming.ares.dto.*;
import com.terraforming.ares.mars.MarsGame;
import com.terraforming.ares.model.*;
import com.terraforming.ares.model.request.AllProjectsRequest;
import com.terraforming.ares.model.turn.DiscardCardsTurn;
import com.terraforming.ares.model.turn.Turn;
import com.terraforming.ares.model.turn.TurnType;
import com.terraforming.ares.repositories.GameRepositoryImpl;
import com.terraforming.ares.repositories.caching.CachingGameRepository;
import com.terraforming.ares.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by oleksii.nikitin
 * Creation date 25.04.2022
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class GameController {
    private final GameService gameService;
    private final CardFactory cardFactory;
    private final CardService cardService;
    private final WinPointsService winPointsService;
    private final CachingGameRepository cachingGameRepository;
    private final TurnService turnService;
    private final GameRepositoryImpl gameRepository;

    @PostMapping("/game/new")
    public PlayerUuidsDto startNewGame(@RequestBody GameParameters gameParameters) {
        try {
            int aiPlayerCount = (int) gameParameters.getComputers().stream().filter(item -> item).count();
            int playersCount = gameParameters.getPlayerNames().size();
            if (playersCount == 0 || playersCount > Constants.MAX_PLAYERS) {
                throw new IllegalArgumentException("Only 1 to 4 players are supported so far");
            }

//            for (int i = 0; i < 1000; i++) {
//                MarsGame marsGame = gameService.startNewGame(gameParameters);
//
//                if (aiPlayerCount == playersCount) {
//                    turnService.pushGame(marsGame.getId());
//                }
//            }

            MarsGame marsGame = gameService.startNewGame(gameParameters);

            if (aiPlayerCount == playersCount) {
                turnService.pushGame(marsGame.getId());
            }

            Map<String, Player> playerNameToPlayer = marsGame.getPlayerUuidToPlayer().values().stream()
                    .collect(Collectors.toMap(
                            Player::getName, Function.identity()
                    ));


            return PlayerUuidsDto.builder()
                    .players(gameParameters.getPlayerNames().stream()
                            .map(
                                    playerName -> PlayerReference.builder()
                                            .name(playerName)
                                            .uuid(playerNameToPlayer.get(playerName).getUuid())
                                            .build()
                            )
                            .collect(Collectors.toList())
                    )
                    .build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public void statistics() {
        List<Integer> winCardOccurenceBeforeHalf = new ArrayList<>();
        List<Integer> winCardOccurenceAfterHalf = new ArrayList<>();

        List<Integer> occurenceBeforeHalf = new ArrayList<>();
        List<Integer> occurenceAfterHalf = new ArrayList<>();

        long totalTurnsCount = 0;
        long totalPointsCount = 0;


        for (int i = 0; i < 220; i++) {
            winCardOccurenceBeforeHalf.add(0);
            winCardOccurenceAfterHalf.add(0);
            occurenceBeforeHalf.add(0);
            occurenceAfterHalf.add(0);
        }

        List<MarsGame> finishedGames = gameRepository.getAllGames().stream()
                .filter(MarsGame::gameEndCondition)
                .filter(game -> game.getPlayerUuidToPlayer().size() == 2)
                .collect(Collectors.toList());

        System.out.println("Games count: " + finishedGames.size());

        for (int i = 0; i < finishedGames.size(); i++) {
            MarsGame game = finishedGames.get(i);

            int totalTurns = game.getTurns();

            totalTurnsCount += totalTurns;

            game.getPlayerUuidToPlayer().values().forEach(
                    player -> {
                        for (Integer playedCard : player.getPlayed().getCards()) {
                            if (playedCard < 250) {
                                if (playedCard < 250) {
                                    if (player.getPlayed().getCardToTurn().get(playedCard) <= totalTurns / 2) {
                                        occurenceBeforeHalf.set(playedCard, occurenceBeforeHalf.get(playedCard) + 1);
                                    } else {
                                        occurenceAfterHalf.set(playedCard, occurenceAfterHalf.get(playedCard) + 1);
                                    }
                                }
                            }
                        }
                    }
            );


            List<Player> players = new ArrayList<>(game.getPlayerUuidToPlayer().values());
            Player firstPlayer = players.get(0);
            Player secondPlayer = players.get(1);

            int firstPlayerPoints = winPointsService.countWinPoints(firstPlayer, game);
            int secondPlayerPoints = winPointsService.countWinPoints(secondPlayer, game);

            totalPointsCount += firstPlayerPoints;
            totalPointsCount += secondPlayerPoints;

            if (firstPlayerPoints != secondPlayerPoints) {
                Player winCardsPlayer = (firstPlayerPoints > secondPlayerPoints ? firstPlayer : secondPlayer);

                for (Integer playedCard : winCardsPlayer.getPlayed().getCards()) {
                    if (playedCard < 250) {
                        if (winCardsPlayer.getPlayed().getCardToTurn().get(playedCard) <= totalTurns / 2) {
                            winCardOccurenceBeforeHalf.set(playedCard, winCardOccurenceBeforeHalf.get(playedCard) + 1);
                        } else {
                            winCardOccurenceAfterHalf.set(playedCard, winCardOccurenceAfterHalf.get(playedCard) + 1);
                        }
                    }
                }
            }
        }

        System.out.println("All");
        for (int i = 1; i < occurenceBeforeHalf.size(); i++) {
            System.out.println("i: " + i + " " + " % " + (double) winCardOccurenceBeforeHalf.get(i) * 100 / occurenceBeforeHalf.get(i) + " " + " % " + (double) winCardOccurenceAfterHalf.get(i) * 100 / occurenceAfterHalf.get(i) + " " + cardService.getCard(i).getClass().getSimpleName());

         //   System.out.println("cardToWeightFirstHalf.put(" + i + ", " + (double) winCardOccurenceBeforeHalf.get(i) * 100 / occurenceBeforeHalf.get(i) + ");");
        }

//        for (int i = 1; i < occurenceBeforeHalf.size(); i++) {
//            System.out.println("cardToWeightSecondHalf.put(" + i + ", " + (double) winCardOccurenceAfterHalf.get(i) * 100 / occurenceAfterHalf.get(i) + ");");
//        }

        System.out.println((double)totalTurnsCount / finishedGames.size());

        System.out.println((double) totalPointsCount / (2 * finishedGames.size()));
    }

    @GetMapping("/game/player/{playerUuid}")
    public GameDto getGameByPlayerUuid(@PathVariable String playerUuid) {
        MarsGame game = gameService.getGame(playerUuid);

        Planet phasePlanet = game.getPlanetAtTheStartOfThePhase();

        return GameDto.builder()
                .phase(game.getCurrentPhase())
                .player(buildCurrentPlayer(game.getPlayerByUuid(playerUuid), game))
                .temperature(game.getPlanet().getTemperatureValue())
                .phaseTemperature(phasePlanet != null ? phasePlanet.getTemperatureValue() : null)
                .phaseTemperatureColor(phasePlanet != null ? phasePlanet.getTemperatureColor() : null)
                .oxygen(game.getPlanet().getOxygenValue())
                .phaseOxygen(phasePlanet != null ? phasePlanet.getOxygenValue() : null)
                .phaseOxygenColor(phasePlanet != null ? phasePlanet.getOxygenColor() : null)
                .oceans(game.getPlanet().getRevealedOceans().stream().map(OceanDto::of).collect(Collectors.toList()))
                .phaseOceans(phasePlanet != null ? phasePlanet.getRevealedOceans().size() : null)
                .otherPlayers(buildOtherPlayers(game, playerUuid))
                .turns(game.getTurns())
                .awards(game.getAwards().stream().map(AwardDto::from).collect(Collectors.toList()))
                .milestones(game.getMilestones().stream().map(MilestoneDto::from).collect(Collectors.toList()))
                .build();
    }

    @GetMapping("/cache/reset")
    public int resetGameCache() {
        return cachingGameRepository.evictGameCache();
    }

    @GetMapping("/game/short/player/{playerUuid}")
    public GameDtoShort getShortGameByPlayerUuid(@PathVariable String playerUuid) {
        MarsGame game = gameService.getGame(playerUuid);

        Planet phasePlanet = game.getPlanetAtTheStartOfThePhase();

        return GameDtoShort.builder()
                .temperature(game.getPlanet().getTemperatureValue())
                .phaseTemperature(phasePlanet != null ? phasePlanet.getTemperatureValue() : null)
                .oxygen(game.getPlanet().getOxygenValue())
                .phaseOxygen(phasePlanet != null ? phasePlanet.getOxygenValue() : null)
                .oceans(game.getPlanet().getRevealedOceans().stream().map(OceanDto::of).collect(Collectors.toList()))
                .phaseOceans(phasePlanet != null ? phasePlanet.getRevealedOceans().size() : null)
                .otherPlayers(buildOtherPlayers(game, playerUuid))
                .build();
    }

    @PostMapping("/projects")
    public List<CardDto> getAllProjectCards(@RequestBody AllProjectsRequest request) {
        List<Card> corporations = cardFactory.getAllCorporations(request.getExpansions());
        List<Card> projects = cardFactory.getAllProjects();

        return Stream.of(corporations, projects)
                .flatMap(List::stream)
                .map(CardDto::from)
                .collect(Collectors.toList());
    }

    private List<AnotherPlayerDto> buildOtherPlayers(MarsGame game, String currentPlayerUuid) {
        return game.getPlayerUuidToPlayer().values()
                .stream()
                .filter(player -> !player.getUuid().equals(currentPlayerUuid))
                .map(player -> buildAnotherPlayer(player, game))
                .collect(Collectors.toList());
    }

    private AnotherPlayerDto buildAnotherPlayer(Player player, MarsGame game) {
        return AnotherPlayerDto.builder()
                .playerUuid(player.getUuid())
                .name(player.getName())
                .phase(player.getChosenPhase())
                .winPoints(winPointsService.countWinPoints(player, game))
                .mc(player.getMc())
                .mcIncome(player.getMcIncome())
                .cardIncome(player.getCardIncome())
                .heat(player.getHeat())
                .heatIncome(player.getHeatIncome())
                .plants(player.getPlants())
                .plantsIncome(player.getPlantsIncome())
                .steelIncome(player.getSteelIncome())
                .titaniumIncome(player.getTitaniumIncome())
                .terraformingRating(player.getTerraformingRating())
                .forests(player.getForests())
                .hand(player.getHand().getCards().stream().map(cardService::getCard).map(CardDto::from).collect(Collectors.toList()))
                .played(player.getPlayed().getCards().stream().map(cardService::getCard).map(CardDto::from).collect(Collectors.toList()))
                .cardResources(getPlayerCardResources(player))
                .build();
    }

    private PlayerDto buildCurrentPlayer(Player player, MarsGame game) {
        Deck corporations = player.getCorporations();

        return PlayerDto.builder()
                .playerUuid(player.getUuid())
                .name(player.getName())
                .corporations(corporations.getCards().stream().map(cardService::getCard).map(CardDto::from).collect(Collectors.toList()))
                .hand(player.getHand().getCards().stream().map(cardService::getCard).map(CardDto::from).collect(Collectors.toList()))
                .played(player.getPlayed().getCards().stream().map(cardService::getCard).map(CardDto::from).collect(Collectors.toList()))
                .corporationId(player.getSelectedCorporationCard())
                .phase(player.getChosenPhase())
                .previousPhase(player.getPreviousChosenPhase())
                .mc(player.getMc())
                .mcIncome(player.getMcIncome())
                .cardIncome(player.getCardIncome())
                .heat(player.getHeat())
                .heatIncome(player.getHeatIncome())
                .plants(player.getPlants())
                .plantsIncome(player.getPlantsIncome())
                .steelIncome(player.getSteelIncome())
                .titaniumIncome(player.getTitaniumIncome())
                .nextTurn(buildTurnDto(player.getNextTurn()))
                .cardResources(getPlayerCardResources(player))
                .activatedBlueCards(player.getActivatedBlueCards().getCards())
                .activatedBlueActionTwice(player.isActivatedBlueActionTwice())
                .terraformingRating(player.getTerraformingRating())
                .winPoints(winPointsService.countWinPoints(player, game))
                .forests(player.getForests())
                .builtSpecialDesignLastTurn(player.isBuiltSpecialDesignLastTurn())
                .builtWorkCrewsLastTurn(player.isBuiltWorkCrewsLastTurn())
                .canBuildAnotherGreenWith9Discount(player.isCanBuildAnotherGreenWith9Discount())
                .assortedEnterprisesDiscount(player.isAssortedEnterprisesDiscount())
                .selfReplicatingDiscount(player.isSelfReplicatingDiscount())
                .mayNiDiscount(player.isMayNiDiscount())
                .build();
    }

    private Map<Integer, Integer> getPlayerCardResources(Player player) {
        return player.getPlayed().getCards().stream().map(cardService::getCard)
                .filter(card -> card.getCollectableResource() != CardCollectableResource.NONE)
                .collect(Collectors.toMap(
                        Card::getId,
                        card -> player.getCardResourcesCount().get(card.getClass())
                ));
    }

    private TurnDto buildTurnDto(Turn turn) {
        if (turn != null && turn.getType() == TurnType.DISCARD_CARDS) {
            DiscardCardsTurn discardCardsTurnDto = (DiscardCardsTurn) turn;
            return DiscardCardsTurnDto.builder()
                    .size(discardCardsTurnDto.getSize())
                    .onlyFromSelectedCards(discardCardsTurnDto.isOnlyFromSelectedCards())
                    .cards(
                            discardCardsTurnDto.getCards().stream().map(cardService::getCard).map(CardDto::from).collect(Collectors.toList())
                    ).build();
        }
        return null;

    }

}

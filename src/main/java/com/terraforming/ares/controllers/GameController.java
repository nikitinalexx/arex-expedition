package com.terraforming.ares.controllers;

import com.terraforming.ares.dto.*;
import com.terraforming.ares.mars.MarsGame;
import com.terraforming.ares.model.*;
import com.terraforming.ares.model.turn.DiscardCardsTurn;
import com.terraforming.ares.model.turn.Turn;
import com.terraforming.ares.model.turn.TurnType;
import com.terraforming.ares.services.CardFactory;
import com.terraforming.ares.services.CardService;
import com.terraforming.ares.services.GameService;
import com.terraforming.ares.services.WinPointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping("/game/new")
    public PlayerUuidsDto startNewGame(@RequestBody GameParameters gameParameters) {
        try {
            if (gameParameters.getPlayersCount() != 2 && gameParameters.getPlayersCount() != 1) {
                throw new IllegalArgumentException("Only one/two players are supported so far");
            }

            MarsGame marsGame = gameService.startNewGame(gameParameters);

            return PlayerUuidsDto.builder()
                    .players(new ArrayList<>(marsGame.getPlayerUuidToPlayer().keySet()))
                    .build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/game/player/{playerUuid}")
    public GameDto getGameByPlayerUuid(@PathVariable String playerUuid) {
        MarsGame game = gameService.getGame(playerUuid);

        return GameDto.builder()
                .phase(game.getCurrentPhase())
                .player(buildCurrentPlayer(game.getPlayerByUuid(playerUuid)))
                .otherPlayers(Collections.emptyList())//TODO
                .build();
    }

    @GetMapping("/projects")
    public List<CardDto> getAllProjectCards() {
        return cardFactory.getAllProjects()
                .stream()
                .map(CardDto::from)
                .collect(Collectors.toList());
    }

    private PlayerDto buildCurrentPlayer(Player player) {
        Deck corporations = player.getCorporations();

        return PlayerDto.builder()
                .playerUuid(player.getUuid())
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
                .cardResources(
                        player.getPlayed().getCards().stream().map(cardService::getCard)
                                .filter(card -> card.getCollectableResource() != CardCollectableResource.NONE)
                                .collect(Collectors.toMap(
                                        Card::getId,
                                        card -> player.getCardResourcesCount().get(card.getClass())
                                ))
                )
                .activatedBlueCards(player.getActivatedBlueCards().getCards())
                .activatedBlueActionTwice(player.isActivatedBlueActionTwice())
                .terraformingRating(player.getTerraformingRating())
                .winPoints(winPointsService.countWinPoints(player))
                .forests(player.getForests())
                .build();
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

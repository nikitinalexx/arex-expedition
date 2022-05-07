package com.terraforming.ares.model;

import com.terraforming.ares.model.turn.Turn;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by oleksii.nikitin
 * Creation date 25.04.2022
 */
@Builder
@Data
public class Player {
    private String uuid;
    private Deck hand;
    @Builder.Default
    private Deck played = Deck.builder().build();
    private Deck corporations;
    private Deck activatedBlueCards;
    private boolean activatedBlueActionTwice;
    private Integer selectedCorporationCard;
    @Builder.Default
    private Map<Class<?>, Integer> cardResourcesCount = new HashMap<>();
    @Builder.Default
    private int terraformingRating = Constants.STARTING_RT;
    @Builder.Default
    private int canBuildInSecondPhase = 0;

    private Integer previousChosenPhase;
    private Integer chosenPhase;

    private Turn nextTurn;

    private int mc;
    private int mcIncome;

    private int cardIncome;

    private int heat;
    private int heatIncome;

    private int plants;
    private int plantsIncome;

    private int steelIncome;
    private int titaniumIncome;

    public void clearRoundResults() {
        previousChosenPhase = chosenPhase;
        chosenPhase = null;
        activatedBlueCards = Deck.builder().build();
        activatedBlueActionTwice = false;
        canBuildInSecondPhase = 0;
    }

}
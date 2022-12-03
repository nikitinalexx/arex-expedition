package com.terraforming.ares.dto;

import lombok.Getter;
import lombok.Synchronized;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by oleksii.nikitin
 * Creation date 03.12.2022
 */
@Getter
public class GameStatistics {
    public static final int MAX_TURNS_TO_CONSIDER = 50;
    public static final int CARDS_TOTAL = 219;

    private Map<Integer, List<Integer>> turnToWinCardsOccurence;
    private Map<Integer, List<Integer>> turnToCardsOccurence;

    private long totalGames = 0;
    private long totalTurnsCount = 0;
    private long totalPointsCount = 0;

    private long firstWins = 0;
    private long secondWins = 0;


    public GameStatistics() {
        turnToWinCardsOccurence = new HashMap<>();
        turnToCardsOccurence = new HashMap<>();

        for (int turn = 1; turn <= MAX_TURNS_TO_CONSIDER; turn++) {
            turnToWinCardsOccurence.put(turn, initCardsList());
            turnToCardsOccurence.put(turn, initCardsList());
        }
    }

    public synchronized void cardOccured(int turn, int card) {
        if (turn == 0) {
            turn++;
        }
        final List<Integer> turnCards = turnToCardsOccurence.get(turn);

        turnCards.set(card, turnCards.get(card) + 1);

    }

    public synchronized void winCardOccured(int turn, int card) {
        if (turn == 0) {
            turn++;
        }
        final List<Integer> turnCards = turnToWinCardsOccurence.get(turn);

        turnCards.set(card, turnCards.get(card) + 1);
    }

    public synchronized void addTotalGames(int games) {
        totalGames += games;
    }

    public synchronized void addTotalTurnsCount(int turnsCount) {
        totalTurnsCount += turnsCount;
    }

    public synchronized void addTotalPointsCount(int points) {
        totalPointsCount += points;
    }

    public synchronized void addFirstWins() {
        firstWins++;
    }

    public synchronized void addSecondWins() {
        secondWins++;
    }

    private List<Integer> initCardsList() {
        List<Integer> cards = new ArrayList<>();
        for (int i = 0; i <= CARDS_TOTAL; i++) {
            cards.add(0);
        }
        return cards;
    }
}
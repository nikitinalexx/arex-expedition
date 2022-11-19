package com.terraforming.ares.model.milestones;

import com.terraforming.ares.model.Card;
import com.terraforming.ares.model.Player;
import com.terraforming.ares.services.CardService;

import java.util.Collection;

/**
 * Created by oleksii.nikitin
 * Creation date 13.06.2022
 */
public class DiversifierMilestone extends Milestone {

    @Override
    public boolean isAchievable(Player player, CardService cardService) {
        return countUniqueTags(player, cardService) >= 9;
    }

    @Override
    public int getValue(Player player, CardService cardService) {
        return Math.min(countUniqueTags(player, cardService), 9);
    }

    private int countUniqueTags(Player player, CardService cardService) {
        return (int) player.getPlayed()
                .getCards()
                .stream()
                .map(cardService::getCard)
                .map(Card::getTags)
                .flatMap(Collection::stream)
                .distinct()
                .count();
    }

    @Override
    public MilestoneType getType() {
        return MilestoneType.DIVERSIFIER;
    }

}

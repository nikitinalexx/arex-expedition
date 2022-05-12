package com.terraforming.ares.processors.action;

import com.terraforming.ares.cards.blue.AdvancedScreeningTechnology;
import com.terraforming.ares.dto.ProjectCardDto;
import com.terraforming.ares.dto.blueAction.AutoPickDiscardCardsAction;
import com.terraforming.ares.mars.MarsGame;
import com.terraforming.ares.model.*;
import com.terraforming.ares.services.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Created by oleksii.nikitin
 * Creation date 05.05.2022
 */
@Service
@RequiredArgsConstructor
public class AdvancedScreeningTechnologyActionProcessor implements BlueActionCardProcessor<AdvancedScreeningTechnology> {
    private final CardService deckService;

    @Override
    public Class<AdvancedScreeningTechnology> getType() {
        return AdvancedScreeningTechnology.class;
    }

    @Override
    public TurnResponse process(MarsGame game, Player player) {
        AutoPickDiscardCardsAction.AutoPickDiscardCardsActionBuilder resultBuilder = AutoPickDiscardCardsAction.builder();

        for (Integer card : game.dealCards(3)) {
            ProjectCard projectCard = deckService.getProjectCard(card);
            if (projectCard.getTags().contains(Tag.SCIENCE) || projectCard.getTags().contains(Tag.PLANT)) {
                player.getHand().addCard(card);
                resultBuilder.takenCard(ProjectCardDto.from(projectCard));
            } else {
                resultBuilder.discardedCard(ProjectCardDto.from(projectCard));
            }
        }

        return resultBuilder.build();
    }


}
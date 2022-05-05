package com.terraforming.ares.processors.action;

import com.terraforming.ares.cards.blue.AdvancedScreeningTechnology;
import com.terraforming.ares.dto.CardDto;
import com.terraforming.ares.dto.blueAction.AutoPickDiscardCardsAction;
import com.terraforming.ares.mars.MarsGame;
import com.terraforming.ares.model.*;
import com.terraforming.ares.services.DeckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Created by oleksii.nikitin
 * Creation date 05.05.2022
 */
@Service
@RequiredArgsConstructor
public class AdvancedScreeningTechnologyBlueActionProcessor implements BlueActionCardProcessor<AdvancedScreeningTechnology> {
    private final DeckService deckService;

    @Override
    public Class<AdvancedScreeningTechnology> getType() {
        return AdvancedScreeningTechnology.class;
    }

    @Override
    public TurnResponse process(MarsGame game, PlayerContext player) {
        Deck deck = game.getProjectsDeck().dealCards(3);

        AutoPickDiscardCardsAction.AutoPickDiscardCardsActionBuilder resultBuilder = AutoPickDiscardCardsAction.builder();

        for (Integer card : deck.getCards()) {
            ProjectCard projectCard = deckService.getProjectCard(card);
            if (projectCard.getTags().contains(Tag.SCIENCE) || projectCard.getTags().contains(Tag.PLANT)) {
                resultBuilder.takenCard(CardDto.from(projectCard));
            } else {
                resultBuilder.discardedCard(CardDto.from(projectCard));
            }
        }

        return resultBuilder.build();
    }


}
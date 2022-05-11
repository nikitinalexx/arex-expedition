package com.terraforming.ares.processors.action;

import com.terraforming.ares.cards.blue.DevelopmentCenter;
import com.terraforming.ares.dto.ProjectCardDto;
import com.terraforming.ares.dto.blueAction.AutoPickCardsAction;
import com.terraforming.ares.mars.MarsGame;
import com.terraforming.ares.model.Player;
import com.terraforming.ares.model.ProjectCard;
import com.terraforming.ares.model.TurnResponse;
import com.terraforming.ares.services.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Created by oleksii.nikitin
 * Creation date 07.05.2022
 */
@Component
@RequiredArgsConstructor
public class DevelopmentCenterActionCardProcessor implements BlueActionCardProcessor<DevelopmentCenter> {
    private final CardService cardService;

    @Override
    public Class<DevelopmentCenter> getType() {
        return DevelopmentCenter.class;
    }

    @Override
    public TurnResponse process(MarsGame game, Player player) {
        player.setHeat(player.getHeat() - 2);

        AutoPickCardsAction.AutoPickCardsActionBuilder resultBuilder = AutoPickCardsAction.builder();

        for (Integer card : game.dealCards(1)) {
            player.getHand().addCard(card);

            ProjectCard projectCard = cardService.getProjectCard(card);
            resultBuilder.takenCard(ProjectCardDto.from(projectCard));
        }

        return resultBuilder.build();
    }

}

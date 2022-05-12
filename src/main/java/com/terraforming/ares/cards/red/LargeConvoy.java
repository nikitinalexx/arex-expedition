package com.terraforming.ares.cards.red;

import com.terraforming.ares.cards.CardMetadata;
import com.terraforming.ares.dto.ProjectCardDto;
import com.terraforming.ares.dto.blueAction.AutoPickCardsAction;
import com.terraforming.ares.mars.MarsGame;
import com.terraforming.ares.model.*;
import com.terraforming.ares.model.income.Gain;
import com.terraforming.ares.model.income.GainType;
import com.terraforming.ares.services.CardService;
import com.terraforming.ares.services.TerraformingService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Created by oleksii.nikitin
 * Creation date 08.05.2022
 */
@RequiredArgsConstructor
@Getter
public class LargeConvoy implements BaseExpansionRedCard {
    private final int id;
    private final CardMetadata cardMetadata;

    public LargeConvoy(int id) {
        this.id = id;
        this.cardMetadata = CardMetadata.builder()
                .name("Large Convoy")
                .description("Flip an ocean tile. Draw 2 cards. Gain 5 plants or add 3 animals to ANY card.")
                .bonuses(List.of(Gain.of(GainType.OCEAN, 1), Gain.of(GainType.CARD, 2)))
                .build();
    }

    @Override
    public CardMetadata getCardMetadata() {
        return cardMetadata;
    }

    @Override
    public boolean onBuiltEffectApplicableToItself() {
        return true;
    }

    @Override
    public void onProjectBuiltEffect(CardService cardService, MarsGame game, Player player, ProjectCard project, Map<Integer, List<Integer>> input) {
        if (input.containsKey(InputFlag.LARGE_CONVOY_PICK_PLANT.getId())) {
            player.setPlants(player.getPlants() + 5);
            return;
        }

        List<Integer> animalsInput = input.get(InputFlag.LARGE_CONVOY_ADD_ANIMAL.getId());
        Integer animalsCardId = animalsInput.get(0);

        ProjectCard animalsCard = cardService.getProjectCard(animalsCardId);

        player.getCardResourcesCount().put(
                animalsCard.getClass(),
                player.getCardResourcesCount().get(animalsCard.getClass()) + 3
        );
    }

    @Override
    public boolean onBuiltEffectApplicableToOther() {
        return false;
    }

    @Override
    public TurnResponse buildProject(MarsContext marsContext) {
        TerraformingService terraformingService = marsContext.getTerraformingService();

        terraformingService.revealOcean(marsContext.getGame(), marsContext.getPlayer());

        AutoPickCardsAction.AutoPickCardsActionBuilder resultBuilder = AutoPickCardsAction.builder();

        for (Integer card : marsContext.getGame().dealCards(2)) {
            marsContext.getPlayer().getHand().addCard(card);
            resultBuilder.takenCard(ProjectCardDto.from(marsContext.getCardService().getProjectCard(card)));
        }

        return resultBuilder.build();
    }

    @Override
    public int getWinningPoints() {
        return 2;
    }

    @Override
    public List<Tag> getTags() {
        return List.of(Tag.SPACE, Tag.EARTH, Tag.EVENT);
    }

    @Override
    public int getPrice() {
        return 36;
    }

}
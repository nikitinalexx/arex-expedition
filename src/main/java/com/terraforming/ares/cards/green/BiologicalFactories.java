package com.terraforming.ares.cards.green;

import com.terraforming.ares.cards.CardMetadata;
import com.terraforming.ares.mars.MarsGame;
import com.terraforming.ares.model.*;
import com.terraforming.ares.model.income.Gain;
import com.terraforming.ares.model.income.GainType;
import com.terraforming.ares.services.CardService;
import com.terraforming.ares.services.UpgradePhaseHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Created by oleksii.nikitin
 * Creation date 17.02.2023
 */
@RequiredArgsConstructor
@Getter
public class BiologicalFactories implements DiscoveryExpansionGreenCard {
    private final int id;
    private final CardMetadata cardMetadata;

    public BiologicalFactories(int id) {
        this.id = id;
        this.cardMetadata = CardMetadata.builder()
                .name("Biological Factories")
                .description("Upgrade your phase 4 card. Effect: During the production phase this produces 1 plant.")
                .cardAction(CardAction.UPDATE_PHASE_4_CARD)
                .incomes(List.of(Gain.of(GainType.PLANT, 1)))
                .build();
    }

    @Override
    public void payAgain(MarsGame game, CardService cardService, Player player) {
        player.setPlants(player.getPlants() + 1);
    }

    @Override
    public boolean canPayAgain() {
        return true;
    }

    @Override
    public TurnResponse buildProject(MarsContext marsContext) {
        final Player player = marsContext.getPlayer();

        player.setPlantsIncome(player.getPlantsIncome() + 1);

        return null;
    }

    @Override
    public void postProjectBuiltEffect(MarsContext marsContext, Card project, Map<Integer, List<Integer>> input) {
        List<Integer> cardInput = input.get(InputFlag.PHASE_UPGRADE_CARD.getId());

        UpgradePhaseHelper.upgradePhase(marsContext.getPlayer(), cardInput.get(0));
    }

    @Override
    public boolean onBuiltEffectApplicableToItself() {
        return true;
    }

    @Override
    public CardMetadata getCardMetadata() {
        return cardMetadata;
    }


    @Override
    public List<Tag> getTags() {
        return List.of(Tag.BUILDING, Tag.PLANT);
    }

    @Override
    public int getPrice() {
        return 16;
    }

}

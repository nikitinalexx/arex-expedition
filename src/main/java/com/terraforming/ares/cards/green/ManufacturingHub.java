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
 * Creation date 20.02.2023
 */
@RequiredArgsConstructor
@Getter
public class ManufacturingHub implements DiscoveryExpansionGreenCard {
    private final int id;
    private final CardMetadata cardMetadata;

    public ManufacturingHub(int id) {
        this.id = id;
        this.cardMetadata = CardMetadata.builder()
                .name("Manufacturing Hub")
                .description("Upgrade a phase card. During the production phase, this produces 2 MC and 1 heat.")
                .cardAction(CardAction.UPDATE_PHASE_CARD)
                .incomes(List.of(Gain.of(GainType.MC, 2), Gain.of(GainType.HEAT, 1)))
                .build();
    }

    @Override
    public void payAgain(MarsGame game, CardService cardService, Player player) {
        player.setMc(player.getMc() + 2);
        player.setHeat(player.getHeat() + 1);
    }

    @Override
    public boolean canPayAgain() {
        return true;
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
    public TurnResponse buildProject(MarsContext marsContext) {
        Player player = marsContext.getPlayer();

        player.setMcIncome(player.getMcIncome() + 2);
        player.setHeatIncome(player.getHeatIncome() + 1);

        return null;
    }

    @Override
    public List<Tag> getTags() {
        return List.of(Tag.BUILDING);
    }

    @Override
    public int getPrice() {
        return 22;
    }

}

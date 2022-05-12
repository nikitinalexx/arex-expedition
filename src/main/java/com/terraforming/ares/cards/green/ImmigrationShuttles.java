package com.terraforming.ares.cards.green;

import com.terraforming.ares.cards.CardMetadata;
import com.terraforming.ares.model.MarsContext;
import com.terraforming.ares.model.Player;
import com.terraforming.ares.model.Tag;
import com.terraforming.ares.model.TurnResponse;
import com.terraforming.ares.model.income.Gain;
import com.terraforming.ares.model.income.GainType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Created by oleksii.nikitin
 * Creation date 08.05.2022
 */
@RequiredArgsConstructor
@Getter
public class ImmigrationShuttles implements BaseExpansionGreenCard {
    private final int id;
    private final CardMetadata cardMetadata;

    public ImmigrationShuttles(int id) {
        this.id = id;
        this.cardMetadata = CardMetadata.builder()
                .name("Immigration Shuttles")
                .description("During the production phase, this produces 3 MC. 1 VP per 2 Earth tags you have.")
                .incomes(List.of(Gain.of(GainType.MC, 3)))
                .build();
    }

    @Override
    public CardMetadata getCardMetadata() {
        return cardMetadata;
    }

    @Override
    public TurnResponse buildProject(MarsContext marsContext) {
        Player player = marsContext.getPlayer();

        player.setMcIncome(player.getMcIncome() + 3);

        return null;
    }

    @Override
    public int getWinningPoints() {
        //TODO
        return 0;
    }

    @Override
    public List<Tag> getTags() {
        return List.of(Tag.SPACE, Tag.EARTH);
    }

    @Override
    public int getPrice() {
        return 20;
    }
}
package com.terraforming.ares.cards.green;

import com.terraforming.ares.model.MarsContext;
import com.terraforming.ares.model.Player;
import com.terraforming.ares.model.Tag;
import com.terraforming.ares.model.TurnResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Created by oleksii.nikitin
 * Creation date 08.05.2022
 */
@RequiredArgsConstructor
@Getter
public class VestaShipyard implements BaseExpansionGreenCard {
    private final int id;

    @Override
    public TurnResponse buildProject(MarsContext marsContext) {
        Player player = marsContext.getPlayer();

        player.setTitaniumIncome(player.getTitaniumIncome() + 1);

        return null;
    }

    @Override
    public String description() {
        return "When you play Space, you pay 3 MC less for it.";
    }

    @Override
    public int getWinningPoints() {
        return 1;
    }

    @Override
    public List<Tag> getTags() {
        return List.of(Tag.BUILDING, Tag.SPACE, Tag.JUPITER);
    }

    @Override
    public int getPrice() {
        return 16;
    }
}

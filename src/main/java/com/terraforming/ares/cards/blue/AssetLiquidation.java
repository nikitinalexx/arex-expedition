package com.terraforming.ares.cards.blue;

import com.terraforming.ares.model.Expansion;
import com.terraforming.ares.model.Player;
import com.terraforming.ares.model.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * Created by oleksii.nikitin
 * Creation date 06.05.2022
 */
@RequiredArgsConstructor
@Getter
public class AssetLiquidation implements BlueCard{
    private final int id;

    @Override
    public void buildProject(Player player) {
        player.setCanBuildInSecondPhase(player.getCanBuildInSecondPhase() + 1);
    }

    @Override
    public String description() {
        return "You may play an additional blue or red card this turn. Action: Spend 1 TR to draw 3 cards";
    }

    @Override
    public List<Tag> getTags() {
        return Collections.singletonList(Tag.SCIENCE);
    }

    @Override
    public Expansion getExpansion() {
        return Expansion.BASE;
    }

    @Override
    public boolean isActiveCard() {
        return true;
    }

    @Override
    public int getPrice() {
        return 0;
    }
}
package com.terraforming.ares.cards.corporations;

import com.terraforming.ares.model.CorporationCard;
import com.terraforming.ares.model.Expansion;
import com.terraforming.ares.model.PlayerContext;
import com.terraforming.ares.model.Tag;

import java.util.Collections;
import java.util.Set;

/**
 * Created by oleksii.nikitin
 * Creation date 27.04.2022
 */
public class HelionCorporation implements CorporationCard {

    @Override
    public void buildProject(PlayerContext playerContext) {
        playerContext.setMc(28);
        playerContext.setHeatIncome(3);
    }

    @Override
    public String description() {
        return "MegaCredits: 28. HeatIncome: 3. You may use heat as MC. You may not use MC as heat.";
    }

    @Override
    public Set<Tag> getTags() {
        return Collections.singleton(Tag.SPACE);
    }

    @Override
    public Expansion getExpansion() {
        return Expansion.BASE;
    }

}

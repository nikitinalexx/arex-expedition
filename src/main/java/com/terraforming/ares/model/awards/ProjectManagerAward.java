package com.terraforming.ares.model.awards;

import com.terraforming.ares.model.Player;
import com.terraforming.ares.services.CardService;

import java.util.function.ToIntFunction;

/**
 * Created by oleksii.nikitin
 * Creation date 13.06.2022
 */
public class ProjectManagerAward extends AbstractAward {

    @Override
    public AwardType getType() {
        return AwardType.PROJECT_MANAGER;
    }

    @Override
    protected ToIntFunction<Player> comparableParamExtractor(CardService cardService) {
        return player -> player.getPlayed().size();
    }
}

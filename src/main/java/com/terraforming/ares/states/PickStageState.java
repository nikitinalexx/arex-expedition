package com.terraforming.ares.states;

import com.terraforming.ares.mars.MarsGame;
import com.terraforming.ares.model.StateType;
import com.terraforming.ares.model.turn.TurnType;

import java.util.Collections;
import java.util.List;

/**
 * Created by oleksii.nikitin
 * Creation date 25.04.2022
 */
public class PickStageState extends AbstractState {

    public PickStageState(MarsGame marsGame) {
        super(marsGame);
    }

    @Override
    public List<TurnType> getPossibleTurns(String playerUuid) {
        if (marsGame.getPlayerByUuid(playerUuid).getNextTurn() != null) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(TurnType.PICK_STAGE);
        }
    }

    @Override
    public void updateState() {
        if (marsGame.allStagesSelected()) {
            if (marsGame.getPlayerContexts().values().stream()
                    .anyMatch(p -> p.getCurrentStage() == 1)) {
                marsGame.setStateType(StateType.BUILD_GREEN_PROJECTS);
            }
            //TODO other stages
        }
    }

}

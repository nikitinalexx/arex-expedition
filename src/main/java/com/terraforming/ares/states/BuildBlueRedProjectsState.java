package com.terraforming.ares.states;

import com.terraforming.ares.mars.MarsGame;
import com.terraforming.ares.model.PlayerContext;
import com.terraforming.ares.model.StateType;
import com.terraforming.ares.model.turn.TurnType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by oleksii.nikitin
 * Creation date 26.04.2022
 */
public class BuildBlueRedProjectsState extends AbstractState {

    public BuildBlueRedProjectsState(MarsGame marsGame) {
        super(marsGame);
    }

    @Override
    public List<TurnType> getPossibleTurns(String playerUuid) {
        PlayerContext player = marsGame.getPlayerByUuid(playerUuid);
        if (player.getNextTurn() != null || marsGame.getStateType() == StateType.BUILD_SECOND_BLUE_RED_PROJECTS && player.getChosenStage() != 2) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(
                    TurnType.BUILD_BLUE_RED_PROJECT,
                    TurnType.SELL_CARDS,
                    TurnType.SKIP_TURN
            );
        }
    }

    @Override
    public void updateState() {
        if (marsGame.getStateType() == StateType.BUILD_FIRST_BLUE_RED_PROJECTS) {
            marsGame.setStateType(StateType.BUILD_SECOND_BLUE_RED_PROJECTS);
        } else {
            performStateTransferFromStage(3);
        }
    }
}
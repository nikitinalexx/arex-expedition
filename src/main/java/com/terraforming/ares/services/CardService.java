package com.terraforming.ares.services;

import com.terraforming.ares.model.CorporationCard;
import com.terraforming.ares.model.Deck;
import com.terraforming.ares.model.Expansion;
import com.terraforming.ares.model.ProjectCard;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by oleksii.nikitin
 * Creation date 25.04.2022
 */
@Service
public class CardService {
    private final Map<Integer, ProjectCard> projects;
    private final Map<Integer, CorporationCard> corporations;

    public CardService(CardFactory cardFactory) {
        projects = cardFactory.createProjects();
        corporations = cardFactory.createCorporations();
    }

    public Deck createProjectsDeck(List<Expansion> expansions) {
        //TODO logic that builds a deck based on list of selected expansions
        return Deck.builder()
                .cards(new LinkedList<>(projects.keySet()))
                .build();
    }

    public Deck createCorporationsDeck(List<Expansion> expansions) {
        //TODO logic that builds a deck based on list of selected expansions
        return Deck.builder()
                .cards(new LinkedList<>(corporations.keySet()))
                .build();
    }

    public CorporationCard getCorporationCard(int id) {
        return corporations.get(id);
    }

    public ProjectCard getProjectCard(int id) {
        return projects.get(id);
    }

}
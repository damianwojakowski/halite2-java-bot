package hlt.extended;

import hlt.Planet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanetsManager {
    private Map<Integer, Planet> myPlanets = new HashMap<>();
    private Map<Integer, Planet> enemyPlanets = new HashMap<>();
    private Map<Integer, Planet> freePlanets = new HashMap<>();

    private Integer numberOfAllPlanets;
    private Integer numberOfMyPlanets;
    private Integer numberOfEnemyPlanets;

    private Integer playerId;

    public Map<Integer, Planet> getMyPlanets() {
        return this.myPlanets;
    }

    public Map<Integer, Planet> getEnemyPlanets() {
        return this.enemyPlanets;
    }

    public Map<Integer, Planet> getFreePlanets() {
        return this.freePlanets;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public void updatePlanetsStatus(List<Planet> planetsList) {
        myPlanets.clear();
        enemyPlanets.clear();
        freePlanets.clear();

        for (Planet planet : planetsList) {
            if (planet.isOwned()) {
                if (planet.getOwner() == this.playerId) {
                    myPlanets.put(planet.getId(), planet);
                } else {
                    enemyPlanets.put(planet.getId(), planet);
                }
            } else {
                freePlanets.put(planet.getId(), planet);
            }
        }

        numberOfAllPlanets = planetsList.size();
        numberOfEnemyPlanets = enemyPlanets.size();
        numberOfMyPlanets = myPlanets.size();
    }
}

package hlt.extended;

import hlt.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FleetManager {
    final ArrayList<Move> moveList = new ArrayList<>();

    private GameMap gameMap;
    private Orders orders;
    private PlanetsManager planetsManager;
    private Map<Integer, Ship> allShipsWithEnemyShips = new HashMap<>();
    private Map<Integer, Ship> allShips = new HashMap<>();
    private Map<Integer, Ship> miningShips = new HashMap<>();
    private Map<Integer, Ship> dockingShips = new HashMap<>();
    private Map<Integer, Ship> warriorShips = new HashMap<>();
    private Map<Integer, Ship> freeShip = new HashMap<>();

    private List<Integer> freeShipsList = new ArrayList<>();
    private List<Integer> dockingShipsList = new ArrayList<>();
    private List<Integer> sortedPlanetsList = new ArrayList<>();

    public void setPlanetsManager(PlanetsManager planetsManager) {
        this.planetsManager = planetsManager;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public void setAllShipsWithEnemyShips(List<Ship> ships) {
        for (Ship ship : ships) {
            allShipsWithEnemyShips.put(ship.getId(), ship);
        }
    }

    public void checkShipAndAddToNewShipsIfNotRegistered(List<Ship> ships) {
        freeShipsList.clear();

        for (Ship ship : ships) {
            if (!orders.areOrdersSetForShip(ship.getId())) {
                Log.log("Add free ship: " + ship.getId());
                freeShipsList.add(ship.getId());
            }

            allShips.put(ship.getId(), ship);
        }
    }

    public void assignOrdersForShips() {
        Log.log("* ORDERS: " + orders.getOrders().toString());
        Log.log("Ships: " + allShips.values().toString());

        if (planetsManager.areFreePlanets()) {
            assignTasksToDockPlanets();
        }

        if (planetsManager.notAllMyPlanetsAreFull()) {
            assignTasksToSupportYourPlanets();
        }

        if (planetsManager.areMyPlanetsFull()) {
            assignTasksToAttackEnemies();
        }
    }

    private void assignTasksToSupportYourPlanets() {
        List<Integer> updatedShipsToBeRemoved = new ArrayList<>();

        for (Planet myPlanet : new ArrayList<Planet>(planetsManager.getMyPlanets().values())) {
            if (!myPlanet.isFull()) {
                int leftDocksOnPlanet = myPlanet.getDockingSpots() - myPlanet.getDockedShips().size();
                int assignedShipsCounter = 0;

                for (Integer freeShipId : freeShipsList) {
                    if (assignedShipsCounter >= leftDocksOnPlanet) {
                        break;
                    }

                    if (!updatedShipsToBeRemoved.contains(freeShipId)) {
                        orders.serOrderToDockPlanet(myPlanet.getId(), freeShipId);
                        dockingShipsList.add(freeShipId);
                        updatedShipsToBeRemoved.add(freeShipId);
                        assignedShipsCounter++;
                    }
                }
            }
        }
        for (Integer updatedShipId : updatedShipsToBeRemoved) {
            if (freeShipsList.contains(updatedShipId)) {
                freeShipsList.remove(updatedShipId);
            }
        }
    }

    private void assignTasksToAttackEnemies() {
        List<Integer> updatedShipsToBeRemoved = new ArrayList<>();
        int assignedShipsCounter = 0;
        int myShipsPerEnemy = 7;

        for (Ship enemyShip : gameMap.getAllShips()) {
            assignedShipsCounter = 0;
            if (updatedShipsToBeRemoved.size() >= freeShipsList.size()) {
                break;
            }

            if (enemyShip.getOwner() != gameMap.getMyPlayerId() && enemyShip.getHealth() > 0) {
                for (Integer freeShipId : freeShipsList) {
                    if (assignedShipsCounter >= myShipsPerEnemy) {
                        break;
                    }

                    if (!updatedShipsToBeRemoved.contains(freeShipId)) {
                        orders.serOrderToAttackShip(enemyShip.getId(), freeShipId);
                        dockingShipsList.add(freeShipId);
                        updatedShipsToBeRemoved.add(freeShipId);
                        assignedShipsCounter++;
                    }
                }
            }
        }

        freeShipsList.removeAll(updatedShipsToBeRemoved);
    }

    List<Integer> getSortedPlanetsOrder(Map<Integer, Planet> planets) {
        if (sortedPlanetsList.size() > 0) {
            return sortedPlanetsList;
        } else {
            List<Integer> sortedPlanets = new ArrayList<>();
            Ship firstFreeShip = allShips.get(freeShipsList.get(0));
            Map<Double, Entity> closestEntities = gameMap.nearbyEntitiesByDistance(firstFreeShip);

            for (Entity entity : closestEntities.values()) {
                if (planets.containsKey(entity.getId())) {
                    sortedPlanetsList.add(entity.getId());
                }
            }
            return sortedPlanetsList;
        }
    }

    private List<Planet> getNearestPlanets() {
        Map<Integer, Planet> planets = planetsManager.getFreePlanets();
        List<Planet> sortedPlanets = new ArrayList<>();
        List<Integer> sortedPlanetsList = getSortedPlanetsOrder(planets);

        for (Integer planetId : sortedPlanetsList) {
            sortedPlanets.add(gameMap.getPlanet(planetId));
        }

        Log.log("Planets: " + sortedPlanets.size());
        return sortedPlanets;
    }

    private void assignTasksToDockPlanets() {
        Log.log("assignTasksToDockPlanets");
        int shipsPerPlanet = 2;
        List<Integer> updatedShipsToBeRemoved = new ArrayList<>();

        for (Planet freePlanet : getNearestPlanets()) {
            int shipsCounter = 0;

            for (Integer freeShipId : freeShipsList) {
                if (shipsCounter >= shipsPerPlanet) {
                    break;
                }

                if (!updatedShipsToBeRemoved.contains(freeShipId)) {
                    orders.serOrderToDockPlanet(freePlanet.getId(), freeShipId);
                    dockingShipsList.add(freeShipId);
                    updatedShipsToBeRemoved.add(freeShipId);
                    shipsCounter++;
                }
            }
        }

        freeShipsList.removeAll(updatedShipsToBeRemoved);
    }

    public ArrayList<Move> generateMoveList() {
        moveList.clear();
        Log.log(moveList.toString());

        for (SingleOrder singleOrder : this.orders.getOrders()) {
            switch (singleOrder.getOrderType()) {
                case SingleOrder.DOCK_PLANET:
                    Ship ship = allShips.get(singleOrder.getShipId());
                    Planet planet = planetsManager.getPlanetById(singleOrder.getPlanetId());

                    if (planet.isFull()) {
                        singleOrder.reset();
                    }

                    if (ship.canDock(planet)) {
                        moveList.add(new DockMove(ship, planet));
                        break;
                    }

                    final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, planet, Constants.MAX_SPEED);
                    if (newThrustMove != null) {
                        moveList.add(newThrustMove);
                    }
                    break;
                case SingleOrder.ATTACK_SHIP:
                    Ship attackingShip = allShips.get(singleOrder.getShipId());
                    Ship enemyShip = allShipsWithEnemyShips.get(singleOrder.getEnemyShipId());

                    final ThrustMove attackMove = Navigation.navigateShipToDock(gameMap, attackingShip, enemyShip, Constants.MAX_SPEED);
                    if (attackMove != null) {
                        moveList.add(attackMove);
                    }
                    break;
            }
        }

        this.orders.validateOrders();
        this.orders.removeCompletedOrders();

        return moveList;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }
}

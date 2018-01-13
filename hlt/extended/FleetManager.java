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
    private boolean firstRound = true;

    private List<Integer> freeShipsList = new ArrayList<>();
    private List<Integer> dockingShipsList = new ArrayList<>();
    private List<Integer> sortedPlanetsList = new ArrayList<>();

    private List<Integer> getSortedPlanetsAndCheckIfExist() {
        List<Integer> planetsNotExistingAnymore = new ArrayList<>();

        for (Integer planetId : sortedPlanetsList) {
            if (gameMap.getPlanet(planetId) == null) {
                planetsNotExistingAnymore.add(planetId);
            }
        }

        sortedPlanetsList.removeAll(planetsNotExistingAnymore);

        return sortedPlanetsList;
    }

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
                freeShipsList.add(ship.getId());
            }

            allShips.put(ship.getId(), ship);
        }
        Log.log("Added free ship: " + freeShipsList.size());
    }

    public void assignOrdersForShips() {
        int foreverLoopLimiter = 100;
        int loops = 0;
        while(freeShipsList.size() > 0) {
            if (loops > foreverLoopLimiter) {
                break;
            }

            Log.log("FleetManager.assignOrdersForShips");
            Log.log("* ORDERS: " + orders.getOrders().size());
            Log.log("Ships: " + allShips.values().size());
            Log.log("Free Ships: " + freeShipsList.size());

            if (planetsManager.areFreePlanets()) {
                Log.log("- Free planets detected");
                assignTasksToDockPlanets();
            }

            if (planetsManager.notAllMyPlanetsAreFull()) {
                Log.log("- not all of my planets are full!");
                assignTasksToSupportYourPlanets();
            }

//        if (planetsManager.areMyPlanetsFull()) {
//            Log.log("assigning tasks to conquer planets");
//            assignTasksToConquerEnemyPlanets();
//        }

            if (planetsManager.areAllPlanetsOwned()) {
                Log.log("assigning tasks to conquer planets");
                assignTasksToConquerEnemyPlanets();
            }

            if (planetsManager.areAllPlanetsMine()) {
                Log.log("assigning tasks to attack ships");
                assignTasksToAttackEnemies();
            }

            loops++;
        }

    }

    private void assignTasksToConquerEnemyPlanets() {
        if (freeShipsList.size() <= 0) {
            return;
        }
        List<Planet> planetsToConquer = new ArrayList<>();

        for (Integer planetId : getSortedPlanetsAndCheckIfExist()) {
            Planet planet = gameMap.getPlanet(planetId);

            if (planet.isOwned() && planet.getOwner() != gameMap.getMyPlayerId()) {
                planetsToConquer.add(planet);
            }
        }

        if (planetsToConquer.size() == 0) {
            return;
        }

        Log.log("Planets to conquer: " + planetsToConquer.size());

        List<Integer> freeShipsWithNewlyAssignedJobs = new ArrayList<>();
        int shipsPerPlanetToConquerLimit = (int) Math.floor(freeShipsList.size() / planetsToConquer.size());
        Log.log("Ships per planet to conquer: " + shipsPerPlanetToConquerLimit);

        for (Planet planetToConquer : planetsToConquer) {
            int shipsSetToConquerPlanet = 0;
            Map<Double, Entity> nearestEntities = gameMap.nearbyEntitiesByDistance(planetToConquer);

            for (Entity entity : nearestEntities.values()) {
                if (shipsSetToConquerPlanet >= shipsPerPlanetToConquerLimit) {
                    break;
                }
                if (freeShipsList.contains(entity.getId()) && !freeShipsWithNewlyAssignedJobs.contains(entity.getId())) {
                    freeShipsWithNewlyAssignedJobs.add(entity.getId());
                    orders.setOrderToConquerPlanet(planetToConquer.getId(), entity.getId());
                    shipsSetToConquerPlanet++;
                }
            }
        }

        freeShipsList.removeAll(freeShipsWithNewlyAssignedJobs);
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
        freeShipsList.removeAll(updatedShipsToBeRemoved);
    }

    private void assignTasksToAttackEnemies() {
        List<Integer> updatedShipsToBeRemoved = new ArrayList<>();
        int assignedShipsCounter = 0;
        int myShipsPerEnemy = 5;

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
            return getSortedPlanetsAndCheckIfExist();
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
        List<Integer> sortedPlanetsIds = getSortedPlanetsOrder(planets);

        for (Integer planetId : sortedPlanetsIds) {
            sortedPlanets.add(gameMap.getPlanet(planetId));
        }

        Log.log("Planets: " + sortedPlanets.size());
        return sortedPlanets;
    }

    private void assignTasksToDockPlanets() {
        Log.log("assignTasksToDockPlanets");
        int shipsPerPlanet = 2;

        if (freeShipsList.size() > 20) {
            shipsPerPlanet = 5;
        }

        if (firstRound) {
            shipsPerPlanet = 1;
        }

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

        this.orders.validateOrders();
        this.orders.removeCompletedOrders();

        for (SingleOrder singleOrder : this.orders.getOrders()) {
            switch (singleOrder.getOrderType()) {
                case SingleOrder.DOCK_PLANET:
                    Ship ship = allShips.get(singleOrder.getShipId());
                    Planet planet = planetsManager.getPlanetById(singleOrder.getPlanetId());

                    if (planet.isFull()) {
                        singleOrder.reset();
                        break;
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

                    if (enemyShip == null || enemyShip.getHealth() <= 0) {
                        singleOrder.reset();
                        break;
                    }

                    final ThrustMove attackMove = Navigation.navigateShipToDock(gameMap, attackingShip, enemyShip, Constants.MAX_SPEED);
                    if (attackMove != null) {
                        moveList.add(attackMove);
                    }
                    break;
                case SingleOrder.CONQUER_PLANET:
                    Ship myFreeShip = allShips.get(singleOrder.getShipId());
                    Planet planetToConquer = planetsManager.getPlanetById(singleOrder.getPlanetId());
                    if (planetToConquer.getDockedShips().size() > 0) {
                        Ship firstDockedEnemyShip = allShipsWithEnemyShips.get(planetToConquer.getDockedShips().get(0));

                        final ThrustMove attackDockedShipMove = Navigation.navigateShipToDock(gameMap, myFreeShip, firstDockedEnemyShip, Constants.MAX_SPEED);
                        if (attackDockedShipMove != null) {
                            moveList.add(attackDockedShipMove);
                        }
                    } else {
                        if (myFreeShip.canDock(planetToConquer)) {
                            moveList.add(new DockMove(myFreeShip, planetToConquer));
                            break;
                        }

                        final ThrustMove conquerAndDockMove = Navigation.navigateShipToDock(gameMap, myFreeShip, planetToConquer, Constants.MAX_SPEED);
                        if (conquerAndDockMove != null) {
                            moveList.add(conquerAndDockMove);
                        }
                    }

                    break;
            }
        }

        return moveList;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }
}

import hlt.*;
import hlt.extended.FleetManager;
import hlt.extended.Orders;
import hlt.extended.PlanetsManager;

import java.util.*;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Damiano v1.1.0");
        final FleetManager fleetManager = new FleetManager();
        final PlanetsManager planetsManager = new PlanetsManager();
        final Orders orders = new Orders();

        orders.setPlanetsManager(planetsManager);
        orders.setPlayerId(gameMap.getMyPlayerId());
        orders.setGameMap(gameMap);

        planetsManager.setPlayerId(gameMap.getMyPlayerId());

        fleetManager.setPlanetsManager(planetsManager);
        fleetManager.setOrders(orders);
        fleetManager.setGameMap(gameMap);

        final String initialMapIntelligence =
                "width: " + gameMap.getWidth() +
                "; height: " + gameMap.getHeight() +
                "; players: " + gameMap.getAllPlayers().size() +
                "; planets: " + gameMap.getAllPlanets().size();
        Log.log(initialMapIntelligence);

        for (;;) {
            networking.updateMap(gameMap);

            List<Planet> planets = new ArrayList<>(gameMap.getAllPlanets().values());
            planetsManager.updatePlanetsStatus(planets);

            List<Ship> allShips = new ArrayList<>(gameMap.getAllShips());
            fleetManager.setAllShipsWithEnemyShips(allShips);

            List<Ship> myShips = new ArrayList<>(gameMap.getMyPlayer().getShips().values());
            fleetManager.checkShipAndAddToNewShipsIfNotRegistered(myShips);
            fleetManager.assignOrdersForShips();
            ArrayList<Move> moveList = fleetManager.generateMoveList();

            Networking.sendMoves(moveList);
        }
    }
}

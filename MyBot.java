import hlt.*;
import hlt.extended.FleetManager;
import hlt.extended.PlanetsManager;

import java.util.*;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Damiano v0.1.0");
        final FleetManager fleetManager = new FleetManager();
        final PlanetsManager planetsManager = new PlanetsManager();

        planetsManager.setPlayerId(gameMap.getMyPlayerId());


        // iterate over ships and look for new ones (with no jobs)
        // assign new jobs to free ships
            // iterate plantes
                // are any free?
                // are yours with free docking spots?
                    // assign closest free ships to them

        // if all planets occupied
            // start war (attack enemy ships and planets)



        final String initialMapIntelligence =
                "width: " + gameMap.getWidth() +
                "; height: " + gameMap.getHeight() +
                "; players: " + gameMap.getAllPlayers().size() +
                "; planets: " + gameMap.getAllPlanets().size();
        //Log.log(initialMapIntelligence);

        final ArrayList<Move> moveList = new ArrayList<>();



        for (;;) {
            moveList.clear();
            networking.updateMap(gameMap);

            List<Planet> planets = new ArrayList<>(gameMap.getAllPlanets().values());
            planetsManager.updatePlanetsStatus(planets);

            List<Ship> ships = new ArrayList<>(gameMap.getMyPlayer().getShips().values());
            fleetManager.checkShipAndAddToNewShipsIfNotRegistered(ships);

            fleetManager.assignTasksForShips();

            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }


                for (final Planet planet : gameMap.getAllPlanets().values()) {

                    if (planet.isOwned()) {
                        continue;
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
                }
            }
            Networking.sendMoves(moveList);
        }
    }
}

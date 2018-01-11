package hlt.extended;

import hlt.Ship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FleetManager {
    Map<Integer, Ship> allShips = new HashMap<>();
    Map<Integer, Ship> miningShips = new HashMap<>();
    Map<Integer, Ship> dockingShips = new HashMap<>();
    Map<Integer, Ship> warriorShips = new HashMap<>();
    Map<Integer, Ship> newShips = new HashMap<>();

    public void checkShipAndAddToNewShipsIfNotRegistered(List<Ship> ships) {
        for (Ship ship : ships) {
            if (!allShips.containsKey(ship.getId())) {
                allShips.put(ship.getId(), ship);
                newShips.put(ship.getId(), ship);
            }
        }

    }

}

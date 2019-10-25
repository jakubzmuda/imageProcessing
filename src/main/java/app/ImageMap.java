package app;

import io.vavr.Function3;

import java.util.HashMap;
import java.util.Map;

public class ImageMap {
    private Map<Integer, Map<Integer, Canals>> map = new HashMap<>();

    public void put(int x, int y, Canals canals) {
        Map<Integer, Canals> innerMap = map.get(x);

        if (innerMap == null) {
            innerMap = new HashMap<>();
        }

        innerMap.put(y, canals);

        map.put(x, innerMap);
    }

    public Canals get(int x, int y) {
        return map.get(x).get(y);
    }

    public Canals maxColorCanalValues() {
        Canals currentBiggest = new Canals(0, 0, 0);
        for (Map.Entry<Integer, Map<Integer, Canals>> e : map.entrySet()) {
            Integer x = e.getKey();
            Map<Integer, Canals> value = e.getValue();
            for (Map.Entry<Integer, Canals> entry : value.entrySet()) {
                Integer y = entry.getKey();
                Canals canals = entry.getValue();
                currentBiggest = new Canals(
                        Math.max(canals.red, currentBiggest.red),
                        Math.max(canals.green, currentBiggest.green),
                        Math.max(canals.blue, currentBiggest.blue));
            }
        }
        return currentBiggest;
    }

    public Canals minColorValues() {
        Canals currentLowest = new Canals(255, 255, 255);
        for (Map.Entry<Integer, Map<Integer, Canals>> e : map.entrySet()) {
            Integer x = e.getKey();
            Map<Integer, Canals> value = e.getValue();
            for (Map.Entry<Integer, Canals> entry : value.entrySet()) {
                Integer y = entry.getKey();
                Canals canals = entry.getValue();
                currentLowest = new Canals(
                        Math.min(canals.red, currentLowest.red),
                        Math.min(canals.green, currentLowest.green),
                        Math.min(canals.blue, currentLowest.blue));
            }
        }
        return currentLowest;
    }

    public int height() {
        return map.get(0).size();
    }

    public int width() {
        return map.size();
    }

    public void singlePointOperation(Function3<Integer, Integer, Canals, Void> operator) {
        map.forEach((x, value) -> {
            value.forEach((y, canals) -> {
                operator.apply(x, y, canals);
            });
        });
    }

    public Histogram histogram() {
        int[] red = new int[256];
        int[] green = new int[256];
        int[] blue = new int[256];
        for (int i = 0; i < 256; i++) {
            red[i] = 0;
            blue[i] = 0;
            green[i] = 0;
        }
        for (Map.Entry<Integer, Map<Integer, Canals>> e : map.entrySet()) {
            Integer x = e.getKey();
            Map<Integer, Canals> value = e.getValue();
            for (Map.Entry<Integer, Canals> entry : value.entrySet()) {
                Integer y = entry.getKey();
                Canals canals = entry.getValue();
                red[canals.red]++;
                green[canals.green]++;
                blue[canals.blue]++;
            }
        }
        return new Histogram(red, green, blue);
    }

    public float cumulativeDistributionForRedCanal(int canalColor) {
        return 0; // todo
    }
}

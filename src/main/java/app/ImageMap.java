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

    public int maxRedCanalValue() {
        return 1;
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
}

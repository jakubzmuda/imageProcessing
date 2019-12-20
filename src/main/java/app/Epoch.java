package app;

import java.util.List;

public class Epoch {

    private List<Region> regions;

    public Epoch(List<Region> regions) {
        this.regions = regions;
    }

    public List<Region> regions() {
        return regions;
    }

    public ImageMap map() {
        ImageMap output = new ImageMap();

        for (Region region : regions) {
            for (int x = region.xStart(); x <= region.xEnd(); x++) {
                for (int y = region.yStart(); y <= region.yEnd(); y++) {
                    output.put(x, y, new Canals(0, 0, 0)); // TODO
                }
            }
        }

        return output;
    }
}

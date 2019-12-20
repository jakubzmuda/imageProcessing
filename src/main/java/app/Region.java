package app;

import io.vavr.collection.List;

public class Region {

    // all inclusive
    private final ImageMap imageMap;
    private int xStart;
    private int xEnd;
    private int yStart;
    private int yEnd;

    public Region(ImageMap imageMap) {
        this.imageMap = imageMap;
        this.xStart = 0;
        this.xEnd = imageMap.width();
        this.yStart = 0;
        this.yEnd = imageMap.height();
    }

    public Region(ImageMap imageMap, int xStart, int xEnd, int yStart, int yEnd) {
        this.imageMap = imageMap;
        this.xStart = xStart;
        this.xEnd = xEnd;
        this.yStart = yStart;
        this.yEnd = yEnd;
    }

    public List<Region> split() {
        int xBreakingPoint = this.xEnd / 2;
        int yBreakingPoint = this.yEnd / 2;
        Region firstSquare = new Region(imageMap, 0, xBreakingPoint, 0, yEnd / 2);
        Region secondSquare = new Region(imageMap, xBreakingPoint + 1, xEnd, 0, yEnd / 2);
        Region thirdSquare = new Region(imageMap, 0, xBreakingPoint, yBreakingPoint + 1, yEnd);
        Region fourthSquare = new Region(imageMap, xBreakingPoint + 1, xEnd, yBreakingPoint + 1, yEnd);

        return List.of(firstSquare, secondSquare, thirdSquare, fourthSquare);
    }
}

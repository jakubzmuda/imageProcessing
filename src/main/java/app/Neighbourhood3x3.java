package app;


public class Neighbourhood3x3 {

    Canals i0, i1, i2, i3, i4, i5, i6, i7, i8;

    public Neighbourhood3x3(Canals i0, Canals i1, Canals i2, Canals i3, Canals i4, Canals i5, Canals i6, Canals i7, Canals i8) {
        this.i0 = i0;
        this.i1 = i1;
        this.i2 = i2;
        this.i3 = i3;
        this.i4 = i4;
        this.i5 = i5;
        this.i6 = i6;
        this.i7 = i7;
        this.i8 = i8;
    }

    public Canals middle() {
        return i4;
    }

    public boolean anyMissing() {
        return
                i0 == null
                        || i1 == null
                        || i2 == null
                        || i3 == null
                        || i4 == null
                        || i5 == null
                        || i6 == null
                        || i7 == null
                        || i8 == null;
    }

    public Neighbourhood3x3 adjustBorderValues() {
        return new Neighbourhood3x3(
                firstPresent(i0, i1, i3, i4),
                firstPresent(i1, i0, i3, i4),
                firstPresent(i2, i1, i5, i4),
                firstPresent(i3, i0, i6, i4),
                firstPresent(i4),
                firstPresent(i5, i2, i8, i4),
                firstPresent(i6, i3, i8, i4),
                firstPresent(i7, i6, i8, i4),
                firstPresent(i8, i7, i5, i4)
        );
    }

    public Neighbourhood3x3 emptyToZero() {
        return new Neighbourhood3x3(
                valueOrZero(i0),
                valueOrZero(i1),
                valueOrZero(i2),
                valueOrZero(i3),
                valueOrZero(i4),
                valueOrZero(i5),
                valueOrZero(i6),
                valueOrZero(i7),
                valueOrZero(i8)
        );
    }

    private Canals firstPresent(Canals... alternatives) {
        for (Canals entry : alternatives) {
            if (entry != null) {
                return entry;
            }
        }
        throw new RuntimeException("no alternatives for first present border adjusted value");
    }

    private Canals valueOrZero(Canals canals) {
        if (canals == null) {
            return new Canals(0, 0, 0);
        }
        return canals;
    }
}

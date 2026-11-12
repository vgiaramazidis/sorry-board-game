package model.save;

public class PawnPosition {
    private final int playerIndex;
    private final int pawnIndex;
    private final LocationType locationType;
    private final int locationIndex;


    public PawnPosition(int playerIndex, int pawnIndex, LocationType locationType, int locationIndex) {
        if (playerIndex < 0 || playerIndex > 3) {
            throw new IllegalArgumentException("Invalid player index");
        }
        if (pawnIndex < 0 || pawnIndex > 1) {
            throw new IllegalArgumentException("Invalid pawn index");
        }
        if (locationType == null) {
            throw new IllegalArgumentException("Location type cannot be null");
        }
        int maxLocationIndex;
        switch (locationType) {
            case START:
            case HOME:
                maxLocationIndex = 1;
                break;
            case OUTER:
                maxLocationIndex = 59;
                break;
            case SAFETY:
                maxLocationIndex = 4;
                break;
            default:
                throw new IllegalArgumentException("Unsupported location type");
        }
        if (locationIndex < 0 || locationIndex > maxLocationIndex) {
            throw new IllegalArgumentException("Invalid location index for " + locationType);
        }

        this.playerIndex = playerIndex;
        this.pawnIndex = pawnIndex;
        this.locationType = locationType;
        this.locationIndex = locationIndex;
    }

    public int getPlayerIndex(){
        return this.playerIndex;
    }

    public int getPawnIndex(){
    return this.pawnIndex;
    }

    public LocationType getLocationType(){
        return this.locationType;
    }

    public int getLocationIndex(){
        return this.locationIndex;
    }

}
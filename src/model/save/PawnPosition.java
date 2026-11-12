package model.save;

/** Stores one pawn's logical, screen-independent location in a snapshot. */
public class PawnPosition {
    private final int playerIndex;
    private final int pawnIndex;
    private final LocationType locationType;
    private final int locationIndex;


    /**
     * Creates a validated saved pawn position.
     *
     * @param playerIndex zero-based owner index
     * @param pawnIndex zero-based pawn index within the player
     * @param locationType board area containing the pawn
     * @param locationIndex index within that board area
     * @throws IllegalArgumentException if an index is invalid for the location
     */
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

    /**
     * Returns the pawn's owner.
     *
     * @return zero-based owner index
     */
    public int getPlayerIndex(){
        return this.playerIndex;
    }

    /**
     * Returns the pawn's index within its owner.
     *
     * @return zero-based pawn index
     */
    public int getPawnIndex(){
        return this.pawnIndex;
    }

    /**
     * Returns the logical board area containing the pawn.
     *
     * @return saved location type
     */
    public LocationType getLocationType(){
        return this.locationType;
    }

    /**
     * Returns the pawn's index within its logical board area.
     *
     * @return saved location index
     */
    public int getLocationIndex(){
        return this.locationIndex;
    }

}

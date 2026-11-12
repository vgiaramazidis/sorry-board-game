package model.save;

/** Identifies the logical board area containing a saved pawn. */
public enum LocationType {
    /** One of the player's two Start slots. */
    START,
    /** A square on the shared outer track. */
    OUTER,
    /** A square on the player's colored Safety track. */
    SAFETY,
    /** One of the player's two Home slots. */
    HOME
}

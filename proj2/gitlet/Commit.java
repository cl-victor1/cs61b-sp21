package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Victor
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private final String message;
    private final Date timestamp;

    /* TODO: fill in the rest of this class. */
    //initial commit
    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
    }

    //other commits
    public Commit(String message) {
        this.message = message;
        this.timestamp = new Date();
    }
}

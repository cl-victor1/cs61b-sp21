package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static gitlet.Repository.COMMIT_DIR;
import static gitlet.Repository.headFile;
import static gitlet.Utils.*;

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
    private final String parent;
    // establish content of the commit: key is filename, value is hashcode of the blob
    private HashMap<String, String> content;


    //initial commit
    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.parent = null;
        this.content = new HashMap<>();
    }

    //other commits
    public Commit(String message) {
        this.message = message;
        this.timestamp = new Date();
        this.parent = readContentsAsString(headFile);
        //deep copy the content of parent
        Commit parentCommit = readObject(join(COMMIT_DIR, this.parent), Commit.class);
        this.content = new HashMap<>(parentCommit.getMap());
    }

    public HashMap getMap() {
        return this.content;
    }

    public String getParent() {
        return this.parent;
    }

    public Date getDate() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public String get(String key) {
        return this.content.get(key);
    }

    public void put(String key, String value) {
        this.content.put(key, value);
    }
}

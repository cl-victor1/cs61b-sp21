package gitlet;

import java.io.Serializable;
import java.util.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.

 *  does at a high level.
 *
 *  @author Victor
 */
public class Commit implements Serializable, Dumpable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private final String message;
    private final Date timestamp;
    private final String parent;
    private final String parentTwo;
    // establish content of the commit: key is filename, value is hashcode of the blob
    private HashMap<String, String> content;
    // private final String hash = calcHash();

    @Override
    public void dump() {
        System.out.printf("parent: %s%ncontent: %s%n", parent, content.toString());
    }

    /**
    private String calcHash() {
        byte[] commitObj = serialize(this);
        return sha1(commitObj);
    }
    public String ownHash() {
        return this.hash;
    }*/

    //initial commit
    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.parent = null;
        this.parentTwo = null;
        this.content = new HashMap<>();
    }

    //other commits
    public Commit(String message) {
        this.message = message;
        this.timestamp = new Date();
        this.parent = readContentsAsString(HEADFILE);
        this.parentTwo = null;
        this.content = new HashMap<>();
        //deep copy the content of parent
        Commit parentCommit = readObject(join(COMMIT_DIR, this.parent), Commit.class);
        ArrayList<String> filesToAdd = new ArrayList<>(parentCommit.getMap().keySet());
        for (String fileName : filesToAdd) {
            this.content.put(fileName, parentCommit.getMap().get(fileName));
        }
        // alert: this.content = new HashMap<>(parentCommit.getMap())
        // will serialize all the commit tree
    }

    //merge commits
    public Commit(String message, String parentTwoHash) {
        this.message = message;
        this.timestamp = new Date();
        this.parent = readContentsAsString(HEADFILE);
        this.parentTwo = parentTwoHash;
        this.content = new HashMap<>();
        //deep copy the content of parent
        Commit parentCommit = readObject(join(COMMIT_DIR, this.parent), Commit.class);
        ArrayList<String> filesToAdd = new ArrayList<>(parentCommit.getMap().keySet());
        for (String fileName : filesToAdd) {
            this.content.put(fileName, parentCommit.getMap().get(fileName));
        }
    }

    public HashMap<String, String> getMap() {
        return this.content;
    }

    public String getParent() {
        return this.parent;
    }

    public String getParentTwo() {
        return this.parentTwo;
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

    public void remove(String key) {
        this.content.remove(key);
    }
}

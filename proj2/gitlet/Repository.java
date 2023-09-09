package gitlet;

import java.io.File;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  This class contains helper functions for main class.
 *
 *  @author Victor
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");
    public static final File BLOB_DIR = join(GITLET_DIR, "blobs");
    public static final File BRANCH_DIR = join(GITLET_DIR, "branches");

    /* TODO: fill in the rest of this class. */
    //constructor
    public Repository() {}

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        BRANCH_DIR.mkdir();

        Commit initialCommit = new Commit();
        String hash = sha1((Object) serialize(initialCommit));

        // save head and master branch
        File headFile = join(GITLET_DIR, "HEAD");
        writeObject(headFile, hash);
        File masterFile = join(BRANCH_DIR, "master");
        writeObject(masterFile, hash);
        // save initialCommit
        File initialCommitFile = join(COMMIT_DIR, hash);
        writeObject(initialCommitFile, initialCommit);
    }

    public void add(String filename) {
        File toAddFile = join(CWD, filename);
        if (!toAddFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
    }


}

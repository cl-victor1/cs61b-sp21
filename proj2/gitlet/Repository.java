package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Utils.readContentsAsString;

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
    public static File headFile = join(GITLET_DIR, "HEAD");
    public static File masterFile = join(BRANCH_DIR, "master");
    public static File stageFile = join(GITLET_DIR, "index");

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

        //establish staging area: key is filename, value is hashcode of the blob
        HashMap<String, String> stageArea = new HashMap<>();
        writeObject(stageFile, stageArea);

        Commit initialCommit = new Commit();
        String initialCommitHash = sha1((Object) serialize(initialCommit));

        // save head and master branch, the content of which is hashcode of a commit
        writeContents(headFile, initialCommitHash);
        writeContents(masterFile, initialCommitHash);
        // save initialCommit
        File initialCommitFile = join(COMMIT_DIR, initialCommitHash);
        writeObject(initialCommitFile, initialCommit);
    }

    public void add(String filename) {
        File toAddFile = join(CWD, filename);
        if (!toAddFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        String toAddFileString = readContentsAsString(toAddFile);
        String toAddFileHash = sha1(toAddFileString);

        //check if the current working version of the file is identical to the version in the current commit
        String currentCommitHash = readContentsAsString(headFile);
        File currentCommitFile = join(COMMIT_DIR, currentCommitHash);
        Commit currentCommit = readObject(currentCommitFile, Commit.class);
        //retrieve staging area
        HashMap<String, String> stageArea = readObject(stageFile, HashMap.class);
        if (toAddFileHash.equals(currentCommit.get(filename))) {
            //remove the file from the staging area if it is already there
            stageArea.remove(filename);
        } else {
            stageArea.put(filename, toAddFileHash);
        }
        writeObject(stageFile, stageArea);
    }

    public void commit(String message) {
        Commit newCommit = new Commit(message);

        //retrieve staging area
        HashMap<String, String> stageArea = readObject(stageFile, HashMap.class);
        if (stageArea.size() == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        //update content
        stageArea.forEach((key, value) -> {
            // save files in .gitlet/blobs
            File workingFile = join(CWD, key);
            File blobFile = join(BLOB_DIR, value);
            writeContents(blobFile, readContentsAsString(workingFile));
            newCommit.put(key, value);
        });

        //update master and head
        String newCommitHash = sha1((Object) serialize(newCommit));
        writeContents(headFile, newCommitHash);
        writeContents(masterFile, newCommitHash);

        // clear stageArea after commit
        stageArea = new HashMap<>();
        writeObject(stageFile, stageArea);

        // save newCommit
        File newCommitFile = join(COMMIT_DIR, newCommitHash);
        writeObject(newCommitFile, newCommit);

        //System.out.println(sha1((Object) serialize(newCommit)));
    }

    // retrieve Commit from its hashcode
    private HashMap<String,String> getCommitMap(String hash) {
        File CommitFile = join(COMMIT_DIR, hash);
        if (CommitFile.exists()) {
            Commit thisCommit = readObject(CommitFile, Commit.class);
            return thisCommit.getMap();
        } else {
            return null;
        }
    }

    public void checkout(String... operands) {
        //java gitlet.Main checkout -- [file name]
        if (operands[0].equals("--")) {
            String filename = operands[1];
            //retrieve the map of head commit
            HashMap<String,String> headCommitMap = getCommitMap(readContentsAsString(headFile));
            if (!headCommitMap.containsKey(filename)) {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }
            else {
                String blobHash = headCommitMap.get(filename);
                File blobFile = join(BLOB_DIR, blobHash);
                File workingFile = join(CWD, filename);
                writeContents(workingFile, readContentsAsString(blobFile));
            }
        }
        //java gitlet.Main checkout [commit id] -- [file name]
        else if (operands[1].equals("--")) {
            String commitHash = operands[0];
            String filename = operands[2];
            HashMap<String,String> thisCommitMap = getCommitMap(commitHash);
            if (thisCommitMap == null) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            if (!thisCommitMap.containsKey(filename)) {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }
            else {
                String blobHash = thisCommitMap.get(filename);
                File blobFile = join(BLOB_DIR, blobHash);
                File workingFile = join(CWD, filename);
                writeContents(workingFile, readContentsAsString(blobFile));
            }
        }

        // java gitlet.Main checkout [branch name]
        //else {}
    }

    public void log() {
        File CommitFile = join(COMMIT_DIR, readContentsAsString(headFile));
        Commit thisCommit = readObject(CommitFile, Commit.class);
        while (thisCommit != null) {
            //convert Date format
            Date date = thisCommit.getDate();
            Formatter formatter = new Formatter(Locale.US);
            formatter.format("Date: %1$ta %1$tb %1$te %1$tT %1$tY %1$tz", date);
            String formattedDate = formatter.toString();

            System.out.println("===");
            System.out.printf("commit %s%n", sha1((Object) serialize(thisCommit)));
            System.out.println(formattedDate);
            System.out.println(thisCommit.getMessage());
            System.out.println();

            //update thisCommit
            if (thisCommit.getParent() != null) {
                CommitFile = join(COMMIT_DIR, thisCommit.getParent());
                thisCommit = readObject(CommitFile, Commit.class);
            } else {
                thisCommit = null;
            }
        }
    }


}

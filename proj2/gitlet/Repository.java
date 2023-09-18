package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import static gitlet.Utils.*;
import static gitlet.Utils.readContentsAsString;

/** Represents a gitlet repository.
 *  This class contains helper functions for main class.
 *
 *  @author Victor
 */
public class Repository {
    /**
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
    public static final File HEADFILE = join(GITLET_DIR, "HEAD");
    private static final File STAGEFILE = join(GITLET_DIR, "index");
    private static final File CURRENTBRANCH = join(GITLET_DIR, "current");

    //constructor
    public Repository() { }

    //return hash of an object
    private String hash(Serializable obj) {
        return sha1((Object) serialize(obj));
    }

    // retrieve Commit from its hashcode
    private HashMap<String, String> getCommitMap(String hash) {
        File commitFile = join(COMMIT_DIR, hash);
        if (commitFile.exists()) {
            Commit thisCommit = readObject(commitFile, Commit.class);
            return thisCommit.getMap();
        } else {
            return null;
        }
    }

    private Commit getCommit(File file) {
        File currentCommitFile = join(COMMIT_DIR, readContentsAsString(file));
        return readObject(currentCommitFile, Commit.class);
    }

    private Commit getCommit(String hash) {
        File currentCommitFile = join(COMMIT_DIR, hash);
        return readObject(currentCommitFile, Commit.class);
    }

    private void verifyGitlet() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    // duplicate code in checkout, reset and merge
    private void duplicate(String commitID, Set<String> toCheckKeySet, Set<String> currentKeySet) {
        // find the difference between above two sets based on toCheckKeySet
        HashSet<String> diff1 = new HashSet<>();
        for (String filename : toCheckKeySet) {
            if (!currentKeySet.contains(filename)) {
                diff1.add(filename);
            }
        }
        for (String filename : diff1) {
            File file = join(CWD, filename);
            if (file.exists()) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        //Takes all files in the commit at the head of the given branch,
        // and puts them in the working directory, overwriting the versions of
        // the files that are already there if they exist.
        HashMap<String,String> toCheckCommitMap = getCommitMap(commitID);
        for (String filename : toCheckKeySet) {
            String blobHash = toCheckCommitMap.get(filename);
            File blobFile = join(BLOB_DIR, blobHash);
            File workingFile = join(CWD, filename);
            writeContents(workingFile, readContentsAsString(blobFile));
        }
        // Any files that are tracked in the current branch but are not present in the checked-out branch are deleted.
        // find the difference between above two sets based on currentKeySet
        HashSet<String> diff2 = new HashSet<>();
        for (String filename : currentKeySet) {
            if (!toCheckKeySet.contains(filename)) {
                diff2.add(filename);
            }
        }
        for (String filename : diff2) {
            File file = join(CWD, filename);
            if (file.exists()) {
                restrictedDelete(file);
            }
        }
        // clear stageArea after commit
        writeObject(STAGEFILE, new HashMap<>());
        // update HEAD
        writeContents(HEADFILE, commitID);
    }

    public void mergeCommit(String message, String parentTwoHash) {
        verifyGitlet();
        Commit newCommit = new Commit(message, parentTwoHash);

        //retrieve staging area
        HashMap<String, String> stageArea = readObject(STAGEFILE, HashMap.class);
        if (stageArea.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        //update content
        stageArea.forEach((key, value) -> {
            // untrack the file that has been staged for removal by the rm command
            if (value == null) {
                newCommit.remove(key);
            } else {
                // save files in .gitlet/blobs
                File workingFile = join(CWD, key);
                File blobFile = join(BLOB_DIR, value);
                writeContents(blobFile, readContentsAsString(workingFile));
                newCommit.put(key, value);
            }
        });

        //update branch and head
        String newCommitHash = hash(newCommit);
        writeContents(HEADFILE, newCommitHash);
        File currBranch = join(BRANCH_DIR, readContentsAsString(CURRENTBRANCH));
        writeContents(currBranch, newCommitHash);

        // clear stageArea after commit
        stageArea = new HashMap<>();
        writeObject(STAGEFILE, stageArea);

        // save newCommit
        File newCommitFile = join(COMMIT_DIR, newCommitHash);
        writeObject(newCommitFile, newCommit);
    }

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control "
                    + "system already exists in the current directory.");
            System.exit(0);
        }

        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        BRANCH_DIR.mkdir();

        //establish staging area: key is filename, value is hashcode of the blob
        HashMap<String, String> stageArea = new HashMap<>();
        writeObject(STAGEFILE, stageArea);

        Commit initialCommit = new Commit();
        String initialCommitHash = sha1((Object) serialize(initialCommit));

        // save head and master branch, the content of which is hashcode of a commit
        writeContents(HEADFILE, initialCommitHash);
        writeContents(join(BRANCH_DIR, "master"), initialCommitHash);
        // save initialCommit
        File initialCommitFile = join(COMMIT_DIR, initialCommitHash);
        writeObject(initialCommitFile, initialCommit);
        // save current branch
        writeContents(CURRENTBRANCH, "master");
    }

    public void add(String filename) {
        verifyGitlet();
        File toAddFile = join(CWD, filename);
        if (!toAddFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        String toAddFileString = readContentsAsString(toAddFile);
        String toAddFileHash = sha1(toAddFileString);

        //check if the current working version of the file is identical
        // to the version in the current commit
        String currentCommitHash = readContentsAsString(HEADFILE);
        File currentCommitFile = join(COMMIT_DIR, currentCommitHash);
        Commit currentCommit = readObject(currentCommitFile, Commit.class);
        //retrieve staging area
        HashMap<String, String> stageArea = readObject(STAGEFILE, HashMap.class);
        if (toAddFileHash.equals(currentCommit.get(filename))) {
            //remove the file from the staging area if it is already there
            stageArea.remove(filename);
        } else {
            stageArea.put(filename, toAddFileHash);
        }
        writeObject(STAGEFILE, stageArea);
    }

    public void commit(String message) {
        verifyGitlet();
        Commit newCommit = new Commit(message);

        //retrieve staging area
        HashMap<String, String> stageArea = readObject(STAGEFILE, HashMap.class);
        if (stageArea.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        //update content
        stageArea.forEach((key, value) -> {
            // untrack the file that has been staged for removal by the rm command
            if (value == null) {
                newCommit.remove(key);
            } else {
                // save files in .gitlet/blobs
                File workingFile = join(CWD, key);
                File blobFile = join(BLOB_DIR, value);
                writeContents(blobFile, readContentsAsString(workingFile));
                newCommit.put(key, value);
            }
        });

        //update branch and head
        String newCommitHash = hash(newCommit);
        writeContents(HEADFILE, newCommitHash);
        File currBranch = join(BRANCH_DIR, readContentsAsString(CURRENTBRANCH));
        writeContents(currBranch, newCommitHash);

        // clear stageArea after commit
        stageArea = new HashMap<>();
        writeObject(STAGEFILE, stageArea);

        // save newCommit
        File newCommitFile = join(COMMIT_DIR, newCommitHash);
        writeObject(newCommitFile, newCommit);
    }

    public void checkout(String... operands) {
        verifyGitlet();
        //java gitlet.Main checkout -- [file name]
        if (operands.length == 2 && operands[0].equals("--")) {
            String filename = operands[1];
            //retrieve the map of head commit
            HashMap<String,String> headCommitMap = getCommitMap(readContentsAsString(HEADFILE));
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
        else if (operands.length == 3 && operands[1].equals("--")) {
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
        else if (operands.length == 1) {
            String branchName = operands[0];
            File toBranch = join(BRANCH_DIR, branchName);
            if (!toBranch.exists()) {
                System.out.println("No such branch exists.");
                System.exit(0);
            }
            if (branchName.equals(readContentsAsString(CURRENTBRANCH))) {
                System.out.println("No need to checkout the current branch.");
                System.exit(0);
            }
            // retrieve filenames tracked by the current branch
            String currentCommitHash = readContentsAsString(HEADFILE);
            Set<String> currentKeySet= getCommitMap(currentCommitHash).keySet();
            // retrieve filenames tracked by the to-check-out branch
            String toCheckCommitHash = readContentsAsString(toBranch);
            Set<String> toCheckKeySet= getCommitMap(toCheckCommitHash).keySet();
            // use duplicate code
            duplicate(toCheckCommitHash, toCheckKeySet, currentKeySet);
            // update current Branch
            writeContents(CURRENTBRANCH, branchName);
        }
        else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    public void log() {
        verifyGitlet();
        File commitFile = join(COMMIT_DIR, readContentsAsString(HEADFILE));
        Commit thisCommit = readObject(commitFile, Commit.class);
        while (thisCommit != null) {
            //convert Date format
            Date date = thisCommit.getDate();
            Formatter formatter = new Formatter(Locale.US);
            formatter.format("Date: %1$ta %1$tb %1$te %1$tT %1$tY %1$tz", date);
            String formattedDate = formatter.toString();

            System.out.println("===");
            System.out.printf("commit %s%n", hash(thisCommit));
            if (thisCommit.getParentTwo() != null) {
                System.out.printf("Merge: %s %s%n", thisCommit.getParent().substring(0,7), thisCommit.getParentTwo().substring(0,7));
            }
            System.out.println(formattedDate);
            System.out.println(thisCommit.getMessage());
            System.out.println();

            //update thisCommit
            if (thisCommit.getParent() != null) {
                commitFile = join(COMMIT_DIR, thisCommit.getParent());
                thisCommit = readObject(commitFile, Commit.class);
            } else {
                thisCommit = null;
            }
        }
    }

    public void globalLog() {
        verifyGitlet();
        List<String> filenames = plainFilenamesIn(COMMIT_DIR);
        for (String filename : filenames) {
            File commitFile = join(COMMIT_DIR, filename);
            Commit thisCommit = readObject(commitFile, Commit.class);
            Date date = thisCommit.getDate();
            Formatter formatter = new Formatter(Locale.US);
            formatter.format("Date: %1$ta %1$tb %1$te %1$tT %1$tY %1$tz", date);
            String formattedDate = formatter.toString();

            System.out.println("===");
            System.out.printf("commit %s%n", hash(thisCommit));
            System.out.println(formattedDate);
            System.out.println(thisCommit.getMessage());
            System.out.println();
        }
    }

    public void rm(String filename) {
        verifyGitlet();
        //retrieve staging area
        HashMap<String, String> stageArea = readObject(STAGEFILE, HashMap.class);
        //retrieve the map of head commit
        HashMap<String, String> headCommitMap = getCommitMap(readContentsAsString(HEADFILE));

        //Failure cases
        if (!stageArea.containsKey(filename) && !headCommitMap.containsKey(filename)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        //Unstage the file if it is currently staged for addition
        if (stageArea.containsKey(filename)) {
            stageArea.remove(filename);
            writeObject(STAGEFILE, stageArea);
        }
        // If the file is tracked in the current commit,
        // stage it for removal and remove the file from the working directory
        if (headCommitMap.containsKey(filename)) {
            stageArea.put(filename, null);
            writeObject(STAGEFILE, stageArea);
            File fileToDelete = join(CWD, filename);
            restrictedDelete(fileToDelete);
        }
    }

    public void find(String commitMessage) {
        verifyGitlet();
        List<String> filenames = plainFilenamesIn(COMMIT_DIR);
        //number of commits that have the given commit message
        int indicator = 0;
        for (String filename : filenames) {
            File commitFile = join(COMMIT_DIR, filename);
            Commit thisCommit = readObject(commitFile, Commit.class);
            if (thisCommit.getMessage().equals(commitMessage)) {
                System.out.println(hash(thisCommit));
                indicator++;
            }
        }
        if (indicator == 0) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    public void status() {
        verifyGitlet();
        List<String> branches = plainFilenamesIn(BRANCH_DIR);
        //retrieve staging area
        HashMap<String, String> stageArea = readObject(STAGEFILE, HashMap.class);
        ArrayList<String> addition = new ArrayList<>();
        ArrayList<String> removal = new ArrayList<>();
        // split the stageArea (addition and removal)
        stageArea.forEach((key, value) -> {
            if (value == null) {
                removal.add(key);
            } else {
                addition.add(key);
            }
        });
        addition.sort(Comparator.naturalOrder());
        removal.sort(Comparator.naturalOrder());
        branches.sort(Comparator.naturalOrder());

        System.out.println("=== Branches ===");
        for (String branch : branches) {
            if (branch.equals("master")) {
                System.out.println("*master");
            } else  {
                System.out.println(branch);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        for (String file : addition) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String file : removal) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public void branch(String branch) {
        verifyGitlet();
        File newBranch = join(BRANCH_DIR, branch);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        String currentCommitHash = readContentsAsString(HEADFILE);
        writeContents(newBranch, currentCommitHash);
    }

    public void rmBranch(String branch) {
        verifyGitlet();
        File toRemoveBranch = join(BRANCH_DIR, branch);
        if (!toRemoveBranch.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branch.equals(readContentsAsString(CURRENTBRANCH))) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        toRemoveBranch.delete();
    }

    public void reset(String commitID) {
        verifyGitlet();
        File commitFile = join(COMMIT_DIR, commitID);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        // retrieve filenames tracked by the current branch
        String currentCommitHash = readContentsAsString(HEADFILE);
        Set<String> currentKeySet = getCommitMap(currentCommitHash).keySet();
        // retrieve filenames tracked by the target commit
        Set<String> toCheckKeySet = getCommitMap(commitID).keySet();
        // use duplicate code
        duplicate(commitID, toCheckKeySet, currentKeySet);
        // update current branch head
        File currBranch = join(BRANCH_DIR, readContentsAsString(CURRENTBRANCH));
        writeContents(currBranch, commitID);
    }

    public void merge(String givenBranch) {
        verifyGitlet();

        //***Failure cases
        if (!readObject(STAGEFILE, HashMap.class).isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        File givenBranchFile = join(BRANCH_DIR, givenBranch);
        if (!givenBranchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (givenBranch.equals(readContentsAsString(CURRENTBRANCH))) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        // retrieve filenames tracked by the current branch
        String currentCommitHash = readContentsAsString(HEADFILE);
        Set<String> currentKeySet= getCommitMap(currentCommitHash).keySet();
        // retrieve filenames tracked by the to-check-out branch
        String toCheckCommitHash = readContentsAsString(join(BRANCH_DIR, givenBranch));
        Set<String> toCheckKeySet= getCommitMap(toCheckCommitHash).keySet();
        // find the difference between above two sets based on toCheckKeySet
        HashSet<String> diff1 = new HashSet<>();
        for (String filename : toCheckKeySet) {
            if (!currentKeySet.contains(filename)) {
                diff1.add(filename);
            }
        }
        for (String filename : diff1) {
            File file = join(CWD, filename);
            if (file.exists()) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        // ***find split commit
        // 1.backtrace the current branch
        HashSet<String> currentTrace = backtrace();
        // 2.BFS from givenBranch until meet a commit in currentTrace, and that is split commit
        String splitCommitHash = BFS(givenBranch, currentTrace);

        // check if the split point is the same commit as the given branch
        if (splitCommitHash.equals(readContentsAsString(join(BRANCH_DIR, givenBranch)))) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        // If the split point is the current branch, then the effect is to check out the given branch
        Commit currentCommit = getCommit(HEADFILE);
        if (splitCommitHash.equals(hash(currentCommit))) {
            checkout(givenBranch);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }

        //retrieve staging area
        HashMap<String, String> splitCommitMap = getCommitMap(splitCommitHash);
        HashMap<String, String> currentCommitMap = currentCommit.getMap();
        HashMap<String, String> givenBranchMap = getCommitMap(readContentsAsString(givenBranchFile));
        // ***different situations
        splitCommitMap.forEach((key, value) -> {
            // 1.Any files that have been modified in the given branch since the split point,
            // but not modified in the current branch since the split point
            if (currentCommitMap.containsKey(key) && currentCommitMap.get(key).equals(value)
                    && givenBranchMap.containsKey(key) && !givenBranchMap.get(key).equals(value)) {
                checkout(readContentsAsString(givenBranchFile),"--",key);
                add(key);
            }
            // 6.Any files present at the split point, unmodified in the current branch,
            // and absent in the given branch should be removed (and untracked)
            else if (currentCommitMap.containsKey(key) &&
                    currentCommitMap.get(key).equals(value) && !givenBranchMap.containsKey(key)) {
                // TODO: to verify rm
                rm(key);
                /*
                HashMap<String, String> stageArea = readObject(STAGEFILE, HashMap.class);
                stageArea.put(key, null);
                writeObject(STAGEFILE, stageArea);
                File fileToDelete = join(CWD, key);
                restrictedDelete(fileToDelete);
                 */
            }
            else if (currentCommitMap.containsKey(key) && givenBranchMap.containsKey(key)
                    && !currentCommitMap.get(key).equals(value) && !givenBranchMap.get(key).equals(value)
                    && !currentCommitMap.get(key).equals(givenBranchMap.get(key))) {
                String result = "<<<<<<< HEAD%n" + readContentsAsString(join(BLOB_DIR, currentCommitMap.get(key)))
                        + "=======%n" + readContentsAsString(join(BLOB_DIR, givenBranchMap.get(key))) +
                        ">>>>>>>";
                writeContents(join(CWD, key), result);
                add(key);
                System.out.println("Encountered a merge conflict.");
            }
            else if (currentCommitMap.containsKey(key)
                    && !currentCommitMap.get(key).equals(value) && !givenBranchMap.containsKey(key)) {
                String result = "<<<<<<< HEAD%n" + readContentsAsString(join(BLOB_DIR, currentCommitMap.get(key)))
                        + "=======%n" + "" + ">>>>>>>";
                writeContents(join(CWD, key), result);
                add(key);
                System.out.println("Encountered a merge conflict.");
            }
            else if (!currentCommitMap.containsKey(key) && givenBranchMap.containsKey(key)
                    && !givenBranchMap.get(key).equals(value)) {
                String result = "<<<<<<< HEAD%n" + ""
                        + "=======%n" + readContentsAsString(join(BLOB_DIR, givenBranchMap.get(key))) +
                        ">>>>>>>";
                writeContents(join(CWD, key), result);
                add(key);
                System.out.println("Encountered a merge conflict.");
            }
        });
        // 5.Any files that were not present at the split point and are
        // present only in the given branch should be checked out and staged.
        givenBranchMap.forEach((key, value) -> {
            if (!currentCommitMap.containsKey(key) && !splitCommitMap.containsKey(key)) {
                checkout(readContentsAsString(givenBranchFile),"--",key);
                add(key);
            }
        });
        // 8.the file was absent at the split point and has different contents in the given and current branches
        givenBranchMap.forEach((key, value) -> {
            if (!splitCommitMap.containsKey(key) && currentCommitMap.containsKey(key)
            && !currentCommitMap.get(key).equals(givenBranchMap.get(key))) {
                String result = "<<<<<<< HEAD%n" + readContentsAsString(join(BLOB_DIR, currentCommitMap.get(key)))
                        + "=======%n" + readContentsAsString(join(BLOB_DIR, givenBranchMap.get(key))) +
                        ">>>>>>>";
                writeContents(join(CWD, key), result);
                add(key);
                System.out.println("Encountered a merge conflict.");
            }
        });

        // commit
        String mergeMessage = "Merged " + givenBranch + " into " + readContentsAsString(CURRENTBRANCH);
        mergeCommit(mergeMessage, readContentsAsString(join(BRANCH_DIR, givenBranch)));
    }

    private String BFS(String branchName, HashSet<String> currentTrace) {
        String splitCommitHash = null;
        // initiate a queue to store backtracked commits of the given branch
        Queue<Commit> queue = new LinkedList<>();
        // store hash of visited commits
        HashSet<String> visited = new HashSet<>();

        Commit firstCommit = getCommit(join(BRANCH_DIR, branchName));
        String firstCommitHash = hash(firstCommit);
        queue.offer(firstCommit);
        visited.add(firstCommitHash);
        while (!queue.isEmpty()) {
            Commit thisCommit = queue.poll();
            // check if thisCommit is the split commit
            if (currentTrace.contains(hash(thisCommit))) {
                splitCommitHash = hash(thisCommit);
                break;
            }
            // traverse parent
            if (!visited.contains(thisCommit.getParent()) && thisCommit.getParent() != null) {
                    queue.offer(getCommit(thisCommit.getParent()));
                    visited.add(thisCommit.getParent());
            }
            // traverse parentTwo
            if (!visited.contains(thisCommit.getParentTwo()) && thisCommit.getParentTwo() != null) {
                    queue.offer(getCommit(thisCommit.getParentTwo()));
                    visited.add(thisCommit.getParentTwo());
            }
        }
        return splitCommitHash;
    }

    // backtrace the current branch
    private HashSet<String> backtrace() {
        // initiate a queue to store backtracked commits of HEAD
        Queue<Commit> queue = new LinkedList<>();
        // store hash of visited commits
        HashSet<String> visited = new HashSet<>();

        Commit currentCommit = getCommit(HEADFILE);
        String currentCommitHash = readContentsAsString(HEADFILE);
        queue.offer(currentCommit);
        visited.add(currentCommitHash);
        while (!queue.isEmpty()) {
            Commit thisCommit = queue.poll();
            // traverse parent
            if (!visited.contains(thisCommit.getParent()) && thisCommit.getParent() != null) {
                queue.offer(getCommit(thisCommit.getParent()));
                visited.add(thisCommit.getParent());
            }
            // traverse parentTwo
            if (!visited.contains(thisCommit.getParentTwo()) && thisCommit.getParentTwo() != null) {
                queue.offer(getCommit(thisCommit.getParentTwo()));
                visited.add(thisCommit.getParentTwo());
            }
        }
        return visited;
    }


}

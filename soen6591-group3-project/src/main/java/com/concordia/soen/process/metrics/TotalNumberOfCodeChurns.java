package com.concordia.soen.process.metrics;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.Repository;

public class TotalNumberOfCodeChurns {
	private static Git git;

	private static int calculateChurn(Git git, RevCommit commit, RevCommit parentCommit) throws IOException {
        // Set up the diff formatter
        DiffFormatter diffFormatter = new DiffFormatter(System.out);
        diffFormatter.setRepository(git.getRepository());
        diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
        diffFormatter.setDetectRenames(true);
        
        // Get the diffs
        List<DiffEntry> diffs = diffFormatter.scan(parentCommit, commit);
        // Calculate the churn
        int churn = 0;
        int addedLines = 0;
        int deletedLines = 0;
        int modifiedLines = 0;
        for (DiffEntry diff : diffs) {
            if (diff.getChangeType() == DiffEntry.ChangeType.ADD) {
            	addedLines += diff.getNewPath().length();
                System.out.println("added: " + diff.getNewPath().length());
            } else if (diff.getChangeType() == DiffEntry.ChangeType.DELETE) {
            	deletedLines += diff.getOldPath().length();
                System.out.println("deleted: " + diff.getOldPath().length());
            } else if (diff.getChangeType() == DiffEntry.ChangeType.MODIFY) {
            	modifiedLines += diff.getNewPath().length() - diff.getOldPath().length();
                System.out.println("modified: " + (diff.getNewPath().length() - diff.getOldPath().length()));
            }
        }
        churn = addedLines + deletedLines + modifiedLines;
        
        return churn;
    }
	  
	  
	public static void main(String[] args) {
		try {
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
		    Repository repo = builder.setGitDir(new File("/Users/phong/Documents/abc" + "/.git"))
		            .setMustExist(true).build();
		    git = new Git(repo);
			Iterable<RevCommit> commits = git.log().all().call();
            for (RevCommit commit : commits) {
            	 String commitId = commit.getName();
                 String commitMessage = commit.getFullMessage();
                 System.out.println(commitId);
                 System.out.println(commitMessage);
                 
                 // Get the parent commit
                 RevCommit parentCommit = null;
                 if (commit.getParentCount() > 0) {
                     parentCommit = commit.getParent(0);
                 }
                 
                 // Calculate the churn for this commit
                 int churn = calculateChurn(git, commit, parentCommit);
                 
                 System.out.println("Total of code churns: " + churn);
            }
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

package com.concordia.soen.process.metrics;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.DepthWalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;

public class TotalNumberOfCodeChurns {
	private static Git git;

	public static int getCodeChurn(Repository repository, RevCommit commit) throws IOException {
        int codeChurn = 0;

        try (RevWalk revWalk = new RevWalk(repository, 100)) {
            RevTree parentTree = commit.getParentCount() > 0 ? revWalk.parseTree(commit.getParent(0).getId()) : null;
            RevTree commitTree = revWalk.parseTree(commit.getId());

            if (parentTree != null) {
                try (ObjectReader reader = repository.newObjectReader();
                     DiffFormatter diffFormatter = new DiffFormatter(System.out)) {
                    diffFormatter.setRepository(repository);
                    List<DiffEntry> diffs = diffFormatter.scan(parentTree, commitTree);
                    for (DiffEntry diff : diffs) {
                        // Only consider modifications to Java files
                        if (diff.getNewPath().endsWith(".java")) {
                            List<Edit> edits = diffFormatter.toFileHeader(diff).toEditList();
                            for (Edit edit : edits) {
                            	codeChurn += edit.getLengthA(); // deleted lines
                                codeChurn += edit.getLengthB(); // added lines
                            }
                        }
                    }
                }
            } else {
                // If there are no parents (i.e., first commit), consider all lines as code churn
                codeChurn = countLines(commit.getFullMessage());
            }
        }

        return codeChurn;
    }

    public static int countLines(String text) {
        String[] lines = text.split("\r\n|\r|\n");
        return lines.length;
    }
	  
	public static void main(String[] args) {
		try {
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
		    Repository repo = builder.setGitDir(new File("/Users/phong/Documents/abc" + "/.git"))
		            .setMustExist(true).build();
		    git = new Git(repo);
			Iterable<RevCommit> commits = git.log().all().call();
			int totalChurn = 0;
			int codeChurn = 0;
            for (RevCommit commit : commits) {
            	System.out.println("Commit ID: " + commit.getId().getName());
                System.out.println("Date: " + commit.getAuthorIdent().getWhen());
                System.out.println("Message: " + commit.getFullMessage());
                codeChurn = getCodeChurn(repo, commit);
                System.out.println("Code churn: " + codeChurn);
                System.out.println("---------------------------------------------");
                totalChurn += codeChurn;
            }
            System.out.println("********************************************");
            System.out.println("Total of code churns in branch: " + totalChurn);
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

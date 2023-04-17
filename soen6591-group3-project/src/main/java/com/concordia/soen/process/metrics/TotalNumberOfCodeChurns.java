package com.concordia.soen.process.metrics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.revwalk.DepthWalk.RevWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;

public class TotalNumberOfCodeChurns {
	private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
	    try (RevWalk walk = new RevWalk(repository,1000)) {
	        RevCommit commit = walk.parseCommit(repository.resolve(objectId));
	        ObjectId treeId = commit.getTree().getId();
	        try (ObjectReader reader = repository.newObjectReader()) {
	            return new CanonicalTreeParser(null, reader, treeId);
	        }
	    }
	}
	private static Git git;
	public static void commit_logs() throws IOException, NoHeadException, GitAPIException {
	    List<String> logMessages = new ArrayList<String>();
	    FileRepositoryBuilder builder = new FileRepositoryBuilder();
	    Repository repo = builder.setGitDir(new File("/Users/phong/Documents/abc" + "/.git"))
	            .setMustExist(true).build();
	    git = new Git(repo);
	    Iterable<RevCommit> log = git.log().call();
	    RevCommit previousCommit = null;
	    int numberOfCommits = 0;
	    for (RevCommit commit : log) {
	        if (previousCommit != null) {
	            AbstractTreeIterator oldTreeIterator = getCanonicalTreeParser( previousCommit );
	            AbstractTreeIterator newTreeIterator = getCanonicalTreeParser( commit );
	            OutputStream outputStream = new ByteArrayOutputStream();
	            try( DiffFormatter formatter = new DiffFormatter( outputStream ) ) {
	              formatter.setRepository( git.getRepository() );
	              formatter.format( oldTreeIterator, newTreeIterator );
	            }
	            String diff = outputStream.toString();
	            //System.out.println(diff);
	        }
	        System.out.println("LogCommit: " + commit);
//	        String logMessage = commit.getFullMessage();
//	        System.out.println("LogMessage: " + logMessage);
//	        logMessages.add(logMessage.trim());
	        previousCommit = commit;
	        numberOfCommits ++;
	    }
	    git.close();
	    System.out.println("Number of commits: " + numberOfCommits);
	}


	  private static AbstractTreeIterator getCanonicalTreeParser( ObjectId commitId ) throws IOException {
	    try( RevWalk walk = new RevWalk( git.getRepository(),100 ) ) {
	      RevCommit commit = walk.parseCommit( commitId );
	      ObjectId treeId = commit.getTree().getId();
	      try( ObjectReader reader = git.getRepository().newObjectReader() ) {
	        return new CanonicalTreeParser( null, reader, treeId );
	      }
	    }
	  }

	public static void main(String[] args) {
//		try {
//			commit_logs();
//		} catch (NoHeadException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (GitAPIException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
		// TODO Auto-generated method stub
		try {
//			String userDir = System.getProperty("user.dir");
//			String path = "/src/main/java/com/concordia/soen/process/metrics/TotalNumberOfChanges.java";
//			Git git = Git.open(new File(userDir+path));
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
		    Repository repo = builder.setGitDir(new File("/Users/phong/Documents/abc" + "/.git"))
		            .setMustExist(true).build();
		    Git git = new Git(repo);
//		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		    Date startDate = dateFormat.parse("2021-01-01");
//		    Date endDate = dateFormat.parse("2021-12-31");
//
//		    git.log().setRevFilter(AndRevFilter.)
			Iterable<RevCommit> commits = git.log().call();
			int totalCodeChurns = 0;
			for (RevCommit commit : commits) {
				RevCommit parent = commit.getParent(0);
			    if (parent == null) {
			        continue;
			    }

			    DiffCommand diffCommand = git.diff();
			    diffCommand.setOldTree(getCanonicalTreeParser( parent ));
			    diffCommand.setNewTree(getCanonicalTreeParser(commit));
			    List<DiffEntry> diffs = diffCommand.call();

			    DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
			    diffFormatter.setRepository(repo);
			    for (DiffEntry diff : diffs) {
			        if (diff.getChangeType() == ChangeType.DELETE) {
			            // File was deleted
			        } else {
			            String path = diff.getNewPath();
			            // File was added or modified
			            ObjectId newObjectId = diff.getNewId().toObjectId();
			            ObjectId oldObjectId = diff.getOldId().toObjectId();
			            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			            diffFormatter.format(diff);
			            String diffContent = outputStream.toString();

			            int addedLines = 0;
			            int deletedLines = 0;

			            for (String line : diffContent.split("\n")) {
			                if (line.startsWith("+")) {
			                    addedLines++;
			                } else if (line.startsWith("-")) {
			                    deletedLines++;
			                }
			            }

			            // Calculate code churn for this file
			            int fileChurn = addedLines + deletedLines;

			            // Add to total code churn
			            totalCodeChurns += fileChurn;
			        }
			    }

			}
//
			System.out.println("Total code churns: " + totalCodeChurns);
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoHeadException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (GitAPIException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
//		try {
//			Git git = Git.cloneRepository()
//			  .setURI("https://github.com/phongnq2712/soen6591-group3-project.git")
//			  .setDirectory(new File("/Users/phong/Documents/abc"))
////			  .setBranchesToClone(Arrays.asList("main"))
////			  .setBranch("main")
//			  .call();
			
//			Git git = Git.open(new File("/Users/phong/Documents/abc/soen6591-group3-project/src/main/java/com/concordia/soen/flow/metrics/CatchSizeLOC.java"));
////			Repository repository = git.getRepository();
//			Iterable<RevCommit> commits = git.log().call();
//			int totalChanges = 0;
//			for (RevCommit commit : commits) {
//			    List<DiffEntry> diffs = git.diff()
//			        .setOldTree(prepareTreeParser(git.getRepository(), commit.getParent(0).getId().name()))
//			        .setNewTree(prepareTreeParser(git.getRepository(), commit.getId().name()))
//			        .call();
//
//			    totalChanges += diffs.size();
//			}
//
//			System.out.println("Total changes: " + totalChanges);
//						
//			
		} catch (InvalidRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//
	}

}

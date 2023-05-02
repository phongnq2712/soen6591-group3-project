package com.concordia.soen.process.metrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.DepthWalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.concordia.soen.flow.metrics.FileUtil;

import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;

public class TotalNumberOfChangesNCodeChurns {
	private static RevWalk revWalk;

	public static int getCodeChurn(Repository repository, RevCommit commit, 
			HashMap<String, ArrayList<Integer>> hmCommits, FileWriter fileWriter) throws IOException {
        int totalCodeChurn = 0;

        try {
        	revWalk = new RevWalk(repository, 100);
            RevTree parentTree = commit.getParentCount() > 0 ? revWalk.parseTree(commit.getParent(0).getId()) : null;
            RevTree commitTree = revWalk.parseTree(commit.getId());

            if (parentTree != null) {
                try (ObjectReader reader = repository.newObjectReader();
                     DiffFormatter diffFormatter = new DiffFormatter(System.out)) {
                    diffFormatter.setRepository(repository);
                    List<DiffEntry> diffs = diffFormatter.scan(parentTree, commitTree);
                    int codeChurnByFile;
                    for (DiffEntry diff : diffs) {
                    	codeChurnByFile = 0;
                    	ArrayList<Integer> metricsList = new ArrayList<>();
                     	metricsList.add(0);
                     	metricsList.add(0);
                        // Only consider modifications to Java files
                    	
                        if (diff.getNewPath().endsWith(".java")) {
                        	int commitCount = (hmCommits.get(diff.getNewPath()) != null) ? hmCommits.get(diff.getNewPath()).get(0) : 0;// .get(0);// getOrDefault(diff.getNewPath(), 0);
                        	metricsList.set(0, commitCount + 1);
                        	
                            List<Edit> edits = diffFormatter.toFileHeader(diff).toEditList();
                            for (Edit edit : edits) {
                            	totalCodeChurn += edit.getLengthA(); // deleted lines
                            	totalCodeChurn += edit.getLengthB(); // added lines
                            	codeChurnByFile += edit.getLengthA();
                            	codeChurnByFile += edit.getLengthB();
                            	int codeChurnCount = (hmCommits.get(diff.getNewPath()) != null) ? hmCommits.get(diff.getNewPath()).get(1) : 0;
                            	metricsList.set(1, codeChurnByFile + codeChurnCount);
                            }
                            if(hmCommits.containsKey(diff.getNewPath())) {
                            	hmCommits.replace(diff.getNewPath(), metricsList);
                            } else {
                            	hmCommits.put(diff.getNewPath(), metricsList);// commitCount + 1);
                            }
                        }
                    }
                }
            } else {
                // If there are no parents, consider all lines as code churn
            	totalCodeChurn = countLines(commit.getFullMessage());
            }
        } catch (Exception e) {
			System.out.println("Error(s) occur");
			e.printStackTrace();
		}

        return totalCodeChurn;
    }

    public static int countLines(String text) {
        String[] lines = text.split("\r\n|\r|\n");
        return lines.length;
    }
    
	public static void main(String[] args) {
		try {
			String fileOutput = "hibernate_Git_Jira.csv";
			String startDateStr = "2023-01-01";
			String endDateStr = "2023-04-23";
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        Date startDate = null;
	        Date endDate = null;
			
	        startDate = dateFormat.parse(startDateStr);
            endDate = dateFormat.parse(endDateStr);
	        
			FileWriter fileWriter = new FileWriter(fileOutput);
            fileWriter.write("FilePath,Project,NumberOfChanges,NumberOfCodeChurn\n"); // CSV header
            
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
		    
			//String repoPath = "/Users/asiftanim/Desktop/Github/hadoop/.git";
			String repoPath = "/Users/asiftanim/Desktop/Github/hibernate-orm/.git";
			String projectName = "hibernate-5.0";
			//String projectName = "hadoop-2.6";
			int totalChurn = 0;
			int totalCommits = 0;
			int codeChurn = 0;
			Repository repo = builder.setGitDir(new File(repoPath))
		            .setMustExist(true).build();
		    Git git = new Git(repo);

			Iterable<RevCommit> commits = git.log().all().call();
            HashMap<String, ArrayList<Integer>> hmCommits = new HashMap<>();
           
            for (RevCommit commit : commits) {
            	Date commitDate = new Date(commit.getCommitTime() * 1000L);
            	if(commitDate.after(startDate) && commitDate.before(endDate)) {
            		totalCommits ++;
//                	System.out.println("Commit ID: " + commit.getId().getName());
//                    System.out.println("Date: " + commit.getAuthorIdent().getWhen());
//                    System.out.println("Message: " + commit.getFullMessage());
                    
                    codeChurn = getCodeChurn(repo, commit, hmCommits, fileWriter);
                    
                    System.out.println("Code churn: " + codeChurn);
                    System.out.println("---------------------------------------------");
                    totalChurn += codeChurn;
            	}
            }
            // write data to csv
//            StringBuilder row;
//            for(Entry<String, ArrayList<Integer>> mResult : hmCommits.entrySet()) {
//            	
//            	row = new StringBuilder();
//            	fileWriter.write(row.append(mResult.getKey()).append(",")
//            			.append(projectName).append(",")
//            			.append(mResult.getValue().get(0)).append(",")
//            			.append(mResult.getValue().get(1)).append("\n").toString());
//            }
            
            //String path = "/Users/asiftanim/Downloads/hadoop-release-2.6.0";
            String path = "/Users/asiftanim/Downloads/hibernate-orm-5.0.0.Final";
            List<String> filePathList = FileUtil.getFilePath(path);
            for(String file : filePathList) {
				String[] arr = file.split(path + "/");
				
//				if(hmCommits.get(arr[1]) != null) {
//					System.out.println(hmCommits.get(arr[1]));
//	            	System.out.println(hmCommits.get(arr[1]).get(0));
//	            	System.out.println(hmCommits.get(arr[1]).get(1));
//	            	System.out.println();
//				}
				
            	
        		if(hmCommits.get(arr[1]) == null) {
        			fileWriter.write(file + "," + projectName + ",0,0" + "\n");
        		}else {
        			String rowcsv = file + "," + projectName + "," + hmCommits.get(arr[1]).get(0) + "," + hmCommits.get(arr[1]).get(1) + "\n";
        			System.out.println(rowcsv);
        			fileWriter.write(rowcsv);
        		}
        	}
			System.out.println("CSV generated");
            git.close();
            fileWriter.close();
            
            System.out.println("********************************************");
            System.out.println("Total of code churns from " + startDate + " to " + endDate + ": " + totalChurn);
            System.out.println("Total of number of changes from " + startDate + " to " + endDate + ": " + totalCommits);
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}

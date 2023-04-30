package com.concordia.soen.process.metrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.concordia.soen.flow.metrics.CatchFlowMetricsModel;
import com.concordia.soen.flow.metrics.FileUtil;

public class NumberOfReleaseDefects {
	
	public static boolean isDefect(String urlStr) {
		boolean isDefect = false;
		
		
	        
        // Open an HttpURLConnection to make the GET request
       
		try {
			
			URL url = new URL(urlStr);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			StringBuffer response = new StringBuffer();
			
			
	        if (conn.getResponseCode() == 200) {
	        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            Document doc = builder.parse(conn.getInputStream());
	            doc.getDocumentElement().normalize();
	            NodeList nodes = doc.getElementsByTagName("type");
	            Node node = nodes.item(0);
	            String id = node.getAttributes().getNamedItem("id").getNodeValue();
	            //System.out.println("Type: " + id);
	            
	            if(id.equals("1")) {
	            	isDefect = true;
	            }
	            
	           
	            
	        } else {
	            // Handle the error case
	            System.out.println("Error: " + conn.getResponseCode() + " " + conn.getResponseMessage());
	        }
	        
	        // Close the HttpURLConnection
	        conn.disconnect();
		} catch (IOException | ParserConfigurationException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
		return isDefect;
	}
	
	public NumberOfReleaseDefects(Date startDate, Date endDate) {
		
	}
	
	public static HashMap<String, Integer> FindReleaseDefects(Date startDate, Date endDate, String repoPath, String issueURL) {
		HashMap<String, Integer> defects = new HashMap<String, Integer>();
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
	    try {
			Repository repository = Git.open(new File(repoPath)).getRepository();
			LogCommand log = new Git(repository).log();
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startDateStr = dateFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);
            
            ObjectId head = repository.resolve(Constants.HEAD);
            log.add(head);
            
            Iterable<RevCommit> commits = log.all().call();
            
            for (RevCommit commit : commits) {
            	
            	Date commitDate = new Date(commit.getCommitTime() * 1000L);
                if (commitDate.after(startDate) && commitDate.before(endDate)) {
                    System.out.println(commit.getShortMessage() + " " + dateFormat.format(commitDate));
                    
//                    Pattern pattern = Pattern.compile("HHH-[0-9][0-9][0-9][0-9]");
//                    Pattern pattern2 = Pattern.compile("HHH-[0-9][0-9][0-9][0-9][0-9]");
//                    
//                    Matcher matcher = pattern.matcher(commit.getShortMessage());
//                    Matcher matcher2 = pattern.matcher(commit.getShortMessage());
//                    
//                    String issueCode = "";
//                    if (matcher.find())
//                    {
//                    	issueCode = matcher.group();
//                    }
//                    
//                    if (matcher2.find())
//                    {
//                    	issueCode = matcher2.group();
//                    }
//                	
//                    String urlStr = issueURL + issueCode + "/" + issueCode + ".xml";
                    
                	String[] commitArr = commit.getShortMessage().split(" ");
                	try {
                		commitArr[0] = commitArr[0].substring(0, commitArr[0].length() -1 );
                	}catch(Exception e) {
                		continue;
                	}
                	
                	String urlStr = issueURL + commitArr[0] + "/" + commitArr[0] + ".xml";
                	
                	
                	if(commitArr[0].contains("HDFS") 
                			|| commitArr[0].contains("HADOOP") 
                			|| commitArr[0].contains("YARN") 
                			|| commitArr[0].contains("MAPREDUCE")) {
                		//System.out.println(commitArr[0]);
                		if(isDefect(urlStr)) {
                			//System.out.println("===========================new commit==========================");
                			RevWalk revWalk = new RevWalk(repository);
                			RevTree parentTree = commit.getParentCount() > 0 ? revWalk.parseTree(commit.getParent(0).getId()) : null;
                            RevTree commitTree = revWalk.parseTree(commit.getId());
                            
                            if(parentTree!=null) {
                            	try (ObjectReader reader = repository.newObjectReader();
                            			DiffFormatter diffFormatter = new DiffFormatter(System.out)){
                            		diffFormatter.setRepository(repository);
                                    List<DiffEntry> diffs = diffFormatter.scan(parentTree, commitTree);
                                    for (DiffEntry diff : diffs) {
                                    	if (diff.getNewPath().endsWith(".java")) {
                                    		String path = diff.getNewPath();
                                    		if(defects.get(path) == null) {
                                    			defects.put(path, 1);
                                    		}
	                                		else {
	                                			defects.put(path, defects.get(path) + 1);
	                                		}
                                    	}
                                    }
                            	}
                            }
                            
                		}
                	}
                	
                	
                    
                }
            }
            
//            for (HashMap.Entry<String, Integer> entry : defects.entrySet()) {
//                String key = entry.getKey();
//                Object value = entry.getValue();
//                System.out.println("Key: " + key);
//                System.out.println("Value: " + value);
//            }
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return defects;
	}
	
	public static void generateCSV(String path, List<String> filePathList, HashMap<String, Integer> defects, String project, FileWriter writer) {
		try {
			
			for(String file : filePathList) {
				String[] arr = file.split(path + "/");
				
        		if(defects.get(arr[1]) == null) {
        			writer.write(file + "," + project + ",0" + "\n");
        		}else {
        			String row = file + "," + project + "," + defects.get(arr[1]);
        			//System.out.println(row);
        			writer.write(row + "\n");
        		}
        	}
			System.out.println("CSV generated");
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) {
		//Pre release bugs - hadoop
		//Date startDate = new Date(1416441600000L); // Nov 20, 2014
        //Date endDate = new Date(1417305600000L);   // Nov 30, 2014
		
		//Post Release bugs - hadoop
		Date startDate = new Date(1417305600000L); // Nov 30, 2014
		Date endDate = new Date(1476057600000L);   // Oct 10, 2016
		
		//Pre realase - hibernate
//	      Date startDate = new Date(1390348800000L); // Jan 22, 2014
//	      Date endDate = new Date(1440028800000L);   // Aug 20, 2015
	      
	    //Post realase - hibernate
//	      Date startDate = new Date(1440028800000L); // Aug 20, 2015
//	      Date endDate = new Date(1509926400000L);   // Nov 6, 2017
        
//        ArrayList<String> paths = new ArrayList<String>();
//        paths.add("/Users/asiftanim/Downloads/hadoop-release-2.6.0");
//        paths.add("/Users/asiftanim/Downloads/hibernate-orm-5.0.0.Final");
        
        String path = "/Users/asiftanim/Downloads/hadoop-release-2.6.0";
        //String path = "/Users/asiftanim/Downloads/hibernate-orm-5.0.0.Final";
        
		try {
			FileWriter writer = new FileWriter("post_release_defects_hadoop.csv");
			writer.write("FilePath,Project,POST-ReleaseDefects" + "\n");
			
			//for(String path : paths) {
	        	List<String> filePathList = FileUtil.getFilePath(path);
	        	
//	        	String projectName = "hibernate-5.0";
//	        	String repoPath = "/Users/asiftanim/Desktop/Github/hibernate-orm/.git";
//	        	String issueURL = "https://hibernate.atlassian.net/si/jira.issueviews:issue-xml/";
	        	
	        	String projectName = "hadoop-2.6";
        		String repoPath = "/Users/asiftanim/Desktop/Github/hadoop/.git";
	        	String issueURL = "https://issues.apache.org/jira/si/jira.issueviews:issue-xml/";
	        	
	        	//System.out.println(paths.indexOf(path));
//	        	if(paths.indexOf(path) == 0) {
//	        		projectName = "hadoop-2.6";
//	        		repoPath = "/Users/asiftanim/Desktop/Github/hadoop/.git";
//	        	}else {
//	        		projectName = "hibernate-5.0";
//	        		repoPath = "/Users/asiftanim/Desktop/Github/hibernate-orm/.git";
//	        	}
	        	
	        	HashMap<String, Integer> defects = FindReleaseDefects(startDate, endDate, repoPath, issueURL);
	        	
//		  	      for (HashMap.Entry<String, Integer> entry : defects.entrySet()) {
//				      String key = entry.getKey();
//				      Object value = entry.getValue();
//				      System.out.println("Key: " + key);
//				      System.out.println("Value: " + value);
//			      }
	        	generateCSV(path, filePathList, defects, projectName, writer);
	        	
	        //}
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        
        
		
//	      for (HashMap.Entry<String, Integer> entry : defects.entrySet()) {
//		      String key = entry.getKey();
//		      Object value = entry.getValue();
//		      System.out.println("Key: " + key);
//		      System.out.println("Value: " + value);
//	      }
	}
}

// Type = 1 (Bug)
// https://issues.apache.org/jira/si/jira.issueviews:issue-xml/YARN-2165/YARN-2165.xml
// https://issues.apache.org/jira/si/jira.issueviews:issue-xml/HDFS-16799/HDFS-16799.xml
// https://issues.apache.org/jira/si/jira.issueviews:issue-xml/HDFS-7409./HDFS-7409..xml
// https://hibernate.atlassian.net/si/jira.issueviews:issue-xml/HHH-16533/HHH-16533.xml

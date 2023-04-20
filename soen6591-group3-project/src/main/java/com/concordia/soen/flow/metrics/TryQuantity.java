package com.concordia.soen.flow.metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TryStatement;


/**
 * Detect the number of try blocks in the file.
 *
 */
public class TryQuantity 
{
    public static void main( String[] args )
    {
    	ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
    	String path = "/Users/phong/Documents/hibernate-orm-5.0.16";
		 
		List<String> filePathList = FileUtil.getFilePath(path);
		int totalTry = 0;
		for(String fileName : filePathList) {
			String source;
			try {
				source = FileUtil.read(fileName);	
			} catch (IOException e) {
				System.err.println(e);
				continue;
			}	
			
			parser.setSource(source.toCharArray());
			
			ASTNode root = parser.createAST(null);
			ArrayList<TryStatement> tryStatements = new ArrayList<>();
			Visitor visitor = new Visitor(tryStatements);
			root.accept(visitor);
			System.out.println("File name: " + fileName);
			System.out.println("Number of try blocks: " + tryStatements.size());
			totalTry += tryStatements.size();
		}
		System.out.println("Total number of try blocks: " + totalTry);
    }
        
    static class Visitor extends ASTVisitor {
    	private final ArrayList<TryStatement> tryStatements;
    	
    	public Visitor(ArrayList<TryStatement> tryStatements) {
    		this.tryStatements = tryStatements;
    	}
    	
    	@Override
    	public boolean visit(TryStatement node) {
    		tryStatements.add(node);
    		
    		return super.visit(node);
    	}
	}
}

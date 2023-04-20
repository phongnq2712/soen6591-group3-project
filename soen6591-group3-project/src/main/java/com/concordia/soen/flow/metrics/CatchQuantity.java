package com.concordia.soen.flow.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.w3c.dom.Node;

/**
 * Detect the number of catch blocks in the file.
 *
 */
public class CatchQuantity 
{
    public static void main( String[] args )
    {
    	ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
    	String path = "/Users/phong/Downloads/hibernate-orm-5.0.0.Final";
		 
		List<String> filePathList = FileUtil.getFilePath(path);
		int totalCatch = 0;
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
			Visitor visitor = new Visitor();
			root.accept(visitor);
			System.out.println("Number of catch blocks: " + visitor.getCatchBlockCount());
			totalCatch += visitor.getCatchBlockCount();
		}
		System.out.println("Total number of catch blocks: " + totalCatch);
    }
    
    static class Visitor extends ASTVisitor {
    	private int catchBlockCount;
    	
    	public int getCatchBlockCount() {
    		return catchBlockCount;
    	}
    	@Override
    	public boolean visit(CatchClause node) {
    		catchBlockCount ++;
    		
    		return super.visit(node);
    	}
	}
}

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
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.w3c.dom.Node;

/**
 * Detect the number of invoked methods in a try block.
 *
 */
public class InvokedMethods 
{
	public static int count;
	
	public InvokedMethods(String file) {
			count = 0;
			ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
			String source = "";
			try {
				source = read(file);	
			} catch (IOException e) {
				System.err.println(e);
			}	
			
			parser.setSource(source.toCharArray());
			
			ASTNode root = parser.createAST(null);
			Visitor visitor = new Visitor();
			root.accept(visitor);
			//System.out.println("Number of invoked method in try block: " + visitor.getMethodInvocationCount());
	}
    
    public static String read(String fileName) throws IOException {
    	Path path = Paths.get(fileName);
    	String source = Files.lines(path).collect(Collectors.joining("\n"));
    	
    	return source;
    }
    
    static class Visitor extends ASTVisitor {
    	
    	
    	public int getMethodInvocationCount() {
    		return count;
    	}
    	
    	@Override
    	public boolean visit(MethodInvocation node) {
    		count ++;
    		//System.out.println(node.getName());
    		
    		return super.visit(node);
    	}
	}
}

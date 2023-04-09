package com.concordia.soen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.w3c.dom.Node;

/**
 * Detect the number of lines of code in the try blocks of the file.
 * (not include comments)
 *
 */
public class TrySizeLOC 
{
	public static int loc = 0;
    public static void main( String[] args )
    {
    	ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
		
		for(String fileName : args) {
			String source;
			try {
				source = read(fileName);	
			} catch (IOException e) {
				System.err.println(e);
				continue;
			}	
			
			parser.setSource(source.toCharArray());
			
			ASTNode root = parser.createAST(null);
			Visitor visitor = new Visitor();
			root.accept(visitor);
			System.out.println("LOC = " + loc);
		}
    }
    
    public static String read(String fileName) throws IOException {
    	Path path = Paths.get(fileName);
    	String source = Files.lines(path).collect(Collectors.joining("\n"));
    	
    	return source;
    }
    
    static class Visitor extends ASTVisitor {
    	int countTryBlock = 0;
    	@Override
    	public boolean visit(TryStatement node) {
    		countTryBlock ++;
    		System.out.println("Try block " + countTryBlock + ":");
    		Block tryBlock = node.getBody();
    		if(tryBlock != null) {
    			List<Statement> blockStatements = tryBlock.statements();
    			for(Statement statement: blockStatements) {
    				loc ++;
    				System.out.println(statement);
    			}
    			
    		}
    		
    		return true;
    	}
	}
}

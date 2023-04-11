package com.concordia.soen;

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
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.w3c.dom.Node;

/**
 * Detect the number of try blocks in the file.
 *
 */
public class TryQuantity 
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
			ArrayList<TryStatement> tryStatements = new ArrayList<>();
			Visitor visitor = new Visitor(tryStatements);
			root.accept(visitor);
			System.out.println("Number of try blocks: " + tryStatements.size());
		}
    }
    
    public static String read(String fileName) throws IOException {
    	Path path = Paths.get(fileName);
    	String source = Files.lines(path).collect(Collectors.joining("\n"));
    	
    	return source;
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

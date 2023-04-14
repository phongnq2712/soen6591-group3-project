package com.concordia.soen.flow.metrics;

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
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.w3c.dom.Node;

/**
 * Detect the number of source lines of code in the catch blocks of the file.
 * (not include comments)
 *
 */
public class CatchSizeLOC 
{
	public static int loc = 0;
	static int prevEndLine = 0;
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
			
			final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			cu.accept(new ASTVisitor() {
				int countCatchBlock = 0;
				@Override
				public boolean visit(CatchClause node) {
					Block catchBlock = node.getBody();
			        int endLine = cu.getLineNumber(catchBlock.getStartPosition() + catchBlock.getLength() - 1);
			        if(endLine < prevEndLine) {
			        	return false;
			        }
			        prevEndLine = endLine;
			        countCatchBlock ++;
			        System.out.println("Catch block " + countCatchBlock + ":");
			        System.out.println(catchBlock.toString());
			        String[] lines = catchBlock.toString().split("\\r?\\n");
			        int numLines = lines.length;
			        loc += numLines;
			        System.out.println(numLines);
			        					
			        return super.visit(node);
				}
			});
			System.out.println("LOC = " +loc);
		}
    }
    
    public static String read(String fileName) throws IOException {
    	Path path = Paths.get(fileName);
    	String source = Files.lines(path).collect(Collectors.joining("\n"));
    	
    	return source;
    }
}

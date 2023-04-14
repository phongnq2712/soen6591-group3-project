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
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;

/**
 * Detect the number of source lines of code in the try blocks of the file.
 * (include comments)
 *
 */
public class TrySizeSLOC 
{
	static int sloc = 0;
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
				@Override
				public boolean visit(TryStatement node) {
					Block tryBlock = node.getBody();
			        int startLine = cu.getLineNumber(tryBlock.getStartPosition());
			        int endLine = cu.getLineNumber(tryBlock.getStartPosition() + tryBlock.getLength() - 1);
			        int numLines = endLine - startLine + 1;
			        System.out.println("Try block starts at line " + startLine + " and ends at line " + endLine + " (" + numLines + " lines)");
			        sloc += numLines;
			        
			        return super.visit(node);
				}
			});
			
		}
		System.out.println("SLOC = " + sloc);
    }
    
    public static String read(String fileName) throws IOException {
    	Path path = Paths.get(fileName);
    	String source = Files.lines(path).collect(Collectors.joining("\n"));
    	
    	return source;
    }
    
}

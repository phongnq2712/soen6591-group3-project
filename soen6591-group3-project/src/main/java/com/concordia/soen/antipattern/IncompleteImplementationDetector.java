package com.concordia.soen.antipattern;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;

import com.concordia.soen.flow.metrics.FileUtil;

public class IncompleteImplementationDetector {
	static ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
	public static int count = 0;
	
	public IncompleteImplementationDetector(String file) {
		IncompleteImplementationDetector(file);
	}
	
	public static void IncompleteImplementationDetector(String file) {
		count = 0;
		String source = "";
		
		try {
			source = FileUtil.read(file);
		}catch(Exception ex) {
			System.out.println(ex);
		}

		parser.setSource(source.toCharArray());
		
		final CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		compilationUnit.accept(new ASTVisitor() {
			  
			private String sourceCode;
			private String source;

			@Override
			  public boolean visit(CatchClause node) {
				
				IncompleteImplementationDetector detector = new IncompleteImplementationDetector(source);

				// parse your source code into an AST
				ASTParser parser = ASTParser.newParser(AST.JLS8);
				parser.setSource(sourceCode.toCharArray());
				CompilationUnit cu = (CompilationUnit) parser.createAST(null);

				// visit the AST with the detector
				//cu.accept(detector);

				// check if the anti-pattern was detected
				if (detector.hasIncompleteImplementation()) {
				    System.out.println("Incomplete Implementation anti-pattern detected!");
				}
			    return super.visit(node);
			  }

		});
		
	}

	protected boolean hasIncompleteImplementation() {
		// TODO Auto-generated method stub
		return false;
	}
}

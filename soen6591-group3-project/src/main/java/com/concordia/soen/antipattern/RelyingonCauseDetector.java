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

public class RelyingonCauseDetector {
	static ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
	public static int count = 0;
	
	public RelyingonCauseDetector(String file) {
		RelyingonCauseDetector(file);
	}
	
	public static void RelyingonCauseDetector(String file) {
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
			  
			@Override
			  public boolean visit(CatchClause node) {
				String source = "";
				RelyingonCauseDetector detector = new RelyingonCauseDetector(source);

				// parse your source code into an AST
				ASTParser parser = ASTParser.newParser(AST.JLS8);
				String sourceCode = null;
				parser.setSource(sourceCode.toCharArray());
				CompilationUnit cu = (CompilationUnit) parser.createAST(null);

				// visit the AST with the detector
				//cu.accept0(detector);

				// check if the anti-pattern was detected
				if (detector.hasRelyingOnGetCause()) {
				    System.out.println("Relying on getCause() anti-pattern detected!");
				}
			    return super.visit(node);
			  }

		});
		
	}

	protected boolean hasRelyingOnGetCause() {
		// TODO Auto-generated method stub
		return false;
	}
}

package com.concordia.soen.antipattern;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;

import com.concordia.soen.flow.metrics.FileUtil;

public class CatchandReturnnullDetector {
	static ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
	public static int count = 0;

	public CatchandReturnnullDetector(String file) {
		CatchandReturnnullDetector(file);
	}
	
	public static void CatchandReturnnullDetector(String file) {
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
			public boolean visit(TryStatement node) {
				if (node.getFinally() == null && node.catchClauses().size() == 1) {
					
	                if (((CatchClause) node.catchClauses().get(0)).getException().getType().toString().equals("Exception")) {
	                	
	                    String returnValue = ((CatchClause) node.catchClauses().get(0)).getBody().toString().trim();
	                    if (returnValue.contains("return null;")) {
	                    	count++;
	                        //System.out.println("Warning: catch and return null anti-pattern detected at line " + node.getStartPosition());
	                    }
				
	                }
	            }
				return super.visit(node);
			}

		});
	}
}

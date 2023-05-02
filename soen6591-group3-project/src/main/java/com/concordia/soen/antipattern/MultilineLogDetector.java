package com.concordia.soen.antipattern;

import java.beans.Expression;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;

import com.concordia.soen.flow.metrics.FileUtil;

public class MultilineLogDetector {
	static ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
	public static int count = 0;
	
	public MultilineLogDetector(String file) {
		MultilineLogDetector(file);
	}
	
	public static void MultilineLogDetector(String file) {
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
			  public boolean visit(MethodInvocation node) {
				
				String name = node.getName().getIdentifier();
		        if (name.equals("debug") || name.equals("info") || name.equals("warn") || name.equals("error")) {
		            Expression expression = (Expression) node.arguments().get(0);
		            //if (expression instanceof InfixExpression) {
		                count++;
		            //}
		        }
			    return super.visit(node);
			  }

		});
		
	}
}

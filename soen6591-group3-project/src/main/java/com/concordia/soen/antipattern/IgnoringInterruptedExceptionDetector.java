package com.concordia.soen.antipattern;

import java.util.List;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;

import com.concordia.soen.flow.metrics.FileUtil;

public class IgnoringInterruptedExceptionDetector {
	static ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
	public static int count = 0;
	
	public IgnoringInterruptedExceptionDetector(String file) {
		detectIgnoringInterruptedException(file);
	}
	
	public static void detectIgnoringInterruptedException(String file) {
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
				if(node.getException().getType().toString().equals("InterruptedException")) {
					List<Statement> statements = node.getBody().statements();
					if(statements.size() > 0) {
						for(Statement statement: statements) {
					    	if(statement instanceof ThrowStatement || statement instanceof ReturnStatement || statement instanceof ContinueStatement 
					    			|| statement instanceof BreakStatement) {
					    		count ++;
					    	}
					    }
						if(statements.size() == 1) {
							for(Statement stm : statements) {
								if(stm instanceof ExpressionStatement) {
						    		Expression expression = ((ExpressionStatement) stm).getExpression();
				                    if (expression instanceof MethodInvocation) {
				                    	MethodInvocation methodInvocation = (MethodInvocation) expression;
				                    	if (methodInvocation.getName().toString().equals("printStackTrace")
				                    			|| methodInvocation.getName().toString().equals("debug") 
				                    			|| methodInvocation.getName().toString().equals("info")
				                    			|| methodInvocation.getName().toString().equals("warn")) {
				                    		count ++;
				                    	}
				                    }
								}
							}
						}
					} else {
						count ++;
					}
				}
				
			    return super.visit(node);
			  }

		});
		
	}
}



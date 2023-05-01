package com.concordia.soen.antipattern;

import java.beans.Expression;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;

import com.concordia.soen.flow.metrics.FileUtil;

public class LogandThrowDetector {
	static ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
	public static int count = 0;
	
	public LogandThrowDetector(String file) {
		LogandThrowDetector(file);
	}
	
	public static void LogandThrowDetector(String file) {
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
				
				if (node.getName().getIdentifier().equals("error")) {
					//System.out.println("1");
		            org.eclipse.jdt.core.dom.Expression invocExpr = node.getExpression();
		            if (invocExpr != null && invocExpr instanceof SimpleName) {
//		            	System.out.println("2");
		                SimpleName name = (SimpleName) invocExpr;
//		                System.out.println("name:"+name);
//		                System.out.println("identifier:"+name.getIdentifier());
		                if (name.getIdentifier().equals("logger") || name.getIdentifier().equals(".log") || name.getIdentifier().equals("log.") || name.getIdentifier().equals("log")) {
		                	//System.out.println("3");
		                	System.out.println("parent" + node.getParent());
		                	org.eclipse.jdt.core.dom.ASTNode parent = node.getParent();
		                    if (parent instanceof ThrowStatement) {
		                        count++;
		                        //System.out.println("Insytance found");
		                    }
		                }
		            }
		        }
			    return super.visit(node);
			  }

		});
		
	}
}

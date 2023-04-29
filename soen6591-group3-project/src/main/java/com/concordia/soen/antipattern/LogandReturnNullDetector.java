package com.concordia.soen.antipattern;

import java.awt.Window.Type;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;

import com.concordia.soen.flow.metrics.FileUtil;

public class LogandReturnNullDetector {
	static ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
	public static int count = 0;

	public LogandReturnNullDetector(String file) {
		LogandReturnNullDetector(file);
	}

	public static void LogandReturnNullDetector(String file) {
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
			public boolean visit(MethodDeclaration node) {

				org.eclipse.jdt.core.dom.Type returnType = node.getReturnType2();

				// Check if the return type is compatible with null
				boolean isCompatibleWithNull = returnType == null || returnType.isPrimitiveType() || returnType.toString().equals("void");

				// Check each statement in the method
				if(node.getBody()!=null)
				{
					for (Object obj : node.getBody().statements()) {
						if (obj instanceof ReturnStatement) {
							ReturnStatement returnStmt = (ReturnStatement) obj;
							if (returnStmt.getExpression() == null) {
								// If the return value is null, check if the method logs anything before the return statement
								System.out.println(node.getBody().statements().indexOf(returnStmt));
								if(node.getBody().statements().indexOf(returnStmt)>=1)
								{
									Statement previousStmt = (Statement) node.getBody().statements().get(node.getBody().statements().indexOf(returnStmt) - 1);
									if (previousStmt.toString().contains("Logger") || previousStmt.toString().contains("log.") || previousStmt.toString().contains(".log")) {
										count++;
//										System.out.println("Log and return null anti-pattern detected in method: " + node.getName().getFullyQualifiedName());
									} 
									else if (isCompatibleWithNull) 
									{
										System.out.println("Method " + node.getName().getFullyQualifiedName() + " returns null without logging.");
									}
								}
								else
								{
									continue;
								}
							}

						}
					}
				}
				return super.visit(node);
			}

		});

	}
}

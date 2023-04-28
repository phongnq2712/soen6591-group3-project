package com.concordia.soen.antipattern;

import java.awt.Window.Type;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;

import com.concordia.soen.flow.metrics.FileUtil;

public class ThrowsGenericDetector {
	static ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
	public static int count = 0;
	static final int JLS2_INTERNAL = 2;

	public ThrowsGenericDetector(String file) {
		ThrowsGenericDetector(file);
	}

	public static void ThrowsGenericDetector(String file) {
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
				//				System.out.println("Thrown exception types for method " + node.getName().getIdentifier() + ":");
				//				for (Object exceptionType : node.thrownExceptionTypes()) {
				//		            //Type type = exceptionType;
				//		            System.out.println(exceptionType.toString());
				//				}
				if (node.thrownExceptionTypes().stream().anyMatch(exception -> exception.toString().equals("Exception")) ||
						node.thrownExceptionTypes().stream().anyMatch(exception -> exception.toString().equals("Throwable"))) {

					count++;
				}
				return super.visit(node);
			}

		});

	}
}

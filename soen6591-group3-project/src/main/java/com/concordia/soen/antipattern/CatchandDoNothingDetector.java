package com.concordia.soen.antipattern;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.Block;

import com.concordia.soen.flow.metrics.FileUtil;

public class CatchandDoNothingDetector{
	static ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
	public static int count = 0;

	public CatchandDoNothingDetector(String file) {
		CatchandDoNothingDetector(file);
	}
	
	public static void CatchandDoNothingDetector(String file) {
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

				org.eclipse.jdt.core.dom.Block catchBlock = node.getBody();
		        if (catchBlock.statements().isEmpty()) {
		        	count++;
		        }
				return super.visit(node);
			}

		});
	}
}

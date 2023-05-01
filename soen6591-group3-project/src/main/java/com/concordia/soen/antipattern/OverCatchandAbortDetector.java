package com.concordia.soen.antipattern;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;

import com.concordia.soen.flow.metrics.FileUtil;

public class OverCatchandAbortDetector {
	static ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
	public static int count = 0;
	
	public OverCatchandAbortDetector(String file) {
		OverCatchandAbortDetector(file);
	}
	
	public static void OverCatchandAbortDetector(String file) {
		count = 0;
		String source = "";
		
		try {
			source = FileUtil.read(file);
		}catch(Exception ex) {
			System.out.println(ex);
		}

		boolean hasAbort = false;
		parser.setSource(source.toCharArray());
		
		final CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		compilationUnit.accept(new ASTVisitor() {
			  
			@Override
			  public boolean visit(TryStatement node) {
				
				CatchClause[] catchClauses = (CatchClause[]) node.catchClauses().toArray(new CatchClause[0]);
		        if (catchClauses.length > 1) {
		            for (int i = 1; i < catchClauses.length; i++) {
		                ITypeBinding type1 = catchClauses[i - 1].getException().getType().resolveBinding();
		                ITypeBinding type2 = catchClauses[i].getException().getType().resolveBinding();
		                //if (type1.isAssignableFrom(type2)) {
		                    count++;
		                    break;
		                //}
		            }
		        }
			    return super.visit(node);
			  }

		});
		
	}
	public boolean visit(MethodInvocation node) {
        if (node.getName().getIdentifier().equals("abort")) {
            boolean hasAbort = true;
        }
        return true;
    }
}

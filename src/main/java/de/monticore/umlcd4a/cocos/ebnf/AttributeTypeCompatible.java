/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.literals.literals._ast.ASTBooleanLiteral;
import de.monticore.literals.literals._ast.ASTCharLiteral;
import de.monticore.literals.literals._ast.ASTDoubleLiteral;
import de.monticore.literals.literals._ast.ASTFloatLiteral;
import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.literals.literals._ast.ASTLiteral;
import de.monticore.literals.literals._ast.ASTLongLiteral;
import de.monticore.literals.literals._ast.ASTSignedLiteral;
import de.monticore.literals.literals._ast.ASTStringLiteral;
import de.monticore.literals.literals._visitor.LiteralsInheritanceVisitor;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;

/**
 * Checks that an attribute assignment is compatible w.r.t. the attribute's
 * type.
 *
 * @author Robert Heim
 */
public class AttributeTypeCompatible implements CD4AnalysisASTCDAttributeCoCo {
  
  public static final String ERROR_CODE = "0xC4A11";
  
  public static final String ERROR_MSG_FORMAT = "The value assignment for the attribute %s in class %s is not compatible to its type %s.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAttributeCoCo#check(de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute)
   */
  @Override
  public void check(ASTCDAttribute node) {
    if (node.getValue().isPresent()) {
      CDFieldSymbol symbol = (CDFieldSymbol) node.getSymbol().get();
      String className = symbol.getEnclosingScope().getName();
      String typeName = TypesPrinter.printType(node.getType());
      ASTSignedLiteral lit = node.getValue().get().getSignedLiteral();
      
      // see TypeChecker javadoc for more information
      TypeChecker tc = new TypeChecker(typeName);
      lit.accept(tc);
      if (!tc.isAssignable()) {
        CoCoLog.error(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, node.getName(), className, typeName),
            node.get_SourcePositionStart());
      }
    }
  }
  
  /**
   * This visitor checks for an {@link ASTLiteral} if it is assignable to a
   * given type (see constructor). We use a visitor to avoid if (lit instanceof
   * ASTIntLiteral) ...<br/>
   * We use the inheritance visitor because we then don't need to differentiate
   * between Signed and unsigned numerical literals. The
   * {@link #check(String...)} method does the actual check, while the visit
   * methods define which type names are valid for the corresponding literal.
   *
   * @author Robert Heim
   */
  private static class TypeChecker implements LiteralsInheritanceVisitor {
    
    private String typeUnderCheck;
    
    private boolean isAssignable = false;
    
    /**
     * Constructor for de.monticore.umlcd4a.cocos.ebnf.TypeChecker
     * 
     * @param typeUnderCheck the type for that we want to check if a literal is
     * assignable to it.
     */
    public TypeChecker(String typeUnderCheck) {
      this.typeUnderCheck = typeUnderCheck;
    }
    
    public boolean isAssignable() {
      return this.isAssignable;
    }
    
    /**
     * Checks if the {@link #typeUnderCheck} does fit to any of the given
     * assignableTypes.
     * 
     * @param assignableTypes
     */
    private void check(String... assignableTypes) {
      for (String assignableType : assignableTypes) {
        if (typeUnderCheck.equals(assignableType)) {
          isAssignable = true;
        }
      }
    }
    
    /**
     * @see de.monticore.literals._visitor.LiteralsVisitor#visit(de.monticore.literals._ast.ASTStringLiteral)
     */
    @Override
    public void visit(ASTStringLiteral node) {
      check("String");
    }
    
    /**
     * @see de.monticore.literals._visitor.LiteralsVisitor#visit(de.monticore.literals._ast.ASTBooleanLiteral)
     */
    @Override
    public void visit(ASTBooleanLiteral node) {
      check("boolean", "Boolean");
    }
    
    /**
     * @see de.monticore.literals._visitor.LiteralsVisitor#visit(de.monticore.literals._ast.ASTIntLiteral)
     */
    @Override
    public void visit(ASTIntLiteral node) {
      check("int", "Integer");
    }
    
    /**
     * @see de.monticore.literals._visitor.LiteralsVisitor#visit(de.monticore.literals._ast.ASTCharLiteral)
     */
    @Override
    public void visit(ASTCharLiteral node) {
      check("char", "Character");
    }
    
    /**
     * @see de.monticore.literals._visitor.LiteralsVisitor#visit(de.monticore.literals._ast.ASTFloatLiteral)
     */
    @Override
    public void visit(ASTFloatLiteral node) {
      check("float", "Float");
    }
    
    /**
     * @see de.monticore.literals._visitor.LiteralsVisitor#visit(de.monticore.literals._ast.ASTDoubleLiteral)
     */
    @Override
    public void visit(ASTDoubleLiteral node) {
      check("double", "Double");
    }
    
    /**
     * @see de.monticore.literals._visitor.LiteralsVisitor#visit(de.monticore.literals._ast.ASTLongLiteral)
     */
    @Override
    public void visit(ASTLongLiteral node) {
      check("long", "Long");
    }
  }
}

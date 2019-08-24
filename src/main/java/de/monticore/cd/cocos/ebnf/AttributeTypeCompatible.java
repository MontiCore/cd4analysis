/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol;
import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.BasicGenericsTypesPrinter;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that an attribute assignment is compatible w.r.t. the attribute's
 * type.
 *
 * @author Robert Heim
 */
public class AttributeTypeCompatible implements CD4AnalysisASTCDAttributeCoCo {
  
  @Override
  public void check(ASTCDAttribute node) {
    if (node.isPresentValue()) {
      CDFieldSymbol symbol = (CDFieldSymbol) node.getSymbol();
      String className = symbol.getEnclosingScope().getName().get();
      String typeName = BasicGenericsTypesPrinter.printType(node.getMCType());
      ASTSignedLiteral lit = node.getValue().getSignedLiteral();
      
      // see TypeChecker javadoc for more information
      TypeChecker tc = new TypeChecker(typeName);
      lit.accept(tc);
      if (!tc.isAssignable()) {
        Log.error(
            String
                .format(
                    "0xC4A11 The value assignment for the attribute %s in class %s is not compatible to its type %s.",
                    node.getName(), className, typeName),
            node.get_SourcePositionStart());
      }
    }
  }
  
  /**
   * This visitor checks for an {@link de.monticore.mcliteralsbasis._ast.ASTLiteral} if it is assignable to a
   * given type (see constructor). We use a visitor to avoid if (lit instanceof
   * ASTIntLiteral) ...<br/>
   * We use the inheritance visitor because we then don't need to differentiate
   * between Signed and unsigned numerical literals. The
   * {@link #check(String...)} method does the actual check, while the visit
   * methods define which type names are valid for the corresponding literal.
   *
   * @author Robert Heim
   */
  private static class TypeChecker implements MCCommonLiteralsVisitor {
    
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

    @Override
    public void visit(ASTStringLiteral node) {
      check("String");
    }
    
     @Override
    public void visit(ASTBooleanLiteral node) {
      check("boolean", "Boolean");
    }
    
    @Override
    public void visit(ASTSignedNatLiteral node) {
      check("int", "Integer");
    }

    @Override
    public void visit(ASTSignedBasicFloatLiteral node){
      check("float","Float");
    }

    @Override
    public void visit(ASTSignedBasicDoubleLiteral node){
      check("double","Double");
    }

    @Override
    public void visit(ASTSignedBasicLongLiteral node){
      check("long", "Long");
    }

    @Override
    public void visit(ASTCharLiteral node) {
      check("char", "Character");
    }
    
  }
}

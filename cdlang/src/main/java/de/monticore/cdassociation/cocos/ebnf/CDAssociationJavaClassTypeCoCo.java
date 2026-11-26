/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.cdassociation._symboltable.ICDAssociationScope;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

public class CDAssociationJavaClassTypeCoCo implements CDAssociationASTCDAssociationCoCo {
  
  /**
   * CoCo: Master method to check whether an association has a Java type on either side.
   *
   * @param node the association AST node
   */
  @Override
  public void check(ASTCDAssociation node) {
    check(node, node.getLeft().getMCQualifiedType(), "left");
    check(node, node.getRight().getMCQualifiedType(), "right");
  }
  
  /**
   * CoCo: Helper method to check the left and right side of the association
   * by resolving with the enclosing scope and checking if the resolved symbol is a CDTypeSymbol.
   *
   * @param a (Association)
   * @param qualifiedType (QualifiedType)
   * @param sideName (left or right side of the association)
   */
  protected void check(ASTCDAssociation a, ASTMCQualifiedType qualifiedType, String sideName) {
    
    if (qualifiedType == null || a == null) {
      return;
    }
    
    ICDAssociationScope scope = a.getEnclosingScope();
    String qName = qualifiedType.getMCQualifiedName().getQName();
    
    var optTypeSymbol = scope.resolveType(qName);
    
    if (optTypeSymbol.isPresent()) {
      TypeSymbol typeSymbol = optTypeSymbol.get();
      
      if (!(typeSymbol instanceof CDTypeSymbol)) {
        String assocName = a.isPresentName() ? " '" + a.getName() + "'" : "";
        
        Log.error(String.format("0xCDA64: The type '%s' on the %s side of the association%s is "
            + "not defined in a class diagram (e.g., it is a Java class). Associations may only "
            + "connect class diagram types; use an attribute of type '%s' instead.", qName,
            sideName, assocName, qName), qualifiedType.get_SourcePositionStart(), qualifiedType
                .get_SourcePositionEnd());
        
      }
    }
  }
  
}

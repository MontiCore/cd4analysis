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
  
  @Override
  public void check(ASTCDAssociation node) {
    check(node, node.getLeft().getMCQualifiedType(), "left");
    check(node, node.getRight().getMCQualifiedType(), "right");
  }
  
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
        String assocName = a.isPresentName() ? a.getName() : "unnamed";
        
        Log.error(String.format(
            "0xCDA64: Java class '%s' cannot be used on the %s side of association '%s'.", qName,
            sideName, assocName), a.get_SourcePositionStart());
        
      }
    }
  }
  
}

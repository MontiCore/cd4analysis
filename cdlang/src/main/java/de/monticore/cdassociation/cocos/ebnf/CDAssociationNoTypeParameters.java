/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;

/** Checks that the Types connected by the association are not generic */
public class CDAssociationNoTypeParameters implements CDAssociationASTCDAssociationCoCo {

  @Override
  public void check(ASTCDAssociation assoc) {
    checkNoTypeParameters(assoc.getLeft());
    checkNoTypeParameters(assoc.getRight());
  }

  protected void checkNoTypeParameters(ASTCDAssocSide side) {
    ASTMCQualifiedType type = side.getMCQualifiedType();
    String qName = Names.constructQualifiedName(type.getNameList());
    Optional<TypeSymbol> typeSymbolOpt = side.getEnclosingScope().resolveType(qName);
    if (typeSymbolOpt.isPresent()) {
      TypeSymbol typeSymbol = typeSymbolOpt.get();
      if (!typeSymbol.getSpannedScope().getTypeVarSymbols().isEmpty()) {
        Log.error(
            "0xCDC72 The association refers to a generic type."
                + " This is (currently) not supported.",
            side.getMCQualifiedType().get_SourcePositionStart(),
            side.getMCQualifiedType().get_SourcePositionEnd());
      }
    }
  }
}

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.BuiltInTypes;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that types of attributes are resolvable.
 *
 */
public class AttributeTypeExists
    implements CD4AnalysisASTCDAttributeCoCo {

  @Override
  public void check(ASTCDAttribute node) {
    CDFieldSymbol attrSym = node.getSymbol();
    String typeName = attrSym.getType().getName();
    if (!BuiltInTypes.isBuiltInType(typeName)) {
      Optional<CDTypeSymbol> subClassSym = node.getEnclosingScope()
          .resolveCDType(typeName);
      if (!subClassSym.isPresent()) {
        Log.error(String.format("0xC4A14 Type %s of the attribute %s is unknown.",
            typeName,
            attrSym.getName()),
            node.get_SourcePositionStart());
      }
    }
  }
}

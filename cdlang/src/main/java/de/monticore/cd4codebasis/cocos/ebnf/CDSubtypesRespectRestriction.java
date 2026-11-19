/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.cocos.ebnf;

import de.monticore.cd4codebasis._ast.ASTCDClass;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDClassCoCo;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDInterfaceCoCo;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks on symbol level that a type respects the subtype restrictions of its direct supertypes
 * (JLS 17, §8.1.1.2, §8.1.4, §9.1.3). The restrictions are also available for supertypes that are
 * loaded from symbol files.
 * <ul>
 * <li>0xCDC97: A direct supertype restricts its subtypes, but does not list this type.</li>
 * <li>0xCDC98: A permitted subtype is neither {@code final}, {@code sealed}, nor
 * {@code non-sealed}. Enums are implicitly final and are not checked.</li>
 * <li>0xCDC99: A {@code non-sealed} type has no direct supertype that restricts its subtypes.</li>
 * </ul>
 */
public class CDSubtypesRespectRestriction implements CD4CodeBasisASTCDClassCoCo,
    CD4CodeBasisASTCDInterfaceCoCo, CDInterfaceAndEnumASTCDEnumCoCo {
  
  public static final String ERROR_CODE_NOT_PERMITTED = "0xCDC97";
  
  public static final String ERROR_CODE_PERMITTED_MODIFIER = "0xCDC98";
  
  public static final String ERROR_CODE_NON_SEALED_WITHOUT_RESTRICTION = "0xCDC99";
  
  @Override
  public void check(ASTCDClass node) {
    check(node, node.getSymbol(), node.getModifier());
  }
  
  @Override
  public void check(ASTCDInterface node) {
    check(node, node.getSymbol(), node.getModifier());
  }
  
  @Override
  public void check(ASTCDEnum node) {
    check(node, node.getSymbol(), null);
  }
  
  /**
   * @param modifier the modifier of the type, null for enums
   */
  protected void check(ASTCDType node, OOTypeSymbol symbol, ASTModifier modifier) {
    List<OOTypeSymbol> restrictingSupertypes = symbol.getSuperTypesList().stream().filter(
        SymTypeExpression::hasTypeInfo).map(s -> s.getTypeInfo()).filter(
            s -> s instanceof OOTypeSymbol).map(s -> (OOTypeSymbol) s).filter(
                OOTypeSymbol::hasSubTypeRestriction).collect(Collectors.toList());
    
    boolean permitted = false;
    for (OOTypeSymbol supertype : restrictingSupertypes) {
      if (supertype.getSubTypeRestrictionList().stream().anyMatch(s -> s.hasTypeInfo() && s
          .getTypeInfo().getFullName().equals(symbol.getFullName()))) {
        permitted = true;
      }
      else {
        error(ERROR_CODE_NOT_PERMITTED,
            "Type '%s' extends or implements '%s', but is not one of its permitted subtypes.", node,
            node.getName(), supertype.getName());
      }
    }
    
    if (modifier == null) {
      return;
    }
    boolean sealed = CDSealedModifierCombinations.isSealed(modifier);
    boolean nonSealed = CDSealedModifierCombinations.isNonSealed(modifier);
    if (permitted && !modifier.isFinal() && !sealed && !nonSealed) {
      error(ERROR_CODE_PERMITTED_MODIFIER,
          "Type '%s' is a permitted subtype and must be 'final', 'sealed', or 'non-sealed'.", node,
          node.getName());
    }
    if (nonSealed && restrictingSupertypes.isEmpty()) {
      error(ERROR_CODE_NON_SEALED_WITHOUT_RESTRICTION,
          "Type '%s' is 'non-sealed', but has no direct supertype that is 'sealed'.", node, node
              .getName());
    }
  }
  
  protected void error(String code, String message, ASTCDType node, Object... args) {
    Log.error(code + ": " + String.format(message, args), node.get_SourcePositionStart(), node
        .get_SourcePositionEnd());
  }
  
}

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.cocos;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.List;

/** Symbols that are operators in Alloy should not be contained in the cd */
public class NotAlloyOperator implements CDBasisASTCDDefinitionCoCo {

  /** @see CDBasisASTCDDefinitionCoCo#check(de.monticore.cdbasis._ast.ASTCDDefinition) */
  public void check(ASTCDDefinition node) {

    final MCBasicTypesFullPrettyPrinter pp = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());

    // Check if the used input contains illegal symbols, which are operators in
    // alloy
    List<String> illegalSymbols = new ArrayList<>();
    illegalSymbols.add("$");
    illegalSymbols.add("%");
    illegalSymbols.add("?");
    illegalSymbols.add("!");
    illegalSymbols.add("_");
    illegalSymbols.add("\"");
    illegalSymbols.add("'");

    // check the default package's full-qualified name
    if (node.isPresentDefaultPackage()) {
      checkIllegalSymbol(
          node.getDefaultPackage().get_SourcePositionStart(),
          node.getDefaultPackage().getMCQualifiedName().getQName(),
          illegalSymbols);
    }

    // check classes
    for (ASTCDClass astcdClass : node.getCDClassesList()) {
      checkIllegalSymbol(
          astcdClass.get_SourcePositionStart(),
          astcdClass.getSymbol().getFullName(),
          illegalSymbols);
      for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()) {
        checkIllegalSymbol(
            attribute.get_SourcePositionStart(), attribute.getName(), illegalSymbols);
        checkIllegalSymbol(
            attribute.getMCType().get_SourcePositionStart(),
            attribute.getMCType().printType(pp),
            illegalSymbols);
      }
    }

    // check interfaces
    for (ASTCDInterface astcdInterface : node.getCDInterfacesList()) {
      checkIllegalSymbol(
          astcdInterface.get_SourcePositionStart(),
          astcdInterface.getSymbol().getFullName(),
          illegalSymbols);
      for (ASTCDAttribute attribute : astcdInterface.getCDAttributeList()) {
        checkIllegalSymbol(
            attribute.get_SourcePositionStart(), attribute.getName(), illegalSymbols);
        checkIllegalSymbol(
            attribute.getMCType().get_SourcePositionStart(),
            attribute.getMCType().printType(pp),
            illegalSymbols);
      }
    }

    // check enums
    for (ASTCDEnum astcdEnum : node.getCDEnumsList()) {
      checkIllegalSymbol(
          astcdEnum.get_SourcePositionStart(), astcdEnum.getSymbol().getFullName(), illegalSymbols);
      for (ASTCDEnumConstant constant : astcdEnum.getCDEnumConstantList()) {
        checkIllegalSymbol(constant.get_SourcePositionStart(), constant.getName(), illegalSymbols);
      }
    }

    // check associations
    for (ASTCDAssociation assoc : node.getCDAssociationsList()) {
      ASTCDRole leftRole;
      ASTCDRole rightRole;

      if (assoc.isPresentName()) {
        checkIllegalSymbol(assoc.get_SourcePositionStart(), assoc.getName(), illegalSymbols);
      }
      if (assoc.getLeft().isPresentCDRole()) {
        leftRole = assoc.getLeft().getCDRole();
        checkIllegalSymbol(leftRole.get_SourcePositionStart(), leftRole.getName(), illegalSymbols);
      }
      if (assoc.getRight().isPresentCDRole()) {
        rightRole = assoc.getRight().getCDRole();
        checkIllegalSymbol(
            rightRole.get_SourcePositionStart(), rightRole.getName(), illegalSymbols);
      }
      checkIllegalSymbol(
          assoc.getLeft().get_SourcePositionStart(),
          assoc.getLeftQualifiedName().getQName(),
          illegalSymbols);
      checkIllegalSymbol(
          assoc.getRight().get_SourcePositionStart(),
          assoc.getRightQualifiedName().getQName(),
          illegalSymbols);
    }
  }

  protected void checkIllegalSymbol(SourcePosition src, String name, List<String> illegalSymbols) {
    for (String symbol : illegalSymbols) {
      if (name.contains(symbol)) {
        Log.warn(
            String.format("Symbol %s is not allowed, as it is already defined in alloy.", symbol),
            src);
      }
    }
  }
}

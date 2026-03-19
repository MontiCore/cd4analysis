/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.conf.type;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.attribute.CDAttributeChecker;
import de.monticore.cdconformance.conf.method.CDMethodChecker;
import de.monticore.cdconformance.inc.CDIncarnationMapping;

import java.util.LinkedHashSet;

public class FieldSymTypeConfStrategy extends DeepTypeConfStrategy {
  
  public FieldSymTypeConfStrategy(ASTCDCompilationUnit conCD, ASTCDCompilationUnit refCD,
      CDAttributeChecker attributeChecker, CDMethodChecker methodChecker,
      CDIncarnationMapping incMapping) {
    super(conCD, refCD, attributeChecker, methodChecker, incMapping);
  }
  
  @Override
  protected boolean checkAttributeIncarnation(ASTCDType concrete, ASTCDType ref) {
    // needs to be replaced with fields-based incarnation check
    return checkAttributeIncarnation(new LinkedHashSet<>(CDSymbolTables.getAttributesInHierarchy(
        concrete)), new LinkedHashSet<>(ref.getCDAttributeList()));
  }
  
  @Override
  protected boolean checkMethodIncarnation(ASTCDType concrete, ASTCDType ref) {
    // needs to be replaced with symbol-based incarnation check
    return false;
  }
  
  @Override
  protected boolean checkAttributeConformance(ASTCDType concrete, ASTCDType refType) {
    // needs to be replaced with fields-based conformance check
    return checkAttributeConformance(new LinkedHashSet<>(CDSymbolTables.getAttributesInHierarchy(
        concrete)), refType);
  }
  
  @Override
  protected boolean checkMethodConformance(ASTCDType concrete, ASTCDType refType) {
    // needs to be replaced with fields-based conformance check
    return checkMethodConformance(new LinkedHashSet<>(CDSymbolTables.getMethodsInHierarchy(
        concrete)), refType);
  }
  
}

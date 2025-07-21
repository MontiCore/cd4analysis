/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.conf.type;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.attribute.CDAttributeChecker;
import de.monticore.cdconformance.conf.method.CDMethodChecker;
import de.monticore.cdconformance.inc.CDIncarnationMapping;
import de.monticore.cddiff.CDDiffUtil;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DeepTypeConfStrategy extends BasicTypeConfStrategy {
  
  public DeepTypeConfStrategy(ASTCDCompilationUnit conCD, ASTCDCompilationUnit refCD,
      CDAttributeChecker attributeChecker, CDMethodChecker methodChecker,
      CDIncarnationMapping incMapping) {
    super(conCD, refCD, attributeChecker, methodChecker, incMapping);
  }
  
  @Override
  protected boolean checkAttributeIncarnation(ASTCDType concrete, ASTCDType ref) {
    return checkAttributeIncarnation(new HashSet<>(CDSymbolTables.getAttributesInHierarchy(
        concrete)), new HashSet<>(ref.getCDAttributeList()));
  }
  
  @Override
  protected boolean checkMethodIncarnation(ASTCDType concrete, ASTCDType ref) {
    return checkMethodIncarnation(new HashSet<>(CDSymbolTables.getMethodsInHierarchy(concrete)),
        new HashSet<>(ref.getCDMethodList()));
  }
  
  @Override
  protected boolean checkAttributeConformance(ASTCDType concrete, ASTCDType refType) {
    return checkAttributeConformance(new HashSet<>(CDSymbolTables.getAttributesInHierarchy(
        concrete)), refType);
  }
  
  @Override
  protected boolean checkMethodConformance(ASTCDType concrete, ASTCDType refType) {
    return checkMethodConformance(new HashSet<>(CDSymbolTables.getMethodsInHierarchy(concrete)),
        refType);
  }
  
  @Override
  protected boolean checkAssocIncarnation(ASTCDType concrete, ASTCDType ref) {
    
    Set<ASTCDAssociation> conAssocSet = CDDiffUtil.getAllSuperTypes(concrete, conCD
        .getCDDefinition()).stream().flatMap(supertype -> CDDiffUtil.getReferencingAssociations(
            supertype, conCD).stream().filter(assoc -> supertype.getSymbol()
                .getInternalQualifiedName().contains(assoc.getLeftQualifiedName().getQName())
                || supertype.getSymbol().getInternalQualifiedName().contains(assoc
                    .getRightQualifiedName().getQName()))).collect(Collectors.toSet());
    
    conAssocSet.addAll(CDDiffUtil.getReferencingAssociations(concrete, conCD));
    
    return checkAssocIncarnation(conAssocSet, CDDiffUtil.getReferencingAssociations(ref, refCD));
  }
  
}

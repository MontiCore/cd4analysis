/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.List;
import java.util.stream.Collectors;

/** Matches methods only bei their name. This strategy ignores parameter and return types. */
public class EqNameMethodIncStrategy implements CDMethodMatchingStrategy {
  
  private ASTCDType refType;
  
  @Override
  public List<ASTCDMethod> getMatchedElements(ASTCDMethod concrete) {
    return refType.getCDMethodList().stream().filter(method -> isMatched(concrete, method)).collect(
        Collectors.toList());
  }
  
  @Override
  public boolean isMatched(ASTCDMethod concrete, ASTCDMethod ref) {
    return ref.getName().equals(concrete.getName());
  }
  
  @Override
  public void setReferenceType(ASTCDType refType) { this.refType = refType; }
  
}

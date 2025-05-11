package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.inc.type.TypeIncarnationHelper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class STNamedMethodChecker extends AbstractMethodChecker {
  public STNamedMethodChecker(String mapping, Set<CDConfParameter> params, TypeIncarnationHelper typeHelper) {
    super(mapping, params, typeHelper);
  }

  @Override
  public List<ASTCDMethod> getMatchedElements(ASTCDMethod concrete) {
    return refType.getCDMethodList().stream()
        .filter(ref -> isMatched(concrete, ref))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDMethod concrete, ASTCDMethod ref) {
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return refType.getSpannedScope().resolveMethodMany(refName).contains(ref.getSymbol());
    }
    return false;
  }
}

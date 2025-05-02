package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdconformance.inc.type.TypeIncarnationHelper;

import java.util.List;
import java.util.stream.Collectors;

public class EqNameAttributeChecker extends AbstractAttributeChecker {

  public EqNameAttributeChecker(String mapping, TypeIncarnationHelper typeHelper) {
    super(mapping, typeHelper);
  }

  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    return referenceType.getCDAttributeList().stream()
        .filter(attr -> isMatched(concrete, attr))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    return ref.getName().equals(concrete.getName());
  }
}

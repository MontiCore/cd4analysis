/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.validation;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.util.ASTCDHelper;

public class AttributeChecker extends ModelValidatorBase {

  private AttributeChecker(MergeBlackBoard mergeBlackBoard) {
    super(mergeBlackBoard);
  }

  public static class Builder extends ModelValidatorBuilder {

    @Override
    protected ModelValidator buildModelValidator(MergeBlackBoard blackboard) {
      return new AttributeChecker(blackboard);
    }
  }

  // FIXME Shouldnt this be covered by CoCos?
  protected void checkIfAttributeCalledLikeType(ASTCDDefinition classDiagram) {
    ASTCDHelper mergedCDHelper = getBlackBoard().getASTCDHelperMergedCD();
    for (ASTCDClass clazz : mergedCDHelper.getAllClasses()) {
      for (ASTCDAttribute attr : clazz.getCDAttributeList()) {
        String attributeName = attr.getName();
        String typeName;
        if (attributeName.length() > 1) {
          typeName =
              attributeName.substring(0, 1).toUpperCase()
                  + attributeName.substring(1, attributeName.length());
        } else {
          // Can't be 0...
          typeName = attributeName.substring(0, 1).toUpperCase();
        }
        if (mergedCDHelper.cdContainsType(typeName)) {
          getBlackBoard()
              .addLog(
                  ErrorLevel.FINE,
                  "Attribute '" + attributeName + "' has the same name as a type (ignoring case).",
                  MergePhase.VALIDATION,
                  clazz,
                  mergedCDHelper.getType(typeName).get());
        }
      }
    }
  }

  @Override
  public void apply(ASTCDDefinition classDiagram) {
    checkIfAttributeCalledLikeType(classDiagram);
  }
}

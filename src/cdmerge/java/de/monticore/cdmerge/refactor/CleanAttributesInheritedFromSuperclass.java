/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.refactor;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.util.ASTCDHelper;
import de.monticore.cdmerge.util.CDUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * If during the merge process a class1 is merged with another class2 which has a superclass, then
 * all attributes from class1 which are already present in the superclasses are removed
 */
public class CleanAttributesInheritedFromSuperclass extends ModelRefactoringBase {

  public static class Builder extends ModelRefactoringBuilder {

    @Override
    protected ModelRefactoring buildModelRefactoring(MergeBlackBoard blackBoard) {
      return new CleanAttributesInheritedFromSuperclass(blackBoard);
    }
  }

  /**
   * Constructor for de.monticore.umlcd4a.mergetool.refactor.CleanAttributesInheritedFromSuperclass
   *
   * @param mergeBlackBoard
   */
  private CleanAttributesInheritedFromSuperclass(MergeBlackBoard mergeBlackBoard) {
    super(mergeBlackBoard);
  }

  /** Removes identical attributes from a class hierarchy */
  @Override
  public void apply(ASTCDCompilationUnit cd) {
    ASTCDHelper cdInput1 = getMergeBlackBoard().getASTCDHelperInputCD1();
    ASTCDHelper cdInput2 = getMergeBlackBoard().getASTCDHelperInputCD2();
    for (ASTCDClass c : cd.getCDDefinition().getCDClassesList()) {
      if (c.isPresentCDExtendUsage()) {
        // Filter out attributes which where declared in Super
        // as well as Subclass of one input diagram, i.e. the attribute
        // of the
        // subclass shadowed the one from the superclass. We
        // don't pull-up these Attributes as there was a
        // semantic intention to keep both prior to the merge.
        Set<String> attributesToCheck = new HashSet<>();
        if (cdInput1.cdContainsClass(c.getName())) {
          ASTCDClass classInCD1;
          if (cdInput1.getClass(c.getName()).get().isPresentCDExtendUsage()) {
            classInCD1 = cdInput1.getClass(c.getName()).get();
            cdInput1.retainUniqueAttributesFromSuperClasses(classInCD1).stream()
                .map(a -> a.getName())
                .forEach(attributesToCheck::add);
          }
        }
        if (cdInput2.cdContainsClass(c.getName())) {
          ASTCDClass classInCD2;
          if (cdInput2.getClass(c.getName()).get().isPresentCDExtendUsage()) {
            classInCD2 = cdInput2.getClass(c.getName()).get();
            cdInput2.retainUniqueAttributesFromSuperClasses(classInCD2).stream()
                .map(a -> a.getName())
                .forEach(attributesToCheck::add);
          }
        }
        // Now we have the unique set of attributes from the source
        // classes which haven't been declared in any of the superclasses
        // so far.
        // Now it is safe to remove the attribute if its in the merged
        // super classes
        cleanAttributes(
            c,
            getMergeBlackBoard().getASTCDHelperMergedCD().getLocalSuperClasses(c.getName()),
            attributesToCheck);
      }
    }
  }

  private void cleanAttributes(
      ASTCDClass merged, List<ASTCDClass> superClasses, Set<String> attributesToCheck) {
    Set<ASTCDAttribute> remove = new HashSet<>();
    for (String attrName : attributesToCheck) {
      Optional<ASTCDAttribute> attr = CDUtils.getAttributeFromClass(attrName, merged);
      if (attr.isPresent()) {
        for (ASTCDClass superClass : superClasses) {
          Optional<ASTCDAttribute> attributeInSuperclass =
              CDUtils.getAttributeFromClass(attrName, superClass);
          if (attributeInSuperclass.isPresent()
              && CDUtils.getTypeName(attr.get())
                  .equals(CDUtils.getTypeName(attributeInSuperclass.get().getMCType()))) {
            getMergeBlackBoard()
                .addLog(
                    ErrorLevel.INFO,
                    "Removing attribute '"
                        + CDUtils.getTypeName(attr.get().getMCType())
                        + " "
                        + attr.get().getName()
                        + "' from class '"
                        + merged.getName()
                        + "' as it is now inherited by merged superclass '"
                        + superClass.getName()
                        + "'",
                    PHASE);
            remove.add(attr.get());
            break;
          }
        }
      }
    }

    for (ASTCDAttribute attr : remove) {
      merged.removeCDMember(attr);
    }
  }
}

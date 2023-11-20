/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.refactor;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.util.ASTCDHelper;
import de.monticore.cdmerge.util.CDMergeUtils;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Removes redundant interfaces in a type hierarchy */
public class RemoveRedundantInterfaces extends ModelRefactoringBase {

  public static class Builder extends ModelRefactoringBuilder {

    @Override
    protected ModelRefactoring buildModelRefactoring(MergeBlackBoard blackBoard) {
      return new RemoveRedundantInterfaces(blackBoard);
    }
  }

  /**
   * Constructor for de.monticore.umlcd4a.mergetool.refactor.RemoveRedundantInterfaces
   *
   * @param mergeBlackBoard
   */
  private RemoveRedundantInterfaces(MergeBlackBoard mergeBlackBoard) {
    super(mergeBlackBoard);
  }

  /** Removes redundant interfaces in a type hierarchy */
  @Override
  public void apply(ASTCDCompilationUnit cd) {
    ASTCDHelper helper = new ASTCDHelper(cd);

    // Clean Interfaces
    for (ASTCDInterface interf : cd.getCDDefinition().getCDInterfacesList()) {
      for (ASTMCObjectType superInterface : new ArrayList<>(interf.getInterfaceList())) {
        if (helper.cdContainsInterface(CDMergeUtils.getName(superInterface))) {
          cleanInterfaces(interf, superInterface, helper, new ArrayList<ASTMCObjectType>());
        }
      }
    }
    // Clean classes
    for (ASTCDClass clazz : cd.getCDDefinition().getCDClassesList()) {
      for (ASTMCObjectType superInterface : new ArrayList<>(clazz.getInterfaceList())) {
        if (helper.cdContainsInterface(CDMergeUtils.getName(superInterface))) {
          cleanInterfaces(clazz, superInterface, helper, new ArrayList<ASTMCObjectType>());
        }
      }
      if (clazz.getSuperclassList().size() == 1) {
        cleanInterfacesSuperClass(
            clazz,
            helper.getClass(CDMergeUtils.getName(clazz.getCDExtendUsage().getSuperclass(0))).get(),
            helper,
            new ArrayList<ASTCDClass>());
      }
    }
  }

  private void cleanInterfaces(
      ASTCDType baseType,
      ASTMCObjectType superInterfaceRef,
      ASTCDHelper helper,
      List<ASTMCObjectType> visited) {
    if (visited.contains(superInterfaceRef)) {
      // We have a cycle
      return;
    }
    visited.add(superInterfaceRef);
    if (!baseType.getInterfaceList().isEmpty()
        && helper.cdContainsInterface(CDMergeUtils.getName(superInterfaceRef))) {
      ASTCDInterface superInterface =
          helper.getInterface(CDMergeUtils.getName(superInterfaceRef)).get();
      cleanInterfaces(baseType, superInterface.getInterfaceList());
      for (ASTMCObjectType supIntRef : superInterface.getInterfaceList()) {
        cleanInterfaces(baseType, supIntRef, helper, visited);
      }
    }
  }

  private void cleanInterfaces(ASTCDType baseType, List<ASTMCObjectType> superIntefaces) {
    Set<ASTMCObjectType> remove = new HashSet<>();
    for (ASTMCObjectType interface1 : baseType.getInterfaceList()) {
      for (ASTMCObjectType interface2 : superIntefaces) {
        if (CDMergeUtils.getName(interface1).equals(CDMergeUtils.getName(interface2))) {
          remove.add(interface1);
          getMergeBlackBoard()
              .addLog(
                  ErrorLevel.FINE,
                  "Removing redundant interface '"
                      + CDMergeUtils.getName(interface1)
                      + "' from '"
                      + baseType.getName()
                      + "' as it is already covered in superType.",
                  PHASE);
          break;
        }
      }
    }
    for (ASTMCObjectType interfToRemove : remove) {
      CDMergeUtils.removeSuperInterface(baseType, interfToRemove);
    }
  }

  private void cleanInterfacesSuperClass(
      ASTCDType baseType, ASTCDClass superClass, ASTCDHelper helper, List<ASTCDClass> visited) {
    if (visited.contains(superClass)) {
      // We have a cycle
      return;
    }
    visited.add(superClass);
    for (ASTMCObjectType iface : new ArrayList<>(superClass.getInterfaceList())) {
      cleanInterfaces(baseType, iface, helper, new ArrayList<ASTMCObjectType>());
    }
    if (superClass.getSuperclassList().size() == 1
        && helper.cdContainsClass(CDMergeUtils.getName(superClass.getSuperclassList().get(0)))) {
      ASTCDClass nextSuperClass =
          helper.getClass(CDMergeUtils.getName(superClass.getSuperclassList().get(0))).get();
      cleanInterfacesSuperClass(baseType, nextSuperClass, helper, visited);
    }
  }
}

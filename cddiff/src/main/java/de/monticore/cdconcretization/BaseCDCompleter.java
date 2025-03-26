package de.monticore.cdconcretization;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.type.ITypeCompleter;
import de.monticore.cdconcretization.type.ITypeDetailsCompleter;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.tf.odrulegeneration._ast.ASTAssociation;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

/**
 * Coordinates the completion of a concrete CD from a reference CD. This class defines
 * the overall algorithm for CD concretization, but delegates the actual work to specialized
 * completer classes.
 */
public class BaseCDCompleter implements ICompletionStrategy {

  private final MatchingStrategy<ASTCDType> typeIncStrategy;
  private final ITypeCompleter typeCompleter;
  private final ITypeDetailsCompleter typeDetailsCompleter;

  private final IInheritanceCompleter inheritanceCompleter;

  private final IIncarnationCompleter<ASTAssociation> associationCompleter;

  public BaseCDCompleter(
      MatchingStrategy<ASTCDType> typeIncStrategy,
      ITypeCompleter typeCompleter,
      ITypeDetailsCompleter typeDetailsCompleter,
      IInheritanceCompleter inheritanceCompleter,
      IIncarnationCompleter<ASTAssociation> associationCompleter) {
    this.typeIncStrategy = typeIncStrategy;
    this.typeCompleter = typeCompleter;
    this.typeDetailsCompleter = typeDetailsCompleter;
    this.inheritanceCompleter = inheritanceCompleter;
    this.associationCompleter = associationCompleter;
  }

  @Override
  public ASTCDCompilationUnit complete(ASTCDCompilationUnit referenceCD, ASTCDCompilationUnit concreteCD)
      throws CompletionException {
    // 0. imports
    completeImportStatements(referenceCD, concreteCD);

    // 1. add missing types
    completeMissingTypes(referenceCD.getCDDefinition(), concreteCD.getCDDefinition());

    // 2. inheritance:
    // Inheritance must be completed before adding missing member incarnations
    inheritanceCompleter.setTypeMatcher(typeIncStrategy);
    inheritanceCompleter.completeInheritance(referenceCD, concreteCD);

    // 3. type details
    completeTypeDetails(concreteCD.getCDDefinition(), typeIncStrategy);

    // 4. associations
    // TODO refactor towards new architecture
    associationCompleter.completeIncarnations();

    // 5. cleanup
    // 5.1 remove redundancies that may have been introduced by inheritance
    CDDiffUtil.refreshSymbolTable(concreteCD);
    ReductionTrafo.removeRedundantAttributes(concreteCD);

    // 5.2 reorder so we have a consistent output
    ConcretizationHelper.reorderElements(concreteCD.getCDDefinition());
    return concreteCD;
  }

  private void completeMissingTypes(ASTCDDefinition referenceCD, ASTCDDefinition concreteCD) {
    for (ASTCDClass referenceClass : referenceCD.getCDClassesList()) {
      typeCompleter.completeType(concreteCD, referenceClass);
    }
    for (ASTCDInterface referenceInterface : referenceCD.getCDInterfacesList()) {
      typeCompleter.completeType(concreteCD, referenceInterface);
    }
    for (ASTCDEnum referenceEnum : referenceCD.getCDEnumsList()) {
      typeCompleter.completeType(concreteCD, referenceEnum);
    }
  }

  private void completeTypeDetails(
      ASTCDDefinition concreteCD, MatchingStrategy<ASTCDType> typeIncStrategy)
      throws CompletionException {
    // complete member incarnations
    for (ASTCDClass cClass : concreteCD.getCDClassesList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cClass)) {
        typeDetailsCompleter.completeTypeDetails(cClass, rType);
      }
    }
    for (ASTCDInterface cInterface : concreteCD.getCDInterfacesList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cInterface)) {
        typeDetailsCompleter.completeTypeDetails(cInterface, rType);
      }
    }
    for (ASTCDEnum cEnum : concreteCD.getCDEnumsList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cEnum)) {
        typeDetailsCompleter.completeTypeDetails(cEnum, rType);
      }
    }
  }

  private void completeImportStatements(ASTCDCompilationUnit rcd, ASTCDCompilationUnit ccd) {
    for (ASTMCImportStatement importStatement : rcd.getMCImportStatementList()) {
      boolean alreadyExists = false;
      for (ASTMCImportStatement existingImport : ccd.getMCImportStatementList()) {
        if (existingImport.getQName().equals(importStatement.getQName())
            && existingImport.isStar() == importStatement.isStar()) {
          alreadyExists = true;
          break;
        }
      }
      if (!alreadyExists) {
        ccd.getMCImportStatementList().add(importStatement);
      }
    }
  }
}

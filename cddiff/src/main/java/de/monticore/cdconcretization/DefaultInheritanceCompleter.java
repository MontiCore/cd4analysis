package de.monticore.cdconcretization;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cddiff.ow2cw.expander.FullExpander;
import de.monticore.cddiff.ow2cw.expander.VariableExpander;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmatcher.MatchingStrategy;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultInheritanceCompleter implements IInheritanceCompleter {
  protected MatchingStrategy<ASTCDType> typeMatcher;

  @Override
  public void completeInheritance(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    CDDiffUtil.refreshSymbolTable(srcCD);
    CDDiffUtil.refreshSymbolTable(tgtCD);
    ICD4CodeArtifactScope srcCDScope = (ICD4CodeArtifactScope) srcCD.getEnclosingScope();
    ICD4CodeArtifactScope tgtCDScope = (ICD4CodeArtifactScope) tgtCD.getEnclosingScope();

    // Create a map that maps each type to all its supertypes according to both CDs
    Map<ASTCDType, Set<ASTCDType>> inheritanceGraph = new HashMap<>();

    List<ASTCDClass> classes = tgtCD.getCDDefinition().getCDClassesList();
    List<ASTCDInterface> interfaces = tgtCD.getCDDefinition().getCDInterfacesList();

    Set<ASTCDType> typeSet = new HashSet<>();
    typeSet.addAll(classes);
    typeSet.addAll(interfaces);

    for (ASTCDType type : typeSet) {
      inheritanceGraph.put(type, new HashSet<>(CDInheritanceHelper.getAllSuper(type, tgtCDScope)));

      // for all matching types
      for (ASTCDType matchingTypeInsrcCD : typeMatcher.getMatchedElements(type)) {
        // add all incarnations of super-types
        for (ASTCDType superType :
            CDInheritanceHelper.getAllSuper(matchingTypeInsrcCD, srcCDScope)) {
          // only add super-incarnation if one does not already exist
          if (inheritanceGraph.get(type).stream()
              .noneMatch(superInc -> typeMatcher.isMatched(superInc, superType))) {
            // inherit from all super-incarnations
            inheritanceGraph
                .get(type)
                .addAll(
                    typeSet.stream()
                        .filter(superInc -> typeMatcher.isMatched(superInc, superType))
                        .collect(Collectors.toSet()));
          }
        }
      }

      inheritanceGraph.get(type).remove(type);
    }

    // make sure interfaces do not extend classes
    for (ASTCDInterface current : interfaces) {
      inheritanceGraph
          .get(current)
          .removeAll(
              inheritanceGraph.get(current).stream()
                  .filter(superType -> !(interfaces.contains(superType)))
                  .collect(Collectors.toSet()));
    }

    // remove cyclical inheritance
    for (ASTCDType type : typeSet) {
      inheritanceGraph
          .get(type)
          .removeIf(
              superType ->
                  inheritanceGraph.get(superType).contains(type)
                      && !CDInheritanceHelper.getAllSuper(type, tgtCDScope).contains(superType));
    }

    // remove redundant inheritance
    for (ASTCDType type : typeSet) {
      Set<ASTCDType> superSet = new HashSet<>(inheritanceGraph.get(type));
      for (ASTCDType superType : inheritanceGraph.get(type)) {
        superSet.removeAll(inheritanceGraph.get(superType));
      }
      inheritanceGraph.put(type, superSet);
    }

    // update targetAST (distinguish between extends vs implements)
    FullExpander expander = new FullExpander(new VariableExpander(tgtCD));

    for (ASTCDInterface current : interfaces) {
      Set<String> extendsSet = new HashSet<>();
      for (ASTCDType superType : inheritanceGraph.get(current)) {
        if (interfaces.contains(superType)) {
          extendsSet.add(superType.getSymbol().getInternalQualifiedName());
        }
      }
      expander.updateExtends(current, extendsSet);
    }
    for (ASTCDClass current : classes) {
      Set<String> extendsSet = new HashSet<>();
      Set<String> implementsSet = new HashSet<>();
      for (ASTCDType superType : inheritanceGraph.get(current)) {
        if (classes.contains(superType)) {
          extendsSet.add(superType.getSymbol().getInternalQualifiedName());
        } else if (interfaces.contains(superType)) {
          implementsSet.add(superType.getSymbol().getInternalQualifiedName());
        }
      }
      expander.updateExtends(current, extendsSet);
      expander.updateImplements(current, implementsSet);
    }
    CDDiffUtil.refreshSymbolTable(tgtCD);
    ReductionTrafo.removeRedundantAttributes(tgtCD);
  }

  @Override
  public void setTypeMatcher(MatchingStrategy<ASTCDType> typeMatcher) {
    this.typeMatcher = typeMatcher;
  }
}

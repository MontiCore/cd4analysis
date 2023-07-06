package de.monticore.cddiff.syndiff.imp;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getAllSuper;

public class Syn2SemDiffHelper {
  public static Pair<ASTCDClass, ASTCDClass> getConnectedClasses(ASTCDAssociation association, ASTCDCompilationUnit compilationUnit) {
    Optional<CDTypeSymbol> astcdClass =
        compilationUnit
            .getEnclosingScope()
            .resolveCDTypeDown(association.getLeftQualifiedName().getQName());
    Optional<CDTypeSymbol> astcdClass1 =
        compilationUnit
            .getEnclosingScope()
            .resolveCDTypeDown(association.getRightQualifiedName().getQName());
    return new Pair<ASTCDClass, ASTCDClass>(
        (ASTCDClass) astcdClass.get().getAstNode(), (ASTCDClass) astcdClass1.get().getAstNode());
  }

  /**
   * Compute the classes that extend a given class.
   *
   * @param compilationUnit
   * @param astcdClass
   * @return list of extending classes. This function is similar to getClassHierarchy().
   */
  public static List<ASTCDClass> getSpannedInheritance(ASTCDCompilationUnit compilationUnit, ASTCDClass astcdClass){
    List<ASTCDClass> subclasses = new ArrayList<>();
    for (ASTCDClass childClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      if ((getAllSuper(childClass, (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope())).contains(astcdClass)) {
        subclasses.add(childClass);
      }
    }
    return subclasses;
  }

  public static List<ASTCDClass> getSuperClasses(ASTCDCompilationUnit compilationUnit, ASTCDClass astcdClass){
    List<ASTCDClass> superClasses = new ArrayList<>();
    for (ASTCDType type : getAllSuper(astcdClass, (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope())){
      if (type instanceof ASTCDClass){
        superClasses.add((ASTCDClass) type);
      }
    }
    return superClasses;
  }

    /**
     * Check if the first cardinality is contained in the second cardinality
     * @param cardinality1 first cardinality
     * @param cardinality2 second cardinality
     * @return true if first cardinality is contained in the second one
     */
    //TODO: replace in all statements, where cardinalities are compared
    public static boolean isContainedIn(AssocCardinality cardinality1, AssocCardinality cardinality2){
      if (cardinality1.equals(AssocCardinality.One)
        || cardinality2.equals(AssocCardinality.Multiple)){
        return true;
      } else if (cardinality1.equals(AssocCardinality.Optional)){
        return !cardinality2.equals(AssocCardinality.One)
          && !cardinality2.equals(AssocCardinality.AtLeastOne);
      } else if (cardinality1.equals(AssocCardinality.AtLeastOne)){
        return cardinality2.equals(AssocCardinality.AtLeastOne);
      } else{
        return false;
      }
    }

  static AssocCardinality cardToEnum(ASTCDCardinality cardinality){
    if (cardinality.isOne()) {
      return AssocCardinality.One;
    } else if (cardinality.isOpt()) {
      return AssocCardinality.Optional;
    } else if (cardinality.isAtLeastOne()) {
      return AssocCardinality.AtLeastOne;
    } else {
      return AssocCardinality.Multiple;
    }
  }
}

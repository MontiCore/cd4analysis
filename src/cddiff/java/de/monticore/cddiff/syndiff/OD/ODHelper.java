package de.monticore.cddiff.syndiff.OD;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.syndiff.AssocStruct;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODAttribute;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odlink._ast.ASTODLink;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ODHelper {
  private int indexClass = 1;
  private int indexAssoc = 1;

  private Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();

  /**
   * Create a minimal set of associations and classes that are needed for deriving
   * an object diagram for a given class or association
   * @param astcdClass optional
   * @param astcdAssociation optional
   * @return minimal set of objects
   */
  public Set<Object> createObjectsForOD(ASTCDClass astcdClass, ASTCDAssociation astcdAssociation){
    Set<Object> set = new HashSet<>();
    return (createChains(astcdClass, astcdAssociation, set));
  }

  public Set<Object> createChains(ASTCDClass astcdClass, ASTCDAssociation astcdAssociation, Set<Object> objectSet){
    if (astcdClass != null) {
      if (!objectSet.contains(astcdClass)) {
        objectSet.add(astcdClass);
        List<AssocStruct> list = helper.getSrcMap().get(astcdClass);
        for (AssocStruct pair : list) { //Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> pair
          if (!objectSet.contains(pair.getAssociation())) {
            switch (pair.getSide()) {
              case Left:
                if (pair.getAssociation().getRight().getCDCardinality().isAtLeastOne()
                  || pair.getAssociation().getRight().getCDCardinality().isOne()) {
                  objectSet.add(pair.getAssociation());
                  objectSet.addAll(createChains(Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b, null, objectSet));
                }
              case Right:
                if (pair.getAssociation().getLeft().getCDCardinality().isAtLeastOne()
                  || pair.getAssociation().getLeft().getCDCardinality().isOne()) {
                  objectSet.add(pair.getAssociation());
                  objectSet.addAll(createChains(Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a, null, objectSet));
                }
            }
          }
        }
      }
    }
    else {
      if (!objectSet.contains(astcdAssociation)) {
        objectSet.addAll(createChains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, helper.getSrcCD()).a, null, objectSet));
        objectSet.addAll(createChains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, helper.getSrcCD()).b, null, objectSet));
      }
    }
    return objectSet;
  }

  public ASTODArtifact generateOD(){
    return null;
  }
  public static ASTODArtifact generateArtifact(){
    return null;
  }
  public static String createValue(){
    return null;
  }
  public ASTODAttribute createAttribute(){
    return null;
  }
  public ASTODObject createObject(){
    return null;
  }
  public ASTODLink createLink(){
   return null;
  }
  public String ODTitleForAssoc(DiffType diffType){
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("AssocDiff").append(indexAssoc).append(diffType.toString());
    indexAssoc++;
    return stringBuilder.toString();
  }

  public String ODTitleForClass(DiffType diffType){
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("ClassDiff").append(indexClass).append(diffType.toString());
    indexClass++;
    return stringBuilder.toString();
  }
}

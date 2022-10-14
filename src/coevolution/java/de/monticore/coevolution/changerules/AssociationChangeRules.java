package de.monticore.coevolution.changerules;

import de.monticore.cddiff.syntaxdiff.CDAssociationDiff;
import de.monticore.cddiff.syntaxdiff.CDSyntaxDiff;

import java.util.List;

public class AssociationChangeRules {
  private final CDSyntaxDiff syntaxDiff;

  public AssociationChangeRules(CDSyntaxDiff syntaxDiff){this.syntaxDiff = syntaxDiff;}

  public String[][] getAssociationChangeRules(){

    String[][] changeRules = new String[syntaxDiff.getMatchedClassList().size()][7];
    
    for (int i = 0; i < syntaxDiff.getMatchedAssos().size(); i++) {

      List<CDAssociationDiff> matchedAssos = syntaxDiff.getMatchedAssos();

      changeRules[i][0] = matchedAssos.get(i).getCd1Element().getLeftQualifiedName().getQName();
      changeRules[i][1] = matchedAssos.get(i).getCd1Element().getRightQualifiedName().getQName();

      if (matchedAssos.get(i).getCd1Element().getLeft().isPresentCDRole()) {
        changeRules[i][2] = matchedAssos.get(i).getCd1Element().getLeft().getCDRole().getName();
      }else{
        changeRules[i][2] = matchedAssos.get(i).getCd1Element().getLeftQualifiedName().getQName().toLowerCase();
      }

      if (matchedAssos.get(i).getCd1Element().getRight().isPresentCDRole()) {
        changeRules[i][3] = matchedAssos.get(i).getCd1Element().getRight().getCDRole().getName();
      }else{
        changeRules[i][3] = matchedAssos.get(i).getCd1Element().getRightQualifiedName().getQName().toLowerCase();
      }

      if(matchedAssos.get(i).getCd1Element().getCDAssocDir().isBidirectional()){
        changeRules[i][4] = "isBidirection";}
      if(matchedAssos.get(i).getCd1Element().getCDAssocDir().isDefinitiveNavigableLeft()){
        changeRules[i][4] = "isDefinitiveNavigableLeft";}
      if(matchedAssos.get(i).getCd1Element().getCDAssocDir().isDefinitiveNavigableRight()){
        changeRules[i][4] = "isDefinitiveNavigableRight";}

      if(matchedAssos.get(i).getCd1Element().getLeft().getCDCardinality().isAtLeastOne()){
        changeRules[i][5] = "isAtLeastOne";}
      if(matchedAssos.get(i).getCd1Element().getLeft().getCDCardinality().isMult()){
        changeRules[i][5] = "isMult";}
      if(matchedAssos.get(i).getCd1Element().getLeft().getCDCardinality().isOne()){
        changeRules[i][5] = "isOne";}
      if(matchedAssos.get(i).getCd1Element().getLeft().getCDCardinality().isOpt()){
        changeRules[i][5] = "isOpt";}

      if(matchedAssos.get(i).getCd1Element().getRight().getCDCardinality().isAtLeastOne()){
        changeRules[i][6] = "isAtLeastOne";}
      if(matchedAssos.get(i).getCd1Element().getRight().getCDCardinality().isMult()){
        changeRules[i][6] = "isMult";}
      if(matchedAssos.get(i).getCd1Element().getRight().getCDCardinality().isOne()){
        changeRules[i][6] = "isOne";}
      if(matchedAssos.get(i).getCd1Element().getRight().getCDCardinality().isOpt()){
        changeRules[i][6] = "isOpt";}
    }
    return changeRules;
  }
}

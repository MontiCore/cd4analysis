package de.monticore.coevolution.changerules;

import de.monticore.ast.ASTNode;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.syntaxdiff.ASTNodeDiff;
import de.monticore.cddiff.syntaxdiff.CDMemberDiff;
import de.monticore.cddiff.syntaxdiff.CDSyntaxDiff;
import de.monticore.cddiff.syntaxdiff.CDTypeDiff;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.Arrays;

public class VariableTypeChangeRules {
  private final CDSyntaxDiff syntaxDiff;

  public VariableTypeChangeRules(CDSyntaxDiff syntaxDiff){
    this.syntaxDiff = syntaxDiff;
  }

  public String[][] getVariableTypeChangeRules(){

    int amountMatchedElement = 0;
    for (int i = 0; i < syntaxDiff.getMatchedClassList().size(); i++) {
      CDTypeDiff<ASTCDClass, ASTCDClass> typeDiff = syntaxDiff.getMatchedClassList().get(i);
      amountMatchedElement = amountMatchedElement + typeDiff.getMatchedAttributesList().size();
    }
    //System.out.println(amountMatchedElement);
    String[][] changeRules = new String[amountMatchedElement][3];
    int j = 0;

    for (int i = 0; i < syntaxDiff.getMatchedClassList().size(); i++) {
      CDTypeDiff<ASTCDClass, ASTCDClass> typeDiff = syntaxDiff.getMatchedClassList().get(i);
      String matchedClass = syntaxDiff.getMatchedClassList().get(i).getCd1Element().getName();

      for (CDMemberDiff<ASTCDAttribute> elementDiff : typeDiff.getMatchedAttributesList()) {
        String machtedElement = elementDiff.getCd1Element().getName();

        for (ASTNodeDiff<? extends ASTNode, ? extends ASTNode> fieldDiff : elementDiff.getDiffList()) {
          if (fieldDiff.getCd1Value().isPresent() && fieldDiff.getCd1Value().get() instanceof ASTMCQualifiedType) {
            changeRules[j][0] =  matchedClass;
            changeRules[j][1] = machtedElement;
            String astcdAttribute = ((ASTMCQualifiedType) fieldDiff.getCd1Value().get()).getMCQualifiedName().getBaseName();
            changeRules[j][2] = astcdAttribute;
          }
          if (fieldDiff.getCd1Value().isPresent() && fieldDiff.getCd1Value().get() instanceof ASTMCPrimitiveType) {
            changeRules[j][0] =  matchedClass;
            changeRules[j][1] = machtedElement;
            String astcdAttribute = ((ASTMCPrimitiveType) fieldDiff.getCd1Value().get()).toString();
            changeRules[j][2] = astcdAttribute;
          }
          j = j + 1;
        }
      }
    }
    return changeRules;
  }
}

package de.monticore.coevolution.changerules;

import de.monticore.ast.ASTNode;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.syntaxdiff.ASTNodeDiff;
import de.monticore.cddiff.syntaxdiff.CDMemberDiff;
import de.monticore.cddiff.syntaxdiff.CDSyntaxDiff;
import de.monticore.cddiff.syntaxdiff.CDTypeDiff;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;

public class MethodReturnTypeChangeRules {
  private final CDSyntaxDiff syntaxDiff;

  public MethodReturnTypeChangeRules (CDSyntaxDiff syntaxDiff){
    this.syntaxDiff = syntaxDiff;
  }

  public String[][] getMethodReturnTypeChangeRules(){

    int amountMatchedElement = 0;
    for (int i = 0; i < syntaxDiff.getMatchedClassList().size(); i++) {
      CDTypeDiff<ASTCDClass, ASTCDClass> typeDiff = syntaxDiff.getMatchedClassList().get(i);
      amountMatchedElement = amountMatchedElement + typeDiff.getMatchedMethodeList().size();
    }
    //System.out.println(amountMatchedElement);
    String[][] changeRules = new String[amountMatchedElement][3];
    int j = 0;

    for (int i = 0; i < syntaxDiff.getMatchedClassList().size(); i++) {
      CDTypeDiff<ASTCDClass, ASTCDClass> typeDiff = syntaxDiff.getMatchedClassList().get(i);

      String matchedClass = syntaxDiff.getMatchedClassList().get(i).getCd1Element().getName();

      for (CDMemberDiff<ASTCDMethod> methodDiff : typeDiff.getMatchedMethodeList()) {
        String matchedMethod = methodDiff.getCd1Element().getName();

        for (ASTNodeDiff<? extends ASTNode, ? extends ASTNode> fieldDiff : methodDiff.getDiffList()) {
          if (fieldDiff.getCd1Value().isPresent() && fieldDiff.getCd1Value().get() instanceof ASTMCReturnType) {
            changeRules[j][0] = matchedClass;
            changeRules[j][1] = matchedMethod;
            String methodReturnType = ((ASTMCReturnType) fieldDiff.getCd1Value().get()).getMCType().toString();
            changeRules[j][2] = methodReturnType;
          }
          j = j + 1;
        }
      }
    }
    return changeRules;
  }
}

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.Helper;

import com.microsoft.z3.*;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.se_rwth.commons.logging.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMTHelper {
  public static String fCharToLowerCase(String str) {
    return Character.toLowerCase(str.charAt(0)) + str.substring(1);
  }

  public static String printSMTAssociationName(ASTCDAssociation myAssociation) {
    StringBuilder myString = new StringBuilder();
    myString.append(myAssociation.getLeft().getCDRole().getName());
    if (myAssociation.isPresentName()) {
      myString.append(myAssociation.getName());
    }
    myString.append(myAssociation.getRight().getCDRole().getName());
    return myString.toString();
  }

  public static String printAttributeNameSMT(ASTCDType astcdType, ASTCDAttribute myAttribute) {
    return SMTHelper.fCharToLowerCase(astcdType.getName()) + "_attrib_" + myAttribute.getName();
  }

  public static String printSubclassFuncName(ASTCDType astcdType) {
    return SMTHelper.fCharToLowerCase(astcdType.getName()) + "_get_subclass";
  }

  public static String buildObjectName(Expr<? extends Sort> expr, String typeName) {
    Matcher matcher = Pattern.compile("\\d+$").matcher(expr.toString());
    String digits;
    if (matcher.find()) {
      digits = matcher.group();
    } else {
      digits = "";
      Log.error("Error by building  object names. no digit at the end of the expression name");
    }
    return SMTHelper.fCharToLowerCase(typeName) + "_" + digits;
  }
}

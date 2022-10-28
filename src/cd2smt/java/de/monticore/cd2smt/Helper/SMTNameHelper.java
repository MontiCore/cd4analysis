package de.monticore.cd2smt.Helper;

import com.microsoft.z3.Sort;
import de.monticore.cd2smt.ODArtifacts.SMTObject;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;


public class SMTNameHelper {
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

  static public String printObjectType(SMTObject obj) {
    return sort2CDTypeName(obj.getSmtExpr().getSort());
  }

  static public String printAttributeNameSMT(ASTCDType astcdType, ASTCDAttribute myAttribute) {
    return SMTNameHelper.fCharToLowerCase(astcdType.getName()) + "_attrib_" + myAttribute.getName();
  }

  static public String printSubclassFuncName(ASTCDType astcdType) {
    return SMTNameHelper.fCharToLowerCase(astcdType.getName()) + "_get_subclass";
  }

  static public String printSMTCDTypeName(ASTCDType myClass) {
    return myClass.getName() + "_obj";
  }

  static public String sort2CDTypeName(Sort sort) {
    int length = sort.toString().length();
    StringBuilder stringBuilder = new StringBuilder(sort.toString());
    stringBuilder.delete(length - 4 ,length); //remove the 4 last Characters
    return stringBuilder.toString();
  }

}

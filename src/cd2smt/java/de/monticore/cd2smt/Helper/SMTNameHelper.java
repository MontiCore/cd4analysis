package de.monticore.cd2smt.Helper;

import de.monticore.cd2smt.context.SMTObject;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;



public class SMTNameHelper {
  public static String fCharToLowerCase(String str) {
    return Character.toLowerCase(str.charAt(0)) + str.substring(1);
  }

  public static String printSMTAssociationName(ASTCDAssociation myAssociation) {
    StringBuilder myString  = new StringBuilder();
    myString.append(myAssociation.getLeft().getCDRole().getName());
    if (myAssociation.isPresentName()){
      myString.append( myAssociation.getName());
    }
      myString.append(myAssociation.getRight().getCDRole().getName());
    return  myString.toString();
  }

  static public String printObjectName(SMTObject obj){
    return  obj.getSmtExpr().toString().split("_")[0];
  }
 static public String printAttributeNameSMT(ASTCDClass myClass, ASTCDAttribute myAttribute) {
    return SMTNameHelper.fCharToLowerCase(myClass.getName()) + "_attrib_" + myAttribute.getName();
  }
  public static String printAttributeNameSMT(ASTCDClass myClass, String attrName) {
    return SMTNameHelper.fCharToLowerCase(myClass.getName()) + "_attrib_" + attrName;
  }

 static public String printSubclassFuncName(ASTCDClass myClass) {
    return SMTNameHelper.fCharToLowerCase(myClass.getName()) + "_get_subclass";
  }

  static public String printSMTClassName(ASTCDClass myClass) {
    return myClass.getName() + "_obj";
  }

}

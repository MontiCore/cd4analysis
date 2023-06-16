/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.multExpression;

import com.microsoft.z3.Constructor;
import com.microsoft.z3.DatatypeSort;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.HashMap;
import java.util.Map;

/***
 * this is a data class that saves the features declared in SMT for inheritance-relation
 * of a class/interface with other ones
 */
public class InheritanceFeatures {

  private final Map<ASTCDType, Constructor<? extends Sort>> subClassConstructorList;
  private final Map<ASTCDType, FuncDecl<Sort>> convert2SuperTypeFuncMap;
  private DatatypeSort<? extends Sort> subclassDatatype;
  private FuncDecl<Sort> getSubClass;

  InheritanceFeatures() {
    this.subClassConstructorList = new HashMap<>();
    this.convert2SuperTypeFuncMap = new HashMap<>();
  }

  public FuncDecl<Sort> getSubClass() {
    return getSubClass;
  }

  public DatatypeSort<? extends Sort> getSubclassDatatype() {
    return subclassDatatype;
  }

  public void setSubclassDatatype(DatatypeSort<? extends Sort> subclassDatatype) {
    this.subclassDatatype = subclassDatatype;
  }

  public Map<ASTCDType, FuncDecl<Sort>> getConvert2SuperTypeFuncMap() {
    return convert2SuperTypeFuncMap;
  }

  public Map<ASTCDType, Constructor<? extends Sort>> getSubClassConstructorList() {
    return subClassConstructorList;
  }

  public void setGetSubClass(FuncDecl<Sort> getSubClass) {
    this.getSubClass = getSubClass;
  }

  public void addSubclassConstructor(
      ASTCDType astcdType, Constructor<? extends Sort> subClassConstructor) {
    subClassConstructorList.put(astcdType, subClassConstructor);
  }

  public void addConvert2SuperTypeFunc(ASTCDType astcdType, FuncDecl<Sort> convertFunc) {
    convert2SuperTypeFuncMap.put(astcdType, convertFunc);
  }
}

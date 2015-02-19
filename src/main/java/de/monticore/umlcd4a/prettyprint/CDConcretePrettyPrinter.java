package de.monticore.umlcd4a.prettyprint;

import mc.ast.ASTNode;
import mc.ast.AbstractVisitor;
import mc.ast.ConcretePrettyPrinter;
import mc.ast.PrettyPrinter;
import mc.helper.IndentPrinter;
import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._ast.ASTCDAssociationList;
import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDAttributeList;
import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.ASTCDClassList;
import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.cd4analysis._ast.ASTCDCompilationUnitList;
import de.cd4analysis._ast.ASTCDConstructor;
import de.cd4analysis._ast.ASTCDConstructorList;
import de.cd4analysis._ast.ASTCDDefinition;
import de.cd4analysis._ast.ASTCDDefinitionList;
import de.cd4analysis._ast.ASTCDEnum;
import de.cd4analysis._ast.ASTCDEnumConstant;
import de.cd4analysis._ast.ASTCDEnumConstantList;
import de.cd4analysis._ast.ASTCDEnumList;
import de.cd4analysis._ast.ASTCDEnumParameter;
import de.cd4analysis._ast.ASTCDEnumParameterList;
import de.cd4analysis._ast.ASTCDInterface;
import de.cd4analysis._ast.ASTCDInterfaceList;
import de.cd4analysis._ast.ASTCDMethod;
import de.cd4analysis._ast.ASTCDMethodList;
import de.cd4analysis._ast.ASTCDParameter;
import de.cd4analysis._ast.ASTCDParameterList;
import de.cd4analysis._ast.ASTCDQualifier;
import de.cd4analysis._ast.ASTCDQualifierList;

/**
 * Concrete pretty-printer for printing class diagrams.
 * 
 * @author Martin Schindler
 */
public class CDConcretePrettyPrinter extends ConcretePrettyPrinter {
  
  /**
   * The following classes can be pretty-printed by this class
   * 
   * @return classes can be pretty-printed by this class
   */
  @Override
  public Class<?>[] getResponsibleClasses() {
    return new Class[] { ASTCDAssociation.class, ASTCDAssociationList.class, ASTCDAttribute.class, ASTCDAttributeList.class, ASTCDClass.class, ASTCDClassList.class, ASTCDConstructor.class, ASTCDConstructorList.class, ASTCDDefinition.class, ASTCDDefinitionList.class, ASTCDEnum.class, ASTCDEnumConstant.class, ASTCDEnumConstantList.class, ASTCDEnumList.class, ASTCDEnumParameter.class, ASTCDEnumParameterList.class, ASTCDInterface.class, ASTCDInterfaceList.class, ASTCDMethod.class, ASTCDMethodList.class, ASTCDParameter.class, ASTCDParameterList.class, ASTCDQualifier.class, ASTCDQualifierList.class, ASTCDCompilationUnit.class, ASTCDCompilationUnitList.class };
  }
  
  /**
   * Pretty-print class diagrams
   * 
   * @param a ASTNode to pretty print (Should be of type ASTCDDefinition)
   * @param printer Printer to use
   */
  @Override
  public void prettyPrint(ASTNode a, IndentPrinter printer) {
    PrettyPrinter parent = this.getPrettyPrinter();
    
    // Run visitor
    AbstractVisitor.run(new CDPrettyPrinterConcreteVisitor(parent, printer), a);
  }
  
}

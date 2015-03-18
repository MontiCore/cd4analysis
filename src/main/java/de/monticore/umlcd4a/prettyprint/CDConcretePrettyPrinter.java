package de.monticore.umlcd4a.prettyprint;

import mc.ast.ASTNode;
import mc.ast.AbstractVisitor;
import mc.ast.ConcretePrettyPrinter;
import mc.ast.PrettyPrinter;
import mc.helper.IndentPrinter;
import de.monticore.umlcd4a._ast.ASTCDAssociation;
import de.monticore.umlcd4a._ast.ASTCDAssociationList;
import de.monticore.umlcd4a._ast.ASTCDAttribute;
import de.monticore.umlcd4a._ast.ASTCDAttributeList;
import de.monticore.umlcd4a._ast.ASTCDClass;
import de.monticore.umlcd4a._ast.ASTCDClassList;
import de.monticore.umlcd4a._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a._ast.ASTCDCompilationUnitList;
import de.monticore.umlcd4a._ast.ASTCDConstructor;
import de.monticore.umlcd4a._ast.ASTCDConstructorList;
import de.monticore.umlcd4a._ast.ASTCDDefinition;
import de.monticore.umlcd4a._ast.ASTCDDefinitionList;
import de.monticore.umlcd4a._ast.ASTCDEnum;
import de.monticore.umlcd4a._ast.ASTCDEnumConstant;
import de.monticore.umlcd4a._ast.ASTCDEnumConstantList;
import de.monticore.umlcd4a._ast.ASTCDEnumList;
import de.monticore.umlcd4a._ast.ASTCDEnumParameter;
import de.monticore.umlcd4a._ast.ASTCDEnumParameterList;
import de.monticore.umlcd4a._ast.ASTCDInterface;
import de.monticore.umlcd4a._ast.ASTCDInterfaceList;
import de.monticore.umlcd4a._ast.ASTCDMethod;
import de.monticore.umlcd4a._ast.ASTCDMethodList;
import de.monticore.umlcd4a._ast.ASTCDParameter;
import de.monticore.umlcd4a._ast.ASTCDParameterList;
import de.monticore.umlcd4a._ast.ASTCDQualifier;
import de.monticore.umlcd4a._ast.ASTCDQualifierList;

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

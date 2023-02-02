/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.misc;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._visitor.CDBasisHandler;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import java.util.ArrayList;
import java.util.List;

/**
 * this class should only be used with {@link
 * de.monticore.cdassociation.trafo.CDAssociationDirectCompositionTrafo}
 */
public class CDBasisDirectCompositionTrafo extends CDAfterParseHelper
    implements CDBasisVisitor2, CDBasisHandler {
  protected CDBasisTraverser traverser;
  protected List<String> packageNameList = new ArrayList<>();

  public CDBasisDirectCompositionTrafo() {
    this(new CDAfterParseHelper());
  }

  public CDBasisDirectCompositionTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
  }

  @Override
  public CDBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CDBasisTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void visit(ASTCDClass node) {
    typeStack.push(node);
    removedDirectCompositions.clear();
  }

  @Override
  public void endVisit(ASTCDClass node) {
    node.removeAllCDMembers(removedDirectCompositions);
    typeStack.pop();
  }

  @Override
  public void visit(ASTCDPackage node) {
    createdAssociations.clear();
  }

  @Override
  public void endVisit(ASTCDPackage node) {
    node.addAllCDElements(createdAssociations);
    createdAssociations.clear();
  }

  @Override
  public void visit(ASTCDDefinition node) {
    createdAssociations.clear();
  }

  @Override
  public void endVisit(ASTCDDefinition node) {
    node.getCDElementList().addAll(createdAssociations);
    createdAssociations.clear();
  }
}

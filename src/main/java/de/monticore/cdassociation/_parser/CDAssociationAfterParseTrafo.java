/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._parser;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation._visitor.CDAssociationVisitor;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.umlmodifier.UMLModifierMill;

import java.util.Collections;

public class CDAssociationAfterParseTrafo extends CDAfterParseHelper
    implements CDAssociationVisitor {
  protected CDAssociationVisitor realThis;

  public CDAssociationAfterParseTrafo() {
    this(new CDAfterParseHelper());
  }

  public CDAssociationAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
    setRealThis(this);
  }

  @Override
  public CDAssociationVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CDAssociationVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void visit(ASTCDAssociation node) {
    assocStack.push(node);
  }

  @Override
  public void endVisit(ASTCDAssociation node) {
    assocStack.pop();
  }

  @Override
  public void visit(ASTCDAssocLeftSide node) {
    createASTCDRoleIfAbsent(node);
  }

  @Override
  public void visit(ASTCDAssocRightSide node) {
    createASTCDRoleIfAbsent(node);
  }

  /**
   * <pre>{@code class A {
   *   -> (r) B [*];
   * }}</pre>
   * transforms to:
   * <pre>{@code composition [1] A -> (r) B [*];}</pre>
   *
   * @param node
   */
  @Override
  public void visit(ASTCDDirectComposition node) {
    // transform a direct composition to a "normal" association

    final ASTMCQualifiedName leftSideQualifiedName = MCBasicTypesMill.mCQualifiedNameBuilder()
        .setPartList(Collections.singletonList(typeStack.peek().getName())).build();
    ASTCDAssocLeftSide leftSide = CDAssociationMill
        .cDAssocLeftSideBuilder()
        .setModifier(UMLModifierMill.modifierBuilder().build())
        .setCDCardinality(CDAssociationMill.cDCardOneBuilder().build())
        .setMCQualifiedName(leftSideQualifiedName) // TODO SVa: have the correct package path
        .build();

    final ASTCDAssociation assoc = CDAssociationMill
        .cDAssociationBuilder()
        .setModifier(UMLModifierMill.modifierBuilder().build())
        .setCDAssocType(CDAssociationMill.cDAssocTypeCompBuilder().build())
        .setLeft(leftSide)
        .setCDAssocDir(CDAssociationMill.cDLeftToRightDirBuilder().build())
        .setRight(node.getCDAssocRightSide())
        .build();

    createASTCDRoleIfAbsent(assoc);

    // TODO SVa: write visitor to remove the node from the CDType
    //typeStack.peek().get

    packageStack.peek().addCDElement(assoc);
  }

  public void createASTCDRoleIfAbsent(ASTCDAssociation assoc) {
    createASTCDRoleIfAbsent(assoc.getLeft());
    createASTCDRoleIfAbsent(assoc.getRight());
  }

  public void createASTCDRoleIfAbsent(ASTCDAssocSide side) {
    if (!side.isPresentCDRole()) {
      ASTCDRole role = CD4AnalysisMill
          .cDRoleBuilder()
          .setName(getRoleName(side))
          .build();
      side.setCDRole(role);
    }
  }

  public String getRoleName(ASTCDAssocSide side) {
    if (!assocStack.isEmpty()) {
      final ASTCDAssociation assoc = assocStack.peek();
      if (assoc.isPresentName()) {
        return assoc.getName();
      }
    }
    return side.getName();
  }
}

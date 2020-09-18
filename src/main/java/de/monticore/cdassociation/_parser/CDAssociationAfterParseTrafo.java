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
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.umlmodifier.UMLModifierMill;
import de.se_rwth.commons.StringTransformations;

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

  /**
   * <pre>{@code class A {
   *   -> (r) B [*];
   * }}</pre>
   * transforms to:
   * <pre>{@code composition [1] A -> (r) B [*];}</pre>
   */
  @Override
  public void visit(ASTCDDirectComposition node) {
    // transform a direct composition to a "normal" association

    final ASTMCQualifiedType leftSideQualifiedType = MCBasicTypesMill
        .mCQualifiedTypeBuilder()
        .setMCQualifiedName(MCBasicTypesMill
            .mCQualifiedNameBuilder()
            .setPartsList(Collections.singletonList(typeStack.peek().getName()))
            .build())
        .build();
    ASTCDAssocLeftSide leftSide = CDAssociationMill
        .cDAssocLeftSideBuilder()
        .setModifier(UMLModifierMill.modifierBuilder().build())
        .setCDCardinality(CDAssociationMill.cDCardOneBuilder().build())
        .setMCQualifiedType(leftSideQualifiedType)
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

    // remove the CDDirectComposition from the type
    removedDirectCompositions.add(node);

    // add the newly created association
    createdAssociations.add(assoc);
  }

  public static void createASTCDRoleIfAbsent(ASTCDAssociation assoc) {
    createASTCDRoleIfAbsent(assoc, assoc.getLeft());
    createASTCDRoleIfAbsent(assoc, assoc.getRight());
  }

  public static void createASTCDRoleIfAbsent(ASTCDAssociation assoc, ASTCDAssocSide side) {
    if (!side.isPresentCDRole()) {
      ASTCDRole role = CD4AnalysisMill
          .cDRoleBuilder()
          .setName(getRoleName(assoc, side))
          .build();
      side.setCDRole(role);
    }
  }

  public static String getRoleName(ASTCDAssociation assoc, ASTCDAssocSide side) {
    if (assoc != null && assoc.isPresentName()) {
      return assoc.getName();
    }
    return StringTransformations.uncapitalize(side.getName());
  }
}

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation._visitor.CDAssociationHandler;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.umlmodifier.UMLModifierMill;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import java.util.Collections;

/**
 * transforms any CDDirectComposition to a CDAssociation only works, when {@link
 * de.monticore.cdbasis.trafo.CDBasisDirectCompositionTrafo} and {@link
 * de.monticore.cdinterfaceandenum.trafo.CDInterfaceAndEnumDirectCompositionTrafo} are also used.
 *
 * <p>use {@link de.monticore.cd4analysis.trafo.CD4AnalysisDirectCompositionTrafo} or {@link
 * de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo}
 */
public class CDAssociationDirectCompositionTrafo extends CDAfterParseHelper
    implements CDAssociationVisitor2, CDAssociationHandler {
  protected CDAssociationTraverser traverser;

  public CDAssociationDirectCompositionTrafo() {
    this(new CDAfterParseHelper());
  }

  public CDAssociationDirectCompositionTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
  }

  @Override
  public CDAssociationTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CDAssociationTraverser traverser) {
    this.traverser = traverser;
  }

  /**
   *
   *
   * <pre>{@code class A {
   *   -> (r) B [*];
   * }}</pre>
   *
   * transforms to:
   *
   * <pre>{@code composition [1] A -> (r) B [*];}</pre>
   */
  @Override
  public void visit(ASTCDDirectComposition node) {
    // transform a direct composition to a "normal" association

    final ASTMCQualifiedType leftSideQualifiedType =
        MCBasicTypesMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(
                MCBasicTypesMill.mCQualifiedNameBuilder()
                    .setPartsList(Collections.singletonList(typeStack.peek().getName()))
                    .build())
            .build();
    ASTCDAssocLeftSide leftSide =
        CDAssociationMill.cDAssocLeftSideBuilder()
            .setModifier(UMLModifierMill.modifierBuilder().build())
            .setCDCardinality(CDAssociationMill.cDCardOneBuilder().build())
            .setMCQualifiedType(leftSideQualifiedType)
            .set_SourcePositionStart(node.get_SourcePositionStart())
            .set_SourcePositionEnd(node.get_SourcePositionEnd())
            .build();

    final ASTCDAssociation assoc =
        CDAssociationMill.cDAssociationBuilder()
            .setModifier(UMLModifierMill.modifierBuilder().build())
            .setCDAssocType(CDAssociationMill.cDAssocTypeCompBuilder().build())
            .setLeft(leftSide)
            .setCDAssocDir(CDAssociationMill.cDLeftToRightDirBuilder().build())
            .setRight(node.getCDAssocRightSide())
            .set_SourcePositionStart(node.get_SourcePositionStart())
            .set_SourcePositionEnd(node.get_SourcePositionEnd())
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
      ASTCDRole role =
          CD4AnalysisMill.cDRoleBuilder()
              .setName(getRoleName(assoc, side))
              .set_SourcePositionStart(side.get_SourcePositionStart())
              .set_SourcePositionEnd(side.get_SourcePositionEnd())
              .build();
      side.setCDRole(role);
    }
  }

  public static String getRoleName(ASTCDAssociation assoc, ASTCDAssocSide side) {
    return StringTransformations.uncapitalize(Names.getSimpleName(side.getName()));
  }
}

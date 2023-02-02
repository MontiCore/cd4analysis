/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.trafo;

import com.google.common.collect.Lists;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.umlmodifier.UMLModifierMill;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Transforms any CDDirectComposition to a CDAssociation.
 * Intended to be used before symbol table creation.
 */
public class CDAssociationDirectCompositionTrafo implements CDBasisVisitor2, CDAssociationVisitor2 {

  protected List<String> typeParts = new ArrayList<>();
  protected Optional<ASTCDDefinition> cd = Optional.empty();
  protected Optional<ASTCDType> type = Optional.empty();

  protected Map<ASTCDType, List<ASTCDMember>> toRemove = new HashMap<>();
  protected List<ASTCDElement> toAdd = new ArrayList<>();

  @Override
  public void endVisit(ASTCDCompilationUnit node) {
    // after traversal is finished remove obsolete direct compositions
    // and add their replacement, the corresponding general compositions
    // this cannot be done earlier, as we cannot modify lists while
    // iterating over their elements

    // remove the CDDirectComposition from the type
    for (Map.Entry<ASTCDType, List<ASTCDMember>> entry : toRemove.entrySet()) {
      entry.getKey().removeAllCDMembers(entry.getValue());
    }

    // add the newly created association
    if (cd.isPresent()) {
      cd.get().addAllCDElements(toAdd);
    } else {
      Log.error("0xCDA04: Transforming direct comoposition failed. Class diagram missing.");
    }
  }

  @Override
  public void visit(ASTCDDefinition node) {
    cd = Optional.of(node);
  }

  @Override
  public void visit(ASTCDPackage node) {
    typeParts.add(node.getName());
  }

  @Override
  public void endVisit(ASTCDPackage node) {
    typeParts.remove(typeParts.size() - 1);
  }

  @Override
  public void visit(ASTCDClass node) {
    typeParts.add(node.getName());
    type = Optional.of(node);
  }

  @Override
  public void endVisit(ASTCDClass node) {
    typeParts.remove(typeParts.size() - 1);
    type = Optional.empty();
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
                    .setPartsList(typeParts.stream().collect(Collectors.toList())) //.setPartsList(Collections.singletonList(typeStack.peek().getName()))
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

    // only store elements to be removed and added, as we cannot
    // alter the elements, we are currently iterating over

    // remove the CDDirectComposition from the type
    if (type.isPresent()) {
      ASTCDType key = type.get();
      if (toRemove.containsKey(key)) {
        toRemove.get(key).add(node);
      } else {
        toRemove.put(key, Lists.newArrayList(node));
      }
    } else {
      Log.error("0xCDA03: Transforming direct comoposition failed. Surrounding type missing.");
    }

    // add the newly created association
    toAdd.add(assoc);
  }

  public static void createASTCDRoleIfAbsent(ASTCDAssociation assoc) {
    createASTCDRoleIfAbsent(assoc, assoc.getLeft());
    createASTCDRoleIfAbsent(assoc, assoc.getRight());
  }

  public static void createASTCDRoleIfAbsent(ASTCDAssociation assoc, ASTCDAssocSide side) {
    if (!side.isPresentCDRole()) {
      ASTCDRole role =
          CDAssociationMill.cDRoleBuilder()
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

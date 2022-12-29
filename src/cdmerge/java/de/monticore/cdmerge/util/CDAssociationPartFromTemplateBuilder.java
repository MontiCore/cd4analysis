/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.util;

import de.monticore.cdassociation._ast.*;

public class CDAssociationPartFromTemplateBuilder {

  public static ASTCDAssocRightSide buildRightAssociation(ASTCDAssocSide assocSideTemplate) {
    ASTCDAssocRightSideBuilder builder = new ASTCDAssocRightSideBuilder();
    return buildRightSide(assocSideTemplate, builder);
  }

  public static ASTCDAssocLeftSide buildLeftAssociation(ASTCDAssocSide assocSideTemplate) {
    ASTCDAssocLeftSideBuilder builder = new ASTCDAssocLeftSideBuilder();
    return buildLeftSide(assocSideTemplate, builder);
  }

  public static ASTCDLeftToRightDir buildLeftToRightDir(ASTCDAssocDir assocDirTemplate) {
    ASTCDLeftToRightDirBuilder builder = new ASTCDLeftToRightDirBuilder();

    if (assocDirTemplate.isPresent_SourcePositionEnd()) {
      builder.set_SourcePositionEnd(assocDirTemplate.get_SourcePositionEnd());
    } else {
      builder.set_SourcePositionEndAbsent();
    }

    if (assocDirTemplate.isPresent_SourcePositionStart()) {
      builder.set_SourcePositionStart(assocDirTemplate.get_SourcePositionStart());
    } else {
      builder.set_SourcePositionStartAbsent();
    }

    builder.set_PreCommentList(assocDirTemplate.get_PreCommentList());
    builder.set_PostCommentList(assocDirTemplate.get_PostCommentList());
    return builder.build();
  }

  public static ASTCDRightToLeftDir buildRightToLeftDir(ASTCDAssocDir assocDirTemplate) {
    ASTCDRightToLeftDirBuilder builder = new ASTCDRightToLeftDirBuilder();
    if (assocDirTemplate.isPresent_SourcePositionEnd()) {
      builder.set_SourcePositionEnd(assocDirTemplate.get_SourcePositionEnd());
    } else {
      builder.set_SourcePositionEndAbsent();
    }

    if (assocDirTemplate.isPresent_SourcePositionStart()) {
      builder.set_SourcePositionStart(assocDirTemplate.get_SourcePositionStart());
    } else {
      builder.set_SourcePositionStartAbsent();
    }

    builder.set_PreCommentList(assocDirTemplate.get_PreCommentList());
    builder.set_PostCommentList(assocDirTemplate.get_PostCommentList());
    return builder.build();
  }

  private static ASTCDAssocRightSide buildRightSide(
      ASTCDAssocSide assocSideTemplate, ASTCDAssocRightSideBuilder builder) {

    if (assocSideTemplate.isPresentCDOrdered()) {
      builder.setCDOrdered(assocSideTemplate.getCDOrdered());
    } else {
      builder.setCDOrderedAbsent();
    }

    builder.setModifier(assocSideTemplate.getModifier());

    if (assocSideTemplate.isPresentCDCardinality()) {
      builder.setCDCardinality(assocSideTemplate.getCDCardinality());
    } else {
      builder.setCDCardinalityAbsent();
    }

    builder.setMCQualifiedType(assocSideTemplate.getMCQualifiedType());

    if (assocSideTemplate.isPresentCDQualifier()) {
      builder.setCDQualifier(assocSideTemplate.getCDQualifier());
    } else {
      builder.setCDQualifierAbsent();
    }

    if (assocSideTemplate.isPresentCDRole()) {
      builder.setCDRole(assocSideTemplate.getCDRole());
    } else {
      builder.setCDRoleAbsent();
    }

    if (assocSideTemplate.isPresent_SourcePositionEnd()) {
      builder.set_SourcePositionEnd(assocSideTemplate.get_SourcePositionEnd());
    } else {
      builder.set_SourcePositionEndAbsent();
    }

    if (assocSideTemplate.isPresent_SourcePositionStart()) {
      builder.set_SourcePositionStart(assocSideTemplate.get_SourcePositionStart());
    } else {
      builder.set_SourcePositionStartAbsent();
    }

    builder.set_PreCommentList(assocSideTemplate.get_PreCommentList());
    builder.set_PostCommentList(assocSideTemplate.get_PostCommentList());

    return builder.build();
  }

  private static ASTCDAssocLeftSide buildLeftSide(
      ASTCDAssocSide assocSideTemplate, ASTCDAssocLeftSideBuilder builder) {

    if (assocSideTemplate.isPresentCDOrdered()) {
      builder.setCDOrdered(assocSideTemplate.getCDOrdered());
    } else {
      builder.setCDOrderedAbsent();
    }

    builder.setModifier(assocSideTemplate.getModifier());

    if (assocSideTemplate.isPresentCDCardinality()) {
      builder.setCDCardinality(assocSideTemplate.getCDCardinality());
    } else {
      builder.setCDCardinalityAbsent();
    }

    builder.setMCQualifiedType(assocSideTemplate.getMCQualifiedType());

    if (assocSideTemplate.isPresentCDQualifier()) {
      builder.setCDQualifier(assocSideTemplate.getCDQualifier());
    } else {
      builder.setCDQualifierAbsent();
    }

    if (assocSideTemplate.isPresentCDRole()) {
      builder.setCDRole(assocSideTemplate.getCDRole());
    } else {
      builder.setCDRoleAbsent();
    }

    if (assocSideTemplate.isPresent_SourcePositionEnd()) {
      builder.set_SourcePositionEnd(assocSideTemplate.get_SourcePositionEnd());
    } else {
      builder.set_SourcePositionEndAbsent();
    }

    if (assocSideTemplate.isPresent_SourcePositionStart()) {
      builder.set_SourcePositionStart(assocSideTemplate.get_SourcePositionStart());
    } else {
      builder.set_SourcePositionStartAbsent();
    }

    builder.set_PreCommentList(assocSideTemplate.get_PreCommentList());
    builder.set_PostCommentList(assocSideTemplate.get_PostCommentList());

    return builder.build();
  }
}

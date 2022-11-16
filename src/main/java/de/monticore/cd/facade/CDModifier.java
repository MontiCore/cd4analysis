/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.umlmodifier._ast.ASTModifier;

/** Enum that helps with the simple creation of a ASTModifier */
public enum CDModifier {
  PUBLIC(true, false, false, false, false, false),

  PUBLIC_FINAL(true, false, false, false, true, false),

  PUBLIC_ABSTRACT(true, false, false, false, false, true),

  PUBLIC_STATIC(true, false, false, true, false, false),

  PUBLIC_STATIC_FINAL(true, false, false, true, true, false),

  PROTECTED(false, true, false, false, false, false),

  PROTECTED_FINAL(false, true, false, false, true, false),

  PROTECTED_ABSTRACT(false, true, false, false, false, true),

  PROTECTED_STATIC(false, true, false, true, false, false),

  PROTECTED_STATIC_FINAL(false, true, false, true, true, false),

  PACKAGE_PRIVATE(false, false, false, false, false, false),

  PACKAGE_PRIVATE_FINAL(false, false, false, false, true, false),

  PACKAGE_PRIVATE_ABSTRACT(false, false, false, false, false, true),

  PACKAGE_PRIVATE_STATIC(false, false, false, true, false, false),

  PACKAGE_PRIVATE_STATIC_FINAL(false, false, false, true, true, false),

  PRIVATE(false, false, true, false, false, false),

  PRIVATE_FINAL(false, false, true, false, true, false),

  PRIVATE_STATIC(false, false, true, true, false, false),

  PRIVATE_STATIC_FINAL(false, false, true, true, true, false);

  protected final boolean isPublic;

  protected final boolean isProtected;

  protected final boolean isPrivate;

  protected final boolean isStatic;

  protected final boolean isFinal;

  protected final boolean isAbstract;

  CDModifier(
      boolean isPublic,
      boolean isProtected,
      boolean isPrivate,
      boolean isStatic,
      boolean isFinal,
      boolean isAbstract) {
    this.isPublic = isPublic;
    this.isProtected = isProtected;
    this.isPrivate = isPrivate;
    this.isStatic = isStatic;
    this.isFinal = isFinal;
    this.isAbstract = isAbstract;
  }

  public ASTModifier build() {
    return CD4CodeMill.modifierBuilder()
        .setPublic(isPublic)
        .setProtected(isProtected)
        .setPrivate(isPrivate)
        .setStatic(isStatic)
        .setFinal(isFinal)
        .setAbstract(isAbstract)
        .build();
  }
}

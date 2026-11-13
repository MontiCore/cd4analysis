/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.umlmodifier._ast.ASTModifier;

/** Enum that helps with the simple creation of a ASTModifier */
public enum CDModifier {
  
  PUBLIC(true, false, false, false, false, false, false, false),
  
  PUBLIC_FINAL(true, false, false, false, true, false, false, false),
  
  PUBLIC_ABSTRACT(true, false, false, false, false, true, false, false),
  
  PUBLIC_STATIC(true, false, false, true, false, false, false, false),
  
  PUBLIC_SEALED(true, false, false, false, false, false, true, false),
  
  PUBLIC_NON_SEALED(true, false, false, false, false, false, false, true),
  
  PUBLIC_STATIC_FINAL(true, false, false, true, true, false, false, false),
  
  PUBLIC_ABSTRACT_SEALED(true, false, false, false, false, true, true, false),
  
  PUBLIC_ABSTRACT_NON_SEALED(true, false, false, false, false, true, false, true),
  
  PUBLIC_STATIC_SEALED(true, false, false, true, false, false, true, false),
  
  PUBLIC_STATIC_NON_SEALED(true, false, false, true, false, false, false, true),
  
  PUBLIC_ABSTRACT_STATIC_SEALED(true, false, false, true, false, true, true, false),
  
  PUBLIC_ABSTRACT_STATIC_NON_SEALED(true, false, false, true, false, true, false, true),
  
  PROTECTED(false, true, false, false, false, false, false, false),
  
  PROTECTED_FINAL(false, true, false, false, true, false, false, false),
  
  PROTECTED_ABSTRACT(false, true, false, false, false, true, false, false),
  
  PROTECTED_STATIC(false, true, false, true, false, false, false, false),
  
  PROTECTED_SEALED(false, true, false, false, false, false, true, false),
  
  PROTECTED_NON_SEALED(false, true, false, false, false, false, false, true),
  
  PROTECTED_STATIC_FINAL(false, true, false, true, true, false, false, false),
  
  PROTECTED_ABSTRACT_SEALED(false, true, false, false, false, true, true, false),
  
  PROTECTED_ABSTRACT_NON_SEALED(false, true, false, false, false, true, false, true),
  
  PROTECTED_STATIC_SEALED(false, true, false, true, false, false, true, false),
  
  PROTECTED_STATIC_NON_SEALED(false, true, false, true, false, false, false, true),
  
  PROTECTED_ABSTRACT_STATIC_SEALED(false, true, false, true, false, true, true, false),
  
  PROTECTED_ABSTRACT_STATIC_NON_SEALED(false, true, false, true, false, true, false, true),
  
  PACKAGE_PRIVATE(false, false, false, false, false, false, false, false),
  
  PACKAGE_PRIVATE_FINAL(false, false, false, false, true, false, false, false),
  
  PACKAGE_PRIVATE_ABSTRACT(false, false, false, false, false, true, false, false),
  
  PACKAGE_PRIVATE_STATIC(false, false, false, true, false, false, false, false),
  
  PACKAGE_PRIVATE_SEALED(false, false, false, false, false, false, true, false),
  
  PACKAGE_PRIVATE_NON_SEALED(false, false, false, false, false, false, false, true),
  
  PACKAGE_PRIVATE_STATIC_FINAL(false, false, false, true, true, false, false, false),
  
  PACKAGE_PRIVATE_ABSTRACT_SEALED(false, false, false, false, false, true, true, false),
  
  PACKAGE_PRIVATE_ABSTRACT_NON_SEALED(false, false, false, false, false, true, false, true),
  
  PACKAGE_PRIVATE_STATIC_SEALED(false, false, false, true, false, false, true, false),
  
  PACKAGE_PRIVATE_STATIC_NON_SEALED(false, false, false, true, false, false, false, true),
  
  PACKAGE_PRIVATE_ABSTRACT_STATIC_SEALED(false, false, false, true, false, true, true, false),
  
  PACKAGE_PRIVATE_ABSTRACT_STATIC_NON_SEALED(false, false, false, true, false, true, false, true),
  
  PRIVATE(false, false, true, false, false, false, false, false),
  
  PRIVATE_FINAL(false, false, true, false, true, false, false, false),
  
  PRIVATE_STATIC(false, false, true, true, false, false, false, false),
  
  PRIVATE_SEALED(false, false, true, false, false, false, true, false),
  
  PRIVATE_NON_SEALED(false, false, true, false, false, false, false, true),
  
  PRIVATE_STATIC_FINAL(false, false, true, true, true, false, false, false),
  
  PRIVATE_ABSTRACT_SEALED(false, false, true, false, false, true, true, false),
  
  PRIVATE_ABSTRACT_NON_SEALED(false, false, true, false, false, true, false, true),
  
  PRIVATE_STATIC_SEALED(false, false, true, true, false, false, true, false),
  
  PRIVATE_STATIC_NON_SEALED(false, false, true, true, false, false, false, true),
  
  PRIVATE_ABSTRACT_STATIC_SEALED(false, false, true, true, false, true, true, false),
  
  PRIVATE_ABSTRACT_STATIC_NON_SEALED(false, false, true, true, false, true, false, true);
  
  protected final boolean isPublic;
  protected final boolean isProtected;
  protected final boolean isPrivate;
  protected final boolean isStatic;
  protected final boolean isFinal;
  protected final boolean isAbstract;
  protected final boolean isSealed;
  protected final boolean isNonSealed;
  
  CDModifier(boolean isPublic, boolean isProtected, boolean isPrivate, boolean isStatic,
      boolean isFinal, boolean isAbstract, boolean isSealed, boolean isNonSealed) {
    this.isPublic = isPublic;
    this.isProtected = isProtected;
    this.isPrivate = isPrivate;
    this.isStatic = isStatic;
    this.isFinal = isFinal;
    this.isAbstract = isAbstract;
    this.isSealed = isSealed;
    this.isNonSealed = isNonSealed;
  }
  
  public ASTModifier build() {
    return CD4CodeMill.cDSealedModifierBuilder().setPublic(isPublic).setProtected(isProtected)
        .setPrivate(isPrivate).setStatic(isStatic).setFinal(isFinal).setAbstract(isAbstract)
        .setSealed(isSealed).setNonSealed(isNonSealed).build();
  }
  
}

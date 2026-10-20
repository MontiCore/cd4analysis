/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators.data;

import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.decorators.IDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlmodifier._ast.ASTModifier;
import java.util.Optional;

/**
 * Abstract decorator class, which handles access to shared data structures and provides some
 * utilities
 *
 * @param <D>
 */
public abstract class AbstractDecorator<D> implements IDecorator<D> {
  
  protected DecoratorData decoratorData;
  protected Optional<GlobalExtensionManagement> glexOpt;
  
  @Override
  public void init(DecoratorData util, Optional<GlobalExtensionManagement> glexOpt) {
    this.decoratorData = util;
    this.glexOpt = glexOpt;
  }
  
  protected void addElementToParent(ASTNode decoratedParent, ASTCDElement newElem) {
    if (decoratedParent instanceof ASTCDDefinition)
      ((ASTCDDefinition) decoratedParent).addCDElement(newElem);
    else if (decoratedParent instanceof ASTCDPackage)
      ((ASTCDPackage) decoratedParent).addCDElement(newElem);
    else if (decoratedParent instanceof ASTCDCompilationUnit)
      ((ASTCDCompilationUnit) decoratedParent).getCDDefinition().addCDElement(newElem);
    else
      throw new IllegalStateException("Unhandled addElementToParent " + decoratedParent.getClass()
          .getName());
  }
  
  /**
   * Adds a member to a class, if it does not already exist
   *
   * @param clazz the class
   * @param member the to-be added member
   * @return whether a conflict already exists
   */
  protected boolean addToClass(ASTCDType clazz, ASTCDMember member) {
    // add iff not yet present (#4310)
    if (member instanceof ASTCDMethod method) {
      for (ASTCDMember mem : clazz.getCDMemberList()) {
        if (mem instanceof ASTCDMethod meth) {
          if (method.getName().equals(meth.getName()) && method.getCDParameterList().size() == meth
              .getCDParameterList().size()) {
            // TODO: Check if params are compatible? (#4310)
            return false;
          }
        }
      }
    }
    else if (member instanceof ASTCDAttribute attribute) {
      for (ASTCDMember mem : clazz.getCDMemberList()) {
        if (mem instanceof ASTCDAttribute attr) {
          if (attr.getName().equals(attribute.getName())) {
            return false;
          }
        }
      }
    }
    clazz.addCDMember(member);
    return true;
  }
  
  /**
   * Returns the original model modifier for an attribute. For attributes generated from association
   * roles, the association side modifier is the source of truth because accessor decorators must
   * derive their visibility from the modeled role, not from the already generated field.
   *
   * @param attribute the generated or modeled attribute
   * @return the source modifier to use for visibility-sensitive derived members
   */
  protected ASTModifier getSourceModifier(ASTCDAttribute attribute) {
    var role = decoratorData.fieldToRoles.get(attribute.getSymbol());
    return role == null ? attribute.getModifier().deepClone() : role.getAssocSide().getModifier()
        .deepClone();
  }
  
  public CDGenService getCDGenService() { return decoratorData.cdGenService; }
  
  /** For Decorators not specifying any additional data */
  public static class NoData {}
  
}

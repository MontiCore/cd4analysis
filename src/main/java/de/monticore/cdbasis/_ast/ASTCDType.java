/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._ast;

import de.monticore.cd.CDMill;
import de.monticore.cd._visitor.CDMemberVisitor;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.*;
import java.util.stream.Stream;

public interface ASTCDType extends ASTCDTypeTOP {

  List<ASTMCObjectType> getSuperclassList();

  String printSuperclasses();

  List<ASTMCObjectType> getInterfaceList();

  String printInterfaces();

  /**
   * get a list of the specific CDMember, configured by the options
   * Example:
   * <div>
   * {@code
   *     List<ASTCDAttribute> attributes =  c.getCDMemberList(CDMemberVisitor.Options.ATTRIBUTES);
   * }
   * </div>
   * using {@link CDMemberVisitor.Options}, this converts automatically, when the conversion is unambiguous.
   * When there should be more than one kind, then simply pass all of the options, like:
   * <div>
   * {@code List<ASTCDMember> methodsAndAttributes = c.getCDMemberList(CDMemberVisitor.Options.METHODS, CDMemberVisitor.Options.ATTRIBUTES); }
   * </div>
   *
   * @param options a list of options, what {@link ASTCDMember} should be retrieved
   * @param <T>     the type of the list to return
   * @return the list of collected CDMembers
   */
  default <T extends
      ASTCDMember> List<T> getCDMemberList(CDMemberVisitor.Options option, CDMemberVisitor.Options... options) {
    final ArrayList<CDMemberVisitor.Options> list = new ArrayList<>(Arrays.asList(options));
    list.add(0, option);

    final CDMemberVisitor cdMemberVisitor = CDMill.cDMemberVisitor(list.toArray(new CDMemberVisitor.Options[0]));
    CD4CodeTraverser t = CD4CodeMill.traverser();
    t.add4CDBasis(cdMemberVisitor);
    t.add4CDAssociation(cdMemberVisitor);
    t.add4CDInterfaceAndEnum(cdMemberVisitor);
    t.add4CD4CodeBasis(cdMemberVisitor);
    this.accept(t);
    return cdMemberVisitor.getElements();
  }

  default <T extends
      ASTCDMember> Iterator<T> iterateCDMembers(CDMemberVisitor.Options option, CDMemberVisitor.Options...
      options) {
    return this.<T>getCDMemberList(option, options).iterator();
  }

  default <T extends
      ASTCDMember> Stream<T> streamCDMembers(CDMemberVisitor.Options option, CDMemberVisitor.Options... options) {
    return this.<T>getCDMemberList(option, options).stream();
  }

  default int sizeCDMembers(CDMemberVisitor.Options option, CDMemberVisitor.Options... options) {
    return getCDMemberList(option, options).size();
  }

  default List<ASTCDAttribute> getCDAttributeList() {
    return getCDMemberList(CDMemberVisitor.Options.ATTRIBUTES);
  }

  default List<ASTCDRole> getCDRoleList() {
    return getCDMemberList(CDMemberVisitor.Options.ROLES);
  }

  default List<ASTCDConstructor> getCDConstructorList() {
    return getCDMemberList(CDMemberVisitor.Options.CONSTRUCTORS);
  }

  default List<ASTCDMember> getCDMethodList() {
    return getCDMemberList(CDMemberVisitor.Options.METHODS);
  }

  public ASTModifier getModifier();

  public void setModifier(ASTModifier modifier);

  public boolean addCDMember(ASTCDMember element);

  public boolean addAllCDMembers(Collection<? extends ASTCDMember> collection);
}

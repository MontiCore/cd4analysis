/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.trafo;

import static de.monticore.generating.GeneratorEngine.existsHandwrittenClass;
import static de.se_rwth.commons.Names.constructQualifiedName;

import de.monticore.cd.codegen.decorators.IDecorator;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;
import de.monticore.io.paths.MCPath;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * Adds the suffix TOP to generated AST elements IFF a handwritten equivalent exists. Part of the
 * TOP-mechanism. Attention! does not actually create a new CD object, because then the glex has the
 * wrong objects referenced
 */
public class TOPTrafo extends AbstractDecorator<AbstractDecorator.NoData>
    implements CDBasisVisitor2, CDInterfaceAndEnumVisitor2 {

  public static final String TOP_SUFFIX = "TOP";
  public static final String NEEDS_TOP_IDENTIFIER = "needs" + TOP_SUFFIX;

  protected Stack<String> packageName = new Stack<>();
  protected ASTCDCompilationUnit compilationUnit = null;
  protected final MCPath hwPath;

  public TOPTrafo(MCPath hwPath) {
    this.hwPath = hwPath;
  }

  @Override
  public List<Class<? extends IDecorator<?>>> getMustRunAfter() {
    throw new IllegalStateException(
        "The TOPTrafo MUST be run as a Post-Decorate action (which is currently not configurable)");
  }

  @Override
  public void visit(ASTCDCompilationUnit node) {
    this.compilationUnit = node;
  }

  @Override
  public void endVisit(ASTCDCompilationUnit node) {
    this.compilationUnit = null;
  }

  @Override
  public void visit(ASTCDClass node) {
    if (shouldApplyTOPToClass(node, this.compilationUnit)) {
      this.applyTopMechanism(node);
    }
  }

  @Override
  public void visit(ASTCDInterface node) {
    if (shouldApplyTOPToInterface(node, this.compilationUnit)) {
      this.applyTopMechanism(node);
    }
  }

  @Override
  public void visit(ASTCDEnum node) {
    if (shouldApplyTOPToEnum(node, this.compilationUnit)) {
      this.applyTopMechanism(node);
    }
  }

  @Override
  public void visit(ASTCDPackage node) {
    this.packageName.push(node.getName());
  }

  @Override
  public void endVisit(ASTCDPackage node) {
    this.packageName.pop();
  }

  /** Should the TOP mechanism be applied to a class */
  protected boolean shouldApplyTOPToClass(ASTCDClass cdClass, ASTCDCompilationUnit compUnit) {
    String qualifiedName = determineQualifiedName(cdClass, compUnit);
    boolean existsHw = existsHandwrittenClass(hwPath, qualifiedName);
    // In addition, check for the <<needsTOP="message">> stereo
    checkNeedsHandwrittenClass(existsHw, cdClass, qualifiedName);
    return existsHw;
  }

  /** Should the TOP mechanism be applied to an interface */
  protected boolean shouldApplyTOPToInterface(
      ASTCDInterface cdInterface, ASTCDCompilationUnit compUnit) {
    return existsHandwrittenClass(hwPath, determineQualifiedName(cdInterface, compUnit));
  }

  /** Should the TOP mechanism be applied to an enum */
  protected boolean shouldApplyTOPToEnum(ASTCDEnum cdEnum, ASTCDCompilationUnit compUnit) {
    return existsHandwrittenClass(hwPath, determineQualifiedName(cdEnum, compUnit));
  }

  /**
   * Log an error, if the needsTop stereo is present on a class. Replace the first %s within the
   * value of the stereo with the qualifiedName, and append it to the error message
   */
  protected void checkNeedsHandwrittenClass(
      boolean existsHw, ASTCDClass cdClass, String qualifiedName) {

    // check for stereotype
    getStereotype(cdClass)
        .flatMap(
            stereo ->
                stereo.getValuesList().stream()
                    .filter(stereoValue -> stereoValue.getName().equals(NEEDS_TOP_IDENTIFIER))
                    .findFirst())
        .ifPresent(
            needsTopStereo -> {
              if (!existsHw) {
                String errorMsg = String.format("0xC0FFEE00: %s", needsTopStereo.getValue());
                // an %s in the stereo is substituted with the qualified name of the missing class
                Log.error(
                    String.format(errorMsg, qualifiedName),
                    needsTopStereo.get_SourcePositionStart());
              }
            });
  }

  protected String determineQualifiedName(
      ASTCDType astcdtype, ASTCDCompilationUnit astcdCompilationUnit) {
    if (this.packageName.isEmpty()) {
      return constructQualifiedName(astcdCompilationUnit.getCDPackageList(), astcdtype.getName());
    }
    return constructQualifiedName(this.packageName, astcdtype.getName());
  }

  protected Optional<ASTStereotype> getStereotype(ASTCDClass cdClass) {
    ASTModifier modifier = cdClass.getModifier();
    if (modifier != null && modifier.isPresentStereotype()) {
      return Optional.of(modifier.getStereotype());
    }
    return Optional.empty();
  }

  /** Rename the (now abstract) class and its constructors */
  protected void applyTopMechanism(ASTCDClass cdClass) {
    makeAbstract(cdClass);
    cdClass.setName(cdClass.getName() + TOP_SUFFIX);

    cdClass
        .getCDConstructorList()
        .forEach(constructor -> constructor.setName(constructor.getName() + TOP_SUFFIX));
  }

  protected void makeAbstract(ASTCDType type) {
    makeAbstract(type.getModifier());
  }

  protected void makeAbstract(ASTModifier modifier) {
    modifier.setAbstract(true);
  }

  protected void applyTopMechanism(ASTCDInterface cdInterface) {
    cdInterface.setName(cdInterface.getName() + TOP_SUFFIX);
  }

  protected void applyTopMechanism(ASTCDEnum cdEnum) {
    // Note: While it is impossible to extend enums, we still change the generates ones' name
    cdEnum.setName(cdEnum.getName() + TOP_SUFFIX);
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
    traverser.add4CDInterfaceAndEnum(this);
  }
}

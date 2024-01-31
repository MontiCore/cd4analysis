/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import static de.monticore.generating.GeneratorEngine.existsHandwrittenClass;
import static de.se_rwth.commons.Names.constructQualifiedName;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
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
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.logging.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class TopDecorator {

  /*
  Adds the suffix TOP to hand coded ASTs and makes generated TOP class abstract
  Attention! does not actually create a new CD object, because then the glex has the wrong objects referenced
   */

  public static final String TOP_SUFFIX = "TOP";
  public static final String NEEDS_TOP_IDENTIFIER = "needs" + TOP_SUFFIX;

  @SuppressWarnings("unused")
  public static final Function<String, ASTStereoValue> NEEDS_TOP_STEREO_BUILDER =
      (String reason) -> {
        ASTStereoValue stereoValue = new ASTStereoValue();
        stereoValue.setName(NEEDS_TOP_IDENTIFIER);
        stereoValue.setContent(reason);
        return stereoValue;
      };

  protected final MCPath hwPath;

  protected Map<ASTCDType, String> nameMap = new HashMap<>();

  public TopDecorator(MCPath hwPath) {
    this.hwPath = hwPath;
  }

  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit compUnit) {
    CD4CodeTraverser traverser = CD4CodeMill.traverser();
    DetermineNameVisitor nameVisitor = new DetermineNameVisitor();
    traverser.add4CDBasis(nameVisitor);
    traverser.add4CDInterfaceAndEnum(nameVisitor);
    compUnit.accept(traverser);

    compUnit.getCDDefinition().getCDClassesList().stream()
        .filter(
            cdClass -> {
              boolean existsHw =
                  existsHandwrittenClass(hwPath, determineQualifiedName(cdClass, compUnit));
              checkNeedsHandwrittenClass(existsHw, cdClass);
              return existsHw;
            })
        .forEach(this::applyTopMechanism);

    compUnit.getCDDefinition().getCDInterfacesList().stream()
        .filter(
            cdInterface ->
                existsHandwrittenClass(hwPath, determineQualifiedName(cdInterface, compUnit)))
        .forEach(this::applyTopMechanism);

    compUnit.getCDDefinition().getCDEnumsList().stream()
        .filter(cdEnum -> existsHandwrittenClass(hwPath, determineQualifiedName(cdEnum, compUnit)))
        .forEach(this::applyTopMechanism);

    return compUnit;
  }

  protected String determineQualifiedName(
      ASTCDType astcdtype, ASTCDCompilationUnit astcdCompilationUnit) {
    String packageName = nameMap.get(astcdtype);
    if (packageName.isEmpty()) {
      return constructQualifiedName(astcdCompilationUnit.getCDPackageList(), astcdtype.getName());
    }
    return constructQualifiedName(Lists.newArrayList(packageName), astcdtype.getName());
  }

  protected void applyTopMechanism(ASTCDClass cdClass) {
    makeAbstract(cdClass);
    cdClass.setName(cdClass.getName() + TOP_SUFFIX);

    cdClass
        .getCDConstructorList()
        .forEach(constructor -> constructor.setName(constructor.getName() + TOP_SUFFIX));
  }

  protected void applyTopMechanism(ASTCDInterface cdInterface) {
    cdInterface.setName(cdInterface.getName() + TOP_SUFFIX);
  }

  protected void applyTopMechanism(ASTCDEnum cdEnum) {
    cdEnum.setName(cdEnum.getName() + TOP_SUFFIX);
  }

  protected void makeAbstract(ASTCDType type) {
    makeAbstract(type.getModifier());
  }

  protected void makeAbstract(ASTModifier modifier) {
    modifier.setAbstract(true);
  }

  protected void checkNeedsHandwrittenClass(boolean existsHw, ASTCDClass cdClass) {
    // ensure fail quick
    boolean failQuickEnabled = Log.isFailQuickEnabled();
    Log.enableFailQuick(true);

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
                Log.error(String.format("0xC0FFEE00: %s", needsTopStereo.getContent()));
              }
            });

    // reset fail quick
    Log.enableFailQuick(failQuickEnabled);
  }

  protected Optional<ASTStereotype> getStereotype(ASTCDClass cdClass) {
    ASTModifier modifier = cdClass.getModifier();
    if (modifier != null && modifier.isPresentStereotype()) {
      return Optional.of(modifier.getStereotype());
    }
    return Optional.empty();
  }

  protected class DetermineNameVisitor implements CDBasisVisitor2, CDInterfaceAndEnumVisitor2 {

    /**
     * Public constructor to construct the {@link #nameMap}
     */
    public DetermineNameVisitor() { }

    protected String packageName = "";

    @Override
    public void visit(ASTCDPackage node) {
      packageName = node.getName();
    }

    @Override
    public void endVisit(ASTCDPackage node) {
      packageName = "";
    }

    @Override
    public void visit(ASTCDClass node) {
      nameMap.put(node, packageName);
    }

    @Override
    public void visit(ASTCDInterface node) {
      nameMap.put(node, packageName);
    }

    @Override
    public void visit(ASTCDEnum node) {
      nameMap.put(node, packageName);
    }
  }
}

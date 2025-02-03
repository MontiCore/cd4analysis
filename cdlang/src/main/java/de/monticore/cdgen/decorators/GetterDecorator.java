/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.decorators;

import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * Add get methods to all attributes
 */
public class GetterDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements CDBasisVisitor2 {


  @Override
  public void visit(ASTCDAttribute attribute) {
    if (decoratorData.shouldDecorate(this.getClass(), attribute)) {
      var originalClazz = decoratorData.getParent(attribute);
      var decClazz = (ASTCDClass) decoratorData.getAsDecorated(originalClazz.get());
      if (MCTypeFacade.getInstance().isBooleanType(attribute.getMCType())) {
        decorateMandatory(decClazz, attribute);
      } else if (attribute.getMCType() instanceof ASTMCListType) {
        Log.warn("0xTODO List getter");
      } else if (attribute.getMCType() instanceof ASTMCSetType) {
        Log.warn("0xTODO Set getter");
      } else if (attribute.getMCType() instanceof ASTMCOptionalType) {
        decorateOptional(decClazz, attribute);
      } else {
        decorateMandatory(decClazz, attribute);
      }

    }
  }

  protected void decorateMandatory(ASTCDClass decoratedClazz, ASTCDAttribute attribute) {
    String name = (MCTypeFacade.getInstance().isBooleanType(attribute.getMCType()) ? "is" : "get")
      + StringTransformations.capitalize(attribute.getName());
    ASTMCType type = attribute.getMCType().deepClone();
    ASTCDMethod method = CDMethodFacade.getInstance().createMethod(attribute.getModifier().deepClone(), type, name);
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.Get", attribute)));
    method.getModifier().setAbstract(attribute.getModifier().isDerived());

    addToClass(decoratedClazz, method);

   this.updateModifier(attribute);
  }

  protected void decorateOptional(ASTCDClass decoratedClazz, ASTCDAttribute attribute) {
    String name = "get" + StringTransformations.capitalize(attribute.getName());
    ASTMCType type = getCDGenService().getFirstTypeArgument(attribute.getMCType()).deepClone();

    String generatedErrorCode =
      getCDGenService().getGeneratedErrorCode(attribute.getName() + attribute.getMCType().printType());
    ASTCDMethod getMethod = CDMethodFacade.getInstance().createMethod(attribute.getModifier().deepClone(), type, name);
    String nativeAttributeName = StringUtils.capitalize(getCDGenService().getNativeAttributeName(attribute.getName()));;
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, getMethod, new TemplateHookPoint("methods.opt.Get4Opt", attribute, nativeAttributeName, generatedErrorCode)));
    getMethod.getModifier().setAbstract(attribute.getModifier().isDerived());
    CD4C.getInstance().addImport(decoratedClazz, Log.class.getName());

    addToClass(decoratedClazz, getMethod);


    ASTCDMethod isPresentMethod = CDMethodFacade.getInstance().createMethod(attribute.getModifier().deepClone(), MCTypeFacade.getInstance().createBooleanType(), "isPresent" + StringTransformations.capitalize(attribute.getName()));
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, isPresentMethod, new TemplateHookPoint("methods.opt.IsPresent4Opt", attribute)));
    addToClass(decoratedClazz, isPresentMethod);

    this.updateModifier(attribute);
  }

  protected void decorateList(ASTCDClass decoratedClazz, ASTCDAttribute attribute) {
    String name = "get"  + StringTransformations.capitalize(attribute.getName()) + "List";
    ASTMCType type = getCDGenService().getFirstTypeArgument(attribute.getMCType()).deepClone();

    ASTCDMethod getListMethod = CDMethodFacade.getInstance().createMethod(attribute.getModifier().deepClone(), type, name);
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, getListMethod, new TemplateHookPoint("methods.Get", attribute)));
    getListMethod.getModifier().setAbstract(attribute.getModifier().isDerived());

    String attributeType = type.printType();

    String capitalizedAttributeNameWithS = StringUtils.capitalize(getCDGenService().getNativeAttributeName(attribute.getName()));
    String capitalizedAttributeNameWithOutS;
    // but if the attributeName is derived then the s is removed
    if (capitalizedAttributeNameWithS.endsWith("s") && getCDGenService().hasDerivedAttributeName(attribute)) {
      capitalizedAttributeNameWithOutS = capitalizedAttributeNameWithS.substring(0, capitalizedAttributeNameWithS.length() - 1);
    } else {
      capitalizedAttributeNameWithOutS = capitalizedAttributeNameWithS;
    }

    addToClass(decoratedClazz, getListMethod);


    if (!attribute.getModifier().isDerived()) {
      for (String signature : Arrays.asList(
        String.format(CONTAINS, capitalizedAttributeNameWithOutS),
        String.format(CONTAINS_ALL, capitalizedAttributeNameWithS),
        String.format(IS_EMPTY, capitalizedAttributeNameWithS),
        String.format(ITERATOR, attributeType, capitalizedAttributeNameWithS),
        String.format(SIZE, capitalizedAttributeNameWithS),
        String.format(TO_ARRAY, attributeType, capitalizedAttributeNameWithS, attributeType),
        String.format(TO_ARRAY_, capitalizedAttributeNameWithS),
        String.format(SPLITERATOR, attributeType, capitalizedAttributeNameWithS),
        String.format(STREAM, attributeType, capitalizedAttributeNameWithS),
        String.format(PARALLEL_STREAM, attributeType, capitalizedAttributeNameWithS),
        String.format(GET, attributeType, capitalizedAttributeNameWithOutS),
        String.format(INDEX_OF, capitalizedAttributeNameWithOutS),
        String.format(LAST_INDEX_OF, capitalizedAttributeNameWithOutS),
        String.format(EQUALS, capitalizedAttributeNameWithS),
        String.format(HASHCODE, capitalizedAttributeNameWithS),
        String.format(LIST_ITERATOR, attributeType, capitalizedAttributeNameWithS),
        String.format(LIST_ITERATOR_, attributeType, capitalizedAttributeNameWithS),
        String.format(SUBLIST, attributeType, capitalizedAttributeNameWithS))) {

        ASTCDMethod method = CDMethodFacade.getInstance().createMethodByDefinition(signature);


        this.glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, method, createListImplementation(method, capitalizedAttributeNameWithOutS)));

      }
    }

    this.updateModifier(attribute);
  }

  protected HookPoint createListImplementation(final ASTCDMethod method, String capitalizedAttributeNameWithOutS) {
    String attributeName = StringUtils.uncapitalize(capitalizedAttributeNameWithOutS);
    int attributeIndex = method.getName().lastIndexOf(capitalizedAttributeNameWithOutS);
    String methodName = method.getName().substring(0, attributeIndex);
    String parameterCall =
      method.getCDParameterList().stream()
        .map(ASTCDParameter::getName)
        .collect(Collectors.joining(", "));
    String returnType =
      (new CD4CodeFullPrettyPrinter(new IndentPrinter())).prettyprint(method.getMCReturnType());

    return new TemplateHookPoint(
      "methods.MethodDelegate", attributeName, methodName, parameterCall, returnType);
  }


  protected static final String CONTAINS = "public boolean contains%s(Object element);";
  protected static final String CONTAINS_ALL =
    "public boolean containsAll%s(Collection<?> collection);";
  protected static final String IS_EMPTY = "public boolean isEmpty%s();";
  protected static final String ITERATOR = "public Iterator<%s> iterator%s();";
  protected static final String SIZE = "public int size%s();";
  protected static final String TO_ARRAY = "public %s[] toArray%s(%s[] array);";
  protected static final String TO_ARRAY_ = "public Object[] toArray%s();";
  protected static final String SPLITERATOR = "public Spliterator<%s> spliterator%s();";
  protected static final String STREAM = "public Stream<%s> stream%s();";
  protected static final String PARALLEL_STREAM = "public Stream<%s> parallelStream%s();";
  protected static final String GET = "public %s get%s(int index);";
  protected static final String INDEX_OF = "public int indexOf%s(Object element);";
  protected static final String LAST_INDEX_OF = "public int lastIndexOf%s(Object element);";
  protected static final String EQUALS = "public boolean equals%s(Object o);";
  protected static final String HASHCODE = "public int hashCode%s();";
  protected static final String LIST_ITERATOR = "public ListIterator<%s> listIterator%s();";
  protected static final String LIST_ITERATOR_ =
    "public ListIterator<%s> listIterator%s(int index);";
  protected static final String SUBLIST = "public List<%s> subList%s(int start, int end);";


  protected void updateModifier(ASTCDAttribute attribute) {
    var decoratedModifier = decoratorData.getAsDecorated(attribute).getModifier();
    decoratedModifier.setProtected(true);
    decoratedModifier.setPublic(false);
    decoratedModifier.setPrivate(false);
  }



  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
}

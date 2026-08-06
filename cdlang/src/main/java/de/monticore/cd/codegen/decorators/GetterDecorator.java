/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.codegen.decorators.data.DecoratorData;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Add get methods to all attributes <a
 * href="https://mbse.se-rwth.de/book2/index.php?c=chapter5-1">methodic</a>
 */
public class GetterDecorator extends AbstractDecorator<GetterDecorator.GetterData> implements
    CDBasisVisitor2 {

  protected GetterData getterData;

  @Override
  public void init(DecoratorData util, Optional<GlobalExtensionManagement> glexOpt) {
    super.init(util, glexOpt);
    this.getterData = this.decoratorData.createDataIfAbsent(this.getClass(), GetterData::new);
  }

  @Override
  public void visit(ASTCDAttribute attribute) {
    // First, check if we should decorate the given object
    if (decoratorData.shouldDecorate(this.getClass(), attribute)) {
      // Retrieve the parent of the attribute
      var originalClazz = decoratorData.getParent(attribute).get();
      //
      var decType = (ASTCDType) decoratorData.getAsDecorated(originalClazz);
      if (MCTypeFacade.getInstance().isBooleanType(attribute.getMCType())) {
        this.getterData.getOrCreateMethods(attribute).add(decorateMandatory(decType, attribute));
      }
      else if (MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType())) {
        this.getterData.getOrCreateMethods(attribute).add(decorateList(decType, attribute));
        decorateWithAssocFunctions(decType, attribute, true);
      }
      else if (MCCollectionSymTypeRelations.isSet(attribute.getSymbol().getType())) {
        this.getterData.getOrCreateMethods(attribute).add(decorateSet(decType, attribute));
        decorateWithAssocFunctions(decType, attribute, false);
      }
      else if (MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())) {
        this.getterData.getOrCreateMethods(attribute).add(decorateOptional(decType, attribute));
        this.getterData.getOrCreateMethods(attribute).add(decorateOptionalIsPresent(decType,
            attribute));
      }
      else {
        this.getterData.getOrCreateMethods(attribute).add(decorateMandatory(decType, attribute));
      }
    }
  }

  protected MethodInformation decorateMandatory(ASTCDType decoratedType, ASTCDAttribute attribute) {
    String name = (MCTypeFacade.getInstance().isBooleanType(attribute.getMCType()) ? "is" : "get")
        + StringTransformations.capitalize(attribute.getName());
    ASTMCType type = attribute.getMCType().deepClone();
    ASTCDMethod method = CDMethodFacade.getInstance().createMethod(attribute.getModifier()
        .deepClone(), type, name);
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, method, templateHookPoint(
        "methods.Get", "mandatory", attribute)));
    method.getModifier().setAbstract(attribute.getModifier().isDerived());

    addToClass(decoratedType, method);

    this.updateModifier(attribute);

    return new MethodInformation(GetterMethodKind.GET_MANDATORY_OR_OPT, method, "methods.Get",
        attribute.getName());
  }

  protected TemplateHookPoint templateHookPoint3(String templateName, Object... templateArguments) {
    return templateHookPoint(templateName, "", templateArguments);
  }

  protected void decorateOptional(ASTCDType decoratedType, ASTCDAttribute attribute) {
    String name = "get" + StringTransformations.capitalize(attribute.getName());
    ASTMCType type = getCDGenService().getFirstTypeArgument(attribute.getMCType()).deepClone();

    String generatedErrorCode = getCDGenService().getGeneratedErrorCode(attribute.getName()
        + attribute.getMCType().printType());
    ASTCDMethod getMethod = CDMethodFacade.getInstance().createMethod(attribute.getModifier()
        .deepClone(), type, name);
    String nativeAttributeName = StringUtils.capitalize(getCDGenService().getNativeAttributeName(
        attribute.getName()));
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, getMethod, templateHookPoint3(
        "methods.opt.Get4Opt", attribute, nativeAttributeName, generatedErrorCode)));
    getMethod.getModifier().setAbstract(attribute.getModifier().isDerived());
    CD4C.getInstance().addImport(decoratedType, Log.class.getName());

    addToClass(decoratedType, getMethod);
    return new MethodInformation(GetterMethodKind.GET_MANDATORY_OR_OPT, getMethod,
        "methods.opt.Get4Opt", attribute.getName());
  }

  protected MethodInformation decorateOptionalIsPresent(ASTCDType decoratedType,
      ASTCDAttribute attribute) {
    ASTCDMethod isPresentMethod = CDMethodFacade.getInstance().createMethod(attribute.getModifier()
        .deepClone(), MCTypeFacade.getInstance().createBooleanType(), "isPresent"
            + StringTransformations.capitalize(attribute.getName()));
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, isPresentMethod, templateHookPoint3(
        "methods.opt.IsPresent4Opt", attribute)));
    addToClass(decoratedType, isPresentMethod);

    this.updateModifier(attribute);
    return new MethodInformation(GetterMethodKind.IS_PRESENT, isPresentMethod,
        "methods.opt.IsPresent4Opt", attribute.getName());
  }

  protected MethodInformation decorateSet(ASTCDType decoratedType, ASTCDAttribute attribute) {
    String name = "get" + StringTransformations.capitalize(attribute.getName());
    ASTMCType type = getCDGenService().getFirstTypeArgument(attribute.getMCType()).deepClone();

    ASTCDMethod getListMethod = CDMethodFacade.getInstance().createMethod(attribute.getModifier()
        .deepClone(), MCTypeFacade.getInstance().createSetTypeOf(type), name);
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, getListMethod, templateHookPoint3(
        "methods.Get", attribute)));
    getListMethod.getModifier().setAbstract(attribute.getModifier().isDerived());
    addToClass(decoratedType, getListMethod);

    this.updateModifier(attribute);

    return new MethodInformation(GetterMethodKind.GET_COLLECTION, getListMethod, "methods.Get",
        attribute.getName());
  }

  protected MethodInformation decorateList(ASTCDType decoratedType, ASTCDAttribute attribute) {
    String name = "get" + StringTransformations.capitalize(attribute.getName());
    ASTMCType type = getCDGenService().getFirstTypeArgument(attribute.getMCType()).deepClone();

    ASTCDMethod getListMethod = CDMethodFacade.getInstance().createMethod(attribute.getModifier()
        .deepClone(), MCTypeFacade.getInstance().createListTypeOf(type), name);
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, getListMethod, templateHookPoint3(
        "methods.Get", attribute)));
    getListMethod.getModifier().setAbstract(attribute.getModifier().isDerived());
    addToClass(decoratedType, getListMethod);

    this.updateModifier(attribute);
    return new MethodInformation(GetterMethodKind.GET_COLLECTION, getListMethod, "methods.Get",
        attribute.getName());
  }

  protected void decorateWithAssocFunctions(ASTCDType decoratedType, ASTCDAttribute attribute,
      boolean isList) {
    ASTMCType type = getCDGenService().getFirstTypeArgument(attribute.getMCType()).deepClone();

    String attributeType = type.printType();

    String capitalizedAttributeNameWithS = StringUtils.capitalize(getCDGenService()
        .getNativeAttributeName(attribute.getName()));
    String capitalizedAttributeNameWithOutS;
    // but if the attributeName is derived then the s is removed
    if (capitalizedAttributeNameWithS.endsWith("s") && getCDGenService().hasDerivedAttributeName(
        attribute)) {
      capitalizedAttributeNameWithOutS = capitalizedAttributeNameWithS.substring(0,
          capitalizedAttributeNameWithS.length() - 1);
    }
    else {
      capitalizedAttributeNameWithOutS = capitalizedAttributeNameWithS;
    }

    if (!attribute.getModifier().isDerived()) {
      for (String signature : Arrays.asList(String.format(CONTAINS,
          capitalizedAttributeNameWithOutS), String.format(CONTAINS_ALL,
              capitalizedAttributeNameWithS), String.format(IS_EMPTY,
                  capitalizedAttributeNameWithS), String.format(ITERATOR, attributeType,
                      capitalizedAttributeNameWithS), String.format(SIZE,
                          capitalizedAttributeNameWithS), String.format(TO_ARRAY, attributeType,
                              capitalizedAttributeNameWithS, attributeType), String.format(
                                  TO_ARRAY_, capitalizedAttributeNameWithS), String.format(
                                      SPLITERATOR, attributeType, capitalizedAttributeNameWithS),
          String.format(STREAM, attributeType, capitalizedAttributeNameWithS), String.format(
              PARALLEL_STREAM, attributeType, capitalizedAttributeNameWithS), String.format(EQUALS,
                  capitalizedAttributeNameWithS), String.format(HASHCODE,
                      capitalizedAttributeNameWithS))) {
        ASTCDMethod method = CDMethodFacade.getInstance().createMethodByDefinition(signature);
        addToClass(decoratedType, method);
        this.glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, method,
            createListImplementation(method, capitalizedAttributeNameWithOutS)));
      }
      if (isList) {
        for (String signature : Arrays.asList(String.format(GET, attributeType,
            capitalizedAttributeNameWithOutS), String.format(INDEX_OF,
                capitalizedAttributeNameWithOutS), String.format(LAST_INDEX_OF,
                    capitalizedAttributeNameWithOutS), String.format(LIST_ITERATOR, attributeType,
                        capitalizedAttributeNameWithS), String.format(LIST_ITERATOR_, attributeType,
                            capitalizedAttributeNameWithS), String.format(SUBLIST, attributeType,
                                capitalizedAttributeNameWithS))) {
          ASTCDMethod method = CDMethodFacade.getInstance().createMethodByDefinition(signature);
          method.setModifier(attribute.getModifier().deepClone());
          addToClass(decoratedType, method);
          this.glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, method,
              createListImplementation(method, capitalizedAttributeNameWithOutS)));
        }
      }
    }
  }

  protected HookPoint createListImplementation(final ASTCDMethod method,
      String capitalizedAttributeNameWithOutS) {
    String attributeName = StringUtils.uncapitalize(capitalizedAttributeNameWithOutS);
    int attributeIndex = method.getName().lastIndexOf(capitalizedAttributeNameWithOutS);
    String methodName = method.getName().substring(0, attributeIndex);
    String parameterCall = method.getCDParameterList().stream().map(ASTCDParameter::getName)
        .collect(Collectors.joining(", "));
    String returnType = (new CD4CodeFullPrettyPrinter(new IndentPrinter())).prettyprint(method
        .getMCReturnType());

    return templateHookPoint3("methods.AnyMethodDelegate", attributeName, methodName, parameterCall,
        returnType);
  }

  protected static final String CONTAINS = "public boolean contains%s(Object element);";
  protected static final String CONTAINS_ALL =
      "public boolean containsAll%s(java.util.Collection<?> collection);";
  protected static final String IS_EMPTY = "public boolean isEmpty%s();";
  protected static final String ITERATOR = "public java.util.Iterator<%s> iterator%s();";
  protected static final String SIZE = "public int size%s();";
  protected static final String TO_ARRAY = "public %s[] toArray%s(%s[] array);";
  protected static final String TO_ARRAY_ = "public Object[] toArray%s();";
  protected static final String SPLITERATOR = "public java.util.Spliterator<%s> spliterator%s();";
  protected static final String STREAM = "public java.util.stream.Stream<%s> stream%s();";
  protected static final String PARALLEL_STREAM =
      "public java.util.stream.Stream<%s> parallelStream%s();";
  protected static final String GET = "public %s get%s(int index);";
  protected static final String INDEX_OF = "public int indexOf%s(Object element);";
  protected static final String LAST_INDEX_OF = "public int lastIndexOf%s(Object element);";
  protected static final String EQUALS = "public boolean equals%s(Object o);";
  protected static final String HASHCODE = "public int hashCode%s();";
  protected static final String LIST_ITERATOR =
      "public java.util.ListIterator<%s> listIterator%s();";
  protected static final String LIST_ITERATOR_ =
      "public java.util.ListIterator<%s> listIterator%s(int index);";
  protected static final String SUBLIST =
      "public java.util.List<%s> subList%s(int start, int end);";

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

  public static class GetterData {

    protected final Map<ASTCDAttribute, List<MethodInformation>> attributesData =
        new LinkedHashMap<>();

    public List<MethodInformation> getMethods(ASTCDAttribute node) {
      var ret = attributesData.get(node);
      if (ret == null) {
        Log.warn("Requested setter of " + node.getSymbol().getFullName()
            + ", which has no setters!", node.get_SourcePositionStart());
      }
      return ret == null ? List.of() : ret;
    }

    protected List<MethodInformation> getOrCreateMethods(ASTCDAttribute node) {
      return this.attributesData.computeIfAbsent(node, a -> new ArrayList<>());
    }

  }

  public static class MethodInformation {

    private final GetterMethodKind kind;
    private final ASTCDMethod getMethod;
    private final String templateName;
    private final String paramName;

    public MethodInformation(GetterMethodKind kind, ASTCDMethod setMethod, String templateName,
        String paramName) {
      this.kind = kind;
      this.getMethod = setMethod;
      this.templateName = templateName;
      this.paramName = paramName;
    }

    public GetterMethodKind getKind() { return kind; }

    public ASTCDMethod getGetMethod() { return getMethod; }

    public String getTemplateName() { return templateName; }

    public String getParamName() { return paramName; }

  }

  public enum GetterMethodKind {
    GET_MANDATORY_OR_OPT, IS_PRESENT, GET_COLLECTION
  }

}

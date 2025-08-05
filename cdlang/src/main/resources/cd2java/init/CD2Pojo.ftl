<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template configures the default CD2Pojo generation.

  Call it using the CLI: .. -ct cd2java.CD2Pojo

-->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="decConfig" type="de.monticore.cd.codegen.DecoratorConfig" -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="genSetup" type="de.monticore.generating.GeneratorSetup" -->


<#--
  To be exact, this template adds various decorators to the DecoratorConfig.
  Each decorator only applies to an element if the element or the elements parents match on the decorator.
  Decorators visit the original read-only CD and add/modify elements to/of a decorated CD.
  Dependencies between decorators and their order are resolved via a DAG.
 -->

<#--Apply the default creator: Copy the original CD and use it as the base-->
${decConfig.withCopyCreator().defaultApply()}
<#-- By default (defaultApply) the GetterDecorator is applied, unless an element or its parents are marked with noGetter -->
${decConfig.withGetters().ignoreOnName("noGetter").defaultApply()}
<#--  Similar configuration for a decorator setting the initial value of associations -->
${decConfig.withDefaultsForCardinalityAttrs().ignoreOnName("noDefaultCardinality").defaultApply()}
<#-- Similar configuration for the Setter Decorator -->
${decConfig.withSetters().ignoreOnName("noSetter").defaultApply()}
<#-- And the NavigableSetters (for bidirectional assocs). -->
<#-- The implementation of the NavigableSetters decorator requires that the Setter decorator has run before.-->
${decConfig.withNavigableSetters().ignoreOnName("noSetter").defaultApply()}
<#-- The DeepCloneAndDeepEqualsDecorator is applied by default-->
<#-- The implementation requires that no CD element excluded. Therefore we have no ignore Statement -->
${decConfig.withDeepCloneAndDeepEquals().defaultApply()}
<#-- The VisitorDecorator and the InheritanceVisitorDecorator are applied by default -->
${decConfig.withVisitors().defaultApply()}
${decConfig.withInheritanceVisitors().defaultApply()}
<#--Method signatures will be turned into abstract methods-->
${decConfig.withAbstractMethodSignatures().ignoreOnName("nonAbstractMethod").defaultApply()}
<#--The following decorators are not applied by default, instead they have to be explicitly configured using stereos/tags/etc-->
<#-- By default, the Builders decorator is NOT applied, unless an element or its parents are marked with builder -->
<#--  Builders are also not applied when the element is not marked and the parent is marked with noBuilder. -->
${decConfig.withBuilders().applyOnName("builder").ignoreOnName("noBuilder")}
<#-- Similarly, the Observable decorator is NOT applied by default, unless an element or its parents are marked with observable -->
${decConfig.withObservers().applyOnName("observable").ignoreOnName("notObservable")}


<#--
 You can include & override the defaults by including this template
  ${tc.includeArgs("CD2Pojo", ...)}
 -->

<#--It is possible to add your own decorators via a config template, see the CD2OwnDecorator.ftl (located in the tests) -->

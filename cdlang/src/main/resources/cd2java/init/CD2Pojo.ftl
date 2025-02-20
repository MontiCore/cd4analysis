<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template configures the default CD2Pojo generation.

  Call it using the CLI: .. -ct cd2java.CD2Pojo

-->
${tc.signature("glex", "deConf")}
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="deConf" type="de.monticore.cd.codegen.DecoratorConfig" -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->


<#--
  To be exact, this template adds various decorators to the DecoratorConfig.
  Each decorator only applies to an element if the element or the elements parents match on the decorator.
  Decorators visit the original read-only CD and add/modify elements to/of a decorated CD.
  Dependencies between decorators and their order are resolved via a DAG.
 -->
<#-- By default (defaultApply) the GetterDecorator is applied, unless an element or its parents are marked with noGetter -->
${deConf.withGetters().ignoreOnName("noGetter").defaultApply()}
<#-- Similar configuration for the Setter Decorator -->
${deConf.withSetters().ignoreOnName("noSetter").defaultApply()}
<#-- And the NavigableSetters (for bidirectional assocs). -->
<#-- The implementation of the NavigableSetters decorator requires that the Setter decorator has run before.-->
${deConf.withNavigableSetters().ignoreOnName("noSetter").defaultApply()}
<#--The following decorators are not applied by default, instead they have to be explicitly configured using stereos/tags/etc-->
<#-- By default, the Builders decorator is NOT applied, unless an element or its parents are marked with builder -->
<#--  Builders are also not applied when the element is not marked and the parent is marked with noBuilder. -->
${deConf.withBuilders().applyOnName("builder").ignoreOnName("noBuilder")}
<#-- Similarly, the Observable decorator is NOT applied by default, unless an element or its parents are marked with observable -->
${deConf.withObservers().applyOnName("observable").ignoreOnName("notObservable")}


<#--
 You can include & override the defaults by including this template
  ${tc.includeArgs("CD2Pojo", ...)}
 -->

<#--It is possible to add your own decorators via a config template, see the CD2OwnDecorator.ftl (located in the tests) -->

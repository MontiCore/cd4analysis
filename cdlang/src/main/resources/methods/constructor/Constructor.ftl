<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->

${tc.signature("attributes")}
<#list attributes as attr>
<#--  ${tc.includeArgs("methods.Set", attr)}  # todo: new list?-->
this.${attr.getName()} = ${attr.getName()};
</#list>

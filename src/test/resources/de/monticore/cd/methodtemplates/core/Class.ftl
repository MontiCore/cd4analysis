<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java class

-->
${tc.signature("printer")}

<#--${ast.printAnnotation()}
${ast.printModifier()}--> class ${ast.getName()}

<#-- add additional methods -->
${cd4c.addMethod(ast, "de.monticore.cd.methodtemplates.Counter")};

{
<#-- generate all methods -->
  <#list ast.getCDConstructorList() as constructor>
    ${tc.includeArgs("de.monticore.cd.methodtemplates.core.Constructor", [constructor, printer])}
  </#list>

<#-- generate all methods -->
  <#list ast.getCDMethodList() as method>
    ${tc.includeArgs("de.monticore.cd.methodtemplates.core.Method", [method, printer])}
  </#list>

}

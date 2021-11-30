<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default CD2Java generation.

  Call it using the CLI: .. -ct de.monticore.cd.CD2Java

-->
${tc.signature("glex", "cdGenerator")}

<#assign cl = ast.getCDDefinition().getCDClassesList()>
<#list cl as clazz>
  ${cd4c.addAttribute(clazz, "int dummy = 0;")}
  <#if clazz.getName() == "H">
    ${cd4c.addAttribute(clazz, "int counter = 0;")}
    ${cd4c.addMethod(clazz, "de.monticore.cd.Counter")}
  </#if>
</#list>

${cdGenerator.generate(ast)}

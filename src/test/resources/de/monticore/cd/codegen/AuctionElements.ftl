<#-- (c) https://github.com/MontiCore/monticore -->
<#assign name=ast.getName()>
<#if name == "Person">
${cd4c.addAttribute(ast, true, true, "int counter = 0;")}
${cd4c.addMethod(ast, "de.monticore.cd.methodtemplates.Counter")}
</#if>

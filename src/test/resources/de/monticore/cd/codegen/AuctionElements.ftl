<#assign name=ast.getName()>
<#if name == "Person">
${cd4c.addAttribute(ast, "int counter = 0;")}
${cd4c.addMethod(ast, "de.monticore.cd.methodtemplates.Counter")}
</#if>

<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java Constructor

-->

${tc.signature("ast", "printer")}

${printer.prettyprint(ast.getModifier())}  ${ast.getName()}(<#list ast.getCDParameterList() as param>
${printer.prettyprint(param)}<#if param_has_next>, </#if>
</#list>) <#if ast.isPresentCDThrowsDeclaration()>${printer.prettyprint(ast.getCDThrowsDeclaration())}</#if>
${tc.include("de.monticore.cd.methodtemplates.core.EmptyMethod", ast)}

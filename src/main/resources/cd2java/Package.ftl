<#-- (c) https://github.com/MontiCore/monticore -->
<#assign packageName=cdPrinter.printPackageName(ast)>
<#if packageName?has_content>
package ${cdPrinter.printPackageName(ast)};
</#if>

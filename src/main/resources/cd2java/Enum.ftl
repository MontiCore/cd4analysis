<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("package")}
/* (c) https://github.com/MontiCore/monticore */
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

${tc.include("cd2java.Package", package)}

${tc.include("cd2java.Annotations")}
public enum ${ast.getName()}
<#if ast.isPresentCDInterfaceUsage()>implements ${cdPrinter.printObjectTypeList(ast.getCDInterfaceUsage().getInterfaceList())} </#if>{

<#list ast.getastConstantList() as constants>
  ${tc.include("cd2java.EmptyConstants", constants)}<#if !constants?is_last>,</#if>
</#list>
;

<#list ast.getCDAttributeList() as attribute>
  ${tc.include("cd2java.Attribute", attribute)}
</#list>

<#list ast.getCDConstructorList() as constructor>
  ${tc.include("cd2java.Constructor", constructor)}
</#list>

<#list ast.getCDMethodList() as method>
  ${tc.include("cd2java.Method", method)}
</#list>
}

<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributes","staticErrorCode")}

<#list attributes as attribute>
<#assign errorCode = staticErrorCode + cdGenService.getGeneratedErrorCode(attribute.getName()+attribute.getMCType().printType())>
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>

<#-- Check if the attribute is not a list, set or optional as they have isAbsent methods-->
<#if (!(CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCListType(attribute.getMCType()) ||
     CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(attribute.getMCType()) ||
     CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(attribute.getMCType())))>

  <#-- as primitive types cannot be check for == null we need to ignore them -->
  <#if (!(CD4AnalysisTypeDispatcher.isMCBasicTypesASTMCPrimitiveType(attribute.getMCType())))>
if (this.${attribute.getName()} == null) {
  Log.error("${errorCode} ${attribute.getName()} of type ${attribute.printType()} must not be null");
  return false;
}
  </#if>
</#if>
</#list>

return true;

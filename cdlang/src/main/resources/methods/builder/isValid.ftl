<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributes","staticErrorCode","cD4AnalysisTypeDispatcher")}

<#list attributes as attribute>
<#assign errorCode = staticErrorCode + cdGenService.getGeneratedErrorCode(attribute.getName()+attribute.getMCType().printType())>
<#assign MCCollectionSymTypeRelations = glex.getGlobalVar("mcCollectionSymTypeRelations")>

<#-- Check if the attribute is not a list, set or optional as they have isAbsent methods-->
<#if (!(MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isSet(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())))>

  <#-- as primitive types cannot be check for == null we need to ignore them -->
  <#if (!(cD4AnalysisTypeDispatcher.isMCBasicTypesASTMCPrimitiveType(attribute.getMCType())))>
if (this.${attribute.getName()} == null) {
  Log.error("${errorCode}");
  return false;
}
  </#if>
</#if>
</#list>

return true;

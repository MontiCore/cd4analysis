<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributes","staticErrorCode")}

<#list attributes as attribute>
<#assign errorCode = staticErrorCode + cdGenService.getGeneratedErrorCode(attribute.getName()+attribute.getMCType().printType()) + " " + attribute.getName() + " of type " + attribute.getMCType().printType() + " must not be null">
<#assign MCCollectionSymTypeRelations = glex.getGlobalVar("mcCollectionSymTypeRelations")>

<#-- Check if the attribute is not a list, set or optional as they have isAbsent methods-->
<#if (!(MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isSet(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())))>

<#-- Primitive types no way to get them better yet -->
<#-- PLEASE FIX THIS ASAP -->
<#-- this is copied from the isPrimitiveType method of CDHelper which is no dependency here: Question: What is with char? -->
<#if (!(attribute.getMCType().printType() == "Boolean" ||
     attribute.getMCType().printType() == "boolean" ||
     attribute.getMCType().printType() == "Integer" ||
     attribute.getMCType().printType() == "int" ||
     attribute.getMCType().printType() == "Double" ||
     attribute.getMCType().printType() == "double" ||
     attribute.getMCType().printType() == "String"))>

if (this.${attribute.getName()} == null) {
  Log.error("${errorCode}");
  return false;
}
</#if>
</#if>
</#list>

return true;

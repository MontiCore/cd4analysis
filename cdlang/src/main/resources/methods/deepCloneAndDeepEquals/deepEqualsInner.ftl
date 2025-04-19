<#-- (c) https://github.com/MontiCore/monticore -->
<#-- inner method for deepEquals -->
<#-- this method is used to compare the attributes of the current object with the attributes of the given object -->
<#-- its primary purpose is to enable recursive which are need when resolving Lists and Sets -->
${tc.signature("originalClazzType","attribute", "PojoClazzesAsStringList","firstObjectName", "secondObjectName","resultBooleanName")}
<#assign MCCollectionSymTypeRelations = glex.getGlobalVar("mcCollectionSymTypeRelations")>
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- Set types -->
<#if (MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType()))>
if(${firstObjectName}.${attribute.getName().size()} != ${secondObjectName}.${attribute.getName().size()}){
  ${resultBooleanName} = false;
} else {
  java.util.Iterator<Object> it1${attribute.hashCode()} = ${firstObjectName}.${attribute.getName()}.iterator();
  while(it1${attribute.hashCode()}.hasNext()){
    Object it1Next${attribute.hashCode()} = it1${attribute.hashCode()}.next();
    boolean matchFound${attribute.hashCode()} = false;
    java.util.Iterator<Object> it2${attribute.hashCode()} = ${secondObjectName}.${attribute.getName()}.iterator();
    while(it2${attribute.hashCode()}.hasNext()){
      {tc.include("methods.deepCloneAndDeepEquals.deepEqualsInner",originalClazzType, attribute, PojoClazzesAsStringList, it1${attribute.hashCode()}, it2${attribute.hashCode()}, matchFound${attribute.hashCode()})};
      if(matchFound${attribute.hashCode()}){
        break;
      }
    }
    if(!matchFound${attribute.hashCode()}){
      ${resultBooleanName} = false;
    }
  }
}
<#-- List types -->
<#elseif (MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType()))>
if(${firstObjectName}.${attribute.getName().size()} != ${secondObjectName}.${attribute.getName().size()}){
  ${resultBooleanName} = false;
} else {
if(forceSameOrder){
  <#assign it1Name = "it1" + attribute.hashCode()>
  <#assign it2Name = "it2" + attribute.hashCode()>
  java.util.Iterator<Object> ${it1Name} = ${firstObjectName}.${attribute.getName()}.iterator();
  java.util.Iterator<Object> ${it3Name} = ${secondObjectName}.${attribute.getName()}.iterator();
  while(${it1Name}.hasNext() && ${it2Name}.hasNext()){
    <#assign it1NextName = "it1Next" + attribute.hashCode()>
    <#assign it2NextName = "it2Next" + attribute.hashCode()>
    Object ${it1NextName} = ${it1Name}.next();
    Object ${it2NextName} = ${it2Name}.next();
    ${tc.include("methods.deepCloneAndDeepEquals.deepEqualsInner", originalClazzType, attribute, PojoClazzesAsStringList, it1NextName, it2NextName, resultBooleanName)};
  }
} else {
  <#assign it1Name = "it1" + attribute.hashCode()>
  <#assign it2Name = "it2" + attribute.hashCode()>
  java.util.Iterator<Object> ${it1Name} = ${firstObjectName}.${attribute.getName()}.iterator();
  while(${it1Name}.hasNext()){
    <#assign it1NextName = "it1Next" + attribute.hashCode()>
    Object ${it1NextName} = ${it1Name}.next();
    <#assign matchFoundName = "matchFound" + attribute.hashCode()>
    boolean ${matchFoundName} = false;
    java.util.Iterator<Object> ${it2Name} = ${secondObjectName}.${attribute.getName()}.iterator();
    while(${it2Name}.hasNext()){
      <#assign it2NextName = "it2Next" + attribute.hashCode()>
      Object ${it2NextName} = ${it2Name}.next();
      {tc.include("methods.deepCloneAndDeepEquals.deepEqualsInner", originalClazzType, attribute, PojoClazzesAsStringList, it1NextName, it2NextName, matchFoundName)};
      if(${matchFoundName}){
        break;
      }
    }
    if(!${matchFoundName}){
      ${resultBooleanName} = false;
    }
  }
}
<#-- optional types -->
<#elseif (MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType()))>
if(${firstObjectName}.${attribute.getName()}.isPresent() != ${secondObjectName}.${attribute.getName()}.isPresent() ||
  (${firstObjectName}.${attribute.getName()}.isPresent() && !${firstObjectName}.${attribute.getName()}.get().deepEquals(${secondObjectName}.${attribute.getName()}.get(), forceSameOrder, visitedObjects))){
  ${resultBooleanName} = false;
}
<#-- primitive types -->
<#elseif attribute??>
<#if (!(CD4AnalysisTypeDispatcher.isMCBasicTypesASTMCPrimitiveType(attribute.getMCType())))>
${firstObjectName}.${attribute.getName()} == ${secondObjectName}.${attribute.getName()};
<#-- pojo class types -->
<#elseif (PojoClazzesAsStringList?seq_contains(attribute.getSymbol().getFullName()))>
${firstObjectName}.attribute.getName().deepEquals(${secondObjectName}.${attribute.getName()}, forceSameOrder, visitedObjects);
<#-- all other types -->
<#else>
  ${resultBooleanName} = Object.equals(${secondObjectName}.${attribute.getName()}, ${firstObjectName}.${attribute.getName()});
</#if>
</#if>


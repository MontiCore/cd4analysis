<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classesFromClassdiagramAsString","mCType","objectName")}
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- Define a macro to repeat a string n times -->
<#-- Array types -->
<#if (CD4AnalysisTypeDispatcher.isMCArrayTypesASTMCArrayType(mCType))>
<#assign arrayType = mCType.getMCType()>
<#assign arrayTypeName = arrayType.printType()>
<#assign depth = mCType.getDimensions()>
<#assign thisObjectArrayBracketsWith0index = "">
if(${objectName}!=null){
  <#list 0..depth-1 as i>
  </#list>
  <#list 0..depth-1 as i>
    for(int i${i} = 0; i${i} < ${objectName + thisObjectArrayBracketsWith0index}.length; i${i}++) {
    <#assign thisObjectArrayBracketsWith0index = thisObjectArrayBracketsWith0index + "[0]">
  </#list>
  <#assign currentObjectArrayBrackets = "">
   <#list 0..depth-1 as j>
     <#assign currentObjectArrayBrackets = currentObjectArrayBrackets + "[i${j}]">
   </#list>
  ${includeArgs("methods.visitor.traverseInner", classesFromClassdiagramAsString, arrayType, objectName + currentObjectArrayBrackets)}
  <#list 0..depth-1 as i>
     }
  </#list>
}
<#-- Set/List types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(mCType)) || (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCListType(mCType))>
<#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
<#assign iteratorName = "it" + mCType.hashCode()?replace(".","")?replace(",","")>
<#assign itNextName = "itNext" + mCType.hashCode()?replace(".","")?replace(",","")>
if(${objectName}!=null){
  java.util.Iterator<${innerType.printType()}> ${iteratorName} = ${objectName}.iterator();
  while(${iteratorName}.hasNext()){
    ${innerType.printType()} ${itNextName} = ${iteratorName}.next();
    ${includeArgs("methods.visitor.traverseInner", classesFromClassdiagramAsString, innerType, itNextName)};
  }
}
<#-- Map types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCMapType(mCType))>
<#assign keyType = mCType.getKey().getMCTypeOpt().get()>
<#assign valueType = mCType.getValue().getMCTypeOpt().get()>
<#assign keySetName = "KeySet" + mCType.hashCode()?replace(".","")?replace(",","")>
<#assign keySetIteratorName = "keySetIterator" + mCType.hashCode()?replace(".","")?replace(",","")>
if(${objectName}!=null){
  Set<${keyType.printType()}> ${keySetName} = ${objectName}.keySet();
  Iterator<${keyType.printType()}> ${keySetIteratorName} = ${keySetName}.iterator();
  while(${keySetIteratorName}.hasNext()){
    <#assign keyObjectName = "keyObjectName" + mCType.hashCode()?replace(".","")?replace(",","")>
    <#assign valueObjectName = "valueObjectName" + mCType.hashCode()?replace(".","")?replace(",","")>
    ${keyType.printType()} ${keyObjectName} = ${keySetIteratorName}.next();
    ${valueType.printType()} ${valueObjectName} = ${objectName}.get(${keyObjectName});
    ${includeArgs("methods.visitor.traverseInner", classesFromClassdiagramAsString, keyType, keyObjectName)};
    ${includeArgs("methods.visitor.traverseInner", classesFromClassdiagramAsString, valueType, valueObjectName)};
  }
}
<#-- Optional types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(mCType))>
<#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
<#-- if the first object is not present and the second object is present, return false -->
if(${objectName}!=null && ${objectName}.isPresent()){
  ${includeArgs("methods.visitor.traverseInner", classesFromClassdiagramAsString, innerType,  objectName + ".get()")};
}
<#-- Primitive types -->
<#-- Primitive types can not be null -->
// primitive types are no pojo types
<#-- pojo class types -->
<#else>
  <#-- only when the type is present in the class diagram the getDefiningSymbol is present -->
  <#if mCType.getDefiningSymbol().isPresent()>
    <#assign resolvedClassName = mCType.getDefiningSymbol().get().getFullName()>
  <#else>
    <#assign resolvedClassName = mCType.getMCQualifiedName().getQName()>
  </#if>
  <#if (classesFromClassdiagramAsString?seq_contains(resolvedClassName))>
if(${objectName}!=null){
  ${objectName}.accept(this);
}
  <#else>
  <#-- all other types -->
    //not a pojo type
  </#if>
</#if>

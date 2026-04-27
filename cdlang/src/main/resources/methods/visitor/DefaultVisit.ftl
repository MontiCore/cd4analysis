<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superClass")}
if (!traversedElements.contains(node)) {
<#if superClass??>
  // Call visit of parent class
  this.visit((${superClass}) node);
</#if>
  traversedElements.add(node);
  ${defineHookPoint("VisitorImplementation:Traverse")}

}

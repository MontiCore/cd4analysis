<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ObserverList","ObserverType","AttributeName", "prefix", "params")}
for(${ObserverType} observer : this.${ObserverList}) {
  observer.notifyUpdate${prefix}${AttributeName?cap_first}(this, ${params});
}

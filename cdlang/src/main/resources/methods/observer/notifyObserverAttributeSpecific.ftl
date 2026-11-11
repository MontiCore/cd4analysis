<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ObserverList","ObserverType","AttributeName")}
for(${ObserverType} observer : this.${ObserverList}) {
    observer.notifyUpdateSet${AttributeName?cap_first}(this, ov);
}

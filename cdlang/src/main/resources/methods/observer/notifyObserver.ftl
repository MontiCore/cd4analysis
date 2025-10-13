<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ObserverList","ObserverType")}
for(${ObserverType} observer : this.${ObserverList}) {
    observer.notifyUpdate(this);
}

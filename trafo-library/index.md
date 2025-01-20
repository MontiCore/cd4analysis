<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

<!-- Relevant Publications -->
[UML/P]: https://mbse.se-rwth.de/
[Hoe18]: https://www.se-rwth.de/phdtheses/Diss-Hoelldobler-MontiTrans-Agile-modellgetriebene-Entwicklung-von-und-mit-domaenenspezifischen-kompositionalen-Transformationssprachen.pdf

<!-- Other Links -->
[README]: ../README.md/#step-10-transformation-of-models

# CDTrans: Transformations on Class Diagrams

The Transformation library is a submodule of the `CD4Analysis` project and
contains a collection of transformations for [UML/P] class diagrams.
This collection includes a large number of refactorings as well as the
transformations to introduce various design patterns. In the following, we provide an overview
of the provided transformations and how to define your own transformations.
A more detailed insight into their functionality and implementation can be found in [[Hoe18]].

## Idea
During modeling, with ongoing evolution or possible merges, class diagrams can become increasingly complex. 
This can lead to optimization potential that could reduce complexity.
However, not only in general cases, but also for specific class diagrams, modifications may be desired prior to code generation that cannot be made in the actual models. 
For example, an optimization may only be possible after merging several class diagrams. 
Therefore, we introduce transformation templates to integrate this into the generation pipeline.
A transformation template allows the invocation of any number of MontiTrans [[Hoe18]] based transformations.
There is a set of predefined transformations (library) as well as the possibility to call custom transformations, generated and compiled from `mtr` files.

## Creating a Transformation Template
A transformation template is a Freemarker template that instantiates a `CDTransformationRunner` and performs an arbitrary sequence of transformations.

As an example, we will look at the following class diagram:
```
classdiagram Vehicle {
	class Vehicle {
	}
	
	class Car extends Vehicle {
		public String brand;
		public String model;
		public Integer year;
		public Integer numberOfDoors;
	}
	
	class Truck extends Vehicle {
		public String brand;
		public String model;
		public Integer year;
		public Double maxLoad;
	}
}
```

The corresponding transformation template starts by initializing the `CDTransformationRunner`.
To do this, a new runner is instantiated and a reference to the abstract syntax tree is passed.
The variable `ast` serves as a reference to it in the template:
```injectedfreemarker
<#assign trafoRunner = tc.instantiate("de.monticore.cdlib.CDTransformationRunner", [ast])>
```

### Trafo-Library
To execute transformations from the library, the method [`CDTransformationRunner.transform()`](src/main/java/en/monticore/cdlib/CDTransformationRunner.java) is used.
It requires only a single mandatory argument, the name of a transformation defined in the library (see [List of transformations](#list-of-all-library-transformations)).

However, many transformations require additional parameters, so it is possible to pass a map as the second argument.
The keys of the map must match the required parameters, while the value can be either a string or a list of strings.

The following statement executes the `PULL_UP_ATTRIBUTES` transformation. This transformation does not require any further arguments.
```injectedfreemarker
${trafoRunner.transform("PULL_UP_ATTRIBUTES")}
```

In the example, this transformation pulls all common attributes of the `Car` and `Truck` classes into the common superclass `Vehicle`.
However, as these attributes are `public`, they should also be encapsulated to introduce getter and setter methods.
To encapsulate only the attributes in the `Vehicle` class, we pass their names as additional arguments to the `ENCAPSULATE_ATTRIBUTES` transformation.
```injectedfreemarker
${trafoRunner.transform("ENCAPSULATE_ATTRIBUTES", {"attributes":["brand", "model", "year"]})}
```

When assembled, the transformation template now looks like this:
```injectedfreemarker
<#assign trafoRunner = tc.instantiate("de.monticore.cdlib.CDTransformationRunner", [ast])>

${trafoRunner.transform("PULL_UP_ATTRIBUTES")}
${trafoRunner.transform("ENCAPSULATE_ATTRIBUTES", {"attributes":["brand","model","year"]})}
```

The resulting class diagram after applying the transformation template looks like this:
```injectedfreemarker
classdiagram Vehicle { 
  class Vehicle { 
    private String brand;
    private String model;
    private Integer year;
    public String getBrand();
    public void setBrand(String brand);
    public String getModel();
    public void setModel(String model);
    public Integer getYear();
    public void setYear(Integer year);

  } 
  class Car extends Vehicle { 
    public Integer numberOfDoors;
  } 
  class Truck extends Vehicle { 
    public Double maxLoad;
  } 
} 
```


### List of all library transformations

| Transformation                                | Description                                                           | Parameters                                                                                                                                                                                                              |
|-----------------------------------------------|-----------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| INTRODUCE_OBJECT_ADAPTER_PATTERN              | Introduces the Object Adapter Pattern.                                | - `adapteeName`: Name of the adaptee. <br> - `targetName`: Target name. <br> - `method`: (optional) Method, if available.                                                                                               |
| INTRODUCE_CLASS_ADAPTER_PATTERN               | Introduces the Class Adapter Pattern.                                 | - `adapteeName`: Name of the adaptee. <br> - `targetName`: Target name. <br> - `method`: (optional) Method, if available.                                                                                               |
| INTRODUCE_DECORATOR_PATTERN                   | Introduces the Decorator Pattern.                                     | - `concreteComponent`: The concrete component name.<br>- `componentName`: Name of the decorator component.<br>- `method`: Method to be decorated.                                                                       | 
| INTRODUCE_FACADE_PATTERN                      | Introduces the Facade Pattern.                                        | - `facadeClasses`: List of facade classes.<br>- `facadeClassName`: (optional) Name of the facade class.                                                                                                                 |
| INTRODUCE_FACTORY_PATTERN                     | Introduces the Factory Pattern.                                       | - `subclasses`: List of subclasses.<br>- `className`: Name of the class to be created.                                                                                                                                  | 
| INTRODUCE_OBSERVER_PATTERN                    | Introduces the Observer Pattern.                                      | - `subjectName`: Name of the subject in observer pattern.<br>- `observerName`: Name of observer.<br>- `observableName`: Name of observable object.                                                                      | 
| INTRODUCE_PROXY_PATTERN                       | Introduces the Proxy Pattern.                                         | - `className`: Name of class being proxied.<br>- `methods`: (optional) List of methods to be proxied.                                                                                                                   | 
| INTRODUCE_VISITOR_PATTERN                     | Introduces the Visitor Pattern.                                       | - `className`: Name of the class a visitor should be introduced for.<br>- `replacedMethods`: List of methods to be replaced.<br>- `visitors`: (optional) List of visitors.                                              | 
| COLLAPSE_HIERARCHY                            | Collapses a hierarchy in a class or structure                         | - `className`: The name of class whose hierarchy is to be collapsed.                                                                                                                                                    | 
| COLLAPSE_HIERARCHY_METHOD                     | Collapses a method in a hierarchy                                     | - `className`: The name of class whose methods are to be collapsed.                                                                                                                                                     | 
| COLLAPSE_HIERARCHY_ATTRIBUTE                  | Collapses attributes in a hierarchy                                   | - `className`: The name of class whose attributes are to be collapsed.                                                                                                                                                  | 
| DELETE_SUPERCLASS                             | Deletes a superclass from model                                       | - `className`: The name of superclass to delete.                                                                                                                                                                        | 
| DELETE_INHERITANCE                            | Deletes inheritance between classes                                   | - `className`: The name of class from which inheritance is deleted.                                                                                                                                                     |  
| ENCAPSULATE_ATTRIBUTES                        | Encapsulates attributes within a class                                | -&nbsp;`attributes`:(optional) List of attributes to encapsulate; if not provided, all will be encapsulated.                                                                                                            |
| EXTRACT_CLASS                                 | Extracts a new class from existing elements                           | - `oldClass`: The name of the old class to be extracted. <br> - `newClass`: The name of the new class to be created. <br> - `attributes`: List of attributes to extract. <br> - `methods`: List of methods to extract.  |
| EXTRACT_INTERFACE                             | Extracts an interface from existing elements                          | - `interfaceName`: The name of the interface to be extracted. <br> - `subclasses`: List of subclasses that implement this interface.                                                                                    |
| EXTRACT_ALL_INTERMEDIATE_CLASSES              | Extracts all intermediate classes from existing elements              |                                                                                                                                                                                                                         |
| EXTRACT_INTERMEDIATE_CLASS                    | Extracts an intermediate class based on specified parameters          | - `newSuperclassName`: New superclass name for extraction.<br>- `subclasses`: List containing subclasses related to this extraction process; extracts them accordingly.                                                 |
| EXTRACT_ALL_INTERMEDIATE_CLASSES_ATTRIBUTE    | Extracts all intermediate classes with specific attributes            | - `className` (optional): If provided, it will filter based on this classname; otherwise, it extracts without filtering.                                                                                                |
| EXTRACT_ALL_INTERMEDIATE_CLASSES_METHOD       | Extracts all intermediate classes with specific methods               | - `className` (optional): If provided, it will filter based on this classname; otherwise, it extracts without filtering.                                                                                                |
| EXTRACT_SUPER_CLASS                           | Extracts superclasses from existing structures                        |                                                                                                                                                                                                                         |
| EXTRACT_SUPER_CLASS_ATTRIBUTE                 | Extracts superclass attributes                                        |                                                                                                                                                                                                                         |
| EXTRACT_SUPER_CLASS_METHOD                    | Extracts superclass methods                                           |                                                                                                                                                                                                                         |
| EXTRACT_SUPER_CLASS_WITH_NAME                 | Extracts a superclass by given name                                   | - `className`: Specifies which superclass should be extracted based on its classname.                                                                                                                                   |
| EXTRACT_SUPER_CLASS_ATTRIBUTE_WITH_NAME       | Extracts superclass attributes by given name                          | - `className`: Specifies which attribute should be extracted based on its classname.                                                                                                                                    |
| EXTRACT_SUPER_CLASS_METHOD_WITH_NAME          | Extracts superclass methods by given name                             | - `className`: Specifies which method should be extracted based on its classname.                                                                                                                                       |
| INLINE_CLASS                                  | Inlines a specified class into another                                | - `classToRemove`: Name of the class to be removed and inlined into another.<br>- `newClass`: Name where it gets inlined to.                                                                                            |
| MOVE_METHODS_AND_ATTRIBUTES                   | Moves methods and attributes from one class to another                | - `sourceClass`: The name of the source class from which methods and attributes are moved. <br> - `targetClass`: The name of the target class to which methods and attributes are moved.                                |
| MOVE_METHODS_AND_ATTRIBUTES_TO_NEIGHBOR_CLASS | Moves methods and attributes to a neighboring class                   | - `sourceClass`: The name of the source class. <br> - `targetClass`: The name of the neighboring target class.                                                                                                          |
| MOVE_ALL_METHODS                              | Moves all methods from one class to another                           | - `sourceClass`: The name of the source class from which all methods are moved. <br> - `targetClass`: The name of the target class to which all methods are moved.                                                      |
| MOVE_METHODS                                  | Moves specified methods from one class to another                     | - `sourceClass`: The name of the source class. <br> - `targetClass`: The name of the target class.<br>- `methodsToMove`: List of specific methods to be moved.                                                          |
| MOVE_METHODS_TO_NEIGHBOR_CLASS                | Moves specified methods to a neighboring class                        | - `sourceClass`: The name of the source class.<br>- `targetClass`: Name of neighboring target.<br>- `methodsToMove`: List of specific methods to be moved.                                                              | 
| MOVE_ALL_ATTRIBUTES                           | Moves all attributes from one class to another                        | - `sourceClass`: The name of the source class from which all attributes are moved.<br>- `targetClass`: Name of target where they will go.                                                                               | 
| MOVE_ATTRIBUTES                               | Moves specified attributes from one class to another                  | - `sourceClass`: Name of source where we move data out.<br>- `targetClass`: Name where we want our selected data moved too.<br>- `attributesToMove`: List specifying which attribute(s) need moving.                    | 
| MOVE_ATTRIBUTES_TO_NEIGHBOR_CLASS             | Moves specified attributes to a neighboring class                     | - `sourceClass`: Name of source where we move data out.<br>- `targetClass`: Neighboring destination where we want our data moved too.. <br> - &nbsp;`attributesToMove`: List specifying which attribute(s) need moving. | 
| PULL_UP                                       | Pulls up members (methods/attributes) into superclass                 |                                                                                                                                                                                                                         | 
| PULL_UP_ATTRIBUTES                            | Pulls up all attributes into superclass                               |                                                                                                                                                                                                                         | 
| PULL_UP_METHODS                               | Pulls up all methods into superclass                                  |                                                                                                                                                                                                                         | 
| PULL_UP_ASSOCIATIONS                          | Pulls up associations into superclass                                 |                                                                                                                                                                                                                         |
| PUSH_DOWN                                     | Pushes down members (methods/attributes) into subclasses              | - `superClassName`: The name of the superclass that will receive the pushed-down members.                                                                                                                               |
| PUSH_DOWN_ALL_ATTRIBUTES                      | Pushes down all attributes into subclasses                            | - `superClassName`: The name of the superclass that will receive all pushed-down attributes.                                                                                                                            |
| PUSH_DOWN_ATTRIBUTES                          | Pushes down specified attributes into subclasses                      | - `superClassName`: The name of the superclass.<br>- `subClasses` (optional): List of subclasses to push down attributes to.<br>- `attributes`: List of specific attributes to be pushed down.                          |
| PUSH_DOWN_ALL_METHODS                         | Pushes down all methods into subclasses                               | - `superClassName`: The name of the superclass that will receive all pushed-down methods.                                                                                                                               |
| PUSH_DOWN_METHODS                             | Pushes down specified methods into subclasses                         | - `superClassName`: The name of the superclass.<br>- `subClasses` (optional): List of subclasses to push down methods to.<br>- `methods`: List of specific methods to be pushed down.                                   |
| REMOVE_CLASS                                  | Removes a specified class from the model                              | - `className`: The name of the class to be removed.                                                                                                                                                                     |
| REMOVE_METHOD                                 | Removes a specified method from a class                               | - `className`: The name of the class containing the method.<br>- `methodName`: The name of the method to be removed.                                                                                                    |
| REMOVE_ATTRIBUTE                              | Removes a specified attribute from a class                            | - `className`: The name of the class containing the attribute.<br>- `methodName`: The name of the attribute to be removed.                                                                                              |
| RENAME_CLASS                                  | Renames an existing class                                             | - `oldName`: The current name of the class.<br>- `newName`: The new name for the class.                                                                                                                                 |
| RENAME_ATTRIBUTE                              | Renames an existing attribute                                         | - `oldName`: The current name of the attribute.<br>- `newName`: The new name for the attribute.                                                                                                                         |
| REPLACE_ASSOCIATION_BY_ATTRIBUTE              | Replaces an association with an attribute                             | - `className`: Name of the class where association is replaced.<br>- `classToAttribute`: Name for replacing with an attribute.                                                                                          |
| REPLACE_INHERITANCE_BY_DELEGATION             | Replaces inheritance with delegation                                  | - `superClassName`: Name of superclass in original inheritance.<br>- `subclassName`: Name of subclass being modified.                                                                                                   | 
| REPLACE_DELEGATION_BY_INHERITANCE             | Replaces delegation with direct inheritance                           | - &nbsp;`superClassName` : Name of superclass in original delegation;<br>&nbsp;-&nbsp;`subclassName` : Name of subclass being modified.                                                                                 | 
| CREATE_RIGHT_ASSOCIATION                      | Creates a right-direction association between classes                 | - &nbsp;`leftReferenceName` : Left reference's identifier;<br>&nbsp;-&nbsp;`rightReferenceName` : Right reference's identifier.                                                                                         | 
| CREATE_BI_ASSOCIATION                         | Creates bi-directional associations between classes                   | - &nbsp;`leftClass` : Identifier for left-side relationships;<br>&nbsp;-&nbsp;`rightClasses` : List containing identifiers for right-side relationships.                                                                | 
| DELETE_ALL_ASSOCIATIONS                       | Deletes all associations related to a specified class                 | - &nbsp;`className` : Identifier specifying which associations should be deleted.                                                                                                                                       | 
| CREATE_CLASS                                  | Creates a new simple named class                                      | - &nbsp;`className` : Identifier specifying what new named item should be created.                                                                                                                                      | 
| CREATE_INTERFACE                              | Creates a new interface                                               | - &nbsp;`interfaceName` : Identifier specifying what new interface should be created.                                                                                                                                   | 
| CREATE_INHERITANCE_TO_INTERFACE               | Creates inheritance linkage towards an interface                      | - &nbsp;`subclass`, specifies which subclass will inherit this newly formed interface; <b>(interface)</b>: specifies which interface it inherits from.;                                                                 |
| ADD_INHERITANCE_TO_INTERFACE                  | Creates an inheritance from a class to an interface                   | - `className`: The name of the class that will inherit from the new interface.<br>- `newInterface`: The name of the interface to be inherited.                                                                          |
| CREATE_INHERITANCE_TO_CLASS                   | Creates an inheritance relationship between a subclass and superclass | - `subclass`: The name of the subclass that will inherit.<br>- `superclass`: The name of the superclass from which it will inherit.                                                                                     |
| CHANGE_INHERITANCE_CLASS                      | Changes the inheritance relationship of a class                       | - `oldSuperclass`: The current superclass that needs to be changed.<br>- `newSuperclass`: The new superclass to be assigned.                                                                                            |




### Custom Transformations
While the transformation library already contains a variety of general-purpose transformations for refactoring and design pattern introduction, user-defined transformations are a powerful tool for creating transformations for specific class diagrams.

According to [[Hoe18]], such transformations are defined using the DSTL for CD4Code in an `mtr` file.
The `CD4CodeTFGenTool` can then be used to generate Java code for this transformation.
Finally, the code can be compiled and added to the `classpath` when running the `CDTool`.

Any transformations from the classpath can be instantiated in the transformation template.
The transformations of the library are often composed of several transformations and linked with additional Java code to allow more powerful operations.
This is not possible for custom MontiTrans transformations.

First, a custom transformation must be loaded and instantiated. Again, a reference to the `ast` is passed as argument:
```injectedfreemarker
<#assign customTrafo = tc.instantiate("de.monticore.cdlib.TransformationUtility.CreateClass",[ast])>
```

The `CDTransformationRunner` can then be used with the `CDTransformationRunner.genericTransform()` method to execute the custom transformation.
It is mandatory to pass the previously instantiated transformation as an argument. 
If a transformation contains unbound variables, it is possible to specify a map of transformation arguments in the same way as the transformations in the library:

```injectedfreemarker
${trafoRunner.genericTransform(customTrafo, {"className":"E-Scooter"})}
```



## Meeting Protocol for 2023-06-14

* CD4A Test cases in bestehende Testklasse einbetten
* Gleiche test cases für PlantUMLUtil
* Bestehenden JSON PrettyPrinter für PlantUML wiederverwenden
&rarr; Evtl bestehende test cases wiederverwenden und erweitern
* Visualization: Bestehende SVG anschauen und "reverse-engineeren"

## Meeting Protocol for 2023-06-21

* `PlantUMLUtil.java`: make methods returning `String`s `deprecated` and create equivalent methods returning `Path`s
* PlantUML CD style: borders black and hide methods/variables if empty
* PlantUML JSON style: style like your IDE of choice
* Build in styles, e.g., in `PlantUMLConfig.java` and `CD4AnalysisPlantUMLFullPrettyPrinter.java`

## Meeting Protocol for 2023-06-28

* Colors to `PlantUMLConfig.java` and remove unused
* `JSONTool.java` for JSON like in CD4Analysis
* Repair associaton prints in CD4Analysis project + test cases
* Add class diagram model tag to PlantUML diagrams
* Test/run util methods

## Meeting Protocol for 2023-07-05

* Remove whitespace from "\*- -\*"-associations
* Label class modifiers with <<*modifier*>> & class name below
* JSON: Test various styles. Is the output valid PlantUML?
* CLI
* Document code w/ Javadoc (use tags)
* Write "tutorial" in Markdown

# ObserverDecorator

The `ObserverDecorator` implements the Observer Design Pattern by automatically turning the classes in your class
diagram into observable entities. It generates the necessary infrastructure allowing other components of your
system to subscribe to and react to state changes within these generated objects.

## The Core Mechanism

When the generator runs, this decorator traverses the AST and enriches it with the Observer pattern:

1.  **Listener Interface Generation:** For a class `X`, it creates a listener/observer interface (e.g., `IXObserver`).
2.  **Subscription Management:** It adds fields to the target class to manage a collection of registered listeners.
3.  **Registration Methods:** It generates methods in the target class to `addObserver()` and `removeObserver()`.
4.  **Notification Hooks:** It injects code into state-changing methods (like setters) to notify all registered observers whenever a value is updated, providing both generic and attribute-specific notifications.

---

![Figure 1.1 The original class diagramm after applying the mandatory CopyDecorator](../../myOrganizer/img/MyOrganizerNoDecorators.svg)
<figcaption>Figure 1.1 The class diagram after the mandatory CopyDecorator</figcaption>

![Figure 1.2 The original class diagram after applying the ObserverDecorator](../../myOrganizer/img/MyOrganizerOnlyObservers.svg)
<figcaption>Figure 1.2 The original class diagram after applying the ObserverDecorator</figcaption>


---

## Real-World Breakdown: Generation Strategies
If we look at the `Task` class, we might want UI components or project managers to be notified when the
`taskStatus` changes. By enabling the `ObserverDecorator`, the class becomes observable.

For the `Task` class the decorator generates the interface `ITaskObserver.java` with default empty methods for both generic updates and attribute-specific updates:

```java
package MyOrganizer;

public interface ITaskObserver extends de.monticore.cd.ICDObserver<Task> { 

  default public void notifyUpdate(Task clazz) {
      // empty body
  }

  default public void notifyUpdateSetTaskStatus(Task clazz, Status ov) {
      // empty body
  }

  default public void notifyUpdateSetProject(Task clazz, MyOrganizer.Project ov) {
      // empty body
  }
}
```

And add management and notification methods directly to the `Task` class:
```java
public class Task extends Asset implements de.monticore.cd.ICDObservable<MyOrganizer.ITaskObserver,Task> {

    protected List<MyOrganizer.ITaskObserver> observerList = new ArrayList<>();

    public void addObserver(MyOrganizer.ITaskObserver observer) {
        this.observerList.add(observer);
    }

    public void removeObserver(MyOrganizer.ITaskObserver observer) {
        this.observerList.remove(observer);
    }

    protected void notifyObservers() {
        for(MyOrganizer.ITaskObserver observer : this.observerList) {
            observer.notifyUpdate(this);
        }
    }

    protected void notifyObserversSetTaskStatus(Status ov) {
        for(ITaskObserver observer : this.observerList) {
            observer.notifyUpdateSetTaskStatus(this, ov);
        }
    }
    
    // ... other attribute-specific notification methods
}
```
It is strongly recommended to use the `ObserverDecorator` together with the `SetterDecorator` to automatically
generate the necessary hooks for attribute-specific notifications. The generated setters will then automatically trigger these specific notifications like `notifyObserversSetProject(oldValue)`.

```java
public void setProjectLocal (MyOrganizer.Project project) {
    /* Hookpoint: Setter:Before */
    var _oldValue = this.project;
    
    this.project = project;
    /* Hookpoint: Setter:After */
    this.notifyObserversSetProject(_oldValue );
    this.notifyObservers();
}
```
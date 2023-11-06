# Unfold Attributes

The Unfold Attributes refactoring is a similar technique to the [Pull-Up Field](pull_up_field.md) refactoring.
Multiple fields can be selected and via the Unfold Attributes refactoring extracted into a super-class.

![](../../assets/images/unfold_attributes_action.png)

### Details
**Execution**: Fully mark all fields in the editor that should be included in the new super class.

The new created class is named "SuperClass".

If the class containing the attributes that need to be unfolded already extends a class, the newly created "SuperClass" also extends the original class.  If the "SuperClass" name is already taken, a counter is added to the new class name until it is valid.
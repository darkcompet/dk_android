package tool.compet.appbundle.arch.vml;

public class VmlComponent {
   // Real component
   final Object obj;

   // Indicates all component-fields inside it is not yet initialized.
   boolean needInitialize;

   VmlComponent(Object component) {
      this.obj = component;
      this.needInitialize = true;
   }
}

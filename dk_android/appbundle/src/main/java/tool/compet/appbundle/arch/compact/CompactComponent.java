package tool.compet.appbundle.arch.compact;

public class CompactComponent {
   // Real component
   final Object obj;

   // Indicates all component-fields inside it is not yet initialized.
   boolean needInitialize;

   CompactComponent(Object component) {
      this.obj = component;
      this.needInitialize = true;
   }
}

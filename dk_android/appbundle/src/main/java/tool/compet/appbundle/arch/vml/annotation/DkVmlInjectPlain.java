package tool.compet.appbundle.arch.vml.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Inject an unique instance (create new if not yet exist).
 * Note that, In original, injected instance cannot survive configuration changes,
 * so new instance will be created for each instance of the View (Activity, Fragment...).
 * But don't worry, in Vml design pattern, we save all fields in a ViewModel instance of the View,
 * so all vml-annotated fields will not be lost when configuration changed.
 * Usage example:
 * <pre>{@code
 *    class HomeModelLogic extends DkVmlModelLogic<HomeViewLogic> {
 *       @DkVmlInjectPlain
 *       private Data dt;
 *    }
 * }</pre>
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface DkVmlInjectPlain {
}

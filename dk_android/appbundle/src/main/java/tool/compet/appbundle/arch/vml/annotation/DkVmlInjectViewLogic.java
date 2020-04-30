package tool.compet.appbundle.arch.vml.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Inject an unique instance (create new if not yet exist).
 * Note that, injected instance can survive configuration changes.
 * Usage example:
 * <pre>{@code
 *    class HomeModelLogic extends DkVmlModelLogic<HomeViewLogic> {
 *       @DkVmlInjectViewLogic
 *       private HomeViewLogic vl;
 *    }
 * }</pre>
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface DkVmlInjectViewLogic {
}

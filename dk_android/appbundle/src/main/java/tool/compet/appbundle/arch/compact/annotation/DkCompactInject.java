package tool.compet.appbundle.arch.compact.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Inject an unique instance (create new if not yet exist).
 * Note that, injected instance can survive configuration changes,
 * so only one instance will be created for multiple instances of the View (Activity, Fragment...).
 *
 * Usage example:
 * <pre>{@code
 *    class HomeModelLogic extends DkVmlModelLogic<HomeViewLogic> {
 *       @DkInject
 *       private Data dt;
 *    }
 * }</pre>
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface DkCompactInject {
}

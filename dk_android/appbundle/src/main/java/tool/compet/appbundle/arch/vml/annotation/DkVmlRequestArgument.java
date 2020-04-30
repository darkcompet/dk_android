package tool.compet.appbundle.arch.vml.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Inject an unique instance (create new if not yet exist) shared between some Views.
 * Note that, injected instance can survive configuration changes since it is inside a
 * ViewModel under a host (in here, if the view is Fragment, then host is Activity,
 * if the view is Activity, then host is App).
 * Usage example:
 * <pre>{@code
 *    class HomeViewLogic extends DkVmlViewLogic<HomeModelLogic, HomeFragment> {
 *       @DkVmlRequestArgument
 *       private HomeArgument arg;
 *    }
 * }</pre>
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface DkVmlRequestArgument {
}

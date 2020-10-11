package tool.compet.appbundle.arch.compact.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Inject a host-topic (create new if not yet exist) shared between some Views.
 * Note that, injected instance can survive configuration changes, but will be unlinked from
 * the host when all scoped views was deleted.
 *
 * Usage example:
 * <pre>{@code
 *    class HomeViewLogic extends DkVmlViewLogic<HomeFragment> {
 *       @DkInjectHostTopic
 *       private HomeArgument arg;
 *    }
 * }</pre>
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface DkInjectHostTopic {
}

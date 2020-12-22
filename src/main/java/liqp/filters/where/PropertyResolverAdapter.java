package liqp.filters.where;

import liqp.TemplateContext;

/**
 * Used for resolving properties by name for specific kind of objects.
 * Native implementation has equivalent ":to_liquid" and ":data" that used
 * for resolving properties by name for objects that support such method.
 * In java we do not stick to special interfaces of Jekyll/Liquid that
 * do not have equivalent/meaning here, but still provide a way to create
 * alternative properties resolver for custom objects.
 *
 * See here sample implementation for ":to_liquid" via this interface here:
 * https://gist.github.com/msangel/74c6cec96ea4a4ecc01187e465fdeb14
 *
 * See sample implementation for ":data" here:
 * https://gist.github.com/msangel/4a9b4404b233a6ff57a4ca54db3bfc1f
 *
 */
public interface PropertyResolverAdapter {
    Object getItemProperty(TemplateContext context, Object input, Object property);

    boolean support(Object target);
}

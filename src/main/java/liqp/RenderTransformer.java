package liqp;

import java.io.Writer;

/**
 * <p>
 * A {@link RenderTransformer} handles the conversion of objects to be returned by
 * {@link Template#renderToObject()} etc.
 * </p>
 * <p>
 * Implementations may optimize how objects are actually appended/serialized, and when exceptions are
 * thrown in the case that the final result would be too long.
 * </p>
 * The default implementation, {@link RenderTransformerDefaultImpl}, simply appends strings to a
 * {@link StringBuilder}. While this will work in many cases, there is a chance that large strings are
 * created temporarily on the heap, increasing the chance of an out-of-memory situation, and a generally
 * high allocation activity (spikes in the memory allocation graph).
 * <p>
 * For example, the following liquid template results in <em>10012</em>
 * {@link StringBuilder#toString()} conversions:
 * 
 * <pre><code>{% for l in (1..10)%}{% for k in (1..1000) %}
 *   {% for i in (1..1000) %}Hello! {{ i }} world
 *{% endfor %}{% endfor %}{% endfor %}</code></pre>
 * 
 * With a custom implementation such as stringhold's <a href=
 * "https://github.com/kohlschutter/stringhold/blob/main/stringhold-liqp/src/main/java/com/kohlschutter/stringhold/liqp/StringHolderRenderTransformer.java">StringHolderReaderTransformer</a>,
 * the number of actual `toString` conversions can be as low as <em>1</em>, and — depending on specific
 * implementation tricks (such as reusing instances of identical partial sequences) — a significantly
 * lower memory footprint can be observed (in this case, with stringhold, <em>7x</em> less usage). If the
 * data is to be written to a {@link Writer} (instead of serializing to a complete string), an even lower
 * memory footprint can be achieved (<em>35x</em> less usage was observed).
 * </p>
 * 
 * @author Christian Kohlschütter
 * @see RenderTransformerDefaultImpl
 * @see <a href="https://kohlschutter.github.io/stringhold/">stringhold</a>
 */
public interface RenderTransformer {
    /**
     * Something that can append objects.
     */
    @FunctionalInterface
    interface ObjectAppender {
        /**
         * Something that can append objects and return the result.
         */
        interface Controller extends ObjectAppender {
            /**
             * The result of the calls to {@link #append(Object)}.
             * 
             * @return The result.
             */
            Object getResult();

            @Override
            void append(Object obj);
        }

        /**
         * Appends the given object.
         * 
         * @param obj
         *            The object to append.
         */
        void append(Object obj);
    }

    /**
     * Creates a new {@link RenderTransformer.ObjectAppender.Controller} for the given
     * {@link TemplateContext}, optionally using the given estimate for the number of calls to
     * {@link RenderTransformer.ObjectAppender#append(Object)}.
     * 
     * @param context
     *            The template context.
     * @param estimatedNumberOfAppends
     *            The estimate number of calls to {@link ObjectAppender#append(Object)}.
     * @return The appender controller instance.
     */
    ObjectAppender.Controller newObjectAppender(TemplateContext context, int estimatedNumberOfAppends);

    /**
     * Transforms an object to a representation that is suitable to call {@link Object#toString()} on
     * during the "render"-phase ({@link Template#render()} etc.).
     * 
     * @param context
     *            The template context.
     * @param obj
     *            The object to transform.
     * @return The transformed object, or the object itself if no transformation is necessary.
     */
    Object transformObject(TemplateContext context, Object obj);
}

package liqp;

/**
 * A {@link RenderTransformer} handles the conversion of appendable objects in the "prerender"-phase
 * ({@link Template#prerender()} etc.).
 * 
 * Implementations may optimize how objects are actually appended/serialized, and when exceptions are
 * thrown in the case that the final result would be too long.
 * 
 * @author Christian Kohlschütter
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
     * Creates a new {@link ObjectAppender.Controller} for the given {@link TemplateContext}, optionally
     * using the given estimate for the number of calls to {@link ObjectAppender#append(Object)}.
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

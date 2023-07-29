package liqp;

import liqp.RenderTransformer.ObjectAppender.Controller;

/**
 * The default {@link RenderTransformer}.
 * 
 * Objects are converted to {@link CharSequence}, and appended to a {@link StringBuilder} where
 * necessary. The resulting object is always transformed to {@link String}.
 */
final class RenderTransformerDefaultImpl implements RenderTransformer {
    static final RenderTransformerDefaultImpl INSTANCE = new RenderTransformerDefaultImpl();

    private RenderTransformerDefaultImpl() {
    }

    @Override
    public Controller newObjectAppender(TemplateContext context, int estimatedNumberOfAppends) {
        return new Controller() {
            private CharSequence result = "";
            private ObjectAppender appender = (o) -> {
                result = o instanceof CharSequence ? (CharSequence) o : String.valueOf(o);
                appender = (o2) -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(result);
                    sb.append(o2);

                    result = sb;
                    appender = sb::append;

                    checkLength();
                };
            };

            private void checkLength() {
                int maxLen = context.getParser().getLimitMaxSizeRenderedString();
                if (result.length() > maxLen) {
                    throw new RuntimeException("rendered string exceeds " + maxLen);
                }
            }

            @Override
            public Object getResult() {
                checkLength();
                return transformObject(context, result);
            }

            @Override
            public void append(Object obj) {
                appender.append(obj);
            }
        };
    }

    @Override
    public Object transformObject(TemplateContext context, Object obj) {
        return String.valueOf(obj);
    }
}

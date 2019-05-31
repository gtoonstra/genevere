package org.gt.transforms;


import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;
import org.gt.pipeline.ITransform;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

public class JavascriptTransformer implements ITransform {

    private static final Logger logger = LogManager.getLogger();

    private ScriptEngine scriptEngine;
    private Invocable invocable;

    public JavascriptTransformer() {
         ScriptEngineManager factory = new ScriptEngineManager();
         scriptEngine = factory.getEngineByName("nashorn");
    }

    public void init_transform(Map<String, String> props) throws GenevereException {
        String script = props.get("script");
        try {
            scriptEngine.eval(script);
            invocable = (Invocable) scriptEngine;
        } catch (ScriptException ex) {
            logger.error(ex);
            throw new GenevereException("Parsing the script failed", ex);
        }
    }

    public Object[] transform(Object[] source) throws GenevereException {
        try {
            Object[] xformed = (Object[])invocable.invokeFunction("transform", new Object[] {source});
            for (int i = 0; i < xformed.length; i++) {
                if (ScriptObjectMirror.isUndefined(xformed[i])) {
                    xformed[i] = null;
                }
            }
            return xformed;
        } catch (ScriptException ex) {
            logger.error(ex);
            throw new GenevereException("Script invocation error", ex);
        } catch (NoSuchMethodException ex) {
            logger.error(ex);
            throw new GenevereException("No transform method exists in javascript", ex);
        }
    }
}

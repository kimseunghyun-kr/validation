//package hello.itemservice.javascript;
//
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import org.graalvm.polyglot.Context;
//import org.graalvm.polyglot.Source;
//import org.graalvm.polyglot.Value;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;
//import javax.script.ScriptException;
//import java.io.IOException;
//
//public class CustomScriptAssertValidator implements ConstraintValidator<CustomScriptAssert, Object> {
//
//    private final ScriptEngine scriptEngine;
//    private String script;
//
//    public CustomScriptAssertValidator() {
//        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
//        this.scriptEngine = scriptEngineManager.getEngineByName("graal.js");
//    }
//
//    @Override
//    public void initialize(CustomScriptAssert annotation) {
//        this.script = annotation.script();
//    }
//
//    @Override
//    public boolean isValid(Object value, ConstraintValidatorContext context) {
//        try {
//            scriptEngine.eval(script.replace("_this", "value"));
//            Object result = scriptEngine.get("result");
//            return result instanceof Boolean && (Boolean) result;
//        } catch (ScriptException e) {
//            return false;
//        }
//    }
//}
//package hello.itemservice.javascript;
//
//import hello.itemservice.javascript.CustomScriptAssertValidator;
//import jakarta.validation.Constraint;
//import jakarta.validation.Payload;
//
//import java.lang.annotation.*;
//
//@Target({ElementType.TYPE})
//@Retention(RetentionPolicy.RUNTIME)
//@Constraint(validatedBy = CustomScriptAssertValidator.class)
//public @interface CustomScriptAssert {
//    String message() default "Custom validation failed";
//
//    String script();
//    Class<?>[] groups() default {};
//    Class<? extends Payload>[] payload() default {};
////    Class<?>[] groups() default {};
////    Class<? extends Payload>[] payload() default {};
//}
package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    /**
     *  검증 오류 결과를 보관  -> replaced from a HashMap to BindingResult
     *  BindingResult takes over the use of Model(for error) and the HashMap<K,V> below
     *  Map<String, String> errors = new HashMap<>();
     */
    private final ItemRepository itemRepository;

    /**
     * used in V5 to store the itemValidator class(implements Validator)
     */
    private final ItemValidator itemValidator;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    /**
     * @param bindingResult ->
     * BindingResult is an interface that inherits the Errors interface
     * The implementation of BindingResult that is used is called
     * BeanPropertyBindingResult.
     *
     * BindingResult thus adds on the Errors interface(which only provides basic
     * error storage and views.)
     *
     * BindingResult is especially useful in Validation,
     * as when used in conjunction with @ModelAttribute, should an error occur with the
     * binding of values to keys in @ModelAttribute occurs, BindingResult catches the exception
     * allowing the controller to still be called normally,
     * unlike the usual termination of the console due to error 400;
     * try the above by comparing v1 and v2 with wrong bindings ,
     * ie) adding a String to the price(Integer) input
     *
     *
     *  and also the thymeleaf shenanigans that try to optimise the usage of BindingResult
     */
//    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if(!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item","itemName", "name field is required"));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > Integer.MAX_VALUE) {
            bindingResult.addError(new FieldError("item","price", "price field should not be null and be between 1000 and 2147483647"));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999 || item.getQuantity() < 0) {
            bindingResult.addError(new FieldError("item","quantity", "quantity field is required and must be between 0 and 9999"));
        }
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "the multiple of price and quantity" +
                        " must be more than 10k. current :" + resultPrice));
            }
        }

        if(bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/validation/v2/items/{itemId}";
    }


    /**
     *
     * previously, addItemV1 had a problem where in which should an error occur,
     * the data of the Item disappears. V2 will aim to remedy that issue
     *
     * Should there be an error during the binding process of @ModelAttribute, the Model
     * object is impossible to store the value input from the user.
     *
     * For instance, should there be a String input for a value that was supposed to be mapped to
     * an Integer, there will be a typeError, and the initial String input cannot be stored by @ModelAttribute
     *
     * so, there needs to be separate method to store these values, in order to show what value did the
     * User input wrongly
     *
     * and FieldError does exactly the above.
     *
     * FieldError has 2 constructors
     *
     * 1. public FieldError(String objectName, String field, String defaultMessage);
     *
     * 2. public FieldError(String objectName, String field, @Nullable Object
     * rejectedValue, boolean bindingFailure, @Nullable String[] codes, @Nullable
     * Object[] arguments, @Nullable String defaultMessage)
     *
     * ObjectError also has 2 constructors
     *
     * 	1. public ObjectError(String objectName, @Nullable String defaultMessage)
     *
     * 	2. public ObjectError(String objectName, @Nullable String[] codes, @Nullable Object[] arguments,
     * 	@Nullable String defaultMessage);
     *
     * 	The rejectedValue in FieldError is the parameter that stores the erroneous user input value.
     * 	the Boolean bindingfailure can denote whether a typeError occured during the binding process.
     * 	In the use case below, a typeError did not manifest, thus thevalue is set to false
     *
     * 	Thymeleaf further adds on to FieldError, with an additional abstraction that calls
     * FIeldError to store the erroneous input automatically should a binding error result from
     * @ModelAttribute
     *
     * for type errors below is a simplified call routine
     * TypeError -> fieldError initialised and stores the erroneous value
     * -> passes the parameters to BindingResult -> calls controller
     *
     * this is the reason why error messages can appear even if there exist a typeError(Exception)
     *
     * FieldError and ObjectError constructors also provides parameters for codes and arguments
     * this is to create Error codes to quickly identify what kind of errors had occured.
     *
     */
//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if(!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item","itemName", item.getItemName(),
                    false, null , null,  "name field is required"));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > Integer.MAX_VALUE) {
            bindingResult.addError(new FieldError("item","price", item.getPrice(),
                    false, null, null, "price field should not be null and be between 1000 and 2147483647"));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999 || item.getQuantity() < 0) {
            bindingResult.addError(new FieldError("item","quantity", item.getQuantity(),
                    false, null, null, "quantity field is required and must be between 0 and 9999"));
        }
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", null, null,
                        "the multiple of price and quantity" +
                        " must be more than 10k. current :" + resultPrice));
            }
        }

        if(bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName",
                    item.getItemName(), false, new String[]{"required.item.itemName"}, null,
                    null));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > Integer.MAX_VALUE) {
            bindingResult.addError(new FieldError("item","price", item.getPrice(),
                    false, new String[]{"range.item.price"}, new Object[]{1000, Integer.MAX_VALUE}, null));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999 || item.getQuantity() < 0) {
            bindingResult.addError(new FieldError("item","quantity", item.getQuantity(),
                    false, new String[]{"max.item.quantity"}, new Object[]{0, 9999}, null));
        }
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
            }
        }

        if(bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * using rejectValue() , reject() instead of FieldError, ModelError
     *
     * void rejectValue(@Nullable String field, String errorCode,
     * @Nullable Object[] errorArgs, @Nullable String defaultMessage);
     *  BindingResult already knows its validation target.
     *
     *  Hence, target(item) is not needed
     *  errorcode is similar to above but truncated
     *
     * truncation is possible due to MessageCodesResolver
     * it searches the properties file from the most specific to the least and finds the'
     * closest match.
     */

//    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required");
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > Integer.MAX_VALUE) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, Integer.MAX_VALUE}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999 || item.getQuantity() < 0) {
            bindingResult.rejectValue("quantity", "max", new Object[]{0, 9999}, null);
        }
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

//        log.info("objectName={}", bindingResult.getObjectName());
//        log.info("target={}", bindingResult.getTarget());
//        log.info("errorCodes = {}", bindingResult.getAllErrors());

        if(bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/validation/v2/items/{itemId}";
    }


    /**
     * developing error codes specifically for all errors are ass
     * so for less important errors, use a less specific message like "required"
     *
     * and save the more specific error messages for more severe or important messages
     * this is the general strategy in implementing error codes
     *
     * this is done on error.properties, try commenting them out and see what kind of error messages pop up
     */


    /**
     * now we will be using ValidationUtils and certain Validation Error handling
     * presets defined by Spring
     *
     * try typing Strings into the price field. the log will show that the following
     * error message codes are generated
     *
     * [typeMismatch.item.price,
     * typeMismatch.price,
     * typeMismatch.java.lang.Integer,
     * typeMismatch]
     *
     * the base is typeMismatch, which after being resolved by MessageCodeResolver generates the other 3
     *
     * the error messages for these codes are not defined yet in errors.properties so default Spring error is
     * logged.
     *
     * Now lets add the following to error.properties
     * typeMismatch.java.lang.Integer= insert a numerical value.
     * typeMismatch= there was a type error.
     *
     * <important>
     * should BindingResult not be beside @ModelAttribute , 400 error code page will still result
     * </important>
     *
     * through this basic spring error code message generation, we can exploit it to create our own value for
     * these message codes generated
     */


    /**
     * even if validation got easier thanks to the reject(), rejectValue(), it still remains as clutter
     * and when the projects get larger, the amount of error handling needed gets exponentially bigger.
     * so there exists a need to separate out the validating logic away from the controller as a separate class.
     *
     * Integration with ItemValidator
     */
//    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        // validating logic here
        itemValidator.validate(item,bindingResult);

        if(bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * Spring provides the Validator interface to allow users to
     * validate their inputs systematically
     *
     * V5 implemented the Validator interface, and used the implemented class
     * <important>
     * but spring supports additional features for the Validator interface
     * </important>
     */

    /**
     * @param dataBinder is a variable of WebDataBinder Class
     * this method allows for the controller to use the validator automatically
     *
     * @interface - @InitBinder
     * only affects the current controller
     * should one want to add this WebDataBinder globally, separate settings are required
     */
    @InitBinder
    public void init(WebDataBinder dataBinder) {
        log.info("init binder {}", dataBinder);
        dataBinder.addValidators(itemValidator);
    }

    /**global ver
     *
     * @SpringBootApplication
     * public class ItemServiceApplication implements WebMvcConfigurer {
     *  public static void main(String[] args) {
     *  SpringApplication.run(ItemServiceApplication.class, args);
     *  }
     *  @Override
     *  public Validator getValidator() {
     *  return new ItemValidator();
     *  }
     * }
     *
     * as can be seen from the above code, the main class needs to implement WebMvcConfigurer then override
     * the getValidator object.
     *
     * but this comes at the expense of sacrificing the original getValidator -> which includes beanValidator
     *
     * this is thus rarely used
     */

    public @Valid int getAge(){}

    /**
     * @param item now has the @Validated tag.
     * This now means that "item" will automatically pass through all applicable validator classes
     * that supports(return true) its class, which were registered on WebDataBinder.
     *
     *             both @Validated(spring) and @Valid(jakarta EE) works
     *
     *             but to use the jakarta EE version, there needs to be an additional
     *             dependency written on build.gradle
     *
     *             which is:
     *             implementation 'org.springframework.boot:spring-boot-starter-validation'
     */


    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult
            bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }
        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}


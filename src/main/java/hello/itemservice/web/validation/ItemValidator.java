package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * class made to separate validating logic from controller
 *
 * @implements Validator
 * -> thus it needs to implement these 2 methods
 *
 * supports() {} :
 * checks if the clazz can use the current Validator class
 *
 * validate(Object target, Errors errors) :
 * Object that needs to be validated
 * and BindingResult(passed as error, Bindingresult is a child class of Errors)
 */
@Component
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof Item) {  //is this really needed or is it redundant as supports already exists to check?
            Item item = (Item) target;

            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "itemName", "required");

//            price
            if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
                errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null);

            }

//            quantity
            if (item.getQuantity() == null || item.getQuantity() > 10000) {
                errors.rejectValue("quantity", "max", new Object[]{9999}, null);
            }

            //ObjectError
            if (item.getPrice() != null && item.getQuantity() != null) {
                int resultPrice = item.getPrice() * item.getQuantity();
                if (resultPrice < 10000) {
                    errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
                }
            }

        }
    }
}

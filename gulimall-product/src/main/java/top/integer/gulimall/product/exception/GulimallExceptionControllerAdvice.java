package top.integer.gulimall.product.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.integer.common.exception.BizCodeEnume;
import top.integer.common.utils.R;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice("top.integer.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        Map<String, String> reason = e.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors
                        .toMap(FieldError::getField, FieldError::getDefaultMessage));
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(), BizCodeEnume.VAILD_EXCEPTION.getMsg()).put("data", reason);
    }
}

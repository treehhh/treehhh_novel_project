package com.lf.novelbackend.aop;

import com.lf.novelbackend.annotation.UserTypeAuthCheck;
import com.lf.novelbackend.exception.BusinessException;
import com.lf.novelbackend.exception.ErrorCode;
import com.lf.novelbackend.model.entity.User;
import com.lf.novelbackend.model.enums.UserTypeEnum;
import com.lf.novelbackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint         切入点
     * @param userTypeAuthCheck 权限校验注解
     */
    @Around("@annotation(userTypeAuthCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, UserTypeAuthCheck userTypeAuthCheck) throws Throwable {
        int mustUserType = userTypeAuthCheck.mustUserType();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User loginUser = userService.getLoginUser(request);

        UserTypeEnum mustUserTypeEnum = UserTypeEnum.getEnumByValue(mustUserType);
        if (mustUserTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "权限校验出错");
        }
        // 获取当前用户具有的权限
        UserTypeEnum userTypeEnum = UserTypeEnum.getEnumByValue(loginUser.getUserType());
        // 没有权限，拒绝
        if (userTypeEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 要求用户的权限与必须有的权限一致
        if (userTypeEnum != mustUserTypeEnum) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, String.format("没有%s权限", mustUserTypeEnum.getText()));
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

package org.koreait.global.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) // 라이프 사이클 애너테이션이 언제까지 살아 있을지 정하는 애너테이션
public @interface ApplyErrorPage {

}

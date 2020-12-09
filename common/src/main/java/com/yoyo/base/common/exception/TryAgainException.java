package com.yoyo.base.common.exception;

public class TryAgainException extends BizException {

    public TryAgainException(BizExceptionEnum bizExceptionEnum) {
        super(bizExceptionEnum);
    }

}
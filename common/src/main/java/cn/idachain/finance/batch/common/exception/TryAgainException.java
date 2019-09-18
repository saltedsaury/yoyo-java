package cn.idachain.finance.batch.common.exception;

public class TryAgainException extends BizException {

    public TryAgainException(BizExceptionEnum bizExceptionEnum) {
        super(bizExceptionEnum);
    }

}
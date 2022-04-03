package com.jwt.szs.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class SzsTransactionManager {

    private final TransactionTemplate transactionTemplate;

    public SzsTransactionManager(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    public String startTransaction(Runnable runnable) {

        log.info("트랜잭션을 시작합니다. 실패 시 에러메세지를 리턴합니다.");

        AtomicReference<String> exceptionMessage = new AtomicReference<>();
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                log.error("트랜잭션 실패 {}", e.getMessage());
                exceptionMessage.set(e.getMessage());
                transactionStatus.setRollbackOnly();
            }
        });

        return exceptionMessage.get();
    }
}

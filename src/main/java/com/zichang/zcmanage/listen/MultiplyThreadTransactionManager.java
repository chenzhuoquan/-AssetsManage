/*
package com.yupi.springbootinit.listen;


import cn.hutool.core.date.StopWatch;
import com.yupi.springbootinit.model.domain.UsageInfo;
import com.yupi.springbootinit.service.impl.UsageInfoServiceImpl;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class MultiplyThreadTransactionManager {

    */
/**
     * 如果是多数据源的情况下,需要指定具体是哪一个数据源
     *//*

    private final DataSource dataSource;


    public void saveBatchToUsage(List<Runnable> tasks, Executor executor) {


        DataSourceTransactionManager transactionManager = getTransactionManager();
        //是否发生了异常
        AtomicBoolean ex = new AtomicBoolean();
        List<TransactionStatus> transactionStatusList = new Vector<>();
        List<TransactionResource> transactionResources = new Vector<>();
        List<CompletableFuture> completableFutureList = new Vector<>();

        tasks.forEach(task -> {
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {

                transactionStatusList.add(openNewTransaction(transactionManager));
                transactionResources.add(TransactionResource.copyTransactionResource());
                task.run();
            }, executor).exceptionally(exceptions1 -> {
                exceptions1.printStackTrace(); // 在这里处理异常
                ex.set(true);
                completableFutureList.forEach(completableFuture1 -> completableFuture1.cancel(true));
                return null;
            });
            completableFutureList.add(completableFuture);
        });


        System.out.println("打断点");
        try {
            // 确保所有任务成功后才继续
            CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0])).join();
        } catch (Throwable e) {
            ex.set(true);
            e.printStackTrace();
        }
        System.out.println("打断点");
        if (ex.get()) {
            System.out.println("发生异常,全部事务回滚");
            for (int i = 0; i < completableFutureList.size(); i++) {
                TransactionResource resource = transactionResources.get(i);
                try {
                    resource.autoWiredTransactionResource();
                    transactionManager.rollback(transactionStatusList.get(i));
                } finally {
                    // 清理事务资源和状态
                    cleanUpTransactionState(resource);
                }
            }
        } else {
            System.out.println("全部事务正常提交");
            for (int i = 0; i < completableFutureList.size(); i++) {
                TransactionResource resource = transactionResources.get(i);
                try {
                    resource.autoWiredTransactionResource();
                    transactionManager.commit(transactionStatusList.get(i));
                } finally {
                    // 清理事务资源和状态
                    cleanUpTransactionState(resource);
                }
            }
        }


    }

    private TransactionStatus openNewTransaction(DataSourceTransactionManager transactionManager) {
        //JdbcTransactionManager根据TransactionDefinition信息来进行一些连接属性的设置
        //包括隔离级别和传播行为等
        DefaultTransactionDefinition transactionDef = new DefaultTransactionDefinition();
        //开启一个新事务---此时autocommit已经被设置为了false,并且当前没有事务,这里创建的是一个新事务
        return transactionManager.getTransaction(transactionDef);
    }

    private void cleanUpTransactionState(TransactionResource resource) {
        // 移除绑定的资源，避免重复移除 DataSource 抛出异常
        if (resource != null) {
            resource.removeTransactionResource();
        }

        // 清除 TransactionSynchronizationManager 的状态
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
        if (TransactionSynchronizationManager.hasResource(dataSource)) {
            TransactionSynchronizationManager.unbindResourceIfPossible(dataSource);
        }

        // 清除其他事务属性
        TransactionSynchronizationManager.setCurrentTransactionName(null);
        TransactionSynchronizationManager.setCurrentTransactionReadOnly(false);
        TransactionSynchronizationManager.setCurrentTransactionIsolationLevel(null);
        TransactionSynchronizationManager.setActualTransactionActive(false);
    }

    private DataSourceTransactionManager getTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    */
/**
     * 保存当前事务资源,用于线程间的事务资源COPY操作
     *//*

    @Builder
    private static class TransactionResource {
        //事务结束后默认会移除集合中的DataSource作为key关联的资源记录
        private Map<Object, Object> resources = new HashMap<>();
        //下面五个属性会在事务结束后被自动清理,无需我们手动清理
        private Set<TransactionSynchronization> synchronizations = new HashSet<>();
        private String currentTransactionName;
        private Boolean currentTransactionReadOnly;
        private Integer currentTransactionIsolationLevel;
        private Boolean actualTransactionActive;

        public static TransactionResource copyTransactionResource() {
            return TransactionResource.builder()
                    //返回的是不可变集合
                    .resources(TransactionSynchronizationManager.getResourceMap())
                    //如果需要注册事务监听者,这里记得修改--我们这里不需要,就采用默认负责--spring事务内部默认也是这个值
                    .synchronizations(new LinkedHashSet<>())
                    .currentTransactionName(TransactionSynchronizationManager.getCurrentTransactionName())
                    .currentTransactionReadOnly(TransactionSynchronizationManager.isCurrentTransactionReadOnly())
                    .currentTransactionIsolationLevel(TransactionSynchronizationManager.getCurrentTransactionIsolationLevel())
                    .actualTransactionActive(TransactionSynchronizationManager.isActualTransactionActive())
                    .build();
        }

        public void autoWiredTransactionResource() {
            resources.forEach(TransactionSynchronizationManager::bindResource);
            //如果需要注册事务监听者,这里记得修改--我们这里不需要,就采用默认负责--spring事务内部默认也是这个值
            TransactionSynchronizationManager.initSynchronization();
            TransactionSynchronizationManager.setActualTransactionActive(actualTransactionActive);
            TransactionSynchronizationManager.setCurrentTransactionName(currentTransactionName);
            TransactionSynchronizationManager.setCurrentTransactionIsolationLevel(currentTransactionIsolationLevel);
            TransactionSynchronizationManager.setCurrentTransactionReadOnly(currentTransactionReadOnly);
        }

        public void removeTransactionResource() {
            //事务结束后默认会移除集合中的DataSource作为key关联的资源记录
            //DataSource如果重复移除,unbindResource时会因为不存在此key关联的事务资源而报错
            resources.keySet().forEach(key -> {
                if (!(key instanceof DataSource)) {
                    TransactionSynchronizationManager.unbindResource(key);
                }
            });
        }


    }
}
*/

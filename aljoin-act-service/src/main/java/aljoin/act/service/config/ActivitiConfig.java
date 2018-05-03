package aljoin.act.service.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.DynamicBpmnService;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import aljoin.act.listener.GlobalEventListener;
import aljoin.dao.config.DynamicDataSource;

/**
 * 
 * 流程配置
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午4:00:44
 */
@Configuration
public class ActivitiConfig {

    /**
     * 动态数据源
     */
    @Resource
    private DynamicDataSource dataSource;
    /**
     * 事务管理器
     */
    @Resource
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Bean
    public ProcessEngine getProcessEngine() {
        /**
         * 获取流程引擎配置
         */
        /*
         * ProcessEngineConfiguration pec =
         * ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
         */
        SpringProcessEngineConfiguration pec = new SpringProcessEngineConfiguration();
        /**
         * 设置数据源
         */
        pec.setDataSource(dataSource);
        /**
         * 自定义id生成器
         */
        pec.setIdGenerator(new CustomIdGenerator());

        /**
         * 设置事务管理器(使用spring事务管理器)
         */
        pec.setTransactionManager(dataSourceTransactionManager);

        /**
         * 配置模式 true 自动创建和更新表
         */
        pec.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        /**
         * 流程图片乱码问题
         */
        pec.setActivityFontName("微软雅黑");
        pec.setLabelFontName("微软雅黑");
        pec.setAnnotationFontName("微软雅黑");

        /**
         * 监听器
         */
        List<ActivitiEventListener> eventListeners = new ArrayList<ActivitiEventListener>();
        GlobalEventListener globalEventListener = new GlobalEventListener();
        Map<String, String> handlers = new HashMap<String, String>();
        // 任务被完成了。它会在ENTITY_DELETE事件之前触发。当任务是流程一部分时，事件会在流程继续运行之前， 后续事件将是ACTIVITY_COMPLETE，对应着完成任务的节点
        handlers.put("TASK_COMPLETED", "taskCompletedListener");
        // 监听器监听的流程引擎已经创建完毕，并准备好接受API调用
        handlers.put("ENGINE_CREATED", null);
        // 监听器监听的流程引擎已经关闭，不再接受API调用
        handlers.put("ENGINE_CLOSED", null);
        // 创建了一个新实体。实体包含在事件中
        handlers.put("ENTITY_CREATED", null);
        // 创建了一个新实体，初始化也完成了。如果这个实体的创建会包含子实体的创建，这个事件会在子实体都创建/初始化完成后被触发，这是与ENTITY_CREATED的区别
        handlers.put("ENTITY_INITIALIZED", null);
        // 更新了已存在的实体。实体包含在事件中
        handlers.put("ENTITY_UPDATED", null);
        // 删除了已存在的实体。实体包含在事件中
        handlers.put("ENTITY_DELETED", "entityDeletedListener");
        // 暂停了已存在的实体。实体包含在事件中。会被ProcessDefinitions, ProcessInstances 和 Tasks抛出
        handlers.put("ENTITY_SUSPENDED", null);
        // 激活了已存在的实体，实体包含在事件中。会被ProcessDefinitions, ProcessInstances 和 Tasks抛出
        handlers.put("ENTITY_ACTIVATED", null);
        // 作业执行成功。job包含在事件中
        handlers.put("JOB_EXECUTION_SUCCESS", null);
        // 作业执行失败。作业和异常信息包含在事件中
        handlers.put("JOB_EXECUTION_FAILURE", null);
        // 因为作业执行失败，导致重试次数减少。作业包含在事件中
        handlers.put("JOB_RETRIES_DECREMENTED", null);
        // 触发了定时器。job包含在事件中
        handlers.put("TIMER_FIRED", null);
        // 取消了一个作业。事件包含取消的作业。作业可以通过API调用取消， 任务完成后对应的边界定时器也会取消，在新流程定义发布时也会取消
        handlers.put("JOB_CANCELED", null);
        // 一个节点开始执行
        handlers.put("ACTIVITY_STARTED", null);
        // 一个节点成功结束
        handlers.put("ACTIVITY_COMPLETED", null);
        // 一个节点收到了一个信号
        handlers.put("ACTIVITY_SIGNALED", null);
        // 一个节点收到了一个消息。在节点收到消息之前触发。收到后，会触发ACTIVITY_SIGNAL或ACTIVITY_STARTED，这会根据节点的类型（边界事件，事件子流程开始事件）
        handlers.put("ACTIVITY_MESSAGE_RECEIVED", null);
        // 一个节点收到了一个错误事件。在节点实际处理错误之前触发。
        // 事件的activityId对应着处理错误的节点,这个事件后续会是ACTIVITY_SIGNALLED或ACTIVITY_COMPLETE， 如果错误发送成功的话
        handlers.put("ACTIVITY_ERROR_RECEIVED", null);
        // 抛出了未捕获的BPMN错误。流程没有提供针对这个错误的处理器。 事件的activityId为空
        handlers.put("UNCAUGHT_BPMN_ERROR", null);
        // 一个节点将要被补偿。事件包含了将要执行补偿的节点id
        handlers.put("ACTIVITY_COMPENSATE", null);
        // 创建了一个变量。事件包含变量名，变量值和对应的分支或任务（如果存在）
        handlers.put("VARIABLE_CREATED", null);
        // 更新了一个变量。事件包含变量名，变量值和对应的分支或任务（如果存在）
        handlers.put("VARIABLE_UPDATED", null);
        // 删除了一个变量。事件包含变量名，变量值和对应的分支或任务（如果存在）
        handlers.put("VARIABLE_DELETED", null);
        // 任务被分配给了一个人员。事件包含任务
        handlers.put("TASK_ASSIGNED", null);
        // 创建了新任务。它位于ENTITY_CREATE事件之后。当任务是由流程创建时， 这个事件会在TaskListener执行之前被执行
        handlers.put("TASK_CREATED", "taskCreatedListener");
        // 任务已超时，在TIMER_FIRED事件之后，会触发用户任务的超时事件， 当这个任务分配了一个定时器的时候
        handlers.put("TASK_TIMEOUT", null);
        // 流程已结束。在最后一个节点的ACTIVITY_COMPLETED事件之后触发。 当流程到达的状态，没有任何后续连线时， 流程就会结束
        handlers.put("PROCESS_COMPLETED", null);
        // 用户被添加到一个组里。事件包含了用户和组的id
        handlers.put("MEMBERSHIP_CREATED", null);
        // 用户被从一个组中删除。事件包含了用户和组的id
        handlers.put("MEMBERSHIP_DELETED", null);
        // 所有成员被从一个组中删除。在成员删除之前触发这个事件，所以他们都是可以访问的。 因为性能方面的考虑，不会为每个成员触发单独的MEMBERSHIP_DELETED事件
        handlers.put("MEMBERSHIPS_DELETED", null);
        globalEventListener.setHandlers(handlers);
        eventListeners.add(globalEventListener);
        pec.setEventListeners(eventListeners);
        /**
         * 获取流程引擎对象
         */
        ProcessEngine pe = pec.buildProcessEngine();

        return pe;
    }

    /**
     * 
     * 动态Bpmn服务
     *
     * @return：DynamicBpmnService
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午2:58:32
     */
    @Bean
    public DynamicBpmnService getDynamicBpmnService() {
        return getProcessEngine().getDynamicBpmnService();
    }

    /**
     * 
     * 表单服务
     *
     * @return：FormService
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午2:58:20
     */
    @Bean
    public FormService getFormService() {
        return getProcessEngine().getFormService();
    }

    /**
     * 
     * 历史服务
     *
     * @return：HistoryService
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午2:58:11
     */
    @Bean
    public HistoryService getHistoryService() {
        return getProcessEngine().getHistoryService();
    }

    /**
     * 
     * 身份服务
     *
     * @return：IdentityService
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午2:57:59
     */
    @Bean
    public IdentityService getIdentityService() {
        return getProcessEngine().getIdentityService();
    }

    /**
     * 
     * 管理服务
     *
     * @return：ManagementService
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午2:57:51
     */
    @Bean
    public ManagementService getManagementService() {
        return getProcessEngine().getManagementService();
    }

    /**
     * 
     * 仓库服务
     *
     * @return：RepositoryService
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午2:57:06
     */
    @Bean
    public RepositoryService getRepositoryService() {
        return getProcessEngine().getRepositoryService();
    }

    /**
     * 
     * 运行时服务
     *
     * @return：RuntimeService
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午2:56:48
     */
    @Bean
    public RuntimeService getRuntimeService() {
        return getProcessEngine().getRuntimeService();
    }

    /**
     * 
     * 任务服务
     *
     * @return：TaskService
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午2:56:33
     */
    @Bean
    public TaskService getTaskService() {
        return getProcessEngine().getTaskService();
    }

    /**
     * 
     * 引擎配置
     *
     * @return：TaskService
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午2:56:33
     */
    @Bean
    public ProcessEngineConfiguration getProcessEngineConfiguration() {
        return getProcessEngine().getProcessEngineConfiguration();
    }
}

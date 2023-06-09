package cn.meshed.cloud.workflow.domain.engine;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class History implements Serializable {

    String deleteReason;
    Date endTime;
    Long durationInMillis;
    Long workTimeInMillis;
    private String id;
    private String name;
    private String description;
    private int priority;
    private String owner;
    private String assignee;
    private String processInstanceId;
    private String executionId;
    private String taskDefinitionId;
    private String processDefinitionId;
    private String scopeId;
    private String subScopeId;
    private String scopeType;
    private String scopeDefinitionId;
    private String propagatedStageInstanceId;
    private Date createTime;
    private String taskDefinitionKey;
    private Date dueDate;
    private String category;
    private String parentTaskId;

//    private List<? extends IdentityLinkInfo> IdentityLinks;
    private String tenantId;
    private String formKey;

//    /** @deprecated */
//    Date startTime;
    private Map<String, Object> taskLocalVariables;
    private Map<String, Object> processVariables;
    private Date claimTime;
}

package cn.meshed.cloud.workflow.engine.gatewayimpl;

import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.workflow.domain.engine.Definition;
import cn.meshed.cloud.workflow.domain.engine.gateway.DefinitionGateway;
import cn.meshed.cloud.workflow.domain.form.gateway.FormGateway;
import cn.meshed.cloud.workflow.engine.enums.ActiveStatusEnum;
import cn.meshed.cloud.workflow.engine.query.DefinitionPageQry;
import com.alibaba.cola.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>流程定义网关默认实现</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class DefaultDefinitionGateway implements DefinitionGateway {

    private final RepositoryService repositoryService;
    private final FormGateway formGateway;

    /**
     * <h1>搜索列表</h1>
     *
     * @param pageQry 分页参数
     * @return {@link PageResponse<Definition>}
     */
    @Override
    public PageResponse<Definition> searchList(DefinitionPageQry pageQry) {
        ProcessDefinitionQuery query = getProcessDefinitionQuery(pageQry);
        //查询总数
        long total = query.count();
        if (total == 0) {
            return PageResponse.of(Collections.emptyList(), Math.toIntExact(total), pageQry.getPageIndex(), pageQry.getPageSize());
        }
        //查询列表
        List<ProcessDefinition> processDefinitions = query.listPage(pageQry.getOffset(), pageQry.getPageSize());
        List<Definition> definitions = processDefinitions.stream().map(this::toDefinition).collect(Collectors.toList());
        //查询表
        return PageResponse.of(definitions, Math.toIntExact(total), pageQry.getPageIndex(), pageQry.getPageSize());
    }

    private Definition toDefinition(ProcessDefinition processDefinition) {
        Definition definition = CopyUtils.copy(processDefinition, Definition.class);
        definition.setSuspended(processDefinition.isSuspended());
        if (processDefinition.hasStartFormKey()){
            String formKey = formGateway.getStartFormKey(processDefinition.getId());
            definition.setFormKey(formKey);
            definition.setHasStartFormKey(true);
        } else {
            definition.setHasStartFormKey(false);
        }
        return definition;
    }

    /**
     * 查询条件
     *
     * @param pageQry 分页参数
     * @return
     */
    private ProcessDefinitionQuery getProcessDefinitionQuery(DefinitionPageQry pageQry) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();

        //名字模糊匹配
        if (StringUtils.isNotBlank(pageQry.getName())) {
            query.processDefinitionNameLikeIgnoreCase(pageQry.getName());
        }

        //分类匹配
        if (StringUtils.isNotBlank(pageQry.getCategory())) {
            query.processDefinitionCategory(pageQry.getCategory());
        }

        //状态查询
        if (pageQry.getStatus() == ActiveStatusEnum.ACTIVE) {
            query.active();
        } else if (pageQry.getStatus() == ActiveStatusEnum.SUSPENDED) {
            query.suspended();
        }
        return query;
    }

    /**
     * 反转状态
     *
     * @param definitionId 流程定义编码
     */
    @Override
    public void invertedState(String definitionId) {
        ProcessDefinition processDefinition = getProcessDefinition(definitionId);
        //反转操作
        if (processDefinition.isSuspended()) {
            repositoryService.activateProcessDefinitionById(definitionId);
        } else {
            repositoryService.suspendProcessDefinitionById(definitionId);
        }
    }

    /**
     * 获取定义名称
     *
     * @param definitionId 定义编码
     * @return 实例名称
     */
    @Override
    public String getDefinitionName(String definitionId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(definitionId).singleResult();
        if (processDefinition == null) {
            return null;
        }
        return processDefinition.getName();
    }

    /**
     * 查询定义
     *
     * @param definitionId 流程定义编码
     * @return 流程定义
     */
    private ProcessDefinition getProcessDefinition(String definitionId) {
        return repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(definitionId).singleResult();
    }

    /**
     * 查询
     *
     * @param definitionId 流程定义编码
     * @return {@link Definition}
     */
    @Override
    public Definition query(String definitionId) {
        return CopyUtils.copy(getProcessDefinition(definitionId),Definition.class);
    }
}

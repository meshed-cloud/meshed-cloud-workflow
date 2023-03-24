package cn.meshed.cloud.workflow.engine.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import cn.meshed.cloud.workflow.domain.engine.Comment;
import cn.meshed.cloud.workflow.domain.engine.gateway.TaskGateway;
import cn.meshed.cloud.workflow.engine.data.CommentDTO;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class TaskCommentQryExe implements QueryExecute<String, SingleResponse<List<CommentDTO>>> {

    private final TaskGateway taskGateway;

    /**
     * <h1>查询执行器</h1>
     *
     * @param taskId 任务ID
     * @return {@link SingleResponse<List<CommentDTO>>}
     */
    @Override
    public SingleResponse<List<CommentDTO>> execute(String taskId) {
        AssertUtils.isTrue(StringUtils.isNotBlank(taskId), "任务ID不能为空");
        List<Comment> taskComments = taskGateway.getTaskComments(taskId);
        return ResultUtils.copyList(taskComments, CommentDTO::new);
    }
}
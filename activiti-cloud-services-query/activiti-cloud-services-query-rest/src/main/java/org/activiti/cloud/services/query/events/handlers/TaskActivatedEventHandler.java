/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.cloud.services.query.events.handlers;

import java.util.Date;
import java.util.Optional;

import org.activiti.cloud.services.api.events.ProcessEngineEvent;
import org.activiti.cloud.services.query.app.repository.TaskRepository;
import org.activiti.cloud.services.query.events.TaskActivatedEvent;
import org.activiti.cloud.services.query.events.TaskCompletedEvent;
import org.activiti.cloud.services.query.events.TaskSuspendedEvent;
import org.activiti.cloud.services.query.model.Task;
import org.activiti.engine.ActivitiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskActivatedEventHandler implements QueryEventHandler {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskActivatedEventHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void handle(ProcessEngineEvent event) {
        TaskActivatedEvent taskActivatedEvent = (TaskActivatedEvent) event;
        Task eventTask = taskActivatedEvent.getTask();
        Optional<Task> findResult = taskRepository.findById(eventTask.getId());
        if (findResult.isPresent()) {
            Task task = findResult.get();
            if(task.getAssignee() != null && !task.getAssignee().isEmpty()) {
                task.setStatus("ASSIGNED");
            }else{
                task.setStatus("CREATED");
            }
            task.setLastModified(new Date(taskActivatedEvent.getTimestamp()));
            task.setOwner(taskActivatedEvent.getTask().getOwner());
            task.setClaimDate(taskActivatedEvent.getTask().getClaimDate());
            taskRepository.save(task);
        } else {
            throw new ActivitiException("Unable to find task with id: " + eventTask.getId());
        }
    }

    @Override
    public Class<? extends ProcessEngineEvent> getHandledEventClass() {
        return TaskActivatedEvent.class;
    }
}

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

import org.activiti.cloud.services.query.app.repository.ProcessInstanceRepository;
import org.activiti.cloud.services.query.app.repository.TaskRepository;
import org.activiti.cloud.services.query.events.TaskSuspendedEvent;
import org.activiti.cloud.services.query.model.ProcessInstance;
import org.activiti.cloud.services.query.model.Task;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;

/**
 * TaskCreatedEventHandler JPA Repository Integration Tests
 * 
 */
@RunWith(SpringRunner.class)
@DataJpaTest(showSql = true)
@Sql(value = "classpath:/jpa-test.sql")
public class TaskSuspendedEventHandlerIT {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskSuspendedEventHandler handler;

    @SpringBootConfiguration
    @EnableJpaRepositories(basePackageClasses = ProcessInstanceRepository.class)
    @EntityScan(basePackageClasses = ProcessInstance.class)
    @Import(TaskSuspendedEventHandler.class)
    static class Configuation {
    }

    @Test
    public void contextLoads() {
        // Should pass
    }


    @After
    public void tearDown() throws Exception {
        repository.deleteAll();
    }

    @Test
    public void handleShouldStoreSuspendedTaskInstance() throws Exception {
        String processInstanceId = "1";
        String taskId = "5";

        //given
        Task eventTask = new Task(
                                  taskId,
                                  "assignee",
                                  "name",
                                  "description",
                                  new Date() /*createTime*/,
                                  new Date() /*dueDate*/,
                                  "priority",
                                  "category",
                                  "process_definition_id",
                                  processInstanceId,
                                  "runtime-bundle-a",
                                  "ASSIGNED",
                                  new Date() /*lastModified*/,
                                new Date(), /*claimDate*/
                                "owner"
        );
        TaskSuspendedEvent givenEvent = new TaskSuspendedEvent(System.currentTimeMillis(),
                                                              "taskSuspended",
                                                              "10",
                                                              "process_definition_id",
                                                              processInstanceId,
                                                              "runtime-bundle-a",
                                                              eventTask);
        //when
        handler.handle(givenEvent);

        //then
        Optional<Task> result = repository.findById(taskId);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getStatus()).isEqualTo("SUSPENDED");

    }


}

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

import org.activiti.cloud.services.api.events.ProcessEngineEvent;
import org.activiti.cloud.services.query.app.repository.TaskCandidateGroupRepository;
import org.activiti.cloud.services.query.events.TaskCandidateGroupRemovedEvent;
import org.activiti.cloud.services.query.model.TaskCandidateGroup;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class TaskCandidateGroupRemovedEventHandlerTest {

    @InjectMocks
    private TaskCandidateGroupRemovedEventHandler handler;

    @Mock
    private TaskCandidateGroupRepository taskCandidateRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void handleShouldStoreNewTaskInstance() throws Exception {
        //given
        TaskCandidateGroup eventTaskCandidate = mock(TaskCandidateGroup.class);
        TaskCandidateGroupRemovedEvent taskCreated = new TaskCandidateGroupRemovedEvent(System.currentTimeMillis(),
                                                            "taskCandidateGroupRemoved",
                                                            "10",
                                                            "100",
                                                            "200",
                                                            "runtime-bundle-a",
                                                            eventTaskCandidate);

        
        //when
        handler.handle(taskCreated);

        //then
        verify(taskCandidateRepository).delete(eventTaskCandidate);
    }

    @Test
    public void getHandledEventClassShouldReturnTaskCreatedEventClass() throws Exception {
        //when
        Class<? extends ProcessEngineEvent> handledEventClass = handler.getHandledEventClass();

        //then
        assertThat(handledEventClass).isEqualTo(TaskCandidateGroupRemovedEvent.class);
    }
}
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

import java.util.Optional;

import org.activiti.cloud.services.api.events.ProcessEngineEvent;
import org.activiti.cloud.services.query.app.repository.ProcessInstanceRepository;
import org.activiti.cloud.services.query.events.ProcessStartedEvent;
import org.activiti.cloud.services.query.model.ProcessInstance;
import org.activiti.engine.ActivitiException;
import org.activiti.test.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProcessStartedEventHandlerTest {

    @InjectMocks
    private ProcessStartedEventHandler handler;

    @Mock
    private ProcessInstanceRepository processInstanceRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void handleShouldStoreANewProcessInstanceInTheRepository() throws Exception {
        //given
        ProcessStartedEvent event = new ProcessStartedEvent(System.currentTimeMillis(),
                                                            "ProcessStartedEvent",
                                                            "10",
                                                            "100",
                                                            "200",
                                                            "101",
                                                            "201",
                                                            "runtime-bundle-a");
        ProcessInstance currentProcessInstance = mock(ProcessInstance.class);
        given(currentProcessInstance.getStatus()).willReturn("CREATED");
        given(processInstanceRepository.findById("200")).willReturn(Optional.of(currentProcessInstance));

        //when
        handler.handle(event);

        //then
        verify(processInstanceRepository).save(currentProcessInstance);
        verify(currentProcessInstance).setStatus("RUNNING");
    }

    @Test
    public void handleShouldThrowExceptionWhenRelatedProcessInstanceIsNotFound() throws Exception {
        //given
        ProcessStartedEvent event = new ProcessStartedEvent(System.currentTimeMillis(),
                                                            "ProcessStartedEvent",
                                                            "10",
                                                            "100",
                                                            "200",
                                                            "101",
                                                            "201",
                                                            "runtime-bundle-a");

        given(processInstanceRepository.findById("200")).willReturn(Optional.empty());

        //then
        expectedException.expect(ActivitiException.class);
        expectedException.expectMessage("Unable to find process instance with the given id: ");

        //when
        handler.handle(event);
    }

    @Test
    public void getHandledEventClassShouldReturnProcessStartedEvent() throws Exception {
        //when
        Class<? extends ProcessEngineEvent> handledEventClass = handler.getHandledEventClass();

        //then
        assertThat(handledEventClass).isEqualTo(ProcessStartedEvent.class);
    }
}
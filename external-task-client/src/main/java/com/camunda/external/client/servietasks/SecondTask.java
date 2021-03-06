/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.camunda.external.client.servietasks;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecondTask {

  protected static final Logger LOG = LoggerFactory.getLogger(SecondTask.class);

  @Bean
  @ExternalTaskSubscription("mySecondServiceTask")
  public ExternalTaskHandler secondTaskHandler() {
    return (externalTask, externalTaskService) -> {

      String test = externalTask.getVariable("test");
      LOG.info("SecondTask");


      Map<String, Object> variables = new HashMap<>();
      variables.put("test", test);

      // select the scope of the variables
      boolean isRandomSample = Math.random() <= 0.5;
      if (isRandomSample) {
        variables.put("auto", "lalala");
      } else {
        variables.put("auto", "blablabla");
      }
      externalTaskService.complete(externalTask, variables);

      LOG.info("The External Task {} has been completed!", externalTask.getId());

    };
  }

}

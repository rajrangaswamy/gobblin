/*
 * (c) 2014 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 */

package gobblin.instrumented;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import junit.framework.Assert;

import gobblin.Constructs;
import gobblin.configuration.ConfigurationKeys;
import gobblin.metrics.GobblinMetrics;
import gobblin.configuration.State;
import gobblin.metrics.Tag;
import gobblin.instrumented.extractor.InstrumentedExtractor;


@Test(groups = {"gobblin.core"})
public class InstrumentedTest {

  @Test
  public void testInstrumented() {
    GobblinMetrics gobblinMetrics = GobblinMetrics.get("parent.context");

    State state = new State();
    state.setProp(ConfigurationKeys.METRICS_ENABLED_KEY, Boolean.toString(true));
    state.setProp(Instrumented.METRIC_CONTEXT_NAME_KEY, gobblinMetrics.getName());
    Instrumented instrumented = new Instrumented(state, InstrumentedExtractor.class);

    Assert.assertNotNull(instrumented.getMetricContext());
    Assert.assertTrue(instrumented.getMetricContext().getParent().isPresent());
    Assert.assertEquals(instrumented.getMetricContext().getParent().get(), gobblinMetrics.getMetricContext());

    List<Tag<?>> tags = instrumented.getMetricContext().getTags();
    Map<String, String> expectedTags = new HashMap<String, String>();
    expectedTags.put("construct", Constructs.EXTRACTOR.toString());
    expectedTags.put("class", InstrumentedExtractor.class.getCanonicalName());

    Assert.assertEquals(tags.size(), expectedTags.size());
    for(Tag<?> tag : tags) {
      Assert.assertTrue(expectedTags.containsKey(tag.getKey()));
      Assert.assertEquals(expectedTags.get(tag.getKey()), tag.getValue().toString());
    }
  }

}

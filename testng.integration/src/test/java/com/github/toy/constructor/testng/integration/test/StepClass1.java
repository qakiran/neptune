package com.github.toy.constructor.testng.integration.test;

import com.github.toy.constructor.core.api.CreateWith;
import com.github.toy.constructor.core.api.PerformStep;
import com.github.toy.constructor.core.api.ProviderOfEmptyParameters;

@CreateWith(provider = ProviderOfEmptyParameters.class)
public class StepClass1 implements PerformStep<StepClass1> {
}

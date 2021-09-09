package com.github.jvogit.springreactnextjs;

import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.stereotype.Component;

@Component
public class SpringReactNextjsRuntimeWiringConfigurer implements RuntimeWiringConfigurer {

    @Override
    public void configure(RuntimeWiring.Builder builder) {
        builder.scalar(ExtendedScalars.DateTime).build();
    }
}

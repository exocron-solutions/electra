package io.electra.server.rest;

import com.google.common.collect.Sets;
import io.electra.server.rest.resource.DatabaseResource;

import javax.ws.rs.core.Application;
import java.util.Set;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class ElectraApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.newHashSet(DatabaseResource.class);
    }
}

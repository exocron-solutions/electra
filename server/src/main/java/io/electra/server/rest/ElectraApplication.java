package io.electra.server.rest;

import com.google.common.collect.Sets;
import io.electra.server.rest.service.DatabaseResource;

import javax.ws.rs.core.Application;
import java.util.Set;

/**
 * Created by JackWhite20 on 27.11.2017.
 */
public class ElectraApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.newHashSet(DatabaseResource.class);
    }
}

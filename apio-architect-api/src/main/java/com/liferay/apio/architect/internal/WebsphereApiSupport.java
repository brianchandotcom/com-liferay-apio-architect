package com.liferay.apio.architect.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import java.io.InputStream;
import java.net.URL;

/**
 * @author Carlos Sierra Andrés
 */
public class WebsphereApiSupport implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        if (true) {
            Bundle bundle = context.getBundle();

            URL resource = bundle.getResource("/dependencies/javax.annotation-api.jar");

            try (InputStream input = resource.openStream()) {
                _internalBundle = context.installBundle("internal javax.annotation-api.jar", input);
            }
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        try {
            _internalBundle.uninstall();
        }
        catch (BundleException e) {
            e.printStackTrace();
        }
    }

    private Bundle _internalBundle;
}

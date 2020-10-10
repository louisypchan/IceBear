/****************************************************************************
 Copyright (c) 2019 Louis Y P Chen.
 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:
 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ****************************************************************************/
package com.zd.ice.server;

import com.zd.ice.server.impl.DefaultServiceManager;
import com.zd.ice.server.impl.LoggerI;
import com.zd.ice.server.props.IceBoxProperties;
import com.zd.ice.server.util.Ice;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;

import java.util.Map;

public class DefaultIceApplication extends com.zeroc.Ice.Application implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(DefaultIceApplication.class);
    private final ApplicationArguments applicationArguments;
    private final ApplicationContext applicationContext;
    private com.zeroc.Ice.InitializationData initData;
    // state of server
    private boolean running = false;
    // properties
    private IceBoxProperties iceBoxProperties;
    //
    private String applicationName;
    //
    private DefaultServiceManager defaultServiceManager;

    public DefaultIceApplication(ApplicationArguments applicationArguments, ApplicationContext applicationContext) {
        this.applicationArguments = applicationArguments;
        this.applicationContext = applicationContext;
        // init data
        initData = new com.zeroc.Ice.InitializationData();
        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (running) {
                running = false;
                if (this.defaultServiceManager != null) {
                    this.defaultServiceManager.shutdown(null);
                }
            }
        }));
    }

    public DefaultIceApplication build() {
        this.applicationName = String.format("IceBox.Server.%s", StringUtils.isNotBlank(iceBoxProperties.getName()) ? iceBoxProperties.getName() : String.valueOf(System.currentTimeMillis()));
        // print debug info
        if (logger.isDebugEnabled()) {
            for(Map.Entry<String, String> entry : initData.properties.getPropertiesForPrefix("").entrySet()){
                logger.debug(entry.getKey() + " : " + entry.getValue());
            }
        }
        return this;
    }

    public DefaultIceApplication configure(IceBoxProperties iceBoxProperties) {
        logger.info("configure init data from the properties");
        this.iceBoxProperties = iceBoxProperties;
        // use custom logger
        com.zeroc.Ice.Util.setProcessLogger(new LoggerI("zd foundation"));
        initData.properties = com.zeroc.Ice.Util.createProperties();
        // specific default configuration
        initData.properties.setProperty(Ice.BackgroundLocatorCacheUpdates, "1");
        initData.properties.setProperty(Ice.PrintAdapterReady, "1");
        initData.properties.setProperty(Ice.OverrideTimeout, "5000");
        if (StringUtils.isNotBlank(iceBoxProperties.getPrintServicesReady())) {
            initData.properties.setProperty("IceBox.PrintServicesReady", iceBoxProperties.getPrintServicesReady());
        }
        // load orders
        if (StringUtils.isNotBlank(iceBoxProperties.getLoadOrder())) {
            initData.properties.setProperty("IceBox.LoadOrder", iceBoxProperties.getLoadOrder());
        }
        // inherit
        initData.properties.setProperty("IceBox.InheritProperties", iceBoxProperties.getInheritProperties());
        // see if local service is configured, for local development purpose
        if (iceBoxProperties.getServices() != null) {
            for (IceBoxProperties.Service service : iceBoxProperties.getServices()) {
                String entry = service.getEntry();
                String endpoints = service.getEndpoints();
                Assert.notNull(entry, "the entry of service must not be null");
                Assert.notNull(endpoints, "the endpoints of service must not be null");
                String name = "unknown";
                try {
                    Class<?> clz = Class.forName(entry);
                    Class<?> interfaces[] = clz.getInterfaces();
                    if (interfaces.length > 0) {
                        name = String.format("%sPrx", interfaces[0].getName());
                    }
                } catch (ClassNotFoundException e) {
                    logger.error("service configuration error: ", e);
                }
                initData.properties.setProperty(String.format("IceBox.Service.%s", name), entry);
                initData.properties.setProperty(String.format("%s.Endpoints", name), endpoints);
                if (StringUtils.isNotBlank(service.getUseSharedCommunicator())) {
                    initData.properties.setProperty(String.format("IceBox.UseSharedCommunicator.%s", name), service.getUseSharedCommunicator());
                }
            }
        }
        // miscellaneous
        if (iceBoxProperties.getMiscellaneous() != null) {
            for (Map.Entry<String, String> entry : iceBoxProperties.getMiscellaneous().entrySet()){
                initData.properties.setProperty(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    /**
     *  run the server
     */
    public void run() {
        this.running = true;
        System.exit(this.main(this.applicationName, this.applicationArguments.getSourceArgs(), this.initData));
    }


    @Override
    public int run(String[] args) {
        logger.info("Start to run icebox...");
        this.defaultServiceManager = new DefaultServiceManager(applicationContext, communicator(), applicationArguments.getSourceArgs());
        this.running = true;
        return this.defaultServiceManager.run();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!performUnitTest()) {
            this.run();
        }
    }


    private boolean performUnitTest() {
        boolean underUnitTest = true;
        try {
            Class.forName(Ice.UnitTestClassName);
        } catch (ClassNotFoundException e) {
            underUnitTest = false;
        }
        return underUnitTest;
    }

}

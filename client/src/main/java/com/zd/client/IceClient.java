package com.zd.client;

import com.zd.client.props.IceClientProperties;
import com.zd.client.util.Ice;
import com.zeroc.Glacier2.CannotCreateSessionException;
import com.zeroc.Glacier2.PermissionDeniedException;
import com.zeroc.Glacier2.SessionNotExistException;
import com.zeroc.Ice.Communicator;
import com.zeroc.Glacier2.RouterPrx;
import com.zeroc.Ice.Connection;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
public class IceClient {

    private final Logger logger = LoggerFactory.getLogger(IceClient.class);

    private final IceClientProperties iceClientProperties;
    private Communicator communicator;
    // Glacier2 router
    private RouterPrx router;
    // flag to indicate glacier2 session is created or not
    private boolean session = false;

    public IceClient(IceClientProperties iceClientProperties) {
        this.iceClientProperties = iceClientProperties;
        List<String> params = null;
        switch (iceClientProperties.getType()) {
            case Ice.GLACIER2:
                params = useGlacier2();
                break;
            case Ice.REGISTRY:
                params = useRegistry();
                break;
            case Ice.ENDPOINTS:
                params = useEndpoints();
                break;
        }
        if (params != null) {
            if (iceClientProperties.getMiscellaneous() != null) {
                for (Map.Entry<String, String> entry : iceClientProperties.getMiscellaneous().entrySet()){
                    params.add(String.format("--%s=%s", entry.getKey(), entry.getValue()));
                }
            }
            this.communicator = Util.initialize(params.toArray(new String[0]));
        }
        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (communicator != null) {
                communicator.destroy();
            }
        }));
    }

    private List<String> useGlacier2() {
        Assert.notNull(iceClientProperties.getGlacier2(), "Glacier2 configuration must not be null");
        List<String> params = new ArrayList<>();
        params.add(String.format("--Ice.Default.Router=%s", iceClientProperties.getGlacier2().getRouter()));
        return params;
    }

    private List<String> useRegistry() {
        Assert.notNull(iceClientProperties.getLocator(), "the locator of registry must not be null");
        List<String> params = new ArrayList<>();
        params.add(String.format("--Ice.Default.Locator==%s", iceClientProperties.getLocator()));
        return params;
    }

    private List<String> useEndpoints() {
        return new ArrayList<>();
    }

    protected void glacier2SessionHandler() {
        if (!session) {
            // try to destroy the last session if existed
            session = true;
            try {
                router.destroySession();
            } catch (SessionNotExistException e) {
                //ignore error
            }finally {
                createSession();
            }
        }
    }

    private void createSession() {
        try {
            router.createSession(iceClientProperties.getGlacier2().getAppId(), iceClientProperties.getGlacier2().getSecret());
            int timeout = router.getACMTimeout();
            Connection connection = router.ice_getCachedConnection();
            if (timeout > 0) {
                connection.setACM(java.util.OptionalInt.of(timeout),
                        java.util.Optional.of(com.zeroc.Ice.ACMClose.valueOf(iceClientProperties.getGlacier2().getAcm().getClose())),
                        java.util.Optional.of(com.zeroc.Ice.ACMHeartbeat.valueOf(iceClientProperties.getGlacier2().getAcm().getHeartbeat())));
            }
            connection.setCloseCallback(con -> {
               session = false;
            });
        } catch (CannotCreateSessionException e) {
            session = false;
            logger.error(e.reason, e.getMessage());
        } catch (PermissionDeniedException e) {
            session = false;
            logger.error("permission denied");
        }
    }

    private List<ObjectPrx> lookUpService(Class<?> ...classes) {
        List<ObjectPrx> proxyList = new ArrayList<>();
        switch (iceClientProperties.getType()) {
            case Ice.GLACIER2:
                proxyList = lookUpServiceByGlacier2(classes);
                break;
            case Ice.REGISTRY:
                // TODO:
                break;
            case Ice.ENDPOINTS:
                proxyList = lookUpServiceByEndpoints(classes);
                break;
        }
        return  proxyList;
    }


    private List<ObjectPrx> lookUpServiceByGlacier2(Class<?> ...classes) {
        List<ObjectPrx> proxyList = new ArrayList<>();
        if (router == null) {
            router = RouterPrx.uncheckedCast(this.communicator.getDefaultRouter());
        }
        // handler the glacier2 session
        glacier2SessionHandler();
        if (session) {
            Arrays.stream(classes).forEach(it -> {
                ObjectPrx proxy = getProxy(it, this.communicator.stringToProxy(it.getName()));
                proxyList.add(proxy);
            });
        }
        return proxyList;
    }

    private List<ObjectPrx> lookUpServiceByEndpoints(Class<?> ...classes) {
        List<ObjectPrx> proxyList = new ArrayList<>();
        Arrays.stream(classes).forEach(it -> {
            ObjectPrx proxy = getProxy(it,
                    communicator.stringToProxy(String.format("%s:%s", it.getName(), iceClientProperties.getEndpoints().get(it.getSimpleName()))));
            proxyList.add(proxy);
        });
        return proxyList;
    }

    private ObjectPrx getProxy(Class<?> cls, ObjectPrx objectPrx) {
        ObjectPrx proxy = null;
        try {
            Method method = cls.getDeclaredMethod(Ice.LOOKUP, ObjectPrx.class);
            proxy = (ObjectPrx) method.invoke(cls, objectPrx);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("reflect error: {}", e.getMessage());
        }
        return proxy;
    }


    public ObjectPrx call(Class<?> clz) {
        List<ObjectPrx> objectPrxList = lookUpService(clz);
        return objectPrxList.size() > 0 ? objectPrxList.get(0) : null;
    }

    public List<ObjectPrx> call(Class<?> ...classes) {
        return lookUpService(classes);
    }

    public CompletableFuture<List<ObjectPrx>> callAsync(Class<?> ...classes) {
        return CompletableFuture.supplyAsync(() -> call(classes));
    }

    public CompletableFuture<ObjectPrx> callAsync(Class<?> clz) {
        return CompletableFuture.supplyAsync(() -> call(clz));
    }
}

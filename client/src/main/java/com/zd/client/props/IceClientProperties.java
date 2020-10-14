package com.zd.client.props;

import com.zd.client.util.Ice;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

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
@ConfigurationProperties(prefix = "ice.client")
public class IceClientProperties {
    /**
     * enable discovery locator plugin or not
     * If turn on, will ignore what defined on locators property
     * #TODO: to support the feature as soon as possible
     */
    private boolean enableDiscoveryLocator = false;

    /**
     *  by default, will use glacier2 to forward the request to registry and lookup
     *  the corresponding service to serve
     */
    private String type = Ice.GLACIER2;
    /**
     * glacier configuration
     */
    private Glacier2 glacier2;
    /**
     * registry configuration
     */
    private String locator;
    /**
     * endpoints configuration
     */
    private Map<String, String> endpoints = new HashMap<>();
    /**
     * Miscellaneous from ice, more details: https://doc.zeroc.com/ice/latest/property-reference/miscellaneous-ice-properties
     * eg: Ice.ACM.Timeout
     */
    private Map<String, String> miscellaneous;

    public boolean isEnableDiscoveryLocator() {
        return enableDiscoveryLocator;
    }

    public void setEnableDiscoveryLocator(boolean enableDiscoveryLocator) {
        this.enableDiscoveryLocator = enableDiscoveryLocator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Glacier2 getGlacier2() {
        return glacier2;
    }

    public void setGlacier2(Glacier2 glacier2) {
        this.glacier2 = glacier2;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public Map<String, String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<String, String> endpoints) {
        this.endpoints = endpoints;
    }

    public Map<String, String> getMiscellaneous() {
        return miscellaneous;
    }

    public void setMiscellaneous(Map<String, String> miscellaneous) {
        this.miscellaneous = miscellaneous;
    }

    public static class Glacier2 {
        private String router = null;
        private String appId = null;
        private String secret = null;
        private ACM acm = new ACM();

        public String getRouter() {
            return router;
        }

        public void setRouter(String router) {
            this.router = router;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public ACM getAcm() {
            return acm;
        }

        public void setAcm(ACM acm) {
            this.acm = acm;
        }
    }

    public static class ACM {
        private int close = 3;
        private int heartbeat = 3;

        public int getClose() {
            return close;
        }

        public void setClose(int close) {
            this.close = close;
        }

        public int getHeartbeat() {
            return heartbeat;
        }

        public void setHeartbeat(int heartbeat) {
            this.heartbeat = heartbeat;
        }
    }
}

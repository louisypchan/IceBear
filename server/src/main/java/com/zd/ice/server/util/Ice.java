package com.zd.ice.server.util;
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
public class Ice {
    /**
     * see https://doc.zeroc.com/ice/3.7/property-reference/miscellaneous-ice-properties for details
     */
    public final static String UnitTestClassName = "org.junit.Test";
    /**
     * If num is set to 0 (the default), an invocation on an indirect proxy whose endpoints are older than the configured locator cache timeout triggers a locator cache update;
     * the run time delays the invocation until the new endpoints are returned by the locator.
     * If num is set to a value larger than 0, an invocation on an indirect proxy with expired endpoints still triggers a locator cache update, but the update is performed in the background,
     * and the run time uses the expired endpoints for the invocation. This avoids delaying the first invocation that follows expiry of a cache entry.
     */
    public final static String BackgroundLocatorCacheUpdates = "Ice.BackgroundLocatorCacheUpdates";
    /**
     * If num is set to a value larger than 0, an object adapter prints "adapter_name ready" on standard output after activation is complete.
     * This is useful for scripts that need to wait until an object adapter is ready to be used.
     */
    public final static String PrintAdapterReady = "Ice.PrintAdapterReady";

    public final static String OverrideTimeout = "Ice.Override.Timeout";
}

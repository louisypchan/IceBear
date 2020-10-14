package com.zd.demo.test;

import com.zd.client.IceClient;
import com.zd.demo.DemoClientApplication;
import com.zd.service.Demo.HelloPrx;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Mono;

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
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoClientApplication.class)
public class IceClientTest {

    @Autowired
    private IceClient iceClient;

    @Test
    public void testIceClient() {
        HelloPrx helloPrx = (HelloPrx) iceClient.call(HelloPrx.class);
        System.out.println(helloPrx.sayHello());
    }

    @Test
    public void testIceClientCallAsync() {
        Mono.fromFuture(iceClient.callAsync(HelloPrx.class))
                .switchIfEmpty(Mono.error(new Exception("empty")))
                .cast(HelloPrx.class)
                .flatMap(helloPrx -> {
                    System.out.println(helloPrx.sayHello());
                    return Mono.empty();
                })
                .block();
    }

}

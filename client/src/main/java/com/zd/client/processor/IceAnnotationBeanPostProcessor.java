package com.zd.client.processor;

import com.zd.client.IceClient;
import com.zd.client.anno.Ice;
import com.zeroc.Ice.ObjectPrx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

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
@Component
public class IceAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(IceAnnotationBeanPostProcessor.class);

    private final IceClient iceClient;

    public IceAnnotationBeanPostProcessor(IceClient iceClient) {
        this.iceClient = iceClient;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Ice annotation = declaredField.getAnnotation(Ice.class);
            if (annotation == null) {
                continue;
            }
            declaredField.setAccessible(true);
            ObjectPrx objectPrx = iceClient.call(declaredField.getType());
            try {
                declaredField.set(bean, objectPrx);
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }



}

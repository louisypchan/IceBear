package com.zd.glacier2.impl;

import com.zd.ice.server.impl.DefaultService;
import com.zeroc.Glacier2.PermissionDeniedException;
import com.zeroc.Glacier2.PermissionsVerifier;
import com.zeroc.Ice.Current;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
@Service
public class DefaultPermissionVerifier extends DefaultService implements PermissionsVerifier {

    private final Logger logger = LoggerFactory.getLogger(DefaultPermissionVerifier.class);

    @Override
    public CheckPermissionsResult checkPermissions(String userId, String password, Current current) throws PermissionDeniedException {
        logger.info("userId : {}", userId);
        logger.info("password : {}", password);
        CheckPermissionsResult checkPermissionsResult = new CheckPermissionsResult();
        checkPermissionsResult.returnValue = false;
        checkPermissionsResult.reason = "permissions denied";
        // TODO: to be removed, just for test purpose
        String USER_ID = "louis";
        String PASSWORD = "louis";
        if (USER_ID.equals(userId) && PASSWORD.equals(password)) {
            checkPermissionsResult.returnValue = true;
            checkPermissionsResult.reason = "";
        }
        return checkPermissionsResult;
    }
}

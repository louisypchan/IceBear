[["java:package:com.zd.service"]]

#pragma once

module Demo {
    interface Hello {
        idempotent string sayHello();
    }
}
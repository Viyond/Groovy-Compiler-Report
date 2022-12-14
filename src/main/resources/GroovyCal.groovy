package org.wuqi.javacompiler.Cal

import org.wuqi.javacompiler.Cal

class GroovyCal implements Cal{
    @Override
    int cal(int a, int b) {
        (a*b*a) - (b+a)
    }
}
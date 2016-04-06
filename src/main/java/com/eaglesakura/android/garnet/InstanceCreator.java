package com.eaglesakura.android.garnet;

interface InstanceCreator {
    Object newInstance(Object dst);

    void initialize(Object instance, Object dst);
}

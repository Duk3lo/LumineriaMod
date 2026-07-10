package org.astral.lumineriabase.platform;

import java.util.ServiceLoader;

public class Services {
    public static final IPlatformHelper PLATFORM = ServiceLoader.load(IPlatformHelper.class)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("No se encontró IPlatformHelper"));
}
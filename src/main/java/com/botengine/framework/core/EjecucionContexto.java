package com.botengine.framework.core;

/**
 * Mantiene los datos técnicos de la ejecución actual.
 *
 * Permite que el WebDriver conozca el nombre de la
 * transacción sin acoplarlo al portal.
 */
public final class EjecucionContexto {

    private static final InheritableThreadLocal<Datos> CONTEXTO =
            new InheritableThreadLocal<>();

    private EjecucionContexto() {
    }

    public static void iniciar(
            String uuid,
            String nombreVideo) {

        CONTEXTO.set(
                new Datos(
                        uuid,
                        nombreVideo
                )
        );
    }

    public static String uuid() {

        Datos datos =
                CONTEXTO.get();

        return datos == null
                ? null
                : datos.uuid();
    }

    public static String nombreVideo() {

        Datos datos =
                CONTEXTO.get();

        return datos == null
                ? null
                : datos.nombreVideo();
    }

    public static void finalizar() {

        CONTEXTO.remove();
    }

    private record Datos(
            String uuid,
            String nombreVideo) {
    }
}
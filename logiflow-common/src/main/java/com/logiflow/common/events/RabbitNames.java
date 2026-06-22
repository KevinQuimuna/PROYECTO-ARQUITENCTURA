package com.logiflow.common.events;

public final class RabbitNames {

    public static final String EXCHANGE = "logiflow.events";

    public static final String RK_PEDIDO_CREADO = "pedido.creado";
    public static final String RK_PEDIDO_CANCELADO = "pedido.cancelado";
    public static final String RK_ENVIO_ASIGNADO = "envio.asignado";
    public static final String RK_PEDIDO_ENTREGADO = "pedido.entregado";
    public static final String RK_POSICION_ACTUALIZADA = "posicion.actualizada";

    private RabbitNames() {}
}

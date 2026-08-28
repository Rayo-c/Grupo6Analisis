package com.quickbite.quejas.service;

import com.quickbite.quejas.model.TipoNotificacion;

/** CU16 - Comunicacion Web Service. */
public interface NotificacionService {
    void enviar(String destinatario, TipoNotificacion tipo, String detalle, Long quejaId);
}

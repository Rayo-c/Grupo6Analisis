package com.quickbite.quejas.service.impl;

import com.quickbite.quejas.model.BitacoraNotificacion;
import com.quickbite.quejas.model.EstadoNotificacion;
import com.quickbite.quejas.model.TipoNotificacion;
import com.quickbite.quejas.repository.BitacoraNotificacionRepository;
import com.quickbite.quejas.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * CU16 - Comunicacion Web Service.
 * Envia la notificacion (correo) y registra el resultado en la bitacora (AN02 No.17 si falla).
 * El envio es best-effort: un fallo aqui nunca debe interrumpir el flujo del caso de uso que lo origino.
 * Se ejecuta de forma asincrona (@Async) para que el endpoint que lo invoca responda de inmediato,
 * sin esperar a que termine la conexion SMTP (que puede tardar varios segundos).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionServiceImpl implements NotificacionService {

    private final JavaMailSender mailSender;
    private final BitacoraNotificacionRepository bitacoraRepository;

    @Override
    @Async
    public void enviar(String destinatario, TipoNotificacion tipo, String detalle, Long quejaId) {
        EstadoNotificacion estado;
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destinatario);
            mensaje.setSubject(asunto(tipo));
            mensaje.setText(detalle);
            mailSender.send(mensaje);
            estado = EstadoNotificacion.ENVIADO;
        } catch (Exception ex) {
            // FA01 CU16 - Web Service no disponible. AN02 No.17.
            log.warn("Fallo el envio de notificacion tipo {} a {}: {}", tipo, destinatario, ex.getMessage());
            estado = EstadoNotificacion.FALLIDO;
        }

        BitacoraNotificacion registro = BitacoraNotificacion.builder()
                .destinatario(destinatario)
                .tipo(tipo)
                .estado(estado)
                .quejaId(quejaId)
                .detalle(detalle)
                .build();
        bitacoraRepository.save(registro);
    }

    private String asunto(TipoNotificacion tipo) {
        return switch (tipo) {
            case CONFIRMACION_REGISTRO -> "Hemos recibido tu queja";
            case VERIFICACION_CUENTA -> "Verifica tu cuenta";
            case RECUPERACION_PASSWORD -> "Recupera tu contrasena";
            case ASIGNACION_QUEJA -> "Nueva queja asignada";
            case ACTUALIZACION_ESTADO -> "Actualizacion de tu queja";
            case RESOLUCION_QUEJA -> "Tu queja fue resuelta";
            case RECORDATORIO_CALIFICACION -> "Cuentanos tu experiencia";
            case ALERTA_INSATISFACCION -> "Alerta: calificacion baja recibida";
            case ALERTA_ESCALAMIENTO -> "Queja escalada";
            case REAPERTURA_QUEJA -> "Una queja fue reabierta";
            case CREDENCIALES_PERSONAL -> "Tus credenciales de acceso";
        };
    }
}
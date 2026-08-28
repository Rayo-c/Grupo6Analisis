package com.quickbite.quejas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita el envio asincrono de notificaciones (CU16), para que el API
 * responda de inmediato sin esperar a que termine la conexion SMTP.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
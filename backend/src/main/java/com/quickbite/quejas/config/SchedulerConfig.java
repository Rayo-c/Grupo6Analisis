package com.quickbite.quejas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Habilita los procesos automaticos: cierre por inactividad (CU11) y escalamiento por SLA (CU12/RN05). */
@Configuration
@EnableScheduling
public class SchedulerConfig {
}

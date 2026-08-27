<!--
Sync Impact Report
- Cambio de versión: plantilla sin ratificar -> 1.0.0
- Principios materializados en la ratificación inicial:
  - I. Exactitud financiera y trazabilidad inmutable
  - II. Seguridad, privacidad y mínimo privilegio
  - III. Límites arquitectónicos y evolución incremental
  - IV. Especificación y trazabilidad de contratos
  - V. Pruebas y evidencia reproducible
- Secciones añadidas: Restricciones de producto y tecnología; Flujo de entrega y controles
  de calidad
- Secciones eliminadas: ninguna
- Seguimiento completado el 2026-08-26: se reevaluó el Constitution Check de
  specs/001-development-harness/plan.md antes de implementar; el diseño Phase 0/1 quedó
  conforme y sin excepciones abiertas
-->
# InvestingTracker Constitution

## Core Principles

### I. Exactitud financiera y trazabilidad inmutable (NO NEGOCIABLE)

Todo importe persistido o calculado DEBE usar una representación decimal exacta
(`BigDecimal` en el backend; nunca `double` ni `float`), una moneda o unidad explícita y
una política de escala y redondeo definida por el caso de uso. Cada cambio de saldo,
deuda, participación, costo promedio o rendimiento DEBE poder reconstruirse a partir de
transacciones y eventos identificables.

Una corrección NO DEBE borrar ni sobrescribir silenciosamente la historia financiera;
DEBE registrarse como reversión, ajuste o nueva versión enlazada, con actor, instante y
motivo. Las operaciones concurrentes que afecten saldos o participaciones DEBEN declarar
sus invariantes y protegerlas mediante garantías atómicas, control de concurrencia o
restricciones persistentes verificadas. Los precios y cálculos estimados DEBEN indicar
fuente, instante y carácter estimado, y DEBEN permitir una corrección manual auditable.

Razón: una aplicación financiera solo es útil si sus cifras son precisas, explicables
y recuperables incluso después de correcciones o accesos simultáneos.

### II. Seguridad, privacidad y mínimo privilegio (NO NEGOCIABLE)

Cada caso de uso DEBE autenticar y autorizar en el servidor el acceso al usuario, wallet,
cuenta y transacción afectados. Las wallets y cuentas DEBEN ser privadas por defecto; la
visibilidad pública, las invitaciones y cada permiso compartido requieren consentimiento
explícito y el rol mínimo necesario. La interfaz NO DEBE tratar ocultamiento visual,
estado local ni datos del cliente como autorización.

Credenciales y sesiones DEBEN permanecer en cookies gestionadas por el backend con
`HttpOnly`, `Secure` y `SameSite` apropiado. Secretos, contraseñas, OTP, tokens, cookies y
datos personales o financieros NO DEBEN aparecer en código cliente, repositorio, URLs,
logs, analítica, documentación, fixtures ni capturas. Los ejemplos DEBEN usar datos
sintéticos. Los flujos de registro, autenticación y recuperación DEBEN evitar revelar si
una cuenta existe mediante estado, cuerpo, esquema, tiempos de espera o entrega del
proveedor. Las búsquedas públicas DEBEN usar identificadores expresamente publicables,
como el nombre de usuario, y nunca exponer correo o teléfono por defecto.

Razón: InvestingTracker concentra información financiera y social sensible; una fuga
o autorización débil puede causar daño aunque los cálculos sean correctos.

### III. Límites arquitectónicos y evolución incremental

El backend DEBE mantener dependencias hacia el núcleo: adaptadores REST -> puertos de
entrada -> aplicación -> dominio, mientras infraestructura implementa puertos de salida.
El dominio DEBE ser determinista y libre de Spring, HTTP, persistencia y proveedores. Los
DTO de transporte, entidades Mongo, clientes Redis y tipos de proveedor DEBEN mapearse en
sus adaptadores y NO DEBEN convertirse accidentalmente en contratos del núcleo.

El frontend DEBE mantener `core` para infraestructura transversal, `domain` para conceptos
puros compartidos, `shell` para composición, `shared` para piezas agnósticas y cada
`feature` como propietaria de sus páginas, estado, datos y modelos específicos. Una
feature NO DEBE importar los detalles internos de otra. El servidor sigue siendo la
fuente de verdad para el estado financiero y de autorización.

Las rutas WebFlux DEBEN conservar composición no bloqueante: no se permite `block()` ni
suscripciones internas para trabajo que afecte la corrección. El código nuevo DEBE seguir
los límites objetivo. La deuda existente NO constituye precedente y solo se migra en
cambios acotados, con comportamiento caracterizado y preservado; una feature NO DEBE
mezclarse con una reorganización general del repositorio.

Razón: límites explícitos permiten evolucionar frontend y backend sin acoplar reglas
financieras a frameworks, transportes o proveedores.

### IV. Especificación y trazabilidad de contratos

Toda feature DEBE partir de una especificación revisable que defina resultado para el
usuario, requisitos, reglas de negocio, escenarios de aceptación, casos límite, propiedad
de datos, implicaciones de privacidad y alcance excluido antes de descomponer tareas o
implementar. Las ambigüedades que cambien comportamiento, seguridad, datos o alcance DEBEN
resolverse o diferirse explícitamente fuera de la entrega.

Cada requisito implementado DEBE ser trazable a uno o más escenarios y a evidencia de
verificación. Los planes DEBEN ejecutar un Constitution Check antes de la investigación y
repetirlo después del diseño. Los cambios de API pública, esquemas, eventos o formatos de
persistencia DEBEN documentar compatibilidad y migración, y coordinarse entre backend,
frontend y la especificación de la feature. No se implementará comportamiento de producto
que carezca de requisito o decisión aprobada.

Razón: la trazabilidad impide que supuestos implícitos se conviertan en reglas
financieras o contratos incompatibles.

### V. Pruebas y evidencia reproducible (NO NEGOCIABLE)

Todo cambio de comportamiento DEBE incluir sus pruebas en la misma entrega. Un defecto
DEBE reproducirse primero con una prueba fallida. Solo puede omitirse esa prueba cuando el
fallo no sea reproducible de forma determinista; en tal caso, la entrega DEBE registrar la
causa y una evidencia alternativa. Como mínimo, una feature cubre el camino feliz, un caso
límite y un error esperado. Los cambios financieros DEBEN probar precisión y redondeo; los
que modifican saldos o participaciones DEBEN probar concurrencia o documentar y verificar
la garantía adoptada. Los flujos sensibles DEBEN probar autorización, aislamiento entre
usuarios y ausencia de filtraciones observables.

Las pruebas DEBEN ubicarse en el límite adecuado: unitarias para dominio y orquestación,
de controlador o componente para contratos de interfaz, e integración para persistencia,
transacciones, concurrencia y wiring real. Tests, historias y fixtures NO DEBEN llamar
servicios reales de correo, SMS, autenticación o datos. Se ejecutan primero los controles
enfocados y después los controles completos aplicables al área y al riesgo. Un control
obligatorio no ejecutado o fallido bloquea la entrega salvo excepción temporal aprobada.
Nadie DEBE declarar una entrega completa sin salida reciente de comandos o evidencia
directa y reproducible para cambios exclusivamente documentales.

Razón: el software financiero necesita evidencia verificable, no confianza basada en
inspección informal o resultados obsoletos.

## Restricciones de producto y tecnología

- La experiencia DEBE ser comprensible para personas sin conocimientos financieros,
  conservar términos consistentes y ofrecer valores predeterminados seguros sin ocultar
  el detalle necesario para usuarios avanzados.
- Las experiencias soportadas DEBEN ser responsivas en web y móvil y cumplir WCAG 2.2 AA:
  teclado, foco visible, nombres y relaciones accesibles, orden lógico, contraste, señales
  no basadas solo en color y respeto a movimiento reducido. Los temas soportados,
  incluidos el claro y el oscuro, DEBEN preservar estos requisitos.
- Java, Spring Boot, Gradle, Angular, TypeScript, npm y sus scripts configurados en cada
  repositorio son la fuente de verdad. Cambiar una dependencia, versión o herramienta
  requiere una necesidad documentada y revisión de seguridad, mantenimiento y migración.
- WebFlux, MongoDB, Redis, proveedores de entrega y fuentes de precios DEBEN permanecer
  detrás de adaptadores explícitos. Una API bloqueante solo puede aislarse en
  infraestructura mediante un scheduler o frontera asíncrona declarada.
- La entrada manual es válida y autoritativa. Vincular cuentas bancarias, hacer scraping o
  incorporar una fuente externa nueva requiere una especificación aprobada que cubra
  consentimiento, licencia, disponibilidad, degradación, procedencia y corrección manual.
- Los errores NO DEBEN silenciarse ni quedar en bloques `catch` vacíos. Los efectos que
  importen para la corrección DEBEN permanecer observables, y el logging estructurado DEBE
  usar redacción o identificadores de correlación no reversibles.

## Flujo de entrega y controles de calidad

1. **Delimitar**: inspeccionar el estado del área, conservar cambios ajenos y leer la
   especificación y guías propietarias aplicables antes de diseñar.
2. **Especificar y planear**: convertir el objetivo en requisitos y escenarios verificables;
   registrar riesgos financieros, de seguridad, privacidad, accesibilidad, datos y
   migración; ejecutar el Constitution Check.
3. **Diseñar la evidencia**: asignar cada requisito a pruebas o verificaciones y definir
   contratos, invariantes y datos sintéticos antes de implementar comportamiento.
4. **Implementar de forma acotada**: respetar propiedad y dependencias, evitar limpieza no
   relacionada y actualizar en el mismo cambio los contratos y documentos que hayan
   cambiado.
5. **Verificar**: ejecutar controles enfocados y luego la validación completa aplicable. Se
   usan los comandos federados del workspace cuando existan; npm y Gradle siguen siendo
   las autoridades locales de ejecución.
6. **Revisar y entregar**: revisar el diff final, confirmar trazabilidad y cumplimiento,
   registrar comandos y resultados recientes, y declarar supuestos, limitaciones y riesgos
   restantes.

Una excepción a un principio o control DEBE registrar propietario, alcance, justificación,
riesgo, control compensatorio, fecha de vencimiento y condición de eliminación. La
excepción requiere aprobación explícita y NO crea precedente ni permite aumentar deuda
silenciosamente.

## Governance

Esta constitución prevalece sobre guías, prácticas y convenciones en conflicto. Las guías
locales pueden imponer reglas más estrictas y conservan la autoridad sobre detalles de
arquitectura, diseño y ejecución, pero NO pueden debilitar estos principios. Una feature o
decisión que no pueda cumplir DEBE corregirse o tramitar una excepción antes de continuar.

Toda enmienda DEBE incluir el cambio propuesto, su motivo, un Sync Impact Report, los
artefactos afectados y un plan de migración cuando altere obligaciones existentes. La
aprobación explícita del responsable del proyecto o mantenedor designado es obligatoria.
La versión sigue semantic versioning: MAJOR para eliminar o redefinir de forma incompatible
un principio o regla de gobierno; MINOR para añadir un principio, sección u obligación
material; PATCH para aclaraciones sin cambio normativo. Las fechas usan formato ISO
`YYYY-MM-DD`.

Cada especificación, plan y lista de tareas DEBE comprobar esta constitución. Cada revisión
de entrega DEBE confirmar sus gates antes de integrar cambios. Las excepciones abiertas se
revisan en cada entrega afectada y deben cerrarse al cumplirse su condición de eliminación.
El cumplimiento se evalúa mediante evidencia en especificaciones, pruebas, resultados de
validación y revisión del diff; la aprobación no se presume por el solo hecho de que el
código compile.

**Version**: 1.0.0 | **Ratified**: 2026-08-25 | **Last Amended**: 2026-08-25

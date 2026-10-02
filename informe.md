# Laboratorio 2 – Reglas de Negocio con Spring Boot y Drools
**Curso:** Fundamentos de Sistemas de Información – Universidad de Antioquia, 2026
**Estudiante(s):** _[nombre y documento]_
**Caso:** Reglas de negocio para una aerolínea

## 1. Objetivo
Implementar con Spring Boot 3.2.5, Java 17 y Drools 7.74.1 un conjunto de 10 reglas (`.drl`) que gestionan beneficios, restricciones y asignaciones para pasajeros de una aerolínea, separando la lógica de negocio (reglas) del código de la aplicación.

## 2. Arquitectura
Se sigue la arquitectura de la guía: `airline_rules.drl` → `KieFileSystem/KieBuilder` → `KieContainer` (bean en `DroolsConfig`) → `KieSession` (creada por petición en `AirlineEvaluationService`) → resultado.

| Capa | Clase | Función |
|---|---|---|
| config | `DroolsConfig` | Compila el DRL y expone el `KieContainer` |
| model | `Passenger`, `Flight`, `ExitSeat`, `AirlineRequest`, `AirlineResponse` | Hechos de entrada/salida |
| service | `AirlineEvaluationService` | Inserta hechos, ejecuta `fireAllRules()` y registra las reglas disparadas |
| controller | `AirlineController` | `POST /airline/api/evaluate` |

**Hechos:** `Passenger` (edad, membresía, preferencia de asiento, niños, peso del equipaje y campos de resultado), `Flight` (retraso en minutos y duración en horas) y `ExitSeat` (asiento de salida de emergencia y disponibilidad).

## 3. Reglas implementadas
| # | Regla | Condición | Acción |
|---|---|---|---|
| 1 | UpgradeToBusinessClassForFrequentFlyersWithDelays | Gold/Platinum, retraso > 60 min y elegible | `upgradedToBusiness = true` |
| 2 | PriorityCheckInForSeniors | Edad > 65 | `priorityCheckIn = true` |
| 3 | DiscountForLightLuggage | Equipaje < 10 kg | `discountPercent = 10` |
| 4 | DenyUpgradeForOverweightLuggage | Equipaje > 23 kg | `eligibleForUpgrade = false` |
| 5 | AssignEmergencyExitSeatToYoungAdults | 18–40 años, preferencia "Any", asiento disponible | Asigna asiento y lo marca ocupado |
| 6 | CompensationForExtremeDelays | Retraso > 180 min | `compensation = 200` |
| 7 | ExtraLoyaltyPointsForLongFlights | Membresía ≠ Basic, vuelo > 5 h | +500 puntos |
| 8 | RestrictLuggageOnShortFlights | Equipaje > 15 kg, vuelo < 2 h | `luggageAllowed = false` |
| 9 | VipLoungeAccessForPlatinumMembers | Membresía Platinum | `vipLoungeAccess = true` |
| 10 | PreferentialSeatForFamilies | Con niños y sin preferencia | Asiento preferencial familiar |

## 4. Decisiones de diseño
- **Interacción entre reglas 1 y 4:** Drools evalúa las condiciones al insertar los hechos, no al momento de disparar. Por eso la regla 4 tiene `salience 100` y usa `modify`, que fuerza la reevaluación y cancela la activación de la regla 1. Con un simple `setEligibleForUpgrade(false)` la regla 1 se dispararía igualmente.
- **Regla 5:** `modify($s){ setAvailable(false) }` marca el asiento como ocupado, de modo que no se asigne dos veces.
- **`@PropertyReactive`:** el `modify` solo reactiva las reglas que dependen de la propiedad modificada, evitando que otras reglas (p. ej. la 7, que suma puntos) se disparen dos veces.
- **Trazabilidad:** un `AgendaEventListener` devuelve la lista de reglas disparadas en cada respuesta.

## 5. Pruebas
Ejecutar la aplicación (`mvn spring-boot:run`) y enviar a `http://localhost:8080/airline/api/evaluate` (POST, `Content-Type: application/json`).

**Prueba A – Reglas 1, 7, 9 (Platinum, retraso 90 min, vuelo 6 h):**
```json
{ "passenger": {"name":"Ana","age":30,"membership":"Platinum","seatPreference":"None","luggageWeightKg":12},
  "flight": {"code":"AV100","delayMinutes":90,"durationHours":6},
  "emergencyExitSeatAvailable": false }
```
Esperado: `upgradedToBusiness: true`, `loyaltyPoints: 500`, `vipLoungeAccess: true`.

**Prueba B – Regla 4 bloquea la 1 (Gold, equipaje 25 kg):** cambiar `membership` a `Gold` y `luggageWeightKg` a 25 → `eligibleForUpgrade: false`, `upgradedToBusiness: false`.

**Prueba C – Reglas 2 y 3:** `age: 70`, `luggageWeightKg: 5` → `priorityCheckIn: true`, `discountPercent: 10`.

**Prueba D – Regla 5:** `age: 25`, `seatPreference: "Any"`, `emergencyExitSeatAvailable: true` → `assignedSeat: "14A"`.

**Prueba E – Reglas 6 y 7:** `delayMinutes: 200`, `durationHours: 6`, `membership: "Silver"` → `compensation: 200`, `loyaltyPoints: 500`.

**Prueba F – Regla 8:** `durationHours: 1.5`, `luggageWeightKg: 18` → `luggageAllowed: false`.

**Prueba G – Regla 10:** `travelingWithChildren: true`, `seatPreference: "None"` → `assignedSeat: "Asiento preferencial familiar"`.

También se incluyen pruebas automáticas en `AirlineRulesTest` (`mvn test`).

_[Pegar aquí capturas de Postman / salida de `mvn test` por cada prueba]_

## 6. Conclusiones
_[Redactar: ventajas de separar reglas del código, facilidad de cambiar umbrales sin tocar Java, y dificultades encontradas, p. ej. la interacción entre reglas 1 y 4.]_

## 7. Repositorio
_[URL de GitHub]_

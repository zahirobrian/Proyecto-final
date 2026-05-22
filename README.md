# MiauMission Game 🐱

| Campo | Datos |
|---|---|
| **Integrantes** | Bernal Ramírez Brian Ricardo / Saavedra Mata Karla Sofía |
| **Boletas** | 2023630387 / 2019300048 |
| **Asignatura** | Desarrollo de Aplicaciones Móviles Nativas |
| **Institución** | Instituto Politécnico Nacional — ESCOM |

---

## Descripción

**MiauMission** es un videojuego móvil en el que el jugador asume el rol de un rescatista de gatos en la calle. El objetivo es identificar qué gatos necesitan ayuda y rescatarlos resolviendo operaciones matemáticas en tiempo limitado.

## Instalación

1. Clona el repo y abre `MiauMission/` en Android Studio
2. Configura **JDK 17**
3. **Run 'app'**

## Personajes jugables

| Personaje | Descripción |
|---|---|
| 🐱 **Cheeto** | Gato naranja atigrado |
| 🐱 **Pelusa** | Gato gris atigrado |
| 🐱 **Mantecada** | Gato blanco/dorado |
| 🐱 **Waffle** | Gato siamés beige |
| 🐱 **Lui** | Gato atigrado clásico (nuevo) |

## Mecánica

- Aparecen gatos en la calle — con o sin collar
- Solo los **sin collar** pueden ser rescatados
- Al tocar un gato sin collar → aparece desafío matemático
- **3 tipos de pregunta:** respuesta abierta, opción múltiple, verdadero/falso

## Niveles

| Nivel | Operación | Tiempo | Meta |
|---|---|---|---|
| 1 | Sumas | 60s | 5 gatos |
| 2 | Restas | 55s | 6 gatos |
| 3 | Multiplicaciones | 50s | 7 gatos |

## Tecnologías

- Kotlin + Android Studio
- ViewBinding
- CountDownTimer para el cronómetro
- Pixel art generado con Python/Pillow
- Arquitectura simple Activity-based

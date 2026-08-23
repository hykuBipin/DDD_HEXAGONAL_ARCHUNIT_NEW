from PIL import Image, ImageDraw, ImageFont
import os
import math

# Render HD diagram PNGs for PowerPoint slides & GitHub documentation

def create_image(width=1600, height=900, bg_color=(15, 23, 42)):
    img = Image.new("RGBA", (width, height), bg_color + (255,))
    draw = ImageDraw.Draw(img)
    return img, draw

# Load default font
try:
    title_font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 34)
    subtitle_font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 22)
    box_title_font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 24)
    box_body_font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 18)
    small_font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 15)
except Exception:
    title_font = subtitle_font = box_title_font = box_body_font = small_font = ImageFont.load_default()

def draw_rounded_rect(draw, box, radius, fill, outline, width=3):
    x1, y1, x2, y2 = box
    draw.rounded_rectangle([x1, y1, x2, y2], radius=radius, fill=fill, outline=outline, width=width)

def draw_arrow(draw, start, end, color=(59, 130, 246), width=4):
    x1, y1 = start
    x2, y2 = end
    draw.line([x1, y1, x2, y2], fill=color, width=width)
    angle = math.atan2(y2 - y1, x2 - x1)
    arrow_len = 16
    arrow_angle = math.pi / 6
    xa = x2 - arrow_len * math.cos(angle - arrow_angle)
    ya = y2 - arrow_len * math.sin(angle - arrow_angle)
    xb = x2 - arrow_len * math.cos(angle + arrow_angle)
    yb = y2 - arrow_len * math.sin(angle + arrow_angle)
    draw.polygon([(x2, y2), (xa, ya), (xb, yb)], fill=color)

# =============================================================
# 1. DIAGRAM DDD ARCHITECTURE
# =============================================================
img1, draw1 = create_image(1600, 900)

# Main background sections
draw_rounded_rect(draw1, (80, 80, 1520, 240), 16, (30, 41, 59), (71, 85, 105), 3)
draw1.text((100, 95), "INFRASTRUCTURE & ADAPTER LAYER (com.satellite.infrastructure)", font=box_title_font, fill=(148, 163, 184))

draw_rounded_rect(draw1, (100, 140, 520, 215), 10, (15, 23, 42), (59, 130, 246), 2)
draw1.text((115, 160), "SatelliteController.java (REST API)", font=box_body_font, fill=(248, 250, 252))

draw_rounded_rect(draw1, (560, 140, 1000, 215), 10, (15, 23, 42), (16, 185, 129), 2)
draw1.text((575, 160), "SatelliteJpaAdapter.java (JPA / H2)", font=box_body_font, fill=(248, 250, 252))

draw_rounded_rect(draw1, (1040, 140, 1500, 215), 10, (15, 23, 42), (245, 158, 11), 2)
draw1.text((1055, 160), "EventPublisherAdapter (Spring Event)", font=box_body_font, fill=(248, 250, 252))

# Application Layer
draw_rounded_rect(draw1, (80, 280, 1520, 440), 16, (30, 41, 59), (71, 85, 105), 3)
draw1.text((100, 295), "APPLICATION LAYER (com.satellite.application.service)", font=box_title_font, fill=(245, 158, 11))
draw_rounded_rect(draw1, (100, 340, 1500, 415), 10, (15, 23, 42), (245, 158, 11), 2)
draw1.text((120, 360), "Services: LaunchSatelliteService.java | UpdateTelemetryService.java | GetSatelliteService.java", font=box_body_font, fill=(248, 250, 252))

# Domain Layer (Highlight Core)
draw_rounded_rect(draw1, (80, 480, 1520, 840), 20, (30, 58, 138), (59, 130, 246), 4)
draw1.text((100, 495), "DOMAIN CORE (com.satellite.domain — Pure Java, 0 Framework Dependencies)", font=box_title_font, fill=(248, 250, 252))

# Sub-components inside Domain
draw_rounded_rect(draw1, (100, 550, 520, 810), 12, (15, 23, 42), (59, 130, 246), 2)
draw1.text((120, 570), "AGGREGATE ROOT", font=box_title_font, fill=(59, 130, 246))
draw1.text((120, 615), "• Satellite.java\n• State Machine Rules\n• Business Invariants\n• Event Collection", font=box_body_font, fill=(248, 250, 252))

draw_rounded_rect(draw1, (560, 550, 1020, 810), 12, (15, 23, 42), (16, 185, 129), 2)
draw1.text((580, 570), "VALUE OBJECTS", font=box_title_font, fill=(16, 185, 129))
draw1.text((580, 615), "• Orbit.java (LEO/GEO Rules)\n• Telemetry.java (Anomaly Rules)\n• SatelliteId.java (UUID Record)\n• SatelliteStatus.java (Enum)", font=box_body_font, fill=(248, 250, 252))

draw_rounded_rect(draw1, (1060, 550, 1500, 810), 12, (15, 23, 42), (245, 158, 11), 2)
draw1.text((1080, 570), "DOMAIN EVENTS", font=box_title_font, fill=(245, 158, 11))
draw1.text((1080, 615), "• SatelliteLaunchedEvent\n• AnomalyDetectedEvent\n• Immutable Domain Facts", font=box_body_font, fill=(248, 250, 252))

# Arrows connecting layers
draw_arrow(draw1, (310, 215), (310, 340), (59, 130, 246), 4)
draw_arrow(draw1, (780, 415), (780, 480), (245, 158, 11), 4)

img1.save("/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_ddd.png")

# =============================================================
# 2. DIAGRAM CAR ANALOGY
# =============================================================
img2, draw2 = create_image(1600, 900)

draw2.text((80, 60), "HEXAGONAL ARCHITECTURE CONCEPT: REAL-WORLD CAR ANALOGY", font=title_font, fill=(248, 250, 252))

# Column 1: Inputs
draw_rounded_rect(draw2, (80, 140, 420, 820), 16, (30, 41, 59), (71, 85, 105), 3)
draw2.text((100, 160), "DRIVING ADAPTERS\n(Inputs / Triggers)", font=box_title_font, fill=(59, 130, 246))

draw_rounded_rect(draw2, (100, 260, 400, 400), 10, (15, 23, 42), (59, 130, 246), 2)
draw2.text((120, 310), "Foot Gas Pedal", font=box_title_font, fill=(248, 250, 252))

draw_rounded_rect(draw2, (100, 440, 400, 580), 10, (15, 23, 42), (59, 130, 246), 2)
draw2.text((120, 490), "Cruise Control Button", font=box_title_font, fill=(248, 250, 252))

draw_rounded_rect(draw2, (100, 620, 400, 760), 10, (15, 23, 42), (59, 130, 246), 2)
draw2.text((120, 670), "Mobile App Remote", font=box_title_font, fill=(248, 250, 252))

# Column 2: In Ports
draw_rounded_rect(draw2, (460, 140, 720, 820), 16, (15, 23, 42), (245, 158, 11), 3)
draw2.text((480, 160), "INPUT PORT\n(Contract)", font=box_title_font, fill=(245, 158, 11))
draw_rounded_rect(draw2, (480, 350, 700, 610), 10, (30, 41, 59), (245, 158, 11), 2)
draw2.text((500, 420), "Throttle Socket\nInterface", font=box_title_font, fill=(248, 250, 252))

# Column 3: Core Engine
draw_rounded_rect(draw2, (760, 140, 1140, 820), 20, (6, 95, 70), (16, 185, 129), 4)
draw2.text((780, 160), "CORE ENGINE\n(The Hexagon)", font=box_title_font, fill=(248, 250, 252))
draw2.text((780, 330), "Combustion Rules\n&\nAcceleration Logic\n\n(100% Independent\nof who presses\nthe pedal)", font=box_title_font, fill=(248, 250, 252))

# Column 4: Output Adapters
draw_rounded_rect(draw2, (1180, 140, 1520, 820), 16, (30, 41, 59), (71, 85, 105), 3)
draw2.text((1200, 160), "DRIVEN ADAPTERS\n(Power Outputs)", font=box_title_font, fill=(59, 130, 246))

draw_rounded_rect(draw2, (1200, 260, 1500, 400), 10, (15, 23, 42), (16, 185, 129), 2)
draw2.text((1220, 310), "Gasoline Fuel Tank", font=box_title_font, fill=(248, 250, 252))

draw_rounded_rect(draw2, (1200, 440, 1500, 580), 10, (15, 23, 42), (16, 185, 129), 2)
draw2.text((1220, 490), "Electric Battery", font=box_title_font, fill=(248, 250, 252))

draw_rounded_rect(draw2, (1200, 620, 1500, 760), 10, (15, 23, 42), (16, 185, 129), 2)
draw2.text((1220, 670), "Wheels & Tires", font=box_title_font, fill=(248, 250, 252))

# Arrows
draw_arrow(draw2, (400, 330), (480, 440), (59, 130, 246), 3)
draw_arrow(draw2, (400, 510), (480, 480), (59, 130, 246), 3)
draw_arrow(draw2, (400, 690), (480, 520), (59, 130, 246), 3)

draw_arrow(draw2, (700, 480), (760, 480), (245, 158, 11), 4)
draw_arrow(draw2, (1140, 480), (1200, 330), (16, 185, 129), 3)
draw_arrow(draw2, (1140, 480), (1200, 510), (16, 185, 129), 3)

img2.save("/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_car_analogy.png")

# =============================================================
# 3. DIAGRAM SATELLITE HEXAGONAL CODEBASE (With All Java Classes)
# =============================================================
img3, draw3 = create_image(1600, 900)

draw3.text((80, 50), "HEXAGONAL ARCHITECTURE: SATELLITE CODEBASE CLASS MAPPING", font=title_font, fill=(248, 250, 252))

# Driving Adapters
draw_rounded_rect(draw3, (80, 130, 440, 830), 16, (30, 41, 59), (71, 85, 105), 3)
draw3.text((100, 150), "DRIVING ADAPTERS\n(infrastructure.adapter.in)", font=box_title_font, fill=(59, 130, 246))

draw_rounded_rect(draw3, (100, 250, 420, 510), 10, (15, 23, 42), (59, 130, 246), 2)
draw3.text((120, 270), "SatelliteController.java", font=box_title_font, fill=(248, 250, 252))
draw3.text((120, 320), "• REST API Endpoints\n• LaunchSatelliteRequest.java\n• UpdateTelemetryRequest.java\n• SatelliteResponse.java\n• SatelliteRestMapper.java", font=small_font, fill=(148, 163, 184))

draw_rounded_rect(draw3, (100, 550, 420, 790), 10, (15, 23, 42), (59, 130, 246), 2)
draw3.text((120, 570), "TelemetryConsumer.java", font=box_title_font, fill=(248, 250, 252))
draw3.text((120, 620), "• Kafka / MQTT Consumer\n• Ingests Live Satellite Feeds\n• Maps Stream Payloads\n• Triggers Inbound Ports", font=small_font, fill=(148, 163, 184))

# Ports In
draw_rounded_rect(draw3, (480, 130, 740, 830), 16, (15, 23, 42), (245, 158, 11), 3)
draw3.text((500, 150), "INBOUND PORTS\n(domain.port.in)", font=box_title_font, fill=(245, 158, 11))

draw_rounded_rect(draw3, (500, 250, 720, 440), 10, (30, 41, 59), (245, 158, 11), 2)
draw3.text((515, 270), "LaunchSatelliteUseCase", font=box_body_font, fill=(248, 250, 252))
draw3.text((515, 320), "Interface defining\nlaunch operations", font=small_font, fill=(148, 163, 184))

draw_rounded_rect(draw3, (500, 480, 720, 640), 10, (30, 41, 59), (245, 158, 11), 2)
draw3.text((515, 500), "UpdateTelemetryUseCase", font=box_body_font, fill=(248, 250, 252))
draw3.text((515, 550), "Interface defining\ntelemetry updates", font=small_font, fill=(148, 163, 184))

draw_rounded_rect(draw3, (500, 680, 720, 800), 10, (30, 41, 59), (245, 158, 11), 2)
draw3.text((515, 700), "GetSatelliteUseCase", font=box_body_font, fill=(248, 250, 252))

# Domain Core
draw_rounded_rect(draw3, (780, 130, 1120, 830), 20, (30, 58, 138), (59, 130, 246), 4)
draw3.text((800, 150), "DOMAIN CORE\n(domain.model)", font=box_title_font, fill=(248, 250, 252))
draw3.text((800, 270), "Satellite.java\n(Aggregate Root)\n\nOrbit.java\nTelemetry.java\nSatelliteId.java\nSatelliteStatus.java\n(Value Objects)\n\nSatelliteLaunchedEvent\nAnomalyDetectedEvent\n(Domain Events)", font=box_body_font, fill=(248, 250, 252))

# Driven Adapters
draw_rounded_rect(draw3, (1160, 130, 1520, 830), 16, (30, 41, 59), (71, 85, 105), 3)
draw3.text((1180, 150), "DRIVEN ADAPTERS\n(infrastructure.adapter.out)", font=box_title_font, fill=(16, 185, 129))

draw_rounded_rect(draw3, (1180, 250, 1500, 510), 10, (15, 23, 42), (16, 185, 129), 2)
draw3.text((1200, 270), "SatelliteJpaAdapter.java", font=box_title_font, fill=(248, 250, 252))
draw3.text((1200, 320), "• Implements SatelliteRepository\n• SatelliteJpaEntity.java\n• SatelliteJpaRepository.java\n• SatellitePersistenceMapper.java\n• Relational DB (H2 / Postgres)", font=small_font, fill=(148, 163, 184))

draw_rounded_rect(draw3, (1180, 550, 1500, 790), 10, (15, 23, 42), (16, 185, 129), 2)
draw3.text((1200, 570), "EventPublisherAdapter", font=box_title_font, fill=(248, 250, 252))
draw3.text((1200, 620), "• Implements EventPublisher\n• Spring ApplicationEventPublisher\n• Broadcasts Anomaly Events", font=small_font, fill=(148, 163, 184))

# Connectors
draw_arrow(draw3, (420, 340), (500, 340), (59, 130, 246), 4)
draw_arrow(draw3, (420, 670), (500, 560), (59, 130, 246), 4)

draw_arrow(draw3, (720, 340), (780, 340), (245, 158, 11), 4)
draw_arrow(draw3, (1120, 340), (1180, 340), (16, 185, 129), 4)

img3.save("/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_satellite_hex.png")

# =============================================================
# 4. DIAGRAM DUAL DATABASE ADAPTER SWAPPING (The Hexagonal Superpower!)
# =============================================================
img4, draw4 = create_image(1600, 900)

draw4.text((80, 50), "DEMO: PLUG & PLAY MULTI-DATABASE ADAPTER SWAPPING", font=title_font, fill=(248, 250, 252))
draw4.text((80, 100), "Zero changes to Domain Core when swapping Relational DB (JPA/H2) for NoSQL (MongoDB) or Redis Cache", font=subtitle_font, fill=(148, 163, 184))

# Center: Outbound Port Interface inside Domain
draw_rounded_rect(draw4, (80, 340, 500, 620), 20, (30, 58, 138), (59, 130, 246), 4)
draw4.text((100, 360), "OUTBOUND PORT INTERFACE\n(domain.port.out)", font=box_title_font, fill=(248, 250, 252))
draw_rounded_rect(draw4, (100, 440, 480, 590), 10, (15, 23, 42), (245, 158, 11), 2)
draw4.text((120, 460), "public interface SatelliteRepository {\n  Satellite save(Satellite s);\n  Optional<Satellite> findById(SatelliteId id);\n  List<Satellite> findAll();\n}", font=small_font, fill=(248, 250, 252))

# Right Column: Multiple Driven Database Adapters
draw_rounded_rect(draw4, (640, 160, 1520, 830), 16, (30, 41, 59), (71, 85, 105), 3)
draw4.text((660, 180), "INFRASTRUCTURE ADAPTER LAYER (Plug & Play Implementations)", font=box_title_font, fill=(148, 163, 184))

# Database Adapter 1: JPA Relational DB
draw_rounded_rect(draw4, (660, 240, 1060, 790), 14, (15, 23, 42), (16, 185, 129), 3)
draw4.text((680, 260), "ADAPTER 1: RELATIONAL DB", font=box_title_font, fill=(16, 185, 129))
draw4.text((680, 310), "SatelliteJpaAdapter.java\n\n• Implements SatelliteRepository\n• Spring Data JPA + Hibernate\n• SatelliteJpaEntity.java (@Entity)\n• H2 / PostgreSQL / MySQL DB\n• Relational SQL Schema", font=box_body_font, fill=(248, 250, 252))
draw_rounded_rect(draw4, (680, 600, 1040, 760), 10, (6, 95, 70), (16, 185, 129), 2)
draw4.text((700, 640), "🗄️ Relational DB (SQL)\nTables: satellites", font=box_title_font, fill=(248, 250, 252))

# Database Adapter 2: MongoDB Document DB
draw_rounded_rect(draw4, (1100, 240, 1500, 790), 14, (15, 23, 42), (245, 158, 11), 3)
draw4.text((1120, 260), "ADAPTER 2: DOCUMENT DB", font=box_title_font, fill=(245, 158, 11))
draw4.text((1120, 310), "SatelliteMongoAdapter.java\n\n• Implements SatelliteRepository\n• Spring Data MongoDB\n• SatelliteDocument.java (@Document)\n• MongoDB Cluster\n• BSON JSON Documents", font=box_body_font, fill=(248, 250, 252))
draw_rounded_rect(draw4, (1120, 600, 1480, 760), 10, (180, 83, 9), (245, 158, 11), 2)
draw4.text((1140, 640), "🍃 MongoDB (NoSQL)\nCollection: satellites", font=box_title_font, fill=(248, 250, 252))

# Arrows showing implementation of interface
draw_arrow(draw4, (500, 480), (660, 400), (16, 185, 129), 4)
draw_arrow(draw4, (500, 480), (1100, 400), (245, 158, 11), 4)

img4.save("/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_adapter_swapping.png")

print("Successfully generated all 4 HD diagram PNGs!")

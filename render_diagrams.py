from PIL import Image, ImageDraw, ImageFont
import os
import math

# Render HD diagram PNGs inspired by classical Hexagonal & DDD reference diagrams (Herberto Graça & WATA Factory styles)

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
# 1. DIAGRAM 1: HEXAGONAL PRIMARY & SECONDARY ADAPTERS (WATA / Herberto Graça Style)
# =============================================================
img1, draw1 = create_image(1600, 900)

draw1.text((80, 40), "HEXAGONAL ARCHITECTURE: PRIMARY (DRIVING) & SECONDARY (DRIVEN) ADAPTERS", font=title_font, fill=(248, 250, 252))
draw1.text((80, 85), "Modeled after classic Ports & Adapters specification — mapped directly to Satellite Codebase", font=subtitle_font, fill=(148, 163, 184))

# Vertical dividing dashed line
for y in range(130, 850, 20):
    draw1.line([(800, y), (800, y + 10)], fill=(71, 85, 105), width=2)

draw1.text((320, 130), "PRIMARY / DRIVING ADAPTERS (Inputs)", font=box_title_font, fill=(59, 130, 246))
draw1.text((980, 130), "SECONDARY / DRIVEN ADAPTERS (Outputs)", font=box_title_font, fill=(16, 185, 129))

# Outer Hexagon / Infrastructure Layer (Green)
draw_rounded_rect(draw1, (100, 180, 1500, 830), 24, (20, 83, 45), (34, 197, 94), 4)
draw1.text((120, 195), "INFRASTRUCTURE LAYER (com.satellite.infrastructure)", font=box_title_font, fill=(248, 250, 252))

# Middle Layer: Application Layer / Use Cases (Yellow/Orange)
draw_rounded_rect(draw1, (300, 250, 1300, 760), 20, (120, 53, 15), (245, 158, 11), 3)
draw1.text((320, 265), "APPLICATION LAYER (com.satellite.application.service)", font=box_title_font, fill=(248, 250, 252))

# Inner Core: Domain Layer / Entities & Ports (Red/Pink)
draw_rounded_rect(draw1, (500, 320, 1100, 690), 16, (127, 29, 29), (239, 68, 68), 4)
draw1.text((520, 335), "DOMAIN CORE (com.satellite.domain)", font=box_title_font, fill=(248, 250, 252))

# Class details inside Domain Core
draw_rounded_rect(draw1, (520, 380, 1080, 660), 10, (15, 23, 42), (239, 68, 68), 2)
draw1.text((540, 395), "ENTITIES & AGGREGATE ROOT:\n  • Satellite.java (Aggregate Root & State Machine)\nVALUE OBJECTS:\n  • Orbit.java | Telemetry.java | SatelliteId.java\nDOMAIN PORTS (CONTRACTS):\n  • LaunchSatelliteUseCase (In Port)\n  • SatelliteRepository (Out Port)", font=small_font, fill=(248, 250, 252))

# Primary Adapters on Left (Infrastructure)
draw_rounded_rect(draw1, (120, 260, 280, 480), 10, (15, 23, 42), (59, 130, 246), 2)
draw1.text((130, 280), "REST Controller\n(SatelliteController)", font=small_font, fill=(248, 250, 252))
draw1.text((130, 350), "• HTTP POST /GET /PUT\n• LaunchSatelliteRequest\n• SatelliteResponse", font=small_font, fill=(148, 163, 184))

draw_rounded_rect(draw1, (120, 520, 280, 740), 10, (15, 23, 42), (59, 130, 246), 2)
draw1.text((130, 540), "CLI / Stream\n(TelemetryConsumer)", font=small_font, fill=(248, 250, 252))
draw1.text((130, 610), "• Ingests Kafka Stream\n• Console Triggers", font=small_font, fill=(148, 163, 184))

# Secondary Adapters on Right (Infrastructure)
draw_rounded_rect(draw1, (1320, 230, 1480, 410), 10, (15, 23, 42), (16, 185, 129), 2)
draw1.text((1330, 245), "JPA Adapter\n(SatelliteJpaAdapter)", font=small_font, fill=(248, 250, 252))
draw1.text((1330, 315), "• Relational SQL DB\n• SatelliteJpaEntity", font=small_font, fill=(148, 163, 184))

draw_rounded_rect(draw1, (1320, 440, 1480, 600), 10, (15, 23, 42), (245, 158, 11), 2)
draw1.text((1330, 455), "MongoDB Adapter\n(SatelliteMongoAdapter)", font=small_font, fill=(248, 250, 252))
draw1.text((1330, 525), "• NoSQL Document DB\n• BSON Collections", font=small_font, fill=(148, 163, 184))

draw_rounded_rect(draw1, (1320, 630, 1480, 790), 10, (15, 23, 42), (168, 85, 247), 2)
draw1.text((1330, 645), "Event Publisher\n(EventPublisherAdapter)", font=small_font, fill=(248, 250, 252))
draw1.text((1330, 715), "• Spring Event Bus\n• Anomaly Events", font=small_font, fill=(148, 163, 184))

# Connecting Arrows
draw_arrow(draw1, (280, 370), (500, 420), (59, 130, 246), 4)
draw_arrow(draw1, (280, 630), (500, 580), (59, 130, 246), 4)

draw_arrow(draw1, (1100, 420), (1320, 320), (16, 185, 129), 4)
draw_arrow(draw1, (1100, 500), (1320, 520), (245, 158, 11), 4)
draw_arrow(draw1, (1100, 580), (1320, 710), (168, 85, 247), 4)

img1.save("/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_hexagonal_primary_secondary.png")

# =============================================================
# 2. DIAGRAM 2: ONION ARCHITECTURE CONCENTRIC RINGS (Herberto Graça Style)
# =============================================================
img2, draw2 = create_image(1600, 900)

draw2.text((80, 40), "ONION / HEXAGONAL CONCENTRIC LAYERED ARCHITECTURE", font=title_font, fill=(248, 250, 252))
draw2.text((80, 85), "Strict Dependency Rule: Outer layers depend on inner layers; Core Domain is 100% Isolated", font=subtitle_font, fill=(148, 163, 184))

# Concentric Rings (drawn from outside in)
# Ring 4: Infrastructure & Adapters (Blue)
draw2.ellipse([300, 130, 1300, 850], fill=(15, 23, 42), outline=(59, 130, 246), width=6)
draw2.text((640, 150), "INFRASTRUCTURE & ADAPTERS (REST, JPA, Spring Config)", font=box_body_font, fill=(59, 130, 246))

# Ring 3: Application Layer (Yellow)
draw2.ellipse([420, 210, 1180, 770], fill=(30, 41, 59), outline=(245, 158, 11), width=5)
draw2.text((650, 230), "APPLICATION SERVICES (LaunchSatelliteService)", font=box_body_font, fill=(245, 158, 11))

# Ring 2: Domain Ports (Purple)
draw2.ellipse([530, 290, 1070, 690], fill=(46, 16, 101), outline=(168, 85, 247), width=5)
draw2.text((640, 310), "DOMAIN PORTS (LaunchSatelliteUseCase, Repository)", font=box_body_font, fill=(168, 85, 247))

# Ring 1: Core Domain Model (Red)
draw2.ellipse([640, 370, 960, 610], fill=(127, 29, 29), outline=(239, 68, 68), width=5)
draw2.text((700, 430), "DOMAIN CORE\nSatellite.java\nOrbit.java\nTelemetry.java\nSatelliteId.java", font=small_font, fill=(248, 250, 252))

# Labels pointing inward
draw_arrow(draw2, (180, 480), (320, 480), (59, 130, 246), 4)
draw2.text((40, 465), "DEPENDENCIES\nPOINT INWARD ──►", font=box_body_font, fill=(59, 130, 246))

img2.save("/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_onion_concentric.png")

# =============================================================
# 3. DIAGRAM 3: DDD UBIQUITOUS LANGUAGE & SUBDOMAINS
# =============================================================
img3, draw3 = create_image(1600, 900)

draw3.text((80, 40), "DOMAIN-DRIVEN DESIGN (DDD): UBIQUITOUS LANGUAGE & SUBDOMAINS", font=title_font, fill=(248, 250, 252))
draw3.text((80, 85), "Bridging Domain Experts & Developers through shared terminology and bounded contexts", font=subtitle_font, fill=(148, 163, 184))

# Left Box: Problem Domain & Ubiquitous Language Cloud
draw_rounded_rect(draw3, (80, 150, 600, 820), 20, (30, 41, 59), (71, 85, 105), 3)
draw3.text((100, 170), "PROBLEM DOMAIN & UBIQUITOUS LANGUAGE", font=box_title_font, fill=(245, 158, 11))

draw_rounded_rect(draw3, (110, 230, 570, 480), 16, (15, 23, 42), (245, 158, 11), 2)
draw3.text((130, 250), "Shared Terminology (Domain Experts + Devs):", font=box_body_font, fill=(248, 250, 252))
draw3.text((130, 300), "• Orbit Parameters: Altitude (km), Inclination (deg)\n• Orbit Types: LEO (<2,000km), MEO, GEO (~35,786km)\n• Satellite Status: REGISTERED -> ACTIVE -> ANOMALY\n• Telemetry: Battery %, Signal (dBm), Temp (°C)\n• Anomaly Threshold: Battery < 15% OR Temp > 80°C", font=small_font, fill=(248, 250, 252))

draw_rounded_rect(draw3, (110, 520, 570, 780), 16, (15, 23, 42), (59, 130, 246), 2)
draw3.text((130, 540), "Bounded Context & Aggregates:", font=box_body_font, fill=(59, 130, 246))
draw3.text((130, 590), "• Bounded Context: Satellite Operations Management\n• Aggregate Root: Satellite.java\n• Value Objects: Orbit, Telemetry, SatelliteId\n• Domain Events: SatelliteLaunchedEvent", font=small_font, fill=(248, 250, 252))

# Right Box: Subdomains Breakdown
draw_rounded_rect(draw3, (660, 150, 1520, 820), 20, (15, 23, 42), (59, 130, 246), 3)
draw3.text((680, 170), "SUBDOMAIN DISTILLATION", font=box_title_font, fill=(59, 130, 246))

# Core Subdomain
draw_rounded_rect(draw3, (680, 230, 1500, 400), 14, (127, 29, 29), (239, 68, 68), 3)
draw3.text((700, 250), "CORE DOMAIN (Primary Competitive Advantage)", font=box_title_font, fill=(248, 250, 252))
draw3.text((700, 295), "• Satellite State Machine & Orbital Invariants\n• Real-Time Anomaly Detection Engine\n• Class: Satellite.java, Telemetry.java", font=box_body_font, fill=(248, 250, 252))

# Supporting Subdomain
draw_rounded_rect(draw3, (680, 430, 1500, 600), 14, (120, 53, 15), (245, 158, 11), 3)
draw3.text((700, 450), "SUPPORTING DOMAIN (Complements Core Domain)", font=box_title_font, fill=(248, 250, 252))
draw3.text((700, 495), "• Satellite Registration & Fleet Catalog Management\n• Class: LaunchSatelliteService.java, GetSatelliteService.java", font=box_body_font, fill=(248, 250, 252))

# Generic Subdomain
draw_rounded_rect(draw3, (680, 630, 1500, 800), 14, (20, 83, 45), (34, 197, 94), 3)
draw3.text((700, 650), "GENERIC DOMAIN (Standard Infrastructure)", font=box_title_font, fill=(248, 250, 252))
draw3.text((700, 695), "• Database Persistence (JPA / H2 / MongoDB)\n• Spring Web Security / Authentication & Event Publishing", font=box_body_font, fill=(248, 250, 252))

# Arrow connecting language to subdomains
draw_arrow(draw3, (600, 350), (680, 310), (245, 158, 11), 4)

img3.save("/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_ddd_ubiquitous_language.png")

# =============================================================
# 4. DIAGRAM 4: DUAL DATABASE ADAPTER SWAPPING DEMO
# =============================================================
img4, draw4 = create_image(1600, 900)

draw4.text((80, 40), "DEMO: MULTI-DATABASE DRIVEN ADAPTER SWAPPING", font=title_font, fill=(248, 250, 252))
draw4.text((80, 85), "Zero changes to Domain Core when swapping Relational DB (JPA/H2) for NoSQL (MongoDB) or Redis Cache", font=subtitle_font, fill=(148, 163, 184))

# Left: Outbound Port Interface inside Domain
draw_rounded_rect(draw4, (80, 320, 520, 650), 20, (30, 58, 138), (59, 130, 246), 4)
draw4.text((100, 340), "OUTBOUND PORT INTERFACE\n(domain.port.out)", font=box_title_font, fill=(248, 250, 252))
draw_rounded_rect(draw4, (100, 420, 500, 620), 10, (15, 23, 42), (245, 158, 11), 2)
draw4.text((120, 440), "public interface SatelliteRepository {\n  Satellite save(Satellite s);\n  Optional<Satellite> findById(SatelliteId id);\n  List<Satellite> findAll();\n}", font=small_font, fill=(248, 250, 252))

# Right Column: Multiple Driven Database Adapters
draw_rounded_rect(draw4, (640, 150, 1520, 830), 16, (30, 41, 59), (71, 85, 105), 3)
draw4.text((660, 170), "INFRASTRUCTURE ADAPTER LAYER (Plug & Play Implementations)", font=box_title_font, fill=(148, 163, 184))

# Database Adapter 1: JPA Relational DB
draw_rounded_rect(draw4, (660, 230, 1060, 790), 14, (15, 23, 42), (16, 185, 129), 3)
draw4.text((680, 250), "ADAPTER 1: RELATIONAL DB", font=box_title_font, fill=(16, 185, 129))
draw4.text((680, 300), "SatelliteJpaAdapter.java\n\n• Implements SatelliteRepository\n• Spring Data JPA + Hibernate\n• SatelliteJpaEntity.java (@Entity)\n• H2 / PostgreSQL / MySQL DB\n• Relational SQL Schema", font=box_body_font, fill=(248, 250, 252))
draw_rounded_rect(draw4, (680, 600, 1040, 760), 10, (6, 95, 70), (16, 185, 129), 2)
draw4.text((700, 640), "🗄️ Relational DB (SQL)\nTables: satellites", font=box_title_font, fill=(248, 250, 252))

# Database Adapter 2: MongoDB Document DB
draw_rounded_rect(draw4, (1100, 230, 1500, 790), 14, (15, 23, 42), (245, 158, 11), 3)
draw4.text((1120, 250), "ADAPTER 2: DOCUMENT DB", font=box_title_font, fill=(245, 158, 11))
draw4.text((1120, 300), "SatelliteMongoAdapter.java\n\n• Implements SatelliteRepository\n• Spring Data MongoDB\n• SatelliteDocument.java (@Document)\n• MongoDB Cluster\n• BSON JSON Documents", font=box_body_font, fill=(248, 250, 252))
draw_rounded_rect(draw4, (1120, 600, 1480, 760), 10, (180, 83, 9), (245, 158, 11), 2)
draw4.text((1140, 640), "🍃 MongoDB (NoSQL)\nCollection: satellites", font=box_title_font, fill=(248, 250, 252))

# Arrows showing implementation of interface
draw_arrow(draw4, (520, 480), (660, 380), (16, 185, 129), 4)
draw_arrow(draw4, (520, 480), (1100, 380), (245, 158, 11), 4)

img4.save("/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_adapter_swapping.png")

print("Successfully generated all 4 HD diagram PNGs referencing all architecture images!")

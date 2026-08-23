from PIL import Image, ImageDraw, ImageFont
import os

# Render HD diagram PNGs for PowerPoint slides

def create_image(width=1600, height=900, bg_color=(15, 23, 42)):
    img = Image.new("RGBA", (width, height), bg_color + (255,))
    draw = ImageDraw.Draw(img)
    return img, draw

# Load default font
try:
    title_font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 36)
    subtitle_font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 24)
    box_title_font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 26)
    box_body_font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 20)
    small_font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 16)
except Exception:
    title_font = subtitle_font = box_title_font = box_body_font = small_font = ImageFont.load_default()

def draw_rounded_rect(draw, box, radius, fill, outline, width=3):
    x1, y1, x2, y2 = box
    draw.rounded_rectangle([x1, y1, x2, y2], radius=radius, fill=fill, outline=outline, width=width)

def draw_arrow(draw, start, end, color=(59, 130, 246), width=4):
    x1, y1 = start
    x2, y2 = end
    draw.line([x1, y1, x2, y2], fill=color, width=width)
    # arrow head
    import math
    angle = math.atan2(y2 - y1, x2 - x1)
    arrow_len = 16
    arrow_angle = math.pi / 6
    xa = x2 - arrow_len * math.cos(angle - arrow_angle)
    ya = y2 - arrow_len * math.sin(angle - arrow_angle)
    xb = x2 - arrow_len * math.cos(angle + arrow_angle)
    yb = y2 - arrow_len * math.sin(angle + arrow_angle)
    draw.polygon([(x2, y2), (xa, ya), (xb, yb)], fill=color)

# -------------------------------------------------------------
# 1. DIAGRAM DDD ARCHITECTURE
# -------------------------------------------------------------
img1, draw1 = create_image(1600, 900)

# Main background sections
draw_rounded_rect(draw1, (100, 100, 1500, 260), 16, (30, 41, 59), (71, 85, 105), 3)
draw1.text((130, 120), "INFRASTRUCTURE & ADAPTER LAYER (External World)", font=box_title_font, fill=(148, 163, 184))

# Boxes inside infra
draw_rounded_rect(draw1, (140, 165, 520, 235), 10, (15, 23, 42), (59, 130, 246), 2)
draw1.text((160, 185), "REST Controllers / HTTP API", font=box_body_font, fill=(248, 250, 252))

draw_rounded_rect(draw1, (580, 165, 960, 235), 10, (15, 23, 42), (16, 185, 129), 2)
draw1.text((600, 185), "JPA Database / Persistence", font=box_body_font, fill=(248, 250, 252))

draw_rounded_rect(draw1, (1020, 165, 1460, 235), 10, (15, 23, 42), (245, 158, 11), 2)
draw1.text((1040, 185), "Message Brokers (Kafka/RabbitMQ)", font=box_body_font, fill=(248, 250, 252))

# Application Layer
draw_rounded_rect(draw1, (100, 310, 1500, 460), 16, (30, 41, 59), (71, 85, 105), 3)
draw1.text((130, 330), "APPLICATION LAYER (Orchestration & Use Cases)", font=box_title_font, fill=(245, 158, 11))
draw_rounded_rect(draw1, (140, 375, 1460, 440), 10, (15, 23, 42), (245, 158, 11), 2)
draw1.text((160, 395), "Application Services (LaunchSatelliteService, UpdateTelemetryService) - Transaction boundaries only", font=box_body_font, fill=(248, 250, 252))

# Domain Layer (Highlight Core)
draw_rounded_rect(draw1, (100, 510, 1500, 820), 20, (30, 58, 138), (59, 130, 246), 4)
draw1.text((130, 530), "DOMAIN CORE (Pure Java - Zero Framework Dependencies)", font=box_title_font, fill=(248, 250, 252))

# Sub-components inside Domain
draw_rounded_rect(draw1, (140, 580, 520, 790), 12, (15, 23, 42), (59, 130, 246), 2)
draw1.text((160, 600), "AGGREGATE ROOT", font=box_title_font, fill=(59, 130, 246))
draw1.text((160, 645), "• Satellite.java\n• State machine\n• Business rules\n• Event recording", font=box_body_font, fill=(248, 250, 252))

draw_rounded_rect(draw1, (560, 580, 1040, 790), 12, (15, 23, 42), (16, 185, 129), 2)
draw1.text((580, 600), "VALUE OBJECTS", font=box_title_font, fill=(16, 185, 129))
draw1.text((580, 645), "• Orbit (LEO / GEO constraints)\n• Telemetry (Anomaly rules)\n• SatelliteId (Typed UUID)\n• Immutable Java Records", font=box_body_font, fill=(248, 250, 252))

draw_rounded_rect(draw1, (1080, 580, 1460, 790), 12, (15, 23, 42), (245, 158, 11), 2)
draw1.text((1100, 600), "DOMAIN EVENTS", font=box_title_font, fill=(245, 158, 11))
draw1.text((1100, 645), "• SatelliteLaunchedEvent\n• AnomalyDetectedEvent\n• Immutable facts", font=box_body_font, fill=(248, 250, 252))

# Arrows connecting layers
draw_arrow(draw1, (330, 235), (330, 375), (59, 130, 246), 4)
draw_arrow(draw1, (800, 440), (800, 510), (245, 158, 11), 4)

img1.save("/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_ddd.png")

# -------------------------------------------------------------
# 2. DIAGRAM CAR ANALOGY
# -------------------------------------------------------------
img2, draw2 = create_image(1600, 900)

# Section Titles
draw2.text((80, 80), "HEXAGONAL ARCHITECTURE CONCEPT: REAL-WORLD CAR ANALOGY", font=title_font, fill=(248, 250, 252))

# Column 1: Inputs
draw_rounded_rect(draw2, (80, 160, 420, 820), 16, (30, 41, 59), (71, 85, 105), 3)
draw2.text((100, 180), "DRIVING ADAPTERS\n(Inputs / Triggers)", font=box_title_font, fill=(59, 130, 246))

draw_rounded_rect(draw2, (100, 280, 400, 420), 10, (15, 23, 42), (59, 130, 246), 2)
draw2.text((120, 330), "Foot Gas Pedal", font=box_title_font, fill=(248, 250, 252))

draw_rounded_rect(draw2, (100, 460, 400, 600), 10, (15, 23, 42), (59, 130, 246), 2)
draw2.text((120, 510), "Cruise Control Button", font=box_title_font, fill=(248, 250, 252))

draw_rounded_rect(draw2, (100, 640, 400, 780), 10, (15, 23, 42), (59, 130, 246), 2)
draw2.text((120, 690), "Mobile App Remote", font=box_title_font, fill=(248, 250, 252))

# Column 2: In Ports
draw_rounded_rect(draw2, (460, 160, 720, 820), 16, (15, 23, 42), (245, 158, 11), 3)
draw2.text((480, 180), "INPUT PORT\n(Contract)", font=box_title_font, fill=(245, 158, 11))
draw_rounded_rect(draw2, (480, 350, 700, 610), 10, (30, 41, 59), (245, 158, 11), 2)
draw2.text((500, 420), "Throttle Socket\nInterface", font=box_title_font, fill=(248, 250, 252))

# Column 3: Core Engine
draw_rounded_rect(draw2, (760, 160, 1140, 820), 20, (6, 95, 70), (16, 185, 129), 4)
draw2.text((780, 180), "CORE ENGINE\n(The Hexagon)", font=box_title_font, fill=(248, 250, 252))
draw2.text((780, 350), "Combustion Rules\n&\nAcceleration Logic\n\n(100% Independent\nof who presses\nthe pedal)", font=box_title_font, fill=(248, 250, 252))

# Column 4: Output Adapters
draw_rounded_rect(draw2, (1180, 160, 1520, 820), 16, (30, 41, 59), (71, 85, 105), 3)
draw2.text((1200, 180), "DRIVEN ADAPTERS\n(Power Outputs)", font=box_title_font, fill=(59, 130, 246))

draw_rounded_rect(draw2, (1200, 280, 1500, 420), 10, (15, 23, 42), (16, 185, 129), 2)
draw2.text((1220, 330), "Gasoline Fuel Tank", font=box_title_font, fill=(248, 250, 252))

draw_rounded_rect(draw2, (1200, 460, 1500, 600), 10, (15, 23, 42), (16, 185, 129), 2)
draw2.text((1220, 510), "Electric Battery", font=box_title_font, fill=(248, 250, 252))

draw_rounded_rect(draw2, (1200, 640, 1500, 780), 10, (15, 23, 42), (16, 185, 129), 2)
draw2.text((1220, 690), "Wheels & Tires", font=box_title_font, fill=(248, 250, 252))

# Arrows
draw_arrow(draw2, (400, 350), (480, 450), (59, 130, 246), 3)
draw_arrow(draw2, (400, 530), (480, 480), (59, 130, 246), 3)
draw_arrow(draw2, (400, 710), (480, 510), (59, 130, 246), 3)

draw_arrow(draw2, (700, 480), (760, 480), (245, 158, 11), 4)
draw_arrow(draw2, (1140, 480), (1200, 350), (16, 185, 129), 3)
draw_arrow(draw2, (1140, 480), (1200, 530), (16, 185, 129), 3)

img2.save("/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_car_analogy.png")

# -------------------------------------------------------------
# 3. DIAGRAM SATELLITE HEXAGONAL CODEBASE
# -------------------------------------------------------------
img3, draw3 = create_image(1600, 900)

draw3.text((80, 60), "HEXAGONAL ARCHITECTURE IN SATELLITE CODEBASE", font=title_font, fill=(248, 250, 252))

# Driving Adapters
draw_rounded_rect(draw3, (80, 140, 440, 820), 16, (30, 41, 59), (71, 85, 105), 3)
draw3.text((100, 160), "DRIVING ADAPTERS\n(Inbound / HTTP)", font=box_title_font, fill=(59, 130, 246))

draw_rounded_rect(draw3, (100, 260, 420, 480), 10, (15, 23, 42), (59, 130, 246), 2)
draw3.text((120, 280), "SatelliteController.java", font=box_title_font, fill=(248, 250, 252))
draw3.text((120, 330), "• Parses HTTP JSON\n• Validates DTOs\n• Returns HTTP 201/422", font=box_body_font, fill=(148, 163, 184))

draw_rounded_rect(draw3, (100, 520, 420, 740), 10, (15, 23, 42), (59, 130, 246), 2)
draw3.text((120, 540), "Kafka Consumer", font=box_title_font, fill=(248, 250, 252))
draw3.text((120, 590), "• Consumes stream\n• Maps raw payloads", font=box_body_font, fill=(148, 163, 184))

# Ports In
draw_rounded_rect(draw3, (480, 140, 740, 820), 16, (15, 23, 42), (245, 158, 11), 3)
draw3.text((500, 160), "INBOUND PORTS\n(domain.port.in)", font=box_title_font, fill=(245, 158, 11))

draw_rounded_rect(draw3, (500, 260, 720, 480), 10, (30, 41, 59), (245, 158, 11), 2)
draw3.text((515, 280), "LaunchSatelliteUseCase", font=box_body_font, fill=(248, 250, 252))
draw3.text((515, 330), "Interface defining\nlaunch operations", font=small_font, fill=(148, 163, 184))

draw_rounded_rect(draw3, (500, 520, 720, 740), 10, (30, 41, 59), (245, 158, 11), 2)
draw3.text((515, 540), "UpdateTelemetryUseCase", font=box_body_font, fill=(248, 250, 252))
draw3.text((515, 590), "Interface defining\ntelemetry updates", font=small_font, fill=(148, 163, 184))

# Domain Core
draw_rounded_rect(draw3, (780, 140, 1120, 820), 20, (30, 58, 138), (59, 130, 246), 4)
draw3.text((800, 160), "DOMAIN CORE\n(domain.model)", font=box_title_font, fill=(248, 250, 252))
draw3.text((800, 280), "Satellite.java\n(Aggregate Root)\n\nOrbit.java\nTelemetry.java\n(Value Objects)\n\nDomain Events", font=box_title_font, fill=(248, 250, 252))

# Driven Adapters
draw_rounded_rect(draw3, (1160, 140, 1520, 820), 16, (30, 41, 59), (71, 85, 105), 3)
draw3.text((1180, 160), "DRIVEN ADAPTERS\n(Outbound / JPA)", font=box_title_font, fill=(16, 185, 129))

draw_rounded_rect(draw3, (1180, 260, 1500, 480), 10, (15, 23, 42), (16, 185, 129), 2)
draw3.text((1200, 280), "SatelliteJpaAdapter.java", font=box_title_font, fill=(248, 250, 252))
draw3.text((1200, 330), "Implements Repository\nMaps Domain <-> Entity\nSpring Data JPA + H2", font=box_body_font, fill=(148, 163, 184))

draw_rounded_rect(draw3, (1180, 520, 1500, 740), 10, (15, 23, 42), (16, 185, 129), 2)
draw3.text((1200, 540), "EventPublisherAdapter", font=box_title_font, fill=(248, 250, 252))
draw3.text((1200, 590), "Publishes events to\nSpring / Kafka", font=box_body_font, fill=(148, 163, 184))

# Connectors
draw_arrow(draw3, (420, 370), (500, 370), (59, 130, 246), 4)
draw_arrow(draw3, (420, 630), (500, 630), (59, 130, 246), 4)

draw_arrow(draw3, (720, 370), (780, 370), (245, 158, 11), 4)
draw_arrow(draw3, (1120, 370), (1180, 370), (16, 185, 129), 4)

img3.save("/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_satellite_hex.png")

print("Successfully generated all 3 diagram PNGs!")

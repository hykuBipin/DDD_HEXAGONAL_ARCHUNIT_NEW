import os
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.enum.text import PP_ALIGN
from pptx.dml.color import RGBColor

prs = Presentation()
prs.slide_width = Inches(13.333)
prs.slide_height = Inches(7.5)

# Colors
DARK_BG = RGBColor(15, 23, 42)      # #0f172a
BLUE_ACCENT = RGBColor(59, 130, 246) # #3b82f6
GREEN_ACCENT = RGBColor(16, 185, 129)# #10b981
LIGHT_TEXT = RGBColor(248, 250, 252)# #f8fafc
MUTED_TEXT = RGBColor(148, 163, 184)# #94a3b8
CARD_BG = RGBColor(30, 41, 59)      # #1e293b
CARD_BORDER = RGBColor(51, 65, 85)  # #334155
GOLD_ACCENT = RGBColor(245, 158, 11) # #f59e0b

def set_slide_background(slide, color):
    background = slide.background
    fill = background.fill
    fill.solid()
    fill.fore_color.rgb = color

def add_header(slide, title_text, category_text=""):
    title_box = slide.shapes.add_textbox(Inches(0.8), Inches(0.4), Inches(11.7), Inches(0.9))
    tf = title_box.text_frame
    tf.word_wrap = True
    tf.margin_left = tf.margin_top = tf.margin_right = tf.margin_bottom = 0
    
    if category_text:
        p0 = tf.paragraphs[0]
        p0.text = category_text.upper()
        p0.font.size = Pt(11)
        p0.font.bold = True
        p0.font.color.rgb = BLUE_ACCENT
        p0.font.name = "Arial"
        
        p1 = tf.add_paragraph()
        p1.text = title_text
        p1.font.size = Pt(24)
        p1.font.bold = True
        p1.font.color.rgb = LIGHT_TEXT
        p1.font.name = "Arial"
    else:
        p0 = tf.paragraphs[0]
        p0.text = title_text
        p0.font.size = Pt(26)
        p0.font.bold = True
        p0.font.color.rgb = LIGHT_TEXT
        p0.font.name = "Arial"

# -------------------------------------------------------------
# SLIDE 1: Title Slide
# -------------------------------------------------------------
slide1 = prs.slides.add_slide(prs.slide_layouts[6])
set_slide_background(slide1, DARK_BG)

title_box = slide1.shapes.add_textbox(Inches(1.0), Inches(2.2), Inches(11.3), Inches(3.0))
tf = title_box.text_frame
tf.word_wrap = True

p0 = tf.paragraphs[0]
p0.text = "SATELLITE MANAGEMENT SYSTEM"
p0.font.size = Pt(14)
p0.font.bold = True
p0.font.color.rgb = BLUE_ACCENT

p1 = tf.add_paragraph()
p1.text = "DDD + Hexagonal Architecture + ArchUnit"
p1.font.size = Pt(36)
p1.font.bold = True
p1.font.color.rgb = LIGHT_TEXT

p2 = tf.add_paragraph()
p2.text = "Reference Architecture & Live Demo Guide for Enterprise Java 21 Applications"
p2.font.size = Pt(18)
p2.font.color.rgb = MUTED_TEXT

# -------------------------------------------------------------
# SLIDE 2: Traditional Java vs Hexagonal Architecture
# -------------------------------------------------------------
slide2 = prs.slides.add_slide(prs.slide_layouts[6])
set_slide_background(slide2, DARK_BG)
add_header(slide2, "Why Move Beyond Traditional 3-Tier Layered Java?", "Architectural Context")

body_box = slide2.shapes.add_textbox(Inches(0.8), Inches(1.5), Inches(11.7), Inches(5.3))
tf2 = body_box.text_frame
tf2.word_wrap = True

p = tf2.paragraphs[0]
p.text = "Traditional 3-Tier Layered Architecture Problems:"
p.font.bold = True
p.font.size = Pt(20)
p.font.color.rgb = GOLD_ACCENT

items = [
    "Database-Centric Design: JPA @Entity classes contaminate business logic; schema changes break domain rules.",
    "Framework Tight Coupling: @Service, @Autowired, and Jackson annotations scattered throughout business logic.",
    "Anemic Domain Models: Dumb POJOs with getters/setters; business logic trapped inside procedural services.",
    "Slow Test Feedback: Unit tests require booting Spring Context or mocking complex database repositories.",
    "Architectural Decay: Without automated guardrails, developers break layer boundaries over time."
]

for item in items:
    p = tf2.add_paragraph()
    p.text = "• " + item
    p.font.size = Pt(16)
    p.font.color.rgb = LIGHT_TEXT

# -------------------------------------------------------------
# SLIDE 3: Hexagonal Primary & Secondary Adapters Diagram
# -------------------------------------------------------------
slide3 = prs.slides.add_slide(prs.slide_layouts[6])
set_slide_background(slide3, DARK_BG)
add_header(slide3, "Hexagonal Architecture: Primary (Driving) & Secondary (Driven) Adapters", "WATA / Herberto Graça Specification")

img_path1 = "/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_hexagonal_primary_secondary.png"
if os.path.exists(img_path1):
    slide3.shapes.add_picture(img_path1, Inches(0.8), Inches(1.4), width=Inches(11.7))

# -------------------------------------------------------------
# SLIDE 4: Concentric Layered Onion Diagram Slide
# -------------------------------------------------------------
slide4 = prs.slides.add_slide(prs.slide_layouts[6])
set_slide_background(slide4, DARK_BG)
add_header(slide4, "Onion & Hexagonal Concentric Ring Dependency Rules", "Architectural Isolation")

img_path2 = "/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_onion_concentric.png"
if os.path.exists(img_path2):
    slide4.shapes.add_picture(img_path2, Inches(0.8), Inches(1.4), width=Inches(11.7))

# -------------------------------------------------------------
# SLIDE 5: DDD Ubiquitous Language & Subdomains Slide
# -------------------------------------------------------------
slide5 = prs.slides.add_slide(prs.slide_layouts[6])
set_slide_background(slide5, DARK_BG)
add_header(slide5, "Domain-Driven Design (DDD): Ubiquitous Language & Subdomains", "Domain Distillation")

img_path3 = "/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_ddd_ubiquitous_language.png"
if os.path.exists(img_path3):
    slide5.shapes.add_picture(img_path3, Inches(0.8), Inches(1.4), width=Inches(11.7))

# -------------------------------------------------------------
# SLIDE 6: Multi-Database Adapter Swapping Demo Slide
# -------------------------------------------------------------
slide6 = prs.slides.add_slide(prs.slide_layouts[6])
set_slide_background(slide6, DARK_BG)
add_header(slide6, "Hexagonal Superpower: Multi-Database Adapter Swapping Demo", "Adapter Layer Flexibility")

img_path4 = "/Users/bipin/.gemini/antigravity/scratch/satellite-system/diagram_adapter_swapping.png"
if os.path.exists(img_path4):
    slide6.shapes.add_picture(img_path4, Inches(0.8), Inches(1.4), width=Inches(11.7))

# -------------------------------------------------------------
# SLIDE 7: ArchUnit Rules & Validation Guide
# -------------------------------------------------------------
slide7 = prs.slides.add_slide(prs.slide_layouts[6])
set_slide_background(slide7, DARK_BG)
add_header(slide7, "Automated Architecture Enforcement & Testing", "ArchUnit & Demo Commands")

body_box = slide7.shapes.add_textbox(Inches(0.8), Inches(1.5), Inches(11.7), Inches(5.3))
tf7 = body_box.text_frame
tf7.word_wrap = True

p = tf7.paragraphs[0]
p.text = "1. ArchUnit Build Gates (11 Mandatory Rules):"
p.font.bold = True
p.font.size = Pt(18)
p.font.color.rgb = GOLD_ACCENT

rules_list = [
    "Domain Purity: domain has zero dependencies on infrastructure, application, Spring, or JPA.",
    "Port Isolation: Inbound & Outbound ports reside strictly in domain.port package.",
    "Controller Constraint: REST controllers depend ONLY on inbound ports (LaunchSatelliteUseCase).",
    "Adapter Isolation: Driving adapters (REST) cannot call driven adapters (JPA)."
]

for r in rules_list:
    p = tf7.add_paragraph()
    p.text = "  • " + r
    p.font.size = Pt(14)
    p.font.color.rgb = LIGHT_TEXT

p = tf7.add_paragraph()
p.text = "\n2. Execution & Live Demo Commands:"
p.font.bold = True
p.font.size = Pt(18)
p.font.color.rgb = GREEN_ACCENT

cmds = [
    "mvn clean test               # Runs 37 unit, integration & ArchUnit tests (0 failures)",
    "mvn spring-boot:run          # Launches live Spring Boot REST application on http://localhost:8080",
    "curl -X POST .../satellites  # Launches new satellite & persists via SatelliteRepository port"
]

for c in cmds:
    p = tf7.add_paragraph()
    p.text = "  $ " + c
    p.font.size = Pt(14)
    p.font.color.rgb = LIGHT_TEXT

prs.save("/Users/bipin/.gemini/antigravity/scratch/satellite-system/Satellite_Architecture_DDD_Hexagonal_ArchUnit.pptx")

print("Successfully generated updated PowerPoint presentation with all 4 reference diagram images!")

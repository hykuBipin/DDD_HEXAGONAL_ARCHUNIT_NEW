import os
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.enum.text import PP_ALIGN
from pptx.dml.color import RGBColor
from pptx.enum.shapes import MSO_SHAPE

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

def add_card(slide, left, top, width, height, bg_color=CARD_BG, border_color=CARD_BORDER):
    shape = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, left, top, width, height)
    shape.fill.solid()
    shape.fill.fore_color.rgb = bg_color
    if border_color:
        shape.line.color.rgb = border_color
        shape.line.width = Pt(1.5)
    else:
        shape.line.fill.background()
    return shape

# ---------------------------------------------------------
# SLIDE 1: Title Slide
# ---------------------------------------------------------
slide_layout = prs.slide_layouts[6] # blank
slide1 = prs.slides.add_slide(slide_layout)
set_slide_background(slide1, DARK_BG)

title_box = slide1.shapes.add_textbox(Inches(1.0), Inches(1.8), Inches(11.3), Inches(3.5))
tf1 = title_box.text_frame
tf1.word_wrap = True

p = tf1.paragraphs[0]
p.text = "SATELLITE MANAGEMENT SYSTEM"
p.font.size = Pt(14)
p.font.bold = True
p.font.color.rgb = BLUE_ACCENT
p.font.name = "Arial"

p2 = tf1.add_paragraph()
p2.text = "Mastering Enterprise Java Architecture"
p2.font.size = Pt(36)
p2.font.bold = True
p2.font.color.rgb = LIGHT_TEXT
p2.font.name = "Arial"

p3 = tf1.add_paragraph()
p3.text = "Domain-Driven Design (DDD)  |  Hexagonal Architecture  |  ArchUnit"
p3.font.size = Pt(20)
p3.font.color.rgb = GOLD_ACCENT
p3.font.name = "Arial"

p4 = tf1.add_paragraph()
p4.text = "\nA Production-Grade Spring Boot Reference Project with Executable Architecture Controls"
p4.font.size = Pt(14)
p4.font.color.rgb = MUTED_TEXT
p4.font.name = "Arial"

# Cards at bottom of slide 1
card_w = Inches(3.6)
card_h = Inches(1.4)
top_pos = Inches(5.3)

c1 = add_card(slide1, Inches(1.0), top_pos, card_w, card_h)
tf = c1.text_frame
tf.word_wrap = True
p = tf.paragraphs[0]
p.text = "1. DDD Core"
p.font.bold = True
p.font.size = Pt(16)
p.font.color.rgb = LIGHT_TEXT
p2 = tf.add_paragraph()
p2.text = "Rich Aggregates & Value Objects with pure Java business invariants."
p2.font.size = Pt(12)
p2.font.color.rgb = MUTED_TEXT

c2 = add_card(slide1, Inches(4.85), top_pos, card_w, card_h)
tf = c2.text_frame
tf.word_wrap = True
p = tf.paragraphs[0]
p.text = "2. Hexagonal Architecture"
p.font.bold = True
p.font.size = Pt(16)
p.font.color.rgb = LIGHT_TEXT
p2 = tf.add_paragraph()
p2.text = "Inward Ports & Adapters isolating business logic from HTTP & JPA."
p2.font.size = Pt(12)
p2.font.color.rgb = MUTED_TEXT

c3 = add_card(slide1, Inches(8.7), top_pos, card_w, card_h)
tf = c3.text_frame
tf.word_wrap = True
p = tf.paragraphs[0]
p.text = "3. ArchUnit Guardrails"
p.font.bold = True
p.font.size = Pt(16)
p.font.color.rgb = LIGHT_TEXT
p2 = tf.add_paragraph()
p2.text = "Automated JUnit 5 tests failing the build if rules are violated."
p2.font.size = Pt(12)
p2.font.color.rgb = MUTED_TEXT

# ---------------------------------------------------------
# SLIDE 2: The Problem
# ---------------------------------------------------------
slide2 = prs.slides.add_slide(slide_layout)
set_slide_background(slide2, DARK_BG)
add_header(slide2, "The Problem: Architectural Decay & Framework Coupling", "CHALLENGES IN TRADITIONAL ENTERPRISE JAVA")

card1 = add_card(slide2, Inches(0.8), Inches(1.5), Inches(5.6), Inches(5.2))
tf = card1.text_frame
tf.word_wrap = True
p = tf.paragraphs[0]
p.text = "Traditional 3-Tier Pitfalls"
p.font.bold = True
p.font.size = Pt(18)
p.font.color.rgb = GOLD_ACCENT

bullets = [
    ("Anemic Domain Models: ", "Entities are empty data shells (getters/setters). Business logic leaks into 2,000-line Spring @Services."),
    ("Framework Lock-in: ", "@Entity, @Table, and Jackson annotations blur domain boundaries with database and HTTP concerns."),
    ("Architectural Drift: ", "Over time, Controllers bypass Services to call DAOs, creating cyclic dependencies and untestable spaghetti code."),
    ("Manual Reviews Don't Scale: ", "PR code reviews miss subtle architecture violations under tight deadline pressure.")
]
for title, desc in bullets:
    p = tf.add_paragraph()
    p.text = "• " + title
    p.font.bold = True
    p.font.size = Pt(14)
    p.font.color.rgb = LIGHT_TEXT
    run = p.add_run()
    run.text = desc
    run.font.bold = False
    run.font.color.rgb = MUTED_TEXT

card2 = add_card(slide2, Inches(6.8), Inches(1.5), Inches(5.7), Inches(5.2))
tf2 = card2.text_frame
tf2.word_wrap = True
p = tf2.paragraphs[0]
p.text = "Impact on Delivery & Quality"
p.font.bold = True
p.font.size = Pt(18)
p.font.color.rgb = RGBColor(239, 68, 68) # Red

impacts = [
    ("Slow Unit Testing: ", "Cannot test core rules without booting full Spring context & DB."),
    ("Risk of Upgrades: ", "Upgrading Spring or changing DB vendors breaks business logic."),
    ("High Technical Debt: ", "New developers struggle to know where business rules belong.")
]
for title, desc in impacts:
    p = tf2.add_paragraph()
    p.text = "✖ " + title
    p.font.bold = True
    p.font.size = Pt(14)
    p.font.color.rgb = LIGHT_TEXT
    run = p.add_run()
    run.text = desc
    run.font.bold = False
    run.font.color.rgb = MUTED_TEXT

# ---------------------------------------------------------
# SLIDE 3: Pillar 1 - DDD Core
# ---------------------------------------------------------
slide3 = prs.slides.add_slide(slide_layout)
set_slide_background(slide3, DARK_BG)
add_header(slide3, "Pillar 1: Domain-Driven Design (DDD) Core", "PURE BUSINESS LOGIC")

c_left = add_card(slide3, Inches(0.8), Inches(1.5), Inches(6.0), Inches(5.2))
tf = c_left.text_frame
tf.word_wrap = True
p = tf.paragraphs[0]
p.text = "Standalone Domain Architecture"
p.font.bold = True
p.font.size = Pt(18)
p.font.color.rgb = BLUE_ACCENT

ddd_points = [
    ("Domain Core (Heart): ", "Contains Aggregate Root (Satellite), Value Objects (Orbit, Telemetry), and Domain Events."),
    ("Zero Annotations: ", "No @Entity, @Table, @Component, or @JsonProperty. 100% pure Java code."),
    ("Rich Domain Invariants: ", "State transitions (REGISTERED -> ACTIVE -> ANOMALY) are controlled internally by the aggregate."),
    ("Self-Validating Value Objects: ", "Orbit validates altitude constraints (LEO < 2000 km, GEO ~35,786 km) at instant of creation.")
]
for title, desc in ddd_points:
    p = tf.add_paragraph()
    p.text = "✔ " + title
    p.font.bold = True
    p.font.size = Pt(13)
    p.font.color.rgb = LIGHT_TEXT
    run = p.add_run()
    run.text = desc
    run.font.bold = False
    run.font.color.rgb = MUTED_TEXT

c_right = add_card(slide3, Inches(7.1), Inches(1.5), Inches(5.4), Inches(5.2))
tf = c_right.text_frame
tf.word_wrap = True
p = tf.paragraphs[0]
p.text = "Code Snippet: Pure Java Aggregate"
p.font.bold = True
p.font.size = Pt(16)
p.font.color.rgb = GREEN_ACCENT

code_lines = [
    "// Pure Java Aggregate Root",
    "public class Satellite {",
    "  private final SatelliteId id;",
    "  private Orbit orbit;",
    "  private SatelliteStatus status;",
    "",
    "  public void launch() {",
    "    ensureCanTransitionTo(ACTIVE);",
    "    this.status = ACTIVE;",
    "    this.domainEvents.add(",
    "      new SatelliteLaunchedEvent(...));",
    "  }",
    "}"
]
for line in code_lines:
    p = tf.add_paragraph()
    p.text = line
    p.font.size = Pt(12)
    p.font.name = "Courier New"
    p.font.color.rgb = LIGHT_TEXT

# ---------------------------------------------------------
# SLIDE 4: Pillar 2 - Hexagonal Architecture (Car Analogy)
# ---------------------------------------------------------
slide4 = prs.slides.add_slide(slide_layout)
set_slide_background(slide4, DARK_BG)
add_header(slide4, "Pillar 2A: Hexagonal Architecture (Real-World Car Analogy)", "PORTS & ADAPTERS CONCEPT")

cards_data = [
    ("DRIVING ADAPTERS (Inputs)", "Foot Gas Pedal\nCruise Control Button\nMobile App Remote", Inches(0.8), BLUE_ACCENT),
    ("INPUT PORTS", "Throttle Socket Interface\n(Standard Contract)", Inches(3.9), GOLD_ACCENT),
    ("CORE ENGINE (Hexagon)", "Engine Cylinder &\nCombustion Rules\n(Independent Core)", Inches(6.8), GREEN_ACCENT),
    ("OUTPUT PORTS & ADAPTERS", "Fuel Port -> Petrol Tank / Battery\nDrivetrain Port -> Wheels", Inches(9.8), BLUE_ACCENT)
]

for title, desc, left, color in cards_data:
    c = add_card(slide4, left, Inches(1.8), Inches(2.7), Inches(4.8))
    tf = c.text_frame
    tf.word_wrap = True
    p = tf.paragraphs[0]
    p.text = title
    p.font.bold = True
    p.font.size = Pt(14)
    p.font.color.rgb = color
    p2 = tf.add_paragraph()
    p2.text = "\n" + desc
    p2.font.size = Pt(13)
    p2.font.color.rgb = LIGHT_TEXT

# ---------------------------------------------------------
# SLIDE 5: Pillar 2 - Hexagonal in Codebase
# ---------------------------------------------------------
slide5 = prs.slides.add_slide(slide_layout)
set_slide_background(slide5, DARK_BG)
add_header(slide5, "Pillar 2B: Hexagonal Architecture in Satellite Codebase", "CODEBASE MAPPING")

c_code_mapping = [
    ("1. Driving Adapters (REST)", "SatelliteController parses HTTP JSON -> calls Inbound Port interface.", Inches(0.8)),
    ("2. Inbound Ports (Domain)", "LaunchSatelliteUseCase & UpdateTelemetryUseCase interfaces defined in domain.", Inches(3.9)),
    ("3. Outbound Ports (Domain)", "SatelliteRepository interface defined in domain using domain types.", Inches(6.8)),
    ("4. Driven Adapters (JPA)", "SatelliteJpaAdapter implements SatelliteRepository & converts to SatelliteJpaEntity.", Inches(9.8))
]

for title, desc, left in c_code_mapping:
    c = add_card(slide5, left, Inches(1.8), Inches(2.7), Inches(4.8))
    tf = c.text_frame
    tf.word_wrap = True
    p = tf.paragraphs[0]
    p.text = title
    p.font.bold = True
    p.font.size = Pt(14)
    p.font.color.rgb = GREEN_ACCENT
    p2 = tf.add_paragraph()
    p2.text = "\n" + desc
    p2.font.size = Pt(13)
    p2.font.color.rgb = LIGHT_TEXT

# ---------------------------------------------------------
# SLIDE 6: Package Layout
# ---------------------------------------------------------
slide6 = prs.slides.add_slide(slide_layout)
set_slide_background(slide6, DARK_BG)
add_header(slide6, "Package Structure & Separation of Concerns", "CLEAN ARCHITECTURE LAYOUT")

c_pkg = add_card(slide6, Inches(0.8), Inches(1.5), Inches(11.7), Inches(5.2))
tf = c_pkg.text_frame
tf.word_wrap = True

p = tf.paragraphs[0]
p.text = "com.satellite"
p.font.bold = True
p.font.size = Pt(16)
p.font.color.rgb = GOLD_ACCENT

pkg_tree = [
    ("├── domain/                  ", "🔴 PURE JAVA (Inner Core: Satellite, Orbit, Telemetry, Ports)"),
    ("├── application/             ", "🟡 ORCHESTRATION LAYER (Services: LaunchSatelliteService)"),
    ("├── adapter/                 ", "🟢 INFRASTRUCTURE LAYER (REST Controllers, JPA Entities, Mappers)"),
    ("└── config/                  ", "🔵 COMPOSITION ROOT (BeanConfig: Wires Ports to Adapters)")
]
for prefix, note in pkg_tree:
    p = tf.add_paragraph()
    p.text = prefix
    p.font.size = Pt(14)
    p.font.name = "Courier New"
    p.font.bold = True
    p.font.color.rgb = LIGHT_TEXT
    run = p.add_run()
    run.text = note
    run.font.name = "Arial"
    run.font.bold = False
    run.font.color.rgb = MUTED_TEXT

p_sep = tf.add_paragraph()
p_sep.text = "\nKey Rule: Satellite (Domain Aggregate) ≠ SatelliteJpaEntity (Database Table) ≠ SatelliteResponse (REST DTO)"
p_sep.font.bold = True
p_sep.font.size = Pt(14)
p_sep.font.color.rgb = GREEN_ACCENT

# ---------------------------------------------------------
# SLIDE 7: ArchUnit
# ---------------------------------------------------------
slide7 = prs.slides.add_slide(slide_layout)
set_slide_background(slide7, DARK_BG)
add_header(slide7, "Pillar 3: ArchUnit — Architecture as Executable Code", "AUTOMATED BUILD GATES")

c_arch_l = add_card(slide7, Inches(0.8), Inches(1.5), Inches(6.0), Inches(5.2))
tf = c_arch_l.text_frame
tf.word_wrap = True
p = tf.paragraphs[0]
p.text = "ArchUnit Test Code"
p.font.bold = True
p.font.size = Pt(16)
p.font.color.rgb = BLUE_ACCENT

arch_code = [
    "@ArchTest",
    "static final ArchRule domainMustBePure =",
    "  noClasses()",
    "    .that().resideInAPackage(\"..domain..\")",
    "    .should().dependOnClassesThat()",
    "    .resideInAnyPackage(",
    "       \"..adapter..\", \"..application..\")",
    "    .because(\"Domain must be independent\");"
]
for line in arch_code:
    p = tf.add_paragraph()
    p.text = line
    p.font.size = Pt(11)
    p.font.name = "Courier New"
    p.font.color.rgb = LIGHT_TEXT

c_arch_r = add_card(slide7, Inches(7.1), Inches(1.5), Inches(5.4), Inches(5.2))
tf = c_arch_r.text_frame
tf.word_wrap = True
p = tf.paragraphs[0]
p.text = "14 Enforced Architectural Rules"
p.font.bold = True
p.font.size = Pt(16)
p.font.color.rgb = GOLD_ACCENT

rules_list = [
    "1. Domain layer purity (Zero adapter/app dependencies)",
    "2. No JPA/Spring annotations in domain models",
    "3. Controllers must depend on Ports, not Services",
    "4. Outbound adapters must implement outbound ports",
    "5. Naming conventions (*Service, *Adapter, *Controller)",
    "6. No cyclic dependencies between layers"
]
for r in rules_list:
    p = tf.add_paragraph()
    p.text = "✔ " + r
    p.font.size = Pt(13)
    p.font.color.rgb = LIGHT_TEXT

# ---------------------------------------------------------
# SLIDE 8: Live Demo Agenda
# ---------------------------------------------------------
slide8 = prs.slides.add_slide(slide_layout)
set_slide_background(slide8, DARK_BG)
add_header(slide8, "Live Demo Plan & Execution Steps", "HANDS-ON WALKTHROUGH")

demo_steps = [
    ("Step 1: Code Tour", "Inspect Satellite.java aggregate, Orbit value object, and Hexagonal Architecture tests.", Inches(0.8)),
    ("Step 2: Break ArchUnit", "Add @Entity to Satellite.java -> Run 'mvn test' -> Watch build fail with explicit violation log!", Inches(4.8)),
    ("Step 3: REST API Execution", "Run 'mvn spring-boot:run' -> Launch satellite -> Update telemetry -> Trigger automatic Anomaly Alert event.", Inches(8.7))
]

for title, desc, left in demo_steps:
    c = add_card(slide8, left, Inches(1.8), Inches(3.8), Inches(4.8))
    tf = c.text_frame
    tf.word_wrap = True
    p = tf.paragraphs[0]
    p.text = title
    p.font.bold = True
    p.font.size = Pt(16)
    p.font.color.rgb = GOLD_ACCENT
    p2 = tf.add_paragraph()
    p2.text = "\n" + desc
    p2.font.size = Pt(14)
    p2.font.color.rgb = LIGHT_TEXT

# ---------------------------------------------------------
# SLIDE 9: Benefits Summary
# ---------------------------------------------------------
slide9 = prs.slides.add_slide(slide_layout)
set_slide_background(slide9, DARK_BG)
add_header(slide9, "Key Business & Technical Benefits", "WHY ADOPT THIS PATTERN?")

benefits = [
    ("⚡ Ultra-Fast Unit Testing", "Domain logic unit tests run in milliseconds without booting Spring or DB containers.", Inches(0.8), Inches(1.8)),
    ("🛡️ Zero Architectural Drift", "ArchUnit fails CI/CD builds instantly if anyone breaks layer boundaries.", Inches(6.8), Inches(1.8)),
    ("🔄 Seamless Infrastructure Swaps", "Replace JPA with MongoDB or Spring Events with Kafka without touching 1 line of domain code.", Inches(0.8), Inches(4.3)),
    ("🎯 High Business Alignment", "Domain code speaks the exact language of satellite flight operations.", Inches(6.8), Inches(4.3))
]

for title, desc, left, top in benefits:
    c = add_card(slide9, left, top, Inches(5.7), Inches(2.3))
    tf = c.text_frame
    tf.word_wrap = True
    p = tf.paragraphs[0]
    p.text = title
    p.font.bold = True
    p.font.size = Pt(16)
    p.font.color.rgb = GREEN_ACCENT
    p2 = tf.add_paragraph()
    p2.text = "\n" + desc
    p2.font.size = Pt(13)
    p2.font.color.rgb = LIGHT_TEXT

# ---------------------------------------------------------
# SLIDE 10: Conclusion & Q&A
# ---------------------------------------------------------
slide10 = prs.slides.add_slide(slide_layout)
set_slide_background(slide10, DARK_BG)

add_header(slide10, "Conclusion & GitHub Repository", "SUMMARY & Q&A")

c_end = add_card(slide10, Inches(1.5), Inches(1.8), Inches(10.3), Inches(4.8))
tf = c_end.text_frame
tf.word_wrap = True

p = tf.paragraphs[0]
p.text = "Project Summary & Takeaways"
p.font.bold = True
p.font.size = Pt(20)
p.font.color.rgb = GOLD_ACCENT

takeaways = [
    "Domain-Driven Design keeps core business rules clean, testable, and rich.",
    "Hexagonal Architecture isolates business logic from Spring, JPA, and HTTP infrastructure.",
    "ArchUnit automates governance, acting as an unbypassable build gate in CI/CD pipelines.",
    "",
    "GitHub Repository: https://github.com/hykuBipin/DDD_HEXAGONAL_ARCHUNIT_NEW"
]

for t in takeaways:
    p = tf.add_paragraph()
    p.text = "• " + t if t and not t.startswith("GitHub") else t
    p.font.size = Pt(14)
    p.font.color.rgb = GREEN_ACCENT if t.startswith("GitHub") else LIGHT_TEXT
    if t.startswith("GitHub"):
        p.font.bold = True

output_path = "/Users/bipin/.gemini/antigravity/scratch/satellite-system/Satellite_Architecture_DDD_Hexagonal_ArchUnit.pptx"
prs.save(output_path)
print(f"Successfully generated PowerPoint presentation at: {output_path}")

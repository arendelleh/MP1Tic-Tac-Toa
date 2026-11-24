from pathlib import Path
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
import pypdfium2 as pdfium

out=Path('output/pdf'); out.mkdir(parents=True,exist_ok=True)
styles=getSampleStyleSheet()
styles.add(ParagraphStyle(name='BodyCustom',fontName='Helvetica',fontSize=10,leading=14,spaceAfter=8))
styles.add(ParagraphStyle(name='CellCustom',fontName='Helvetica',fontSize=9,leading=12))
styles.add(ParagraphStyle(name='CoverCustom',fontName='Helvetica-Bold',fontSize=28,leading=33,alignment=TA_CENTER,spaceAfter=16))
for n in ['Heading1','Heading2']: styles[n].textColor=colors.black
story=[]
def p(s): story.append(Paragraph(s,styles['BodyCustom']))
def h(s): story.append(Paragraph(s,styles['Heading1']))
def sub(s): story.append(Paragraph(s,styles['Heading2']))
def table(headers,rows,widths):
 data=[[Paragraph(str(c),styles['CellCustom']) for c in row] for row in [headers]+rows]
 t=Table(data,colWidths=widths,repeatRows=1,hAlign='LEFT')
 t.setStyle(TableStyle([('BACKGROUND',(0,0),(-1,0),colors.HexColor('#eee3e9')),('VALIGN',(0,0),(-1,-1),'TOP'),('GRID',(0,0),(-1,-1),.4,colors.HexColor('#cfc6cb')),('LEFTPADDING',(0,0),(-1,-1),8),('RIGHTPADDING',(0,0),(-1,-1),8),('TOPPADDING',(0,0),(-1,-1),7),('BOTTOMPADDING',(0,0),(-1,-1),7)]))
 story.extend([t,Spacer(1,10)])
def page(): story.append(PageBreak())

story.append(Paragraph('Blossom',styles['CoverCustom']))
story.append(Paragraph('Smart Menstrual Cycle Tracker &amp;<br/>Body Literacy Android Application',ParagraphStyle(name='SubtitleCustom',fontName='Helvetica',fontSize=16,leading=22,alignment=TA_CENTER,spaceAfter=16)))
p('<b>CS0011 Final Project Proposal</b><br/>Submitted by: [Student name or names]<br/>Section: [Section]<br/>Instructor: [Instructor name]<br/>Date: November 4, 2026')
h('1 Application Title and Category')
p('<b>Title:</b> Blossom: Smart Menstrual Cycle Tracker &amp; Body Literacy Android Application<br/><b>Category:</b> Health and Wellness / Education<br/><b>Technology:</b> Android Studio, Kotlin, and SQLite')
p('Blossom combines personal menstrual records with short, source-backed lessons. Its flower-inspired interface and learning progress features aim to make tracking approachable and menstrual health education engaging. This proposal describes the planned scope and design for instructor approval.')
h('2 Problem Statement and Target Users')
p('Keeping period records and understanding menstrual health are related needs. Users may find it inconvenient to maintain separate calendars and notes for period dates, moods, and symptoms. They may also encounter online explanations without clear references, making information difficult to assess.')
p('Blossom will bring personal tracking and understandable educational content into one application. Its primary target users are college students and young adults who menstruate and want to organize their records and improve their understanding of menstrual health. The design will use respectful language and explain unfamiliar terms.')
page()
h('3 Proposed Solution')
p('Blossom will be an offline-first Android application for one local user per installation. Users will manage period records and daily check-ins, browse their history, and explore bundled lessons with source links. Internet access will be needed only to open external references.')
sub('Objectives')
for s in ['Provide complete create, read, update, and delete operations for persistent period records and daily logs.','Organize recorded experiences through a calendar and descriptive cycle summaries.','Support body literacy through accessible lessons, reference links, and knowledge checks.','Encourage learning through bookmarks and a flower that gains petals as lessons are completed.','Provide clear navigation, accessible controls, validation, and useful feedback.']: p('&#8226; '+s)
sub('Scope and boundaries')
p('The word smart refers to useful calculations and summaries from user-entered records. Body literacy refers to understanding menstruation, related body changes, and the meaning of tracking information. The first version will not diagnose conditions, recommend treatment, predict fertility, or provide an AI health chatbot. It will not require an account or cloud synchronization.')
p('The flower is a visual metaphor for learning. Growth will depend on completed lessons, not cycle regularity or daily check-in streaks. Missing an entry will not cause the flower to wilt.')
h('4 Core Features')
table(['Feature','Planned behavior'],[
('Period tracking','Add, view, edit, and delete start dates, optional end dates, and notes. An absent end date represents an ongoing period.'),
('Daily check-in','Save one entry per date containing mood, flow level, symptoms, symptom severity, and optional notes; view, edit, or delete it.'),
('Calendar and history','Browse recorded periods and check-ins by date. Open a selected entry to view or edit its details.'),
('Cycle summary','Calculate recorded period durations and intervals between consecutive period start dates. Explain when there are insufficient records.')
],[115,380])
page()
h('4 Core Features continued')
table(['Feature','Planned behavior'],[
('Learning library','Provide six to eight short lessons, organized by topic, with source organizations, reference links, and content review dates.'),
('Knowledge checks','Offer single-answer multiple-choice or myth-or-fact questions. Show the correct explanation and lesson references after answering.'),
('Learning flower','Add petals as lessons are marked complete. Show completed lesson count without penalizing missed days.'),
('Bookmarks and filters','Save and remove article bookmarks; filter lessons by category or saved status.')
],[115,380])
p('<b>Beyond CRUD:</b> calendar browsing, descriptive cycle summaries, category filtering, knowledge checks, bookmarks, and learning progress.')
sub('Validation and feedback')
p('Start dates and log dates will be required. End dates cannot precede start dates. Future tracking dates, overlapping period records, duplicate check-ins, and a second ongoing period will be rejected. A daily check-in will require at least one mood, flow, symptom, or note value. Forms will retain input when an error occurs. Save messages, empty-state guidance, and delete confirmations will be provided.')
sub('Content and privacy approach')
p('Initial topics will include menstruation basics, counting a cycle, recording symptoms, menstrual products and hygiene, common myths, and seeking professional advice. Educational text will be original summaries checked against credible guidance. Content will not be labeled medically reviewed unless reviewed by a qualified professional.')
p('Personal records will remain in local app storage. No cloud upload, advertising integration, or analytics collection is planned. Android cloud backup of tracking data will be disabled in the initial version. Screenshots and demonstrations will use fictional records.')
sub('Initial educational references')
p('American College of Obstetricians and Gynecologists. <i>Your First Period.</i><br/><link href="https://www.acog.org/womens-health/faqs/your-first-period" color="#6e3d58">https://www.acog.org/womens-health/faqs/your-first-period</link>')
p('Office on Women\'s Health. <i>Your Menstrual Cycle.</i><br/><link href="https://womenshealth.gov/menstrual-cycle/your-menstrual-cycle" color="#6e3d58">https://womenshealth.gov/menstrual-cycle/your-menstrual-cycle</link>')
page()
h('5 Database Schema')
p('SQLite will store personal records and bundled educational content. All dates will use ISO format YYYY-MM-DD. PK means primary key; FK means foreign key. Foreign key enforcement will be enabled.')
table(['Table','Fields and constraints'],[
('period_records','period_id INTEGER PK<br/>start_date TEXT NOT NULL UNIQUE<br/>end_date TEXT NULL<br/>notes TEXT'),
('daily_logs','log_id INTEGER PK<br/>log_date TEXT NOT NULL UNIQUE<br/>mood TEXT NULL; flow_level TEXT NULL; notes TEXT NULL'),
('symptoms','symptom_id INTEGER PK<br/>name TEXT NOT NULL UNIQUE'),
('daily_log_symptoms','log_id INTEGER FK to daily_logs<br/>symptom_id INTEGER FK to symptoms<br/>severity INTEGER NOT NULL CHECK 1 to 3<br/>Composite PK (log_id, symptom_id)'),
('articles','article_id INTEGER PK<br/>title TEXT NOT NULL; category TEXT NOT NULL<br/>body TEXT NOT NULL; reviewed_on TEXT NOT NULL'),
('article_sources','source_id INTEGER PK<br/>article_id INTEGER NOT NULL FK to articles<br/>organization TEXT NOT NULL<br/>source_title TEXT NOT NULL; url TEXT NOT NULL'),
('learning_progress','article_id INTEGER PK and FK to articles<br/>is_bookmarked INTEGER NOT NULL DEFAULT 0 CHECK 0 or 1<br/>completed_at TEXT NULL'),
('knowledge_questions','question_id INTEGER PK<br/>article_id INTEGER NOT NULL FK to articles<br/>question_text TEXT NOT NULL<br/>explanation TEXT NOT NULL'),
('question_options','option_id INTEGER PK<br/>question_id INTEGER NOT NULL FK to knowledge_questions<br/>option_text TEXT NOT NULL<br/>is_correct INTEGER NOT NULL DEFAULT 0 CHECK 0 or 1')
],[125,370])
page()
h('5 Database Relationships continued')
table(['Relationship','Implementation'],[
('Daily logs and symptoms','Many to many through daily_log_symptoms. Deleting a log cascades to its symptom links.'),
('Articles and sources','One article has one or more sources; article_sources.article_id references articles.article_id.'),
('Articles and progress','One article has zero or one local progress row. The article ID is both the PK and FK in learning_progress.'),
('Articles and questions','One article can have multiple questions; knowledge_questions.article_id is the FK.'),
('Questions and options','One question has multiple options. Bundled content validation will enforce exactly one correct answer.'),
('Periods and daily logs','Independent records displayed together using their dates. A daily log does not require a period record.')
],[150,345])
p('Articles, references, symptoms, questions, and options will be populated during database initialization. Users perform full CRUD on period records and daily logs. Bookmarking and lesson completion update learning_progress. Cycle summaries and flower petal counts are derived from stored data, avoiding duplicate summary records.')
h('6 Screen and Navigation Plan')
p('The main navigation destinations will be Home, Calendar, and Learn. Separate Activities will provide explicit Intent navigation and meaningful data transfer.')
table(['Activity','Purpose and transferred data'],[
('HomeActivity','Show recent records, summaries, the learning flower, and quick actions. Open a featured article using article_id.'),
('CalendarActivity','Display dates and history. Send selected_date when creating a record or period_id / log_id when editing.'),
('PeriodEditorActivity','Create or edit dates and notes. Accept optional period_id or selected_date.'),
('DailyLogActivity','Create or edit a check-in. Accept optional log_id or selected_date.'),
('LearnActivity','Browse categories and bookmarked lessons. Pass article_id to the article screen.'),
('ArticleActivity','Load a lesson, sources, knowledge checks, bookmark state, and completion status using article_id.')
],[145,350])
page()
h('6 Navigation and Screen Layout continued')
sub('Navigation flow')
p('Home → Calendar → Selected date → Period Editor or Daily Log<br/>Home → Add Period → Period Editor<br/>Home → Daily Check-in → Daily Log<br/>Home → Featured Lesson → Article<br/>Learn → Selected lesson → Article')
sub('Initial layout plan')
table(['Screen','Top to bottom layout'],[
('Home','App name and date; tracking summary; quick log buttons; learning flower and completion count; featured lesson; main navigation.'),
('Calendar','Month selector; calendar grid with labeled indicators; selected-date records; add or edit actions; main navigation.'),
('Record editors','Screen title; date controls; applicable log fields; inline validation messages; Save button; Delete action for existing entries.'),
('Learn','Screen title; category and saved filters; lesson cards with completion indicators; main navigation.'),
('Article','Back control; lesson title and topic; body content; knowledge check and explanation; source links; bookmark and completion controls.')
],[110,385])
p('Activities will load database records using the received IDs and handle missing or invalid IDs with clear messages. Successful saves will return users to the originating screen and refresh the displayed information. Unsaved changes will prompt before being discarded.')
p('The interface will use readable text, sufficient contrast, descriptive labels, accessible touch targets, and flower accents. Calendar states will use text or symbols as well as color. The application will avoid conveying health status through flower appearance.')
page()
h('7 AI Use Plan')
p('ChatGPT and Codex have assisted with brainstorming and proposal drafting. During development, AI may also support explanations, debugging, code review, and test planning. The student developers remain responsible for understanding, modifying, verifying, and explaining all submitted work.')
table(['AI assistance','Review and verification'],[
('Planning and documentation','Review the title, scope, database, and navigation against the project requirements; revise them to match the approved implementation.'),
('Educational drafts','Check each health statement against cited guidance; preserve context and uncertainty; record sources and review dates.'),
('Kotlin and debugging','Inspect generated code, explain its behavior, and test it in Android Studio before accepting it.'),
('Testing support','Use suggested scenarios as plans; document only actual results, defects, corrections, and improvements.')
],[140,355])
p('The AI interaction log will record the tool, date, purpose, significant prompt, accepted output, and changes or verification performed. Prompts will use fictional data only. A final declaration will report actual AI use. Testing results will be documented after implementation rather than presented as completed in this proposal.')

def footer(canvas,doc):
 canvas.setFont('Helvetica',8); canvas.setFillColor(colors.HexColor('#62535c'))
 canvas.drawString(50,28,'Blossom | CS0011 Project Proposal')
 canvas.drawRightString(A4[0]-50,28,str(doc.page))
pdf=out/'Blossom_Project_Proposal.pdf'
SimpleDocTemplate(str(pdf),pagesize=A4,rightMargin=50,leftMargin=50,topMargin=40,bottomMargin=45,title='Blossom Project Proposal',author='').build(story,onFirstPage=footer,onLaterPages=footer)
doc=pdfium.PdfDocument(str(pdf)); qa=Path('tmp/pdfs/blossom'); qa.mkdir(parents=True,exist_ok=True)
for i in range(len(doc)):
 doc[i].render(scale=1.3).to_pil().save(qa/f'page-{i+1}.png')
print(f'Created {pdf.resolve()} | {len(doc)} pages')

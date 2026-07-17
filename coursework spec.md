Requirements Specification
## Malabe Tuk-Tuk & Three-Wheeler Spares Depot — JavaFX Inventory Management System

---

## 1. Overview / Purpose

This coursework requires developing a **JavaFX-based GUI desktop application** for the *Malabe Tuk-Tuk & Three-Wheeler Spares Depot*, a small local spare-parts business that currently manages inventory and dealer information using **manually maintained, messy legacy text files**.

The assessment measures whether students can:
- Design, code, compile, test, execute, explain, and defend a complete Java application using fundamental programming and OOP concepts.
- Work with imperfect, real-world dirty data.
- Make reasonable implementation assumptions where the spec is silent, and document/defend them.

**Important:** This coursework *intentionally* contains realistic ambiguity. Where the spec doesn't prescribe an exact implementation detail, you must:
- Make a reasonable technical assumption.
- Document it clearly in the report.
- Implement it consistently.
- Test it where relevant.
- Defend it in the Viva.
- Assumptions **must not** reduce required functionality or be used to dodge difficult parts of the coursework.

---

## 2. Objective

Build a JavaFX GUI application that allows the user to:
- Manage spare parts (add/update/delete).
- Search and filter inventory.
- Monitor low-stock items.
- Randomly select dealers.
- Process checkout (point-of-sale) transactions.
- Update stock quantities.
- Write audit logs.
- Save/load all data using standard `.txt` files.

The app must demonstrate **foundational Java programming ability** — see Constraints (Section 5) for what is disallowed.

---

## 3. Functional Requirements

### 3.1 Low-Stock Monitoring
- Identify and display all items where quantity is below a **configurable low-stock threshold**.
- The legacy inventory data does **not** include a threshold field — you must decide how the threshold is stored, configured, or initialized, and **document this assumption**.
- The low-stock warning area must update automatically whenever stock quantities change.

### 3.2 Add / Update / Delete Parts
- Users can add new parts, update existing part details, and delete parts — all through the GUI.
- Input validation must be applied.
- Duplicate or invalid records must be handled gracefully.
- Clear user feedback must be given after every operation.

### 3.3 View Inventory (Display & Grouping)
- Display all inventory items in a formatted table.
- Items must be **grouped by category**, then **ordered by part code in ascending order within each category**.
- Display must show:
  - Total number of parts.
  - Total monetary value of the inventory.

### 3.4 Multi-Criteria Search / Filtering
- Users must be able to combine **at least three search filters simultaneously** (e.g., category, price range, keyword).
- Search/filter logic must be implemented **manually** using foundational Java logic (see constraints — no Streams/lambdas).

### 3.5 Random Dealer Selection
- Load dealer data from the legacy dealer text file.
- Randomly select **exactly four unique dealers**.
- Display their full details **sorted alphabetically by location**.
- Must implement own logic to:
  - Prevent duplicate dealer selection.
  - Sort dealer records (no built-in sort for assessed logic).

### 3.6 Point-of-Sale (POS) Cart & Checkout
- Users can select multiple items, specify quantities, add to cart, and process the cart as a transaction.
- Must prevent invalid sales:
  - Zero quantities
  - Negative quantities
  - More units than are in stock
  - Processing an empty cart
- **Discount rules (apply in this order):**
  1. **Bulk discount:** If buying **3 or more units of any single item**, apply a **5% discount to that item's subtotal**.
  2. **Synergy discount:** If the cart contains **at least one Engine part AND at least one Electrical part**, apply a **10% discount to the entire cart total** — applied *after* item-level (bulk) discounts.

### 3.7 Audit Logging
- Every data-modifying action must be written to an **append-only** `audit_log.txt` file (never overwrite previous entries).
- Each log entry must include: timestamp, action taken, affected item code, and quantity (where relevant).
- At minimum, these actions must be logged:
  - Adding a part
  - Deleting a part
  - Processing a checkout transaction

### 3.8 File-Based Persistence / Dirty Data Parsing
- Load inventory and dealer data from standard text files on startup; save updated state when changes are made.
- The parser must robustly handle **dirty legacy data**, including:
  - Inconsistent delimiters (commas, pipes `|`, semicolons — sometimes mixed within the same file)
  - Extra/inconsistent spacing
  - Missing non-critical fields
  - Currency format variations (e.g., `4500.00`, `Rs. 4500.00`, `Rs850`, `1250`)
  - Inconsistent capitalisation of categories (e.g., `Engine`, `ENGINE`, `electrical`, `ELECTRICAL`)
  - Varied date formats (e.g., `2023-10-12`, `12/05/2023`, `Oct 15, 2023`, `01-02-2024`, `2023/11/20`, `15-Aug-2023`)
- **A simple `.split(",")` approach is explicitly stated as NOT sufficient.**
- Must not crash on invalid/malformed records.

### 3.9 OOP Design
- Must use classes, objects, encapsulation, and appropriate OOP design throughout.
- Inheritance, abstraction, and polymorphism should be used **where they genuinely improve the design** — not artificially shoehorned in just to tick a box (this will not be rewarded).

---

## 4. Format / Delivery Requirements

1. **Full functionality delivered via a JavaFX GUI application.** User input must come through GUI controls and be validated before processing. Data must be saved to and loaded from text files.
2. **No databases allowed.** For sorting used in assessed features, students must **not** use built-in Java sorting methods — marks are only given for demonstrating and being able to explain your **own** sorting algorithm in the Viva.
3. JavaFX freedom of interface design is allowed; Scene Builder / FXML are permitted **provided you can explain the resulting structure/code in the Viva**.
4. OOP (classes, objects, encapsulation, and where appropriate inheritance/abstraction/polymorphism) must be used meaningfully — not just for show.
5. **A JUnit test suite is required**, covering the main non-GUI logic, with particular focus on:
   - Dirty-data parsing
   - Search/filter logic
   - Manual sorting
   - Dealer selection
   - Cart calculation
   - Discount rules
   - Stock deduction
   - Low-stock detection
   - Invalid input handling
   - Audit logging (where practical)

---

## 5. Constraints (Technical Restrictions) — READ CAREFULLY

These are hard restrictions on **assessed** searching, filtering, sorting, dealer selection, and parsing logic:

### ✅ Allowed / Required foundational constructs
- Variables, classes and objects
- Arrays or Lists
- Loops
- Conditional statements
- Methods
- Manual comparisons
- Basic exception handling
- Standard Java and JavaFX classes for: GUI construction, file reading/writing, collections **storage**, normal application structure

### ❌ Explicitly Prohibited (for assessed logic)
- `Collections.sort()`
- `List.sort()`
- Java Streams API
- Lambda-based filtering pipelines
- Third-party parsing libraries
- Third-party data-processing libraries
- Database query engines
- Any built-in sorting method for assessed sorting tasks

### ❌ Data storage restrictions
- **All data must be stored in `.txt` files.**
- No SQL databases
- No NoSQL databases
- No embedded databases
- No cloud databases
- No ORM frameworks

### ⚠️ Additional constraints embedded in requirements
- A simple `.split(",")` parser is explicitly called out as **insufficient** — the parser must handle mixed/inconsistent delimiters robustly.
- Sorting, filtering, and dealer-selection logic must be **your own, explainable code** — not just functionally correct but defensible in the Viva.
- Assumptions made to handle spec ambiguity must **not** reduce required functionality or let you skip hard parts.
- Artificial/cosmetic use of OOP purely to satisfy the report will not be rewarded.

---

## 6. Provided Legacy Data Files (samples)

### `inventory_legacy.txt` (mixed delimiters, dirty data — illustrative)
```
P001, Bajaj 4-Stroke Piston, Bajaj, Rs. 4500.00, 15, Engine, 2023-10-12, piston4s.jpg
P002|TVS King Brake Pad|TVS|1250|8|Brakes|12/05/2023|brakepad.png
P003; 205/50-10 Tyre ; ; 6500.00; 24; Bodywork; Oct 15, 2023;
P004| Spark Plug NGK | NGK | Rs850 | 50 | electrical | 2024-01-05 | spark.jpg
P005,Clutch Cable Bajaj RE|Bajaj; 950.00, 12 , ENGINE , 01-02-2024 , cl_cable.jpg
P006 ; Headlight Bulb 12V ;  ; Rs. 450 | 30 ; ELECTRICAL ; 2023/11/20 ; hl_bulb.jpg
P007, 3-Wheeler Canopy Cover, Local, 8500.50, 5, BODYWORK, 15-Aug-2023, canopy.png
P008|Piaggio Ape Filter|Piaggio|Rs.1100.00| 0 | Engine | 2024-02-28 | filter_ape.jpeg
P009, Ignition Coil 2-Stroke , Bajaj , 2200 , 18 ; electrical ; 2023-09-01 ;
P010| Rear View Mirror | ; 800.00 | 45 | Bodywork | 10/10/2023 | mirror.png
```

### `dealers_legacy.txt` (mixed delimiters, dirty data — illustrative)
```
D101, Sunil Motors, 0771234567, Malabe
D102|Kaduwela Spares Hub|0719876543|Kaduwela
D103; Ranatunga Auto; ; Pittugala
D104, Maharagama Tuk Parts, 0705556666 ,Maharagama
D105|Nimal & Sons|0778889999| Malabe
D106 ; Athurugiriya Auto ; 0721112222 ; Athurugiriya
D107, Koswatta Three-Wheelers, , Koswatta
D108|Weliweriya Spares| 0764445555 |Weliweriya
```

> Note: These are sample/illustrative dirty records — your parser should be designed to generalise to similarly-shaped dirty data, not just these exact lines.

---

## 7. Grading Criteria Summary (for reference)

| Criterion | Weight |
|---|---|
| Low-stock monitoring, Add/Delete/Update items + validation | 10% |
| GUI menu system with validations | 10% |
| Git history, authorship, and Viva performance | 10% |
| Inventory table, totals, grouping, and manual sorting | 10% |
| Dirty data parsing and file handling | 10% |
| Point-of-sale cart and discount logic | 10% |
| Dealer management and random selection | 10% |
| Audit logging | 10% |
| OOP usage, code quality, and maintainability | 10% |
| Report, JUnit test plan, and assumptions | 10% |

Each criterion is graded A (Excellent/Outstanding) through F (Unsatisfactory/Fail). Key recurring themes across the grading bands:
- **Top marks require:** correct, robust functionality **AND** the ability to confidently explain/defend/modify the code live in the **Viva**.
- **Using prohibited built-in sorting** for assessed logic caps you at the lowest band regardless of functional correctness.
- **Git history** must show natural, progressive, credible commits — suspicious or thin history hurts this criterion independent of code quality.
- Missing/non-functional core features (GUI won't run, parser crashes on supplied data, no meaningful test plan) result in the lowest grade band (F) for that criterion.

---

## 8. Key Things Not to Miss (Quick Checklist)

- [ ] Configurable low-stock threshold (decide + document your approach)
- [ ] Add / Update / Delete parts with validation + feedback
- [ ] Inventory table grouped by category, sorted by part code (own sort algorithm), with total count + total value
- [ ] Multi-criteria search (≥3 filters combinable), manual logic only
- [ ] Random selection of exactly 4 unique dealers, sorted alphabetically by location (own logic, no duplicates)
- [ ] POS cart: multi-item, quantity validation, stock deduction, prevents zero/negative/over-stock/empty-cart sales
- [ ] Bulk discount (5%, ≥3 units of one item) applied before synergy discount (10%, Engine + Electrical present) — correct order matters
- [ ] Append-only `audit_log.txt` with timestamp, action, item code, quantity — logs add/delete/checkout at minimum
- [ ] Dirty-data parser handling mixed delimiters, spacing, missing fields, currency formats, capitalisation, date formats — no naive `.split(",")`
- [ ] No databases; all persistence via `.txt` files
- [ ] No `Collections.sort()`, `List.sort()`, Streams, lambdas, or third-party libs for assessed logic
- [ ] Meaningful (not cosmetic) OOP: classes, encapsulation, and inheritance/abstraction/polymorphism where genuinely justified
- [ ] JUnit tests covering: parsing, search/filter, sorting, dealer selection, cart calc, discounts, stock deduction, low-stock detection, invalid input, logging
- [ ] Report: flow charts, class docs, JUnit tests, test plan, assumptions, robustness discussion, conclusion
- [ ] Credible, progressive Git history
- [ ] Be ready to explain and defend every part of the code in the Viva — including any Scene Builder/FXML structure if used
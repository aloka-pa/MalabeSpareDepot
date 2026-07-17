# Malabe Tuk-Tuk and Three-Wheeler Spares Depot

A JavaFX desktop application for inventory management, dealer handling, low-stock monitoring, and point-of-sale checkout using messy legacy text files.

This repository is built for coursework assessment with strong focus on:
- Fundamental Java and OOP practice
- Manual logic implementation for sorting, filtering, and dealer selection
- Robust parsing of dirty real-world text data
- File-based persistence using plain text files

## Table of Contents

- [1. Project Objective](#1-project-objective)
- [2. Coursework Compliance Summary](#2-coursework-compliance-summary)
- [3. Core Features](#3-core-features)
  - [3.1 Inventory Management](#31-inventory-management)
  - [3.2 Inventory Display and Grouping](#32-inventory-display-and-grouping)
  - [3.3 Low-Stock Monitoring](#33-low-stock-monitoring)
  - [3.4 Multi-Criteria Search](#34-multi-criteria-search)
  - [3.5 Dealer Selection](#35-dealer-selection)
  - [3.6 Checkout and POS Rules](#36-checkout-and-pos-rules)
  - [3.7 Audit Logging](#37-audit-logging)
- [4. Architecture Overview](#4-architecture-overview)
- [5. Key Design Decisions](#5-key-design-decisions)
  - [5.1 Threshold Storage Assumption](#51-threshold-storage-assumption)
  - [5.2 Parser Strategy for Dirty Data](#52-parser-strategy-for-dirty-data)
  - [5.3 Manual Sorting Implementation](#53-manual-sorting-implementation)
  - [5.4 Search Matching Strategy](#54-search-matching-strategy)
- [6. Data Files](#6-data-files)
  - [6.1 inventory_legacy.txt](#61-inventory_legacytxt)
  - [6.2 dealers_legacy.txt](#62-dealers_legacytxt)
  - [6.3 settings.txt](#63-settingstxt)
  - [6.4 audit_log.txt](#64-audit_logtxt)
- [7. Screenshot Guide](#7-screenshot-guide)
- [8. Known Assumptions](#8-known-assumptions)
- [9. Author and Module Information](#9-author-and-module-information)

## 1. Project Objective

The system supports end-to-end spare-parts operations for a small depot:
- Add, update, delete inventory items
- Search inventory with multiple simultaneous filters
- Show low-stock warnings using a configurable threshold
- Load dealers and randomly choose four unique dealers
- Process cart checkout with business discount rules
- Deduct stock after successful checkout
- Append audit logs for critical actions

## 2. Coursework Compliance Summary

This implementation is designed around the specification constraints.

### Required and implemented
- JavaFX GUI application
- Text file based persistence
- Dirty-data parsing
- Manual sorting logic
- Manual search and filtering logic
- Random unique dealer selection
- Cart and checkout with ordered discount rules
- Low-stock threshold configuration and monitoring
- Audit logging for modifying actions

### Explicitly avoided in assessed logic
- Collections.sort
- List.sort
- Java Streams for assessed search and sorting paths
- Lambda-based filtering pipelines for assessed logic
- Third-party parsing libraries
- Databases and ORM tools

## 3. Core Features

### 3.1 Inventory Management
- Add new parts with validation
- Update existing parts
- Delete parts
- Prevent duplicate item codes
- Show clear operation status messages

### 3.2 Inventory Display and Grouping
- Inventory displayed in a table
- Grouping and ordering behavior follows category first, then code ascending
- Total quantity and total inventory value displayed

### 3.3 Low-Stock Monitoring
- Threshold is configurable
- Items below threshold are displayed in warning panel
- Refreshes automatically after inventory changes and checkout events

### 3.4 Multi-Criteria Search
- Supports combining multiple filters in one query
- Typical filter set:
  - Keyword
  - Category
  - Minimum price
  - Maximum price
  - Minimum quantity

### 3.5 Dealer Selection
- Loads dealer file with dirty records
- Selects exactly four unique dealers at random if enough records exist
- Selected dealers shown sorted alphabetically by location

### 3.6 Checkout and POS Rules
- Cart supports multiple items
- Invalid sales prevented:
  - Empty cart
  - Zero quantity
  - Negative quantity
  - Quantity above stock
- Discount order enforced:
  1. Item-level bulk discount: 5% on a line if quantity is 3 or more
  2. Cart-level synergy discount: 10% if cart has both Engine and Electrical categories, after bulk discounts
- Successful checkout deducts stock and records audit entries

### 3.7 Audit Logging
- Append-only logging
- Logs at minimum:
  - Add part
  - Delete part
  - Checkout
- Includes timestamp, action, item code, and quantity context

## 4. Architecture Overview

The codebase follows a layered structure for clean separation of responsibilities.

- app layer
  - JavaFX UI composition and event handlers
- model layer
  - Domain entities and cart math
- parser layer
  - Dirty legacy text parsing and canonical serialization
- persistence layer
  - Text file load and save repositories plus audit logging
- service layer
  - Business rules and application orchestration
- util layer
  - Manual sort, category normalization, and validation helpers

Benefits of this structure:
- Lower coupling between UI and business behavior
- Better maintainability and extension potential

## 5. Key Design Decisions

### 5.1 Threshold storage assumption
The legacy inventory file does not include threshold data. Threshold is stored in a dedicated settings text file using a key-value format.

Reason:
- Keeps inventory data focused on part records
- Allows threshold updates without rewriting inventory format
- Easy to validate and explain

### 5.2 Parser strategy for dirty data
The parser accepts mixed delimiters and inconsistent formatting by normalizing separators and sanitizing token values.

Handled data variations include:
- Mixed commas, pipes, semicolons
- Extra spacing
- Missing non-critical fields
- Currency formatting variants
- Category capitalization variants
- Multiple date formats

Invalid or malformed records are skipped safely to avoid application crashes.

### 5.3 Manual sorting implementation
Sorting is implemented with custom algorithm logic rather than built-in sorting APIs.

Used for:
- Parts by category then code
- Dealers by location then code

Reason:
- Meets assessment requirement
- Ensures algorithm can be defended and modified during viva

### 5.4 Search matching strategy
Search is implemented with manual conditional checks:
- Category equality check after normalization
- Keyword containment over combined searchable fields
- Numeric bound checks for price and quantity

Reason:
- Transparent and explainable logic
- Fully compliant with no-streams assessed path

## 6. Data Files

Expected runtime files in repository root:
- inventory_legacy.txt
- dealers_legacy.txt
- settings.txt
- audit_log.txt (generated during runtime)

### 6.1 inventory_legacy.txt
Contains part records from legacy source with dirty formatting possibilities.

### 6.2 dealers_legacy.txt
Contains dealer records with mixed quality and delimiters.

### 6.3 settings.txt
Stores low stock threshold in key-value format.

Example format:
lowStockThreshold=10

### 6.4 audit_log.txt
Append-only runtime log generated by application actions.

## 7. Screenshot Guide

### 7.1 Main dashboard
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/3dfb8bd6-0640-4d58-a2ac-67533b73535b" />

### 7.2 Add part
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/07a543e4-5035-474e-ba42-a7e0acd4d9b4" />

### 7.3 Add part validation 
Note: Full input validation for part creation and updates across every required attribute (ID, name, category, price, and stock).

<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/4e8760ad-b694-4a4d-9b21-3dd3f20562e0" />


### 7.4 Search filters combined
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/538326c3-afe3-4736-974a-eeffea150243" />
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/fd99bdb9-24cd-4ca3-a1e1-ecfa684bc1f5" />
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/5e2d63f6-5e95-423f-ba4e-5afa6de28245" />

### 7.5 Low-stock warnings
<img width="1913" height="1077" alt="image" src="https://github.com/user-attachments/assets/dbcb0f03-8eea-4824-81a2-a60441a2a20a" />


### 7.6 Dealer random selection
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/415e5ed0-0dd0-4137-947f-1e3e997fe979" />
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/3af28072-01f0-4457-a22e-9447d15a3eee" />
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/1b427857-40b4-4245-be90-5bfad07d8588" />
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/cbc1b5af-14cd-44c3-8bae-25bf6ae46872" />


### 7.7 Checkout cart and totals
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/c098077a-7553-4b16-bf33-4c1f833fe8a9" />


### 7.8 Checkout completed
<img width="1918" height="1079" alt="image" src="https://github.com/user-attachments/assets/8f596bca-a647-4174-8dc5-7d4e466d40f8" />


### 7.9 Audit log sample
<img width="821" height="271" alt="image" src="https://github.com/user-attachments/assets/1f131291-0654-4d17-aa30-365dee8fbab5" />


## 8. Known Assumptions

- Part codes are expected to follow prefix plus digits pattern
- Missing non-critical fields are tolerated when possible
- Unknown categories are normalized to a readable value
- Threshold defaults to 10 when settings file is missing or invalid
- Dealer count lower than four returns all available unique dealers

## 9. Author and Module Information

- Module: Java and OOP coursework project
- Project: Malabe Tuk-Tuk and Three-Wheeler Spares Depot Inventory System
- Technology: Java, JavaFX, plain text persistence

If you are reviewing this repository, check the screenshot section first and then architecture for the fastest understanding of the implementation.

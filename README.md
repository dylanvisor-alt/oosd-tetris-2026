# oosd-tetris-2026
Object Oriented Software Development (2006ICT) - Milestone 1 - Tetris Game

---

## Project Overview

This repository contains the JavaFX Tetris implementation developed for the 2006ICT Object Oriented Software Development course at Griffith University (2026).

The goal of this project is to rebuild the classic Tetris game with proper object oriented design behind it, not just a working game. It should demonstrate GRASP responsibilities, SOLID design, and design patterns including Factory, Singleton, Observer, and MVC.

This is a Java project using:

- Java 17
- JavaFX
- Maven

---

## Project Structure

- `pom.xml` at the root, standard Maven layout, no nested projects
- `src/main/java` for all source code
- `src/test/java` for unit tests
- `docs/` for diagrams and the milestone submission document

---

## Team Workflow

### Development Approach
Each team member is responsible for one specific part of the project, split between UI screens, core game logic, testing, and documentation.

### Assignment Strategy
- Work individually on your assigned area
- Focus on getting logic correct first, then polish the UI
- Push your work in small commits as you go, not all at once at the end
- Open a Pull Request when a feature is ready, and get it reviewed before merging into main

---

## Page and Component Responsibilities (Example Split)

- Splash screen and main menu
- Configuration screen
- High score screen
- Core gameplay (board, tetromino logic, collision, rotation)
- Pause and exit handling
- Testing and bug tracking
- Documentation, diagrams, and submission compilation

---

## Rules

- Do not overwrite other people's files
- Only edit files you are responsible for
- If unsure, ask before changing shared files or core classes

---

## Code Standards

### Java Standards
- Follow standard Java naming conventions
- Keep classes focused on a single responsibility
- Use interfaces and abstract classes where they genuinely fit the design, not just to tick a box
- Keep code clean, readable, and consistently indented
- Avoid unnecessary complexity

### JavaFX Standards
- Keep UI screens separate from game logic
- Keep styling in the shared stylesheet
- Avoid inline styling where possible
- Maintain consistent spacing and layout across screens

---

## Design Standards (Critical for Marks)

The game must be:

- Simple to navigate
- Clear in its controls and feedback
- Not overwhelming

### Avoid:
- Cluttered or inconsistent screens
- Over complicated menu structures
- Features that are not part of the milestone scope

### Focus on:
- Smooth gameplay movement
- Clear visual hierarchy on menus
- Clean, consistent styling across all screens

---

## Final Integration Process

At the end of development:

### One team member will:
- Combine all completed work
- Fix any broken navigation between screens
- Ensure consistent styling across the project
- Clean up code before submission

### Team will:
- Review together
- Test all screens and navigation
- Confirm the demo video covers every required feature

---

## What NOT to Do

- Do not add features outside the milestone scope
- Do not overcomplicate the design
- Do not use non standard project structures
- Do not change another team member's work without discussion

---

## Definition of Done

A feature or screen is complete when:

- It is structured correctly
- It follows the design standards above
- It is styled consistently with the rest of the project
- It works correctly and links properly to the rest of the game

---

## Final Goal

A clean, working Tetris prototype that demonstrates:

- Solid object oriented design
- Smooth, functional gameplay
- Strong alignment with the Milestone 1 marking criteria

---

## Key Principle

Keep it simple. If it feels overcomplicated, it is probably wrong.
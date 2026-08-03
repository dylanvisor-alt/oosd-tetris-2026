JavaFX Tetris implementation for 2006ICT Object-Oriented Software Development, Griffith University (2026). Built with Maven/IntelliJ using OO design patterns (GRASP, Factory, Singleton, Observer, MVC).

This is our group's build of an enhanced Tetris game for the 2006ICT Object-Oriented Software Development course at Griffith University (2026). The brief is to take the classic 1984 Alexey Pajitnov game and rebuild it properly — with real OO design behind it, not just a working game — using JavaFX for the interface and Maven to manage the build.

We're not just aiming to tick the marking criteria boxes. The goal is a codebase that actually demonstrates the design patterns and principles we're learning in the course: GRASP responsibilities, SOLID design, and a handful of the classic Gang-of-Four patterns (Factory for spawning tetrominoes, Observer for score/board updates, Singleton for scene management, MVC as the overall architecture). If it's built well, extending it for Milestone 2 should be straightforward rather than a rewrite.

Team
Name	Student ID	Role	What they own
...	...	Project Manager	Timeline, task tracking, submission compilation
...	...	Lead Programmer	Core game logic (Board, Tetromino, collision/rotation)
...	...	UI Programmer	JavaFX screens, styling, navigation
...	...	Tester / QA	Unit tests, manual playtesting, bug tracking
...	...	Documentation Lead	Requirements docs, diagrams, README, submission doc

Team Leader: ... Lab Teacher: ... Campus: ...

Roles above are how we've split the work, not strict lanes — everyone touches code and everyone reviews PRs. GitHub history should reflect that spread of contribution, since it's explicitly part of how this milestone gets marked.

Why this structure

We went with a fairly conventional MVC-style split because it maps cleanly onto what the markers are looking for (interfaces, abstract classes, records) without us having to force it:

model/ holds the actual game data and rules — the board, the tetromino shapes, score entries, config. This is where most of the "real" OO design lives: Tetromino is an abstract class, each shape (IPiece, TPiece, etc.) extends it, and a Movable/Rotatable interface sits alongside it so pieces can be handled polymorphically rather than with a pile of if/else checks on piece type.
ui/ is purely JavaFX — screens and layout, nothing about game rules. Each screen from the spec (splash, main menu, config, high scores, game, pause, exit) gets its own class so they're independently testable and easy to demo individually.
controller/ is the glue — handles input, drives the game loop, talks to both the model and the UI without either of those knowing about each other directly.
util/ is small helper stuff — SceneManager for switching between screens (a natural Singleton, since there's only ever one active scene graph) and shared constants (board dimensions, timing values, etc.).
Tech Stack
Java 17
JavaFX — chosen because it's the framework the course specifically wants us to demonstrate, and it handles the animation/timeline stuff Tetris needs (smooth piece movement) better than plain Swing would.
Maven — for dependency management and because the marking rubric explicitly requires a standard Maven project layout.
IntelliJ IDEA — our IDE of choice across the team.
What's actually working for Milestone 1

This milestone isn't the finished game — it's the foundation plus four specific screens, demoed in a short video. What needs to genuinely work by the deadline:

Splash screen displays on launch, shows group/course info, sits for a few seconds
Main menu with all four buttons functional (Play, Configuration, High Scores, Exit)
Configuration screen where every control is actually interactive — checkboxes toggle visibly, sliders show their current value as you drag them. Doesn't need to be wired into real gameplay yet, it just needs to work as a UI.
High score screen showing a top-10 list (dummy data is fine for now), with a Back button
Core gameplay on a 10x20 field, with smooth piece movement — meaning the piece visibly slides between rows during normal automatic descent rather than teleporting from one row to the next. This only applies to the normal timer-driven drop, not to hard-drop/soft-drop via the down key.
Row detection and clearing, including clearing multiple full rows at once
Pause and resume via the P key, with a visible "paused" message
Exit button opens a confirmation dialog — Yes quits, No returns to the main menu
Running the Project
Clone the repo:
bash
   git clone https://github.com/dylanvisor-alt/oosd-tetris-2026.git
Open the folder in IntelliJ IDEA — it should auto-detect the pom.xml and import as a Maven project. Let it download dependencies (JavaFX especially can take a minute the first time).
Run Main.java.

If JavaFX doesn't launch and you get a runtime module error, check the Maven JavaFX plugin config in pom.xml — this is a common first-run issue with JavaFX + Maven and usually means the run configuration needs the JavaFX plugin's javafx:run goal rather than just hitting the regular green Run arrow on Main.java.

Git Workflow
main stays stable — it should always be in a state we could demo from
New work happens on a feature branch, named for what it does (feature/config-screen, feature/board-collision, etc.)
Merges into main go through a Pull Request, and at least one other team member reviews it before it merges — this isn't just process for its own sake, GitHub Advanced (PRs, reviews, tags) is directly marked
Tags get added at meaningful checkpoints (e.g. milestone-1-submission) so there's a clear point-in-time snapshot tied to what we actually submitted
Commit Practices

Aiming for commits that are small enough to tell a story — not one giant "finished everything" commit, and not fifty commits in the same ten minutes right before the deadline either, since that pattern loses marks on its own. Commit messages describe what changed and why, not just "update" or "fix."

Milestone Progress
 Milestone 1 — planning docs, requirements, use case + activity diagrams, splash / main menu / configuration / high score screens, core gameplay loop with smooth movement, pause, exit confirmation
 Milestone 2 — full gameplay completion (to be scoped once M1 spec details drop)
Notes / Known Issues

(Keep this section updated as we go — anything half-built, any known bugs, anything the next person picking up the code should be aware of.)
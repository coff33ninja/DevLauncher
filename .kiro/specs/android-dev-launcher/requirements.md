# Requirements Document: Android Developer Launcher

## Introduction

This document specifies the functional and non-functional requirements for the Android Developer Launcher, a **plugin-based** home screen replacement for Android developers.

### Core Architecture

- **Kotlin-Only**: No Python backend. Pure Kotlin/Android.
- **BYO Gemini**: Users provide their own Gemini API key (optional). No complex key management.
- **Plugin-First**: All features beyond core launcher are plugins.
- **Command-Driven**: Global search acts as a command palette (Raycast/Spotlight style).

### What Changed from Original Design

**Removed**:
- ❌ Python backend + round-robin key management
- ❌ Complex widget system
- ❌ Built-in everything (speedtest, terminal, docker, etc.)
- ❌ Multi-home-screen with dedicated categories
- ❌ Context analyzer and usage pattern tracking (moved to plugins)

**Added**:
- ✅ Plugin system (the real product)
- ✅ Command registry for global search
- ✅ Event bus for plugin communication
- ✅ Minimal core with maximum extensibility

## Glossary

- **Launcher Core**: Minimal app launcher + global search + plugin manager
- **Plugin**: Modular extension that adds functionality (terminal, AI, etc.)
- **Command**: Registered action in global search (e.g., "ssh", "ask", "run")
- **Event Bus**: Decoupled communication system between plugins
- **Plugin Context**: Controlled access to core functionality for plugins
- **Global Search**: Command palette interface (primary interaction point)
- **BYO**: Bring Your Own (user provides their own API key)

## Requirements

### Requirement 1: Launcher Home Screen Interface

**User Story:** As a developer, I want a clean and efficient home screen interface, so that I can quickly access my development tools and applications.

#### Acceptance Criteria

1. WHEN the user presses the home button, THE Launcher SHALL display the home screen with app grid, search bar, and AI suggestions
2. WHEN the user swipes up on the home screen, THE Launcher SHALL display the app drawer with all installed applications
3. WHEN the user long-presses on the home screen, THE Launcher SHALL enter widget placement mode
4. THE Launcher SHALL support both portrait and landscape orientations on mobile devices
5. THE Launcher SHALL provide adaptive layouts for tablet form factors
6. WHEN the system theme changes, THE Launcher SHALL update its theme to match (light/dark mode)

### Requirement 2: Application Management

**User Story:** As a developer, I want to view, search, and launch my applications efficiently, so that I can quickly switch between development tools.

#### Acceptance Criteria

1. WHEN the app drawer is opened, THE App_Repository SHALL display all installed applications sorted alphabetically
2. WHEN a user taps an app icon, THE Launcher SHALL launch the selected application
3. WHEN a user searches for an app, THE Launcher SHALL filter the app list in real-time based on the search query
4. WHEN an app is launched, THE Usage_Stats_Collector SHALL record the launch event with timestamp
5. THE App_Repository SHALL categorize apps into predefined categories (Development, Communication, Productivity, Utilities, Entertainment, Other)
6. WHEN a user long-presses an app icon, THE Launcher SHALL display quick actions and app info options

### Requirement 3: Gemini AI Integration

**User Story:** As a developer, I want AI-powered suggestions and natural language app search, so that I can work more efficiently with intelligent assistance.

#### Acceptance Criteria

1. WHEN the launcher requests AI suggestions, THE Gemini_Client SHALL use the gemini-2.5-flash model or higher
2. WHEN the Context_Analyzer provides context data, THE Gemini_Client SHALL include it in the API request prompt
3. WHEN the Gemini API returns a response, THE Gemini_Client SHALL parse the response and extract structured suggestions
4. WHEN a user enters a natural language query, THE Gemini_Client SHALL interpret the query and return relevant app suggestions
5. IF the Gemini API request fails, THEN THE Gemini_Client SHALL return cached suggestions or gracefully degrade functionality
6. THE Gemini_Client SHALL limit API requests to essential queries to minimize costs

### Requirement 4: Round-Robin API Key Management

**User Story:** As a developer, I want to use multiple Gemini API keys with automatic rotation, so that I can avoid rate limiting and ensure high availability.

#### Acceptance Criteria

1. WHEN an API request is made, THE Key_Manager SHALL select the next available key using round-robin algorithm
2. WHEN a key returns a rate limit error (HTTP 429), THE Key_Manager SHALL mark the key as RATE_LIMITED and select the next key
3. WHEN a rate-limited key's reset time is reached, THE Key_Manager SHALL automatically change the key status to ACTIVE
4. THE Key_Manager SHALL maintain a SQLite database with key metadata (status, last_used, error_count, rate_limit_reset, total_requests)
5. WHEN all keys are rate-limited, THE Key_Manager SHALL notify the user and temporarily disable AI features
6. THE Key_Manager SHALL distribute requests evenly across all ACTIVE keys over time
7. WHEN a key encounters repeated errors, THE Key_Manager SHALL mark it as ERROR status after 5 consecutive failures

### Requirement 5: API Key Storage and Security

**User Story:** As a developer, I want my API keys stored securely, so that they cannot be compromised or exposed.

#### Acceptance Criteria

1. WHEN a user adds an API key, THE Settings_Manager SHALL encrypt the key using Android Keystore before storage
2. WHEN the application retrieves an API key, THE Settings_Manager SHALL decrypt it using Android Keystore
3. THE Settings_Manager SHALL never log API keys in plaintext
4. WHEN exporting settings, THE Settings_Manager SHALL exclude API keys from the export file
5. THE Settings_Manager SHALL validate API key format before accepting it (must match Google API key pattern)

### Requirement 6: Usage Statistics and Context Analysis

**User Story:** As a developer, I want the launcher to learn my usage patterns, so that it can provide relevant app suggestions based on context.

#### Acceptance Criteria

1. WHEN an app is launched, THE Usage_Stats_Collector SHALL record the app package name, timestamp, and context (time of day, day of week)
2. THE Context_Analyzer SHALL run every 15 minutes when the screen is on
3. WHEN the Context_Analyzer runs, THE Context_Analyzer SHALL collect current context data (time, connected devices, location if permitted)
4. THE Usage_Stats_Collector SHALL calculate app usage frequency over the last 7 days
5. WHEN generating AI suggestions, THE Context_Analyzer SHALL provide usage patterns and current context to the Gemini_Client
6. WHERE the user has disabled usage tracking, THE Usage_Stats_Collector SHALL not collect any usage data

### Requirement 7: Settings and Configuration

**User Story:** As a developer, I want to configure launcher preferences and manage API keys, so that I can customize the launcher to my needs.

#### Acceptance Criteria

1. THE Settings_Manager SHALL provide a settings screen accessible from the home screen menu
2. WHEN a user adds an API key, THE Settings_Manager SHALL validate and store it in the encrypted key pool
3. WHEN a user removes an API key, THE Settings_Manager SHALL delete it from the key pool and update the Key_Manager
4. THE Settings_Manager SHALL allow users to enable/disable AI suggestions
5. THE Settings_Manager SHALL allow users to enable/disable usage tracking
6. THE Settings_Manager SHALL allow users to select theme (Light, Dark, System)
7. THE Settings_Manager SHALL allow users to configure app grid size (3x4, 4x5, 5x6)

### Requirement 8: Widget Support

**User Story:** As a developer, I want to place Android widgets on my home screen, so that I can view information at a glance.

#### Acceptance Criteria

1. WHEN a user long-presses the home screen, THE Widget_Host SHALL display available widgets
2. WHEN a user selects a widget, THE Widget_Host SHALL place it on the home screen at the selected position
3. WHEN a widget is placed, THE Widget_Host SHALL manage its lifecycle (updates, configuration)
4. WHEN a user long-presses a widget, THE Widget_Host SHALL allow resizing or removal
5. THE Widget_Host SHALL persist widget configuration across launcher restarts

### Requirement 9: Performance and Responsiveness

**User Story:** As a developer, I want the launcher to be fast and responsive, so that it doesn't slow down my workflow.

#### Acceptance Criteria

1. WHEN the home button is pressed, THE Launcher SHALL display the home screen within 200 milliseconds
2. WHEN the app drawer is opened, THE Launcher SHALL render the app list within 300 milliseconds
3. THE Launcher SHALL cache app icons in memory using an LRU cache with maximum 100 entries
4. WHEN a search query is entered, THE Launcher SHALL debounce input by 300 milliseconds before filtering
5. THE Launcher SHALL load app icons asynchronously to avoid blocking the UI thread
6. THE Gemini_Client SHALL timeout API requests after 30 seconds

### Requirement 10: Error Handling and Resilience

**User Story:** As a developer, I want the launcher to handle errors gracefully, so that it remains functional even when services fail.

#### Acceptance Criteria

1. WHEN the Gemini API is unreachable, THE Gemini_Client SHALL return cached suggestions or disable AI features temporarily
2. WHEN an API key is invalid (HTTP 401), THE Key_Manager SHALL mark it as ERROR and notify the user
3. WHEN all API keys are exhausted, THE Launcher SHALL continue functioning without AI features
4. IF an app fails to launch, THEN THE Launcher SHALL display an error toast and log the error
5. WHEN the Key_Manager database is corrupted, THE Key_Manager SHALL recreate the database schema
6. THE Launcher SHALL implement retry logic with exponential backoff for network errors (max 3 retries)

### Requirement 11: Privacy and Permissions

**User Story:** As a developer, I want control over what data the launcher collects, so that I can maintain my privacy.

#### Acceptance Criteria

1. THE Launcher SHALL request QUERY_ALL_PACKAGES permission to access installed apps
2. THE Launcher SHALL request PACKAGE_USAGE_STATS permission to collect usage statistics
3. WHERE the user denies usage statistics permission, THE Launcher SHALL function without AI suggestions
4. THE Launcher SHALL not send sensitive data (contacts, messages, location) to the Gemini API
5. THE Settings_Manager SHALL provide a clear privacy policy accessible from settings
6. WHEN a user disables usage tracking, THE Usage_Stats_Collector SHALL delete all collected usage data

### Requirement 12: Background Services

**User Story:** As a developer, I want background services to run efficiently, so that they don't drain my battery.

#### Acceptance Criteria

1. THE Context_Analyzer SHALL run as a WorkManager periodic job every 15 minutes
2. THE Usage_Stats_Collector SHALL sync usage data daily at midnight using WorkManager
3. THE Key_Manager SHALL check key health every 5 minutes using WorkManager
4. WHEN the device is in battery saver mode, THE Launcher SHALL reduce background service frequency by 50%
5. THE Launcher SHALL use WorkManager constraints to run background jobs only when device is idle or charging

### Requirement 13: Multi-Device Support

**User Story:** As a developer, I want the launcher to work on both phones and tablets, so that I have a consistent experience across devices.

#### Acceptance Criteria

1. THE Launcher SHALL detect device form factor (phone or tablet) at startup
2. WHEN running on a tablet, THE Launcher SHALL use a multi-column layout for the app drawer
3. WHEN running on a phone, THE Launcher SHALL use a single-column layout for the app drawer
4. THE Launcher SHALL support landscape orientation on both phones and tablets
5. THE Launcher SHALL adjust widget grid size based on screen dimensions

### Requirement 14: App Categorization

**User Story:** As a developer, I want apps automatically categorized, so that I can find development tools quickly.

#### Acceptance Criteria

1. WHEN an app is installed, THE App_Repository SHALL categorize it based on package name patterns
2. THE App_Repository SHALL recognize development tools (package names containing "termux", "git", "ide", "editor") as DEVELOPMENT category
3. WHERE automatic categorization fails, THE App_Repository SHALL use the Android PackageManager category
4. THE App_Repository SHALL allow users to manually override app categories
5. WHERE AI features are enabled, THE Gemini_Client SHALL assist with app categorization for ambiguous apps

### Requirement 15: Search Functionality

**User Story:** As a developer, I want powerful search capabilities, so that I can find apps quickly using natural language or keywords.

#### Acceptance Criteria

1. WHEN a user types in the search bar, THE Launcher SHALL filter apps by name in real-time
2. WHEN a user enters a natural language query (e.g., "open my code editor"), THE Gemini_Client SHALL interpret it and suggest relevant apps
3. THE Launcher SHALL support fuzzy matching for app names (e.g., "trm" matches "Termux")
4. THE Launcher SHALL display search results sorted by relevance (exact match, then fuzzy match, then AI suggestions)
5. WHEN no apps match the search query, THE Launcher SHALL display AI-powered suggestions or web search option

### Requirement 16: Data Persistence

**User Story:** As a developer, I want my settings and preferences saved, so that they persist across app restarts.

#### Acceptance Criteria

1. THE Settings_Manager SHALL use Jetpack DataStore for storing user preferences
2. WHEN the launcher is closed, THE Settings_Manager SHALL persist all settings to disk
3. WHEN the launcher starts, THE Settings_Manager SHALL load settings from DataStore
4. THE Key_Manager SHALL persist API key metadata to SQLite database after each status change
5. THE App_Repository SHALL cache app list and categories in memory, refreshing when apps are installed/uninstalled

### Requirement 17: Network Security

**User Story:** As a developer, I want secure network communication, so that my API requests cannot be intercepted.

#### Acceptance Criteria

1. THE Gemini_Client SHALL use HTTPS for all API requests to the Gemini API
2. THE Gemini_Client SHALL implement certificate pinning for the Gemini API domain
3. WHEN a certificate validation fails, THE Gemini_Client SHALL abort the request and log the error
4. THE Gemini_Client SHALL set a 30-second timeout for all network requests
5. THE Gemini_Client SHALL validate API responses to prevent injection attacks

### Requirement 18: Favorites Management

**User Story:** As a developer, I want to mark apps as favorites, so that I can access them quickly from the home screen.

#### Acceptance Criteria

1. WHEN a user long-presses an app icon, THE Launcher SHALL display an option to add/remove from favorites
2. WHEN an app is marked as favorite, THE Launcher SHALL display it on the home screen
3. THE App_Repository SHALL persist favorite status across app restarts
4. THE Launcher SHALL allow users to reorder favorite apps on the home screen via drag-and-drop
5. THE Launcher SHALL limit favorites to a maximum of 20 apps

### Requirement 19: Gesture Navigation

**User Story:** As a developer, I want gesture-based navigation, so that I can interact with the launcher efficiently.

#### Acceptance Criteria

1. WHEN a user swipes up from the bottom, THE Launcher SHALL open the app drawer
2. WHEN a user swipes down from the top, THE Launcher SHALL open the notification shade (system behavior)
3. WHEN a user long-presses the home screen, THE Launcher SHALL enter widget/wallpaper customization mode
4. WHEN a user double-taps the home screen, THE Launcher SHALL open the search bar with focus
5. THE Launcher SHALL support Android gesture navigation (back, home, recent apps)

### Requirement 20: Logging and Debugging

**User Story:** As a developer, I want comprehensive logging, so that I can debug issues with the launcher.

#### Acceptance Criteria

1. THE Launcher SHALL log all API requests and responses (excluding API keys) at DEBUG level
2. THE Key_Manager SHALL log key rotation events and rate limit occurrences at INFO level
3. WHEN an error occurs, THE Launcher SHALL log the full stack trace at ERROR level
4. THE Launcher SHALL provide a debug mode accessible from settings that enables verbose logging
5. WHERE debug mode is enabled, THE Launcher SHALL display a floating debug panel with real-time logs


### Requirement 21: Multi-Home-Screen Architecture

**User Story:** As a developer, I want multiple dedicated home screens organized by category, so that I can quickly access tools relevant to my current workflow without searching.

#### Acceptance Criteria

1. THE Home_Screen_Manager SHALL provide 5 default home screens: Development, Communication, Productivity, Monitoring, and AI Assistant
2. WHEN a user swipes left or right on a home screen, THE Home_Screen_Manager SHALL transition to the adjacent screen with smooth animation
3. WHEN a user taps a navigation indicator dot, THE Home_Screen_Manager SHALL jump directly to the selected screen
4. THE Home_Screen_Manager SHALL remember the last active screen and restore it when the launcher is reopened
5. WHEN a user long-presses the home button, THE Home_Screen_Manager SHALL display a screen picker overlay showing all available screens
6. THE Home_Screen_Manager SHALL allow users to enable/disable individual screens in settings
7. THE Home_Screen_Manager SHALL allow users to reorder screens via drag-and-drop in settings
8. WHEN a user enables "Classic Mode" in settings, THE Home_Screen_Manager SHALL collapse all screens into a single widget-based home screen
9. THE Home_Screen_Manager SHALL persist screen configuration (enabled screens, order, widgets) across launcher restarts
10. WHEN a screen is disabled, THE Home_Screen_Manager SHALL hide it from navigation and skip it during swipe gestures

### Requirement 22: Development Home Screen

**User Story:** As a developer, I want a dedicated Development home screen with developer tools, so that I can access terminals, IDEs, and build tools quickly.

#### Acceptance Criteria

1. THE Development screen SHALL display a terminal widget by default
2. THE Development screen SHALL display a speedtest widget by default
3. THE Development screen SHALL display a Git status widget by default
4. THE Development screen SHALL provide quick access shortcuts to installed IDEs (Android Studio, VS Code, IntelliJ, etc.)
5. THE Development screen SHALL provide quick access shortcuts to terminal apps (Termux, etc.)
6. THE Development screen SHALL provide quick access shortcuts to Git clients
7. THE Development screen SHALL display a build status widget showing recent build results (if configured)
8. THE Development screen SHALL allow users to add custom widgets and app shortcuts
9. WHEN a development-related app is installed, THE Launcher SHALL suggest adding it to the Development screen

### Requirement 23: Communication Home Screen

**User Story:** As a developer, I want a dedicated Communication home screen, so that I can manage messages, emails, and social media separately from development tools.

#### Acceptance Criteria

1. THE Communication screen SHALL display shortcuts to messaging apps (Slack, Discord, Teams, WhatsApp, etc.)
2. THE Communication screen SHALL display shortcuts to email clients
3. THE Communication screen SHALL display shortcuts to social media apps
4. THE Communication screen SHALL display a notification summary widget showing unread counts
5. THE Communication screen SHALL allow users to add custom widgets and app shortcuts
6. WHEN a communication-related app is installed, THE Launcher SHALL suggest adding it to the Communication screen

### Requirement 24: Productivity Home Screen

**User Story:** As a developer, I want a dedicated Productivity home screen, so that I can manage notes, calendar, and tasks efficiently.

#### Acceptance Criteria

1. THE Productivity screen SHALL display a calendar widget by default
2. THE Productivity screen SHALL display shortcuts to note-taking apps (Notion, Obsidian, Google Keep, etc.)
3. THE Productivity screen SHALL display shortcuts to task management apps (Todoist, Trello, etc.)
4. THE Productivity screen SHALL display shortcuts to document editors
5. THE Productivity screen SHALL display a time tracking widget (if configured)
6. THE Productivity screen SHALL allow users to add custom widgets and app shortcuts
7. WHEN a productivity-related app is installed, THE Launcher SHALL suggest adding it to the Productivity screen

### Requirement 25: Monitoring Home Screen

**User Story:** As a developer, I want a dedicated Monitoring home screen with system stats and logs, so that I can monitor device performance and debug issues.

#### Acceptance Criteria

1. THE Monitoring screen SHALL display a system stats widget showing CPU, RAM, and battery usage
2. THE Monitoring screen SHALL display a network info widget showing IP address, connection type, and data usage
3. THE Monitoring screen SHALL display a device info widget showing Android version, kernel version, and device model
4. THE Monitoring screen SHALL display a storage usage widget showing available space
5. THE Monitoring screen SHALL provide access to a log viewer widget (if configured)
6. THE Monitoring screen SHALL allow users to add custom widgets and app shortcuts
7. THE Monitoring screen SHALL update widget data in real-time (every 5 seconds for system stats)

### Requirement 26: AI Assistant Home Screen

**User Story:** As a developer, I want a dedicated AI Assistant home screen with Gemini integration, so that I can quickly access AI-powered features without leaving the launcher.

#### Acceptance Criteria

1. THE AI Assistant screen SHALL display a Gemini chat interface widget
2. THE AI Assistant screen SHALL display a code snippet search widget
3. THE AI Assistant screen SHALL display a documentation search widget
4. THE AI Assistant screen SHALL display AI-powered app suggestions based on current context
5. THE AI Assistant screen SHALL display context-aware quick actions (e.g., "Open your most used IDE", "Check recent commits")
6. THE AI Assistant screen SHALL allow users to add custom widgets and app shortcuts
7. WHEN a user types a query in the chat widget, THE Gemini_Client SHALL process it and display the response inline
8. THE AI Assistant screen SHALL maintain chat history for the current session

### Requirement 27: Built-in Speedtest Widget

**User Story:** As a developer, I want to test my network speed directly from the home screen, so that I can quickly diagnose connectivity issues without opening a separate app.

#### Acceptance Criteria

1. WHEN a user taps the "Test" button on the speedtest widget, THE Speedtest_Service SHALL initiate a network speed test
2. THE Speedtest_Service SHALL measure download speed in Mbps by downloading a test file from a CDN
3. THE Speedtest_Service SHALL measure upload speed in Mbps by uploading test data to a test server
4. THE Speedtest_Service SHALL measure ping latency in milliseconds by sending HTTP HEAD requests
5. WHEN a test is in progress, THE speedtest widget SHALL display real-time progress and current speed
6. WHEN a test completes, THE speedtest widget SHALL display the results (download speed, upload speed, ping)
7. THE Speedtest_Service SHALL store test results in a database with timestamp, speeds, ping, connection type, and ISP
8. THE speedtest widget SHALL display a historical graph of recent test results (last 10 tests)
9. THE Speedtest_Service SHALL detect connection type (WiFi, 5G, 4G, 3G, Ethernet) and display it with results
10. THE Speedtest_Service SHALL allow users to configure test servers in settings
11. WHEN a test fails, THE Speedtest_Service SHALL automatically retry with a fallback server
12. THE Speedtest_Service SHALL warn users before running tests on metered connections (mobile data)
13. THE Speedtest_Service SHALL allow users to schedule automatic tests (hourly, daily, weekly) in settings
14. WHEN a scheduled test completes, THE Speedtest_Service SHALL send a notification with results
15. THE Speedtest_Service SHALL respect battery saver mode and skip tests when battery is low (<20%)

### Requirement 28: Built-in Terminal Widget

**User Story:** As a developer, I want to run shell commands directly from the home screen, so that I can execute quick commands without opening a full terminal app.

#### Acceptance Criteria

1. WHEN a user types a command in the terminal widget and presses Enter, THE Terminal_Session SHALL execute the command in the configured shell
2. THE Terminal_Session SHALL display command output (stdout and stderr) in the terminal display area
3. THE Terminal_Session SHALL support multiple terminal sessions accessible via tabs
4. WHEN a user taps the "+" button, THE Terminal_Session SHALL create a new terminal session
5. WHEN a user swipes left/right on the terminal widget, THE Terminal_Session SHALL switch between active sessions
6. THE Terminal_Session SHALL maintain command history for each session (up/down arrows to navigate)
7. THE Terminal_Session SHALL support tab completion for commands and file paths
8. THE Terminal_Session SHALL support ANSI color codes for syntax highlighting
9. THE Terminal_Session SHALL support Unicode (UTF-8) characters
10. THE Terminal_Session SHALL allow users to copy text from the terminal output
11. THE Terminal_Session SHALL allow users to paste text into the terminal input
12. THE Terminal_Session SHALL support keyboard shortcuts (Ctrl+C, Ctrl+D, Ctrl+Z)
13. THE Terminal_Session SHALL persist active sessions across launcher restarts
14. WHEN the launcher restarts, THE Terminal_Session SHALL restore each session's working directory, command history, and output buffer
15. THE Terminal_Session SHALL allow users to configure the default shell (sh, bash, zsh) in settings
16. THE Terminal_Session SHALL allow users to customize font size, font family, and color scheme in settings
17. THE Terminal_Session SHALL limit scrollback buffer to 1000 lines by default (configurable in settings)
18. WHEN a user attempts to execute a dangerous command (rm -rf, dd, etc.), THE Terminal_Session SHALL display a warning prompt requiring confirmation
19. THE Terminal_Session SHALL log all executed commands to an audit log (if enabled in settings)
20. THE Terminal_Session SHALL run with limited permissions (no root access by default)

### Requirement 29: Terminal Integration Options

**User Story:** As a developer, I want the terminal widget to integrate with Termux if available, so that I can access a full Linux environment with package management.

#### Acceptance Criteria

1. WHEN the launcher starts, THE Terminal_Session SHALL detect if Termux is installed on the device
2. WHERE Termux is installed, THE Terminal_Session SHALL use Termux libraries for PTY management
3. WHERE Termux is installed, THE Terminal_Session SHALL provide access to Termux packages (git, python, node, etc.)
4. WHERE Termux is not installed, THE Terminal_Session SHALL fall back to the system shell (/system/bin/sh)
5. THE Terminal_Session SHALL display the current shell type (Termux bash, system sh, etc.) in the widget header
6. THE Terminal_Session SHALL allow users to manually select the shell type in settings (Auto-detect, Termux, System)

### Requirement 30: Speedtest Data Management

**User Story:** As a developer, I want to view historical speedtest results and manage data usage, so that I can track network performance over time.

#### Acceptance Criteria

1. THE Speedtest_Service SHALL store the last 100 speedtest results in the database
2. WHEN the database contains more than 100 results, THE Speedtest_Service SHALL delete the oldest results
3. THE speedtest widget SHALL display a "History" button that opens a detailed results screen
4. THE detailed results screen SHALL display a list of all stored test results with timestamp, speeds, and ping
5. THE detailed results screen SHALL display a line graph showing speed trends over time
6. THE detailed results screen SHALL allow users to filter results by date range
7. THE detailed results screen SHALL allow users to export results as CSV
8. THE Speedtest_Service SHALL track cumulative data usage from all tests
9. THE speedtest widget SHALL display total data used by tests in the widget footer
10. THE Speedtest_Service SHALL allow users to clear all test history in settings

### Requirement 31: Home Screen Customization

**User Story:** As a developer, I want to customize each home screen's layout and content, so that I can tailor the launcher to my specific workflow.

#### Acceptance Criteria

1. WHEN a user long-presses on a home screen, THE Home_Screen_Manager SHALL enter customization mode
2. IN customization mode, THE Home_Screen_Manager SHALL allow users to add widgets from a widget picker
3. IN customization mode, THE Home_Screen_Manager SHALL allow users to add app shortcuts from the app drawer
4. IN customization mode, THE Home_Screen_Manager SHALL allow users to remove widgets and shortcuts
5. IN customization mode, THE Home_Screen_Manager SHALL allow users to resize widgets via drag handles
6. IN customization mode, THE Home_Screen_Manager SHALL allow users to reposition widgets and shortcuts via drag-and-drop
7. THE Home_Screen_Manager SHALL persist all customizations to storage
8. THE Home_Screen_Manager SHALL provide a "Reset to Default" option for each screen in settings
9. WHEN a user selects "Reset to Default", THE Home_Screen_Manager SHALL restore the screen to its original configuration

### Requirement 32: Screen Navigation Indicators

**User Story:** As a developer, I want clear visual indicators showing which home screen I'm on, so that I can navigate efficiently between screens.

#### Acceptance Criteria

1. THE Home_Screen_Manager SHALL display navigation dots at the bottom of each home screen
2. THE navigation dots SHALL indicate the total number of enabled screens
3. THE navigation dots SHALL highlight the currently active screen
4. WHEN a user taps a navigation dot, THE Home_Screen_Manager SHALL jump to the corresponding screen
5. THE Home_Screen_Manager SHALL allow users to choose between dot indicators and text labels in settings
6. WHERE text labels are enabled, THE navigation indicator SHALL display the screen name (e.g., "Development", "Communication")

### Requirement 33: Terminal Security and Sandboxing

**User Story:** As a developer, I want the terminal to be secure and sandboxed, so that I cannot accidentally damage my system or compromise security.

#### Acceptance Criteria

1. THE Terminal_Session SHALL run shell processes with the launcher's user permissions (no root)
2. THE Terminal_Session SHALL prevent access to system-critical directories (/system, /data/system, etc.) unless explicitly allowed
3. WHEN a user attempts to execute a command matching a dangerous pattern, THE Terminal_Session SHALL display a warning dialog
4. THE warning dialog SHALL list the potential risks of the command
5. THE warning dialog SHALL require explicit user confirmation ("I understand the risks") before execution
6. THE Terminal_Session SHALL maintain a blacklist of dangerous commands (configurable in settings)
7. THE Terminal_Session SHALL allow users to enable "Safe Mode" which blocks all blacklisted commands
8. THE Terminal_Session SHALL limit process resources (max 50% CPU, max 500MB RAM per session)
9. WHEN a terminal process exceeds resource limits, THE Terminal_Session SHALL terminate it and display an error message
10. THE Terminal_Session SHALL log all executed commands to an audit log (if enabled in settings)

### Requirement 34: Speedtest Network Optimization

**User Story:** As a developer, I want speedtests to be accurate and efficient, so that I get reliable results without wasting data or battery.

#### Acceptance Criteria

1. THE Speedtest_Service SHALL use multiple parallel connections (4-8) for download tests to maximize throughput
2. THE Speedtest_Service SHALL dynamically adjust test duration based on connection speed (faster connections = shorter tests)
3. THE Speedtest_Service SHALL use adaptive test file sizes (10MB for slow connections, 50MB for fast connections)
4. THE Speedtest_Service SHALL cancel tests if they take longer than 60 seconds
5. THE Speedtest_Service SHALL select the geographically closest test server by default
6. THE Speedtest_Service SHALL measure jitter (ping variance) in addition to average ping
7. THE Speedtest_Service SHALL detect ISP name from network information (if available)
8. THE Speedtest_Service SHALL cache test server list and refresh it weekly
9. WHEN a test server is consistently slow or unreliable, THE Speedtest_Service SHALL mark it as degraded and deprioritize it
10. THE Speedtest_Service SHALL provide a "Quick Test" mode that only measures download speed (faster, uses less data)

### Requirement 35: Remote Desktop Widget

**User Story:** As a developer, I want to manage remote desktop connections directly from the home screen, so that I can quickly access remote machines without opening separate apps.

#### Acceptance Criteria

1. THE Remote Desktop Widget SHALL support RDP (Remote Desktop Protocol) connections
2. THE Remote Desktop Widget SHALL support VNC (Virtual Network Computing) connections
3. THE Remote Desktop Widget SHALL support SSH connections with terminal display
4. THE Remote Desktop Widget SHALL support AnyDesk connections
5. THE Remote Desktop Widget SHALL support RustDesk connections
6. WHEN a user taps a saved connection, THE Remote Desktop Widget SHALL launch the connection in a full-screen activity
7. THE Remote Desktop Widget SHALL allow users to save connection profiles with host, port, username, and authentication method
8. THE Remote Desktop Widget SHALL encrypt stored credentials using Android Keystore
9. THE Remote Desktop Widget SHALL display connection status (Connected, Disconnected, Connecting) for each saved profile
10. THE Remote Desktop Widget SHALL support quick actions: Connect, Disconnect, Edit, Delete
11. THE Remote Desktop Widget SHALL display a list of recent connections (last 5)
12. WHEN a connection fails, THE Remote Desktop Widget SHALL display an error message with troubleshooting suggestions
13. THE Remote Desktop Widget SHALL allow users to configure connection quality (Low, Medium, High, Auto)
14. THE Remote Desktop Widget SHALL support clipboard synchronization between local and remote machines
15. THE Remote Desktop Widget SHALL allow users to export/import connection profiles

### Requirement 36: Docker Management Widget

**User Story:** As a developer, I want to manage Docker containers from the home screen, so that I can start, stop, and monitor containers without using the command line.

#### Acceptance Criteria

1. THE Docker Management Widget SHALL connect to Docker daemon via Unix socket or TCP
2. THE Docker Management Widget SHALL display a list of all containers with name, status, and image
3. THE Docker Management Widget SHALL allow users to start, stop, restart, and remove containers
4. THE Docker Management Widget SHALL display container resource usage (CPU, memory, network I/O)
5. THE Docker Management Widget SHALL allow users to view container logs in real-time
6. THE Docker Management Widget SHALL allow users to execute commands inside running containers
7. THE Docker Management Widget SHALL display a list of Docker images with size and creation date
8. THE Docker Management Widget SHALL allow users to pull new images from Docker Hub
9. THE Docker Management Widget SHALL allow users to remove unused images
10. THE Docker Management Widget SHALL display Docker system info (version, storage driver, total containers/images)
11. THE Docker Management Widget SHALL support Docker Compose operations (up, down, restart)
12. THE Docker Management Widget SHALL allow users to configure Docker daemon connection settings
13. WHEN Docker daemon is unreachable, THE Docker Management Widget SHALL display a connection error with troubleshooting steps
14. THE Docker Management Widget SHALL refresh container status every 5 seconds
15. THE Docker Management Widget SHALL allow users to create new containers from images with basic configuration

### Requirement 37: API Testing Widget

**User Story:** As a developer, I want to test REST and GraphQL APIs from the home screen, so that I can quickly verify endpoints without opening Postman or other tools.

#### Acceptance Criteria

1. THE API Testing Widget SHALL support HTTP methods: GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS
2. THE API Testing Widget SHALL allow users to enter request URL, headers, and body
3. THE API Testing Widget SHALL support JSON, XML, and form-data request bodies
4. THE API Testing Widget SHALL display response status code, headers, and body
5. THE API Testing Widget SHALL support GraphQL queries and mutations
6. THE API Testing Widget SHALL provide syntax highlighting for JSON and XML responses
7. THE API Testing Widget SHALL allow users to save request templates with name, method, URL, headers, and body
8. THE API Testing Widget SHALL display a list of saved request templates
9. WHEN a user taps a saved template, THE API Testing Widget SHALL load it into the request editor
10. THE API Testing Widget SHALL support environment variables (e.g., {{BASE_URL}}, {{API_KEY}})
11. THE API Testing Widget SHALL allow users to create and manage environment profiles (Dev, Staging, Production)
12. THE API Testing Widget SHALL display request duration and response size
13. THE API Testing Widget SHALL allow users to export request history as HAR (HTTP Archive) format
14. THE API Testing Widget SHALL support authentication methods: Basic Auth, Bearer Token, API Key, OAuth 2.0
15. THE API Testing Widget SHALL validate JSON and XML request bodies before sending

### Requirement 38: Clipboard History Widget

**User Story:** As a developer, I want to access my clipboard history from the home screen, so that I can quickly retrieve previously copied text without losing it.

#### Acceptance Criteria

1. THE Clipboard History Widget SHALL monitor system clipboard changes in the background
2. WHEN the clipboard content changes, THE Clipboard History Widget SHALL save the new content to history
3. THE Clipboard History Widget SHALL store the last 50 clipboard entries
4. THE Clipboard History Widget SHALL display clipboard entries in reverse chronological order (newest first)
5. WHEN a user taps a clipboard entry, THE Clipboard History Widget SHALL copy it to the system clipboard
6. THE Clipboard History Widget SHALL display a preview of each entry (first 100 characters)
7. THE Clipboard History Widget SHALL support text, URLs, and code snippets
8. THE Clipboard History Widget SHALL detect and syntax-highlight code snippets (JSON, XML, Python, Java, etc.)
9. THE Clipboard History Widget SHALL allow users to pin important entries to the top
10. THE Clipboard History Widget SHALL allow users to delete individual entries or clear all history
11. THE Clipboard History Widget SHALL allow users to search clipboard history by keyword
12. THE Clipboard History Widget SHALL exclude sensitive data (passwords, credit cards) from history (configurable)
13. THE Clipboard History Widget SHALL allow users to export clipboard history as text file
14. THE Clipboard History Widget SHALL persist clipboard history across launcher restarts
15. THE Clipboard History Widget SHALL allow users to configure history size limit (10, 25, 50, 100 entries)

### Requirement 39: JSON/XML Formatter Widget

**User Story:** As a developer, I want to format and validate JSON/XML from the home screen, so that I can quickly clean up messy data without opening a separate tool.

#### Acceptance Criteria

1. THE JSON/XML Formatter Widget SHALL accept JSON or XML input via text field or clipboard paste
2. WHEN a user taps "Format", THE JSON/XML Formatter Widget SHALL pretty-print the input with proper indentation
3. WHEN a user taps "Minify", THE JSON/XML Formatter Widget SHALL remove all whitespace and newlines
4. THE JSON/XML Formatter Widget SHALL validate JSON/XML syntax and display errors with line numbers
5. THE JSON/XML Formatter Widget SHALL support syntax highlighting for formatted output
6. THE JSON/XML Formatter Widget SHALL allow users to configure indentation (2 spaces, 4 spaces, tabs)
7. THE JSON/XML Formatter Widget SHALL allow users to copy formatted output to clipboard
8. THE JSON/XML Formatter Widget SHALL support JSON-to-XML and XML-to-JSON conversion
9. THE JSON/XML Formatter Widget SHALL support JSONPath queries for extracting specific values
10. THE JSON/XML Formatter Widget SHALL support XPath queries for XML documents
11. THE JSON/XML Formatter Widget SHALL display JSON schema validation errors (if schema is provided)
12. THE JSON/XML Formatter Widget SHALL allow users to save formatted output as file
13. THE JSON/XML Formatter Widget SHALL support collapsible tree view for nested JSON/XML
14. THE JSON/XML Formatter Widget SHALL display data statistics (object count, array length, depth)
15. THE JSON/XML Formatter Widget SHALL support diff view for comparing two JSON/XML documents

### Requirement 40: Port Scanner Widget

**User Story:** As a developer, I want to scan network ports from the home screen, so that I can quickly check which services are running on a host.

#### Acceptance Criteria

1. THE Port Scanner Widget SHALL allow users to enter a target host (IP address or hostname)
2. THE Port Scanner Widget SHALL allow users to specify port range (e.g., 1-1000, 80,443,8080)
3. WHEN a user taps "Scan", THE Port Scanner Widget SHALL scan the specified ports and display results
4. THE Port Scanner Widget SHALL display open ports with service name (e.g., 80 - HTTP, 443 - HTTPS)
5. THE Port Scanner Widget SHALL display closed and filtered ports separately
6. THE Port Scanner Widget SHALL support common port presets: Web (80,443,8080,8443), Database (3306,5432,27017), SSH (22)
7. THE Port Scanner Widget SHALL display scan progress (X/Y ports scanned)
8. THE Port Scanner Widget SHALL allow users to cancel an in-progress scan
9. THE Port Scanner Widget SHALL display scan duration and total ports scanned
10. THE Port Scanner Widget SHALL allow users to save scan results with timestamp
11. THE Port Scanner Widget SHALL allow users to export scan results as CSV or JSON
12. THE Port Scanner Widget SHALL support TCP and UDP port scanning
13. THE Port Scanner Widget SHALL detect service versions for common services (HTTP, SSH, FTP)
14. THE Port Scanner Widget SHALL warn users before scanning external hosts (potential legal issues)
15. THE Port Scanner Widget SHALL respect network permissions and handle connection errors gracefully

### Requirement 41: Base64/Hash Encoder Widget

**User Story:** As a developer, I want to encode/decode Base64 and generate hashes from the home screen, so that I can quickly process data without opening a separate tool.

#### Acceptance Criteria

1. THE Base64/Hash Encoder Widget SHALL support Base64 encoding and decoding
2. THE Base64/Hash Encoder Widget SHALL support URL-safe Base64 encoding
3. THE Base64/Hash Encoder Widget SHALL support hash algorithms: MD5, SHA-1, SHA-256, SHA-512
4. THE Base64/Hash Encoder Widget SHALL allow users to enter input text via text field or clipboard paste
5. WHEN a user selects an operation, THE Base64/Hash Encoder Widget SHALL process the input and display the result
6. THE Base64/Hash Encoder Widget SHALL allow users to copy the result to clipboard
7. THE Base64/Hash Encoder Widget SHALL detect invalid Base64 input during decoding and display an error
8. THE Base64/Hash Encoder Widget SHALL support HMAC (Hash-based Message Authentication Code) with secret key
9. THE Base64/Hash Encoder Widget SHALL support bcrypt password hashing
10. THE Base64/Hash Encoder Widget SHALL display hash output in hexadecimal and Base64 formats
11. THE Base64/Hash Encoder Widget SHALL allow users to compare two hashes for equality
12. THE Base64/Hash Encoder Widget SHALL support file hashing (select file and compute hash)
13. THE Base64/Hash Encoder Widget SHALL display processing time for hash operations
14. THE Base64/Hash Encoder Widget SHALL support batch processing (multiple inputs at once)
15. THE Base64/Hash Encoder Widget SHALL allow users to save operation history (last 20 operations)

### Requirement 42: Environment Variables Widget

**User Story:** As a developer, I want to manage environment variables from the home screen, so that I can quickly view and modify them for development tasks.

#### Acceptance Criteria

1. THE Environment Variables Widget SHALL display all environment variables in the current shell session
2. THE Environment Variables Widget SHALL allow users to add new environment variables with name and value
3. THE Environment Variables Widget SHALL allow users to edit existing environment variables
4. THE Environment Variables Widget SHALL allow users to delete environment variables
5. THE Environment Variables Widget SHALL support searching environment variables by name or value
6. THE Environment Variables Widget SHALL display variables in alphabetical order
7. THE Environment Variables Widget SHALL allow users to export variables to a .env file
8. THE Environment Variables Widget SHALL allow users to import variables from a .env file
9. THE Environment Variables Widget SHALL support variable templates (e.g., DATABASE_URL, API_KEY)
10. THE Environment Variables Widget SHALL validate variable names (no spaces, no special characters except underscore)
11. THE Environment Variables Widget SHALL allow users to copy variable values to clipboard
12. THE Environment Variables Widget SHALL support variable groups (Development, Staging, Production)
13. THE Environment Variables Widget SHALL allow users to switch between variable groups
14. THE Environment Variables Widget SHALL persist variables across launcher restarts
15. THE Environment Variables Widget SHALL encrypt sensitive variables (marked as "secret") using Android Keystore

### Requirement 43: Git Repository Widget

**User Story:** As a developer, I want to view Git repository status from the home screen, so that I can quickly check branches, commits, and changes without opening a terminal.

#### Acceptance Criteria

1. THE Git Repository Widget SHALL allow users to add Git repositories by selecting a directory
2. THE Git Repository Widget SHALL display the current branch name for each repository
3. THE Git Repository Widget SHALL display the number of uncommitted changes (modified, added, deleted files)
4. THE Git Repository Widget SHALL display the number of commits ahead/behind the remote branch
5. THE Git Repository Widget SHALL allow users to switch branches via dropdown menu
6. THE Git Repository Widget SHALL allow users to pull latest changes from remote
7. THE Git Repository Widget SHALL allow users to commit changes with a commit message
8. THE Git Repository Widget SHALL allow users to push commits to remote
9. THE Git Repository Widget SHALL display the last commit message and author
10. THE Git Repository Widget SHALL display a list of recent commits (last 10) with hash, message, and timestamp
11. THE Git Repository Widget SHALL allow users to view diff for uncommitted changes
12. THE Git Repository Widget SHALL support multiple repositories (switch between them via tabs)
13. THE Git Repository Widget SHALL detect Git repositories automatically in common directories (~/projects, ~/code)
14. WHEN a Git operation fails, THE Git Repository Widget SHALL display the error message from Git
15. THE Git Repository Widget SHALL allow users to configure Git user name and email in settings

### Requirement 44: VPN & IPN Management Widget

**User Story:** As a developer, I want to manage VPN and IPN connections from the home screen, so that I can quickly connect to secure networks without opening separate apps.

#### Acceptance Criteria

1. THE VPN & IPN Management Widget SHALL support WireGuard VPN connections
2. THE VPN & IPN Management Widget SHALL support Tailscale IPN connections
3. THE VPN & IPN Management Widget SHALL support OpenVPN connections
4. THE VPN & IPN Management Widget SHALL display a list of saved VPN/IPN profiles with name and status
5. WHEN a user taps a profile, THE VPN & IPN Management Widget SHALL connect to the VPN/IPN
6. THE VPN & IPN Management Widget SHALL display connection status (Connected, Disconnected, Connecting)
7. THE VPN & IPN Management Widget SHALL display connection details (IP address, server location, protocol)
8. THE VPN & IPN Management Widget SHALL allow users to disconnect from active VPN/IPN
9. THE VPN & IPN Management Widget SHALL allow users to add new VPN/IPN profiles with configuration
10. THE VPN & IPN Management Widget SHALL support importing WireGuard configs (.conf files)
11. THE VPN & IPN Management Widget SHALL support importing OpenVPN configs (.ovpn files)
12. THE VPN & IPN Management Widget SHALL encrypt stored VPN/IPN credentials using Android Keystore
13. THE VPN & IPN Management Widget SHALL display network statistics (data sent/received, connection duration)
14. THE VPN & IPN Management Widget SHALL allow users to configure auto-connect on launcher start
15. THE VPN & IPN Management Widget SHALL allow users to configure split tunneling (exclude specific apps from VPN)
16. THE VPN & IPN Management Widget SHALL display latency to VPN/IPN server
17. THE VPN & IPN Management Widget SHALL allow users to test VPN/IPN connection before saving
18. WHEN a VPN/IPN connection fails, THE VPN & IPN Management Widget SHALL display error details and troubleshooting suggestions
19. THE VPN & IPN Management Widget SHALL support Tailscale exit nodes (select which node to route traffic through)
20. THE VPN & IPN Management Widget SHALL allow users to view Tailscale network map (connected devices)

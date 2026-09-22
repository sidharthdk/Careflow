# CareFlow

**"From doctor's instructions to recovery"**

CareFlow is a patient-side healthcare companion that turns prescriptions, discharge summaries, lab reports, and healthcare instructions into an actionable care journey.

## 🎯 Project Status

This is the **BASE FOUNDATION** of the CareFlow application. It implements:

- ✅ Clean Android architecture with Jetpack Compose
- ✅ Material 3 design system
- ✅ Four main screens (Home, Care Plan, Recovery, Profile)
- ✅ Local persistence with Room database
- ✅ Document capture foundation (camera + gallery)
- ✅ Mock data for development

## 🏗️ Architecture

```
app/
├── core/
│   ├── navigation/      # Navigation structure
│   ├── ui/theme/        # Material 3 theme, colors, typography
│   └── utils/           # Utility functions
├── data/
│   ├── local/
│   │   ├── database/    # Room database
│   │   ├── dao/         # Data access objects
│   │   └── entities/    # Room entities
│   └── repository/      # Repository implementations
├── domain/
│   ├── model/           # Domain models
│   └── repository/      # Repository interfaces
└── feature/
    ├── home/            # Home dashboard
    ├── careplan/        # Care plan timeline
    ├── recovery/        # Recovery tracking
    ├── profile/         # User profile
    └── documents/       # Document capture
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 26+
- Physical Android device or emulator

### Build and Run

1. Clone the repository:
   ```
   git clone <repository-url>
   cd CareFlow
   ```

2. Open the project in Android Studio

3. Sync Gradle dependencies

4. Build the project:
   ```
   .\gradlew build
   ```

5. Run on device/emulator:
   ```
   .\gradlew installDebug
   ```
   Or use Android Studio's "Run" button

### Required Permissions

The app requests the following permissions:
- **Camera**: For capturing health documents
- **Read Media Images**: For selecting images from gallery

## 📱 Features

### Home Screen
- Time-based greeting
- Today's care tasks
- Progress tracking
- Current care episode overview
- Next appointment reminder
- Quick action to add health updates

### Care Plan Screen
- Timeline-based task view
- Organized by time of day (Morning, Afternoon, Evening)
- Upcoming appointments
- Task completion tracking

### Recovery Screen
- Recovery progress overview
- Daily recovery entries
- Symptom tracking
- Feeling level logging
- Timeline visualization

### Profile Screen
- Patient information
- Caregiver management (placeholder)
- Privacy settings
- App information

### Document Capture
- Camera capture
- Gallery selection
- Manual entry placeholder
- Image preview
- Local storage

## 🔧 Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design**: Material 3
- **Architecture**: MVVM + Repository pattern
- **Database**: Room
- **Concurrency**: Kotlin Coroutines + StateFlow
- **Navigation**: Navigation Compose
- **Image Loading**: Coil
- **Camera**: CameraX

## ⚠️ Important Notes

### What This Foundation Does NOT Include

- ❌ OCR/AI extraction (placeholder only)
- ❌ Cloud backend integration
- ❌ Real medication database
- ❌ Medical diagnosis features
- ❌ Doctor communication features
- ❌ Caregiver sharing (UI only)

### Privacy & Security

- All data is stored locally on the device
- No cloud synchronization in this version
- No analytics or tracking
- Camera/gallery permissions requested when needed
- TODO: Add encryption for sensitive health data

### Medical Disclaimer

**CareFlow is NOT a medical device or diagnostic tool.**

This app:
- Does NOT diagnose diseases
- Does NOT prescribe medication
- Does NOT provide medical advice
- Does NOT replace professional healthcare

Always consult qualified healthcare professionals for medical decisions.

## 🛣️ Roadmap

### Phase 2: AI Extraction
- OCR implementation
- Prescription parsing
- Discharge summary extraction
- User confirmation workflow

### Phase 3: Intelligence
- On-device LLM integration
- Voice notes
- Multilingual support
- Smart reminders

### Phase 4: Sharing & Insights
- Doctor visit brief generation
- Lab report comparison
- Caregiver access control
- Export functionality

### Phase 5: Advanced Features
- Rehabilitation tracking
- Exercise companion
- Medication adherence analytics
- Integration with health devices

## 📝 Development Guidelines

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable names
- Keep composables focused and small
- Separate business logic from UI

### Testing
- Unit tests for ViewModels
- Integration tests for Repository
- UI tests for critical flows

### Git Workflow
- Create feature branches
- Write descriptive commit messages
- Keep commits focused and atomic

## 🐛 Known Issues

- Mock data is currently hardcoded
- Date calculations assume September 2026
- Camera orientation may need adjustment
- No data export functionality yet

## 📄 License

Copyright © 2026 CareFlow

## 🤝 Contributing

This is a hackathon project. Contributions welcome!

## 📧 Contact

For questions or feedback, please open an issue on GitHub.

---

**Built for iQOO Hackathon 2026**

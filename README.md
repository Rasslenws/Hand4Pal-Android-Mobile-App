# Hand4Pal - Android Mobile App

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg?style=flat&logo=kotlin)
![Platform](https://img.shields.io/badge/Platform-Android-green.svg?style=flat&logo=android)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-orange)
![State](https://img.shields.io/badge/Status-In%20Development-yellow)

**Hand4Pal** is a native Android application built with Kotlin, serving as the frontend interface for the **Hand4Pal** humanitarian microservices ecosystem. It connects volunteers, donors, and associations to facilitate campaigns and donations.

This project strictly follows **Clean Architecture** principles and the **MVVM** pattern, structured by **Feature** to mirror the backend microservices architecture.

## 🏗 Architecture Overview

The project structure is designed to be scalable, testable, and modular. It aligns with the backend microservices to ensure clear separation of concerns.

### The Dependency Rule
> The inner layers (Domain) should not know anything about the outer layers (Presentation, Data). Dependencies only point inwards.

graph TD
Presentation[Presentation Layer
(MVVM)] --> Domain[Domain Layer
(Pure Kotlin)]
Data[Data Layer
(Repositories & Sources)] --> Domain


### Microservices Integration
The app interacts with the following backend services via an API Gateway:
*   🏠 **Home (Frontend Aggregator):** Orchestrates data from multiple services for a unified dashboard.
*   🔐 **User Auth:** Authentication (JWT), Registration, and Profile management.
*   📢 **Campaign:** Browsing and creating humanitarian campaigns.
*   💝 **Donation:** Secure donation flow and payment history.
*   💬 **Comment:** Social interactions on campaigns.

---

## 📂 Project Structure (Package by Feature)

The codebase is organized by **Feature** rather than by technical layer. This makes the project easier to navigate and scales better.

com.hand4pal.app
├── core/ # ⚙️ Shared components (Network, DI, Utils)
│ ├── di/ # Hilt Modules (NetworkModule, DatabaseModule)
│ ├── network/ # Retrofit Config, AuthInterceptor (JWT injection)
│ └── util/ # Constants (API_URL), Extensions
│
├── features/ # 📦 Feature Modules
│ │
│ ├── home/ # 🏠 HOME FEATURE (Aggregator)
│ │ └── presentation/ # ViewModels orchestrating other UseCases
│ │
│ ├── auth/ # 🔐 AUTH FEATURE
│ │ ├── data/ # AuthRepositoryImpl, AuthApi
│ │ ├── domain/ # LoginUseCase, User Entity
│ │ └── presentation/ # LoginViewModel, LoginFragment
│ │
│ ├── campaign/ # 📢 CAMPAIGN FEATURE
│ │ ├── data/ # CampaignRepositoryImpl
│ │ ├── domain/ # GetCampaignsUseCase
│ │ └── presentation/ # CampaignListViewModel
│ │
│ ├── donation/ # 💝 DONATION FEATURE
│ │ └── ...
│ │
└── App.kt # Application Entry Point (Hilt)


**Note on the Home Feature:**
The `features/home` package is purely a **Presentation** layer. It acts as an orchestrator by injecting UseCases from `auth`, `campaign`, and `statistic` to display a comprehensive dashboard without duplicating business logic.

---

## 🛠 Tech Stack

*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **Architecture:** MVVM + Clean Architecture
*   **Concurrency:** [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) & [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
*   **Dependency Injection:** [Hilt](https://dagger.dev/hilt/)
*   **Network:** [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
*   **Local Persistence:** [Room Database](https://developer.android.com/training/data-storage/room)
*   **Navigation:** [Jetpack Navigation Component](https://developer.android.com/guide/navigation)

---

## 🚀 Getting Started

### Prerequisites
*   Android Studio Ladybug (or newer)
*   JDK 17
*   Backend Microservices running (locally or remotely)

### Installation

1.  **Clone the repository**
    ```
    git clone https://github.com/yourusername/hand4pal-android.git
    ```

2.  **Configure API URL**
    Open `core/util/Constants.kt` and update the `BASE_URL` to point to your API Gateway:
    ```
    // For Android Emulator (Localhost)
    const val BASE_URL = "http://10.0.2.2:8080/" 
    ```

3.  **Build & Run**
    Sync Gradle and run the app on an emulator or physical device.

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:
1.  Fork the project.
2.  Create your feature branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.


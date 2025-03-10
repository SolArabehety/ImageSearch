# 📸 ImageSearch App  

A modern **image search app** built with **Jetpack Compose** and the latest Android technologies. 🔍✨  

| Light Mode | Dark Mode |
|-----------|-----------|
| <img src="https://github.com/user-attachments/assets/4a1a8f0b-8d92-478e-ac40-55712c339c8b" alt="Light Mode" height="200"> | <img src="https://github.com/user-attachments/assets/af9e9789-e6dc-466a-8021-b47aa751249d" alt="Dark Mode" height="200"> |

---

## 🛠 Technologies Used  
- **Jetpack Compose**
- **Material 3**
- **Kotlin Coroutines & Flow** 
- **Hilt** 
- **Coil** 
- **Retrofit**

---
## 🏗 App Architecture  
This app follows the **MVVM (Model-View-ViewModel)** pattern and is organized into **two main modules**:  

### 📂 **Module: `app/`** (Presentation Layer)  
Handles the **UI and user interaction** using **Jetpack Compose**. This module includes:  
- **Composables** → Screens and UI components.  
- **ViewModel** → Manages UI state and interacts with the domain layer.  

### 📂 **Module: `core/`** (Domain & Data Layer)  
Contains the **business logic and data handling**. This module includes:  
- **Use Cases (`domain/` folder)** → Encapsulates business logic (`SearchImagesUseCase.kt`).  
- **Repositories (`data/repository/` folder)** → Interfaces and implementations for data sources.  
- **Network (`data/network/` folder)** → API service definitions and Retrofit setup.  

```
📂 com.solarabehety.imagesearch
 ├── app        # UI with Jetpack Compose
 │   ├── MainScreen.kt
 │   ├── MainViewModel.kt
 │ 
 ├── core      # Business logic and API calls
     ├── repository
     ├── network
     └── models
```

---
## 📥 How to Run the App in Android Studio  
Follow these steps to run the app in **Android Studio**:  

### 1️⃣ Clone the Repository  
Open a terminal and run:  
```sh
git clone https://github.com/your-username/ImageSearchApp.git
````

### 2️⃣ Open the Project in Android Studio
Open Android Studio.
Click "Open" and select the folder where you cloned the repository.
Wait for Gradle to sync.

### 3️⃣ Set Up an Emulator or a Real Device  
- **Using an emulator**:  
  1. Open **Android Studio** and go to **Device Manager**.  
  2. Click **"Create Virtual Device"**, select a device, and install a system image.  
  3. Start the emulator.  

- **Using a real device**:  
  1. Enable **USB Debugging** on your phone (Settings > Developer Options).  
  2. Connect your phone via USB.  
  3. Accept the debugging prompt on your phone.  

### 4️⃣ Run the App  
- Ensure you have an **active internet connection** (required for searching images).  
- Click ▶️ **Run** in Android Studio or use the shortcut **`Shift + F10`**.  
- The app will launch on your emulator or device! 🎉  

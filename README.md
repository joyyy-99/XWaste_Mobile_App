# XWaste Mobile — Smart Waste Management in Your Pocket

**XWaste Mobile** is the resident-facing companion app to the XWaste web platform, created using **Kotlin** for Android. It offers Nairobi residents a streamlined way to manage their household waste—from requesting pickups and tracking garbage trucks to paying securely via Stripe—all from their smartphones.



## 📱 Key Features

- 🏠 **User Registration & Login** with location capture
- 📆 **Garbage Pickup Scheduling** via mobile interface
- 🚛 **Track Garbage Trucks** in real-time
- 🗑️ **Request Garbage Bins** for different waste categories
- 💳 **Make Payments** using integrated **Stripe Checkout**
- 📬 **Submit Feedback** directly to administrators
- 📢 **Access Recycling Education** through interactive tips



## 🔧 Tech Stack

| Layer       | Tech Used                  |
|-------------|----------------------------|
| **Language**| Kotlin                     |
| **Framework**| Android Jetpack (MVVM)    |
| **Backend** | REST API (Laravel-based)   |
| **UI**      | Jetpack Compose + XML      |
| **Payments**| Stripe Android SDK         |
| **Maps**    | Google Maps API (planned)  |


## 📦 App Architecture

- **MVVM Pattern** (ViewModel, LiveData)
- Retrofit for API calls
- SharedPreferences for user session
- Stripe integration for secure in-app transactions



## 🛠️ Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/joyyy-99/XWaste_Mobile_App.git
cd XWaste_Mobile_App
````

### 2. Open in Android Studio

* Open the project in Android Studio
* Allow Gradle to sync dependencies

### 3. Configure API

* Replace the base URL in `ApiClient.kt` with your Laravel backend endpoint.

### 4. Add Stripe Keys

Create a `local.properties` file and add your Stripe publishable key:

```properties
STRIPE_PUBLISHABLE_KEY=pk_test_********************************
```


## 💡 Future Enhancements

* 📍 GPS-based truck tracking (Google Maps integration)
* 🔔 Push Notifications for pickup reminders
* 🇰🇪 M-Pesa mobile money integration
* 🌐 Multilingual support (Kiswahili, English)


A mobile-first leap toward smarter, cleaner cities. 🌍♻️



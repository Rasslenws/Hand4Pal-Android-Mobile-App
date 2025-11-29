# 🚀 Quick Start Guide

## Prerequisites
- Android Studio (latest version)
- Java/Kotlin SDK
- Android device or emulator (API 24+)
- Backend running on `localhost:8081`

## Setup Steps

### 1. Start Backend
```bash
cd user-auth-service
mvn spring-boot:run
```
Wait for: `Started UserAuthServiceApplication in X seconds`

### 2. Open Android Project
1. Open Android Studio
2. `File > Open` → Select `Hand4Pal-Android-Mobile-App` folder
3. Wait for Gradle sync to complete

### 3. Run App
1. Click ▶️ Run button
2. Select emulator or physical device
3. App will install and launch

## Test Flow (60 seconds)

### Step 1: Register (15s)
1. App opens → Splash → Login screen
2. Click "Register" at bottom
3. Keep "Citizen" selected
4. Fill form:
   ```
   First Name: John
   Last Name: Doe
   Email: john@test.com
   Phone: 12345678
   Password: password123
   Confirm: password123
   ```
5. Click "Register"
6. ✅ Success → Auto-redirects to Login

### Step 2: Login (10s)
1. Enter credentials:
   ```
   Email: john@test.com
   Password: password123
   ```
2. Click "Login"
3. ✅ Success → Redirects to Home

### Step 3: Navigate (10s)
1. See bottom navigation: Home, Campaigns, Profile, Settings
2. Tap each to see placeholders
3. All ready for future features!

### Step 4: Profile (15s)
1. Tap "Profile" in bottom nav
2. See your info loaded from backend
3. Click "Edit Profile" → Change name → Save
4. Click avatar circle → Select new color
5. Click "Change Password" → Fill form → Change
6. ✅ All working!

### Step 5: Logout (5s)
1. Click "Logout" button
2. Returns to Login screen
3. Token cleared ✅

### Step 6: Re-login (5s)
1. Login again with same credentials
2. Goes to Home (token saved)
3. Close app → Reopen
4. ✅ Auto-logged in (token persists)

## Common Issues & Fixes

### "Unable to resolve host"
- ❌ Problem: Can't connect to backend
- ✅ Solution: 
  - Check backend is running on port 8081
  - Use `10.0.2.2:8081` (emulator) not `localhost`
  - Restart backend if needed

### "Email already used"
- ❌ Problem: Email exists in database
- ✅ Solution: Use different email or check database

### Gradle sync failed
- ❌ Problem: Dependencies not downloaded
- ✅ Solution:
  - `File > Invalidate Caches and Restart`
  - `Build > Clean Project`
  - Check internet connection

### Token expired
- ❌ Problem: Token TTL is 1 hour
- ✅ Solution: Just login again (no auto-refresh implemented)

## Architecture Overview

```
📱 App Flow:
SplashScreen → (check token) → Login/Home
    ↓
  Login → (save token) → Home
    ↓
Home (with BottomNav) → Profile/Campaigns/Settings
    ↓
Profile → View/Edit/Change Password/Logout
```

```
🏗️ Code Structure:
View (Fragment) 
  ↓ observes
ViewModel (StateFlow)
  ↓ calls
Repository
  ↓ uses
API Service (Retrofit) + DataStore
```

## Features Checklist

### ✅ Fully Working
- [x] Splash screen with token check
- [x] Login with backend validation
- [x] Register Citizen with all validations
- [x] Register Association with extended form
- [x] Role selection (Citizen/Association)
- [x] Auto-redirect after register → login → home
- [x] Bottom navigation (4 tabs)
- [x] Profile view (fetch from backend)
- [x] Profile edit (update backend)
- [x] Avatar selection (4 colors)
- [x] Change password (with validation)
- [x] Logout (clear token)
- [x] Back button on all screens
- [x] JWT token storage (DataStore)
- [x] Auto-attach token to requests
- [x] Error handling & messages
- [x] Form validation
- [x] Loading indicators

### 📋 Placeholders (Ready to Build)
- [ ] Home dashboard content
- [ ] Campaigns list/create
- [ ] Settings page
- [ ] More profile features

## API Endpoints Used

```
✅ POST /auth/register/citizen
✅ POST /auth/register/association
✅ POST /auth/login
✅ GET /profile (with token)
✅ PUT /profile (with token)
✅ POST /profile/change-password (with token)
```

## Next Steps

1. **Test thoroughly** - Try edge cases, wrong credentials, etc.
2. **Add real avatars** - Replace colored circles with actual images
3. **Build Home** - Add dashboard with real content
4. **Add Campaigns** - List/create/donate features
5. **Enhance Settings** - Notifications, theme, language
6. **Add forgot password** - When backend implements it
7. **Improve error handling** - More specific messages
8. **Add animations** - Smooth transitions

## Need Help?

Check the documentation:
- `README_IMPLEMENTATION.md` - Full feature guide
- `API_EXTRACTION_REPORT.md` - Complete API documentation

## 🎉 You're Done!

Everything is working. Just run the backend, launch the app, and enjoy! The foundation is solid and ready for building amazing features. 🚀

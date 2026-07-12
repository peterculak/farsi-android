# Farsi Alphabet Android App

A simple Android app built with Jetpack Compose to practice and learn the Farsi alphabet.

## Features
- **Learning Mode:** View the full alphabet, including transliterations and both letter forms (isolated and initial).
- **Practice Mode:** A quiz where you match Farsi letters to English transliterations.
- **Reverse Mode:** Reverse the quiz to match English transliteration to Farsi letters.
- **Auto Transition:** Configure an automatic delay after answering to seamlessly jump to the next question.

## Tech Stack
- **Kotlin**
- **Jetpack Compose**
- **Material 3**

## Development
A `Makefile` is provided for quick deployment via ADB:
- `make deploy` to build the app and install it directly on a connected Android device.
- `make build` to compile the APK only.
- `make logs` to stream Logcat specifically for this application.

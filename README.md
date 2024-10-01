# TVMaze Android App

## Architecture

This app follows the **MVVM (Model-View-ViewModel)** architectural pattern. This pattern promotes separation of concerns, making the codebase more maintainable and testable.

## Project Structure

The project is organized into the following main packages:

- **`data`:** Contains data-related components, such as models, repositories, and data sources.
- **`di`:** Handles dependency injection using Koin.
- **`ui`:** Contains UI-related components, such as Jetpack Compose view components and screen views.
- **`util`:** Contains utility classes and extensions.
- **`viewmodels`:** Contains ViewModels for the screens.

## Features

### Mandatory Features

- **Browse Shows:** Users can browse a list of shows fetched from the TVMaze API.
- **Search Shows:** Users can search for shows by name.
- **Show Details:** Users can view details of a selected show, including its summary, schedule, and cast.
- **Episode List:** Users can view a list of episodes for a selected show.
- **Episode Details:** Users can view details of a selected episode, including its summary and image.

### Bonus Features

- **PIN Security:** Users can set a PIN number to secure the application and prevent unauthorized access.
- **Favorite Shows:** Users can save shows as favorites.
- **Delete Favorites:** Users can delete shows from their favorites list.
- **Browse Favorites:** Users can browse their favorite shows in alphabetical order and click on one to see its details.
- **Orientation and tablet support:** The app supports both portrait and landscape orientations and are also optimized to be used on tablets.

## Libraries Used

- **Koin:** For dependency injection.
- **Retrofit:** For networking.
- **Room:** For local database persistence.
- **Kotlin Coroutines:** For asynchronous operations.
- **Jetpack Compose:** For building the UI.
- **MockK:** For mocking in unit tests.
- **Turbine:** For testing Kotlin flows.

## Deliverables

The apk file is located in deliverables directory in the project's root

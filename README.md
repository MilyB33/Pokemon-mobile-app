# Pokedex Lite

Pokedex Lite is a simple mobile application for Android that allows users to browse information about Pokemons, add them to favorites, and manage their accounts.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [API Usage](#api-usage)
- [Database](#database)
- [Components](#components)
- [Libraries Used](#libraries-used)

## Features

- Browse a list of Pokemons.
- Browse a list of favourites Pokemons.
- Search for Pokemon by name. The search is debounced.
- Add Pokemon to the favorites list.
- User registration and login.
- Manage user accounts.
- Delete user accounts along with favorite Pokemon.

## Installation

1. Clone the repository to your local machine:
    ```sh
    git clone https://github.com/MilyB33/pokemon-mobile-app.git
    ```

2. Open the project in Android Studio.

3. Ensure that you have the required dependencies installed as specified in the `build.gradle` file.

4. Run the application on an emulator or physical device.

## Usage

### User Registration

1. Launch the app.
2. Click on context menu in top right corner. 
3. Click the "Don't have an account?" button on the login screen. 
4. Enter a username and password, then click "Create account" button.

### Login

1. Launch the app.
2. Click on context menu in top right corner. 
3. Enter your username and password, then click "Log in" button.

### Browsing the Pokemon List

1. Launch the app.

### Browsing the favourites Pokemon List

1. Click the menu icon in the top-right corner of the screen.
2. Select "Saved" to the list.

### Searching for Pokemon

1. Enter the name of the Pokemon in the search field on the main screen.
2. The search results will automatically update (there is no autocomplete or partial search. You need to provide proper pokemon name).

### Adding to Favorites

1. Click on a Pokemon from the list to view its details.
2. Click the "+" button to add the Pokemon to your favorites list.
3. Click the "-" button to remove the Pokemon from favourites list.

### Managing Your Account

1. Click the menu icon in the top-right corner of the screen.
2. Select "Account" to view your account details.
3. You can delete your account by clicking the "Delete account" button.


## API

## API Usage

Pokedex Lite utilizes the PokeAPI to fetch data about Pokemon. The API provides various endpoints to retrieve detailed information about each Pokemon, including their names, IDs, and other attributes.

### Fetching Pokemon List

The app fetches an initial list of Pokemon using the following endpoint:

https://pokeapi.co/api/v2/pokemon

### Fetching Pokemon Details

For detailed information about a specific Pokemon, the app uses the following endpoint:

https://pokeapi.co/api/v2/pokemon/{id/name}


## Database

The app uses SQLite for local data storage. The database stores user information and their favorite Pokemon.

### User Table

The `users` table stores user details:
- `id` (INTEGER) - Primary key, auto-incremented.
- `username` (TEXT) - Unique username.
- `password` (TEXT) - User's password.

### Favorites Table

The `favorites` table stores the favorite Pokemon for each user:
- `id` (INTEGER) - Primary key, auto-incremented.
- `user_id` (INTEGER) - Foreign key referencing the `users` table.
- `pokemon_name` (TEXT) - Name of the favourite Pokemon.
- `pokemon_id` (INTEGER) - ID of the favorite Pokemon.

## Components

### ApiClient
`ApiClient` handles the creation and configuration of the Retrofit and Okhttp instance used to make API calls to the PokeAPI.

### DatabaseHelper
`DatabaseHelper` manages the SQLite database. It handles the creation of tables, upgrading the database schema, and providing methods for database operations such as adding users, checking user credentials, and managing favorite Pokemon.

### DatabaseModel
`DatabaseModel` defines the data models used in the SQLite database. This includes models for users and their favorite Pokemon.

### PokeApiModel
`PokeApiModel` defines the data models used to parse responses from the PokeAPI. This includes models for Pokemon and their attributes.

### AuthService
`AuthService` provides methods for user authentication. It handles user registration, login, and checking user credentials against the database.

### FavoriteService
`FavoriteService` provides methods for managing favorite Pokemon. It includes functionality to add and remove favorites and retrieve a user's favorite Pokemon from the database.

### PokeApiService
`PokeApiService` defines the endpoints and methods used to interact with the PokeAPI. It includes methods to fetch lists of Pokemon and detailed information about specific Pokemon.

### PokemonListAdapter
`PokemonListAdapter` is an adapter for the RecyclerView that displays the list of Pokemon. It binds Pokemon data to the views in the RecyclerView.

### PokemonViewModel
`PokemonViewModel` provides a layer of abstraction between the UI and the data sources. It manages the data for the Pokemon list and handles business logic, such as fetching data from the API and updating the UI with the results.

## Libraries Used

- **OkHttp**: A library for making HTTP requests. Used for network operations.

- **Retrofit**: A type-safe HTTP client for Android and Java. Used for API communication.

- **Coil**: An image loading library for Android backed by Kotlin Coroutines. Used for loading Pokemon images.

- **SQLite**: A lightweight database library for Android. Used for local data storage.

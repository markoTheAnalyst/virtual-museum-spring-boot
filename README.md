## Virtual Museum Tours

This project implements a web application that allows users to explore museums around the world through virtual tours.

**Features:**

* **RESTful API:** Provides access to museum data and virtual tour schedules in JSON format.
* **User Roles:**
    * **Guest:** Browse museums and search by name/city.
    * **Registered User:**
        * View all museums, search by name/city, and see active virtual tours.
        * Purchase tickets and access virtual tours.
        * Receive cultural news feeds.
        * View weather forecasts for museum locations.
    * **Administrator:**
        * Create and manage virtual tour schedules (date, time, duration) for each museum.
        * Upload virtual tour content (5-10 images and 1 video - YouTube link or .mp4 file).


**Technologies Used**
* Frontend: Angular
* Backend: Spring Boot
* Database: MySQL
* APIs: Google Maps API, OpenWeatherMap API
* RSS Feed: Utilized from HuffPost Arts section feed

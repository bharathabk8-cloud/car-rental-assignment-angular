# Car Rental Service — On-site Programming Assignment

A small, extensible car-rental application with:

- **Backend:** Java 17 + Spring Boot 3
- **Frontend:** Angular 18 (Standalone)
- **Persistence:** In-memory repository (deliberately kept simple for the assignment)
- **API style:** REST/JSON
- **Tests:** JUnit 5 + Spring Boot test

## 1. Functional requirements covered

The backend exposes four public operations:

1. Reserve a single car
2. Modify a reservation for a single car
3. Cancel a reservation for a single car
4. Get options for reserving

Vehicle categories:

- SEDAN
- SUV
- VAN
- PICKUP_TRUCK

Pricing:

| Category | Pricing |
|---|---|
| SEDAN | `< 10 days`: $20/day, otherwise $15/day |
| VAN | $22/day + 10% cleaning fee |
| SUV | $15/day + $0.50/mile |
| PICKUP_TRUCK | $30/day |
| Any category | +10% surcharge when license age is `< 3 years` |

For SUV, `dailyMileage` is supplied by the user and the mileage charge is:

`dailyMileage × numberOfDays × $0.50`

The 10% inexperienced-driver surcharge is applied after the category-specific amount is calculated.

Get Options returns all categories sorted by total amount ascending.

## 2. Design

### Layered architecture

```text
Angular UI
   |
   v
REST Controllers
   |
   v
Application Services
   |
   +--------------------+
   |                    |
   v                    v
Pricing Strategy    Reservation Service
   |                    |
   v                    v
Vehicle Repository   Reservation Repository
```

### Important design decisions

**1. Strategy pattern for pricing**

Each vehicle category has different pricing rules. Instead of putting a large `if/else` or `switch` into the service, pricing is isolated behind:

`PricingStrategy`

Implementations:

- `SedanPricingStrategy`
- `VanPricingStrategy`
- `SuvPricingStrategy`
- `PickupTruckPricingStrategy`

This makes adding a future category such as `LUXURY` a localized change.

**2. Separate pricing from reservation management**

`PricingService` calculates options. `ReservationService` manages reservation lifecycle. This keeps business responsibilities focused and testable.

**3. Repository abstraction**

Repositories are interfaces:

- `VehicleRepository`
- `ReservationRepository`

The assignment uses in-memory implementations. They can later be replaced with JPA/SQL implementations without changing the application/service contracts.

**4. Reservation overlap check**

A vehicle is unavailable when an existing active reservation overlaps the requested date range.

For half-open intervals `[start, end)`, overlap is:

```text
existing.start < requested.end
AND
existing.end > requested.start
```

This allows one customer to return a car on the same date another customer starts renting it.

**5. Money**

The code uses `BigDecimal` rather than `double` for monetary values to avoid floating-point rounding issues.

**6. Dates**

`LocalDate` is used because the assignment describes rental duration in days and does not require time-of-day reservations.

**7. API contract**

The API intentionally exposes category options separately from the actual reservation. A user can first see prices and then reserve a specific category. The backend selects an available physical vehicle from that category.

## 3. UML

```text
+-----------------------+
| ReservationController |
+-----------+-----------+
            |
            v
+-----------------------+
| ReservationService    |
+-----+-------------+---+
      |             |
      v             v
+-----------+   +--------------------+
| Vehicle   |   | Reservation        |
| Repository|   | Repository         |
+-----------+   +--------------------+
      |
      v
+-----------+
| Vehicle   |
+-----------+

+------------------+
| PricingService   |
+--------+---------+
         |
         v
+------------------------+
| PricingStrategy        |
+----+----+----+---------+
     |    |    | 
     v    v    v    v
  Sedan Van  SUV  Pickup
```

## 4. API

Base URL:

`http://localhost:8080/api`

### Get options

`GET /options?startDate=2026-08-20&endDate=2026-08-23&dailyMileage=100&licenseYears=5`

Returns all vehicle categories sorted by total price.

### Reserve

`POST /reservations`

Example:

```json
{
  "customerName": "John",
  "category": "SUV",
  "startDate": "2026-08-20",
  "endDate": "2026-08-23",
  "dailyMileage": 100,
  "licenseYears": 5
}
```

### Modify

`PUT /reservations/{reservationId}`

The request has the same fields as reservation creation.

### Cancel

`DELETE /reservations/{reservationId}`

### Error responses

Business validation errors return HTTP 400.

Missing reservation returns HTTP 404.

No available vehicle returns HTTP 409.

## 5. Running the backend

Requirements:

- Java 17+
- Maven 3.9+

```bash
cd backend
mvn spring-boot:run
```

Backend starts at:

`http://localhost:8080`

Run tests:

```bash
cd backend
mvn test
```

## 6. Running the React frontend

Requirements:

- Node.js 18+

```bash
cd frontend
npm install
npm run dev
```

Frontend starts at the Vite development URL shown in the terminal, normally:

`http://localhost:5173`

The Vite development server proxies `/api` calls to the Spring Boot backend.

## 7. Sample pricing

For a 3-day booking, 100 miles/day and a driver licensed for 5 years:

- SEDAN = 3 × $20 = **$60**
- VAN = 3 × $22 × 1.10 cleaning = **$72.60**
- SUV = (3 × $15) + (3 × 100 × $0.50) = **$195**
- PICKUP_TRUCK = 3 × $30 = **$90**

Sorted result:

`SEDAN ($60), VAN ($72.60), PICKUP_TRUCK ($90), SUV ($195)`

If license age is 2 years, a 10% surcharge is applied after the category calculation.

## 8. Assumptions

1. `endDate` must be after `startDate`.
2. Duration is the number of calendar days between dates.
3. `dailyMileage` must be zero or greater. It is relevant to SUV pricing.
4. License age must be zero or greater.
5. A reservation is for one physical vehicle.
6. The backend maintains a small predefined fleet for the assignment.
7. Cancellation makes the reservation inactive and immediately releases the vehicle.
8. Modification is implemented as release + reallocation after validating the new request. If the new request cannot be satisfied, the existing reservation remains unchanged.
9. Authentication, payment, external vehicle inventory and persistent database storage are intentionally outside the assignment scope.

## 9. Future extensions

The design can be extended with:

- PostgreSQL/MySQL via Spring Data JPA
- Authentication/authorization
- Payment service
- Dynamic fleet management
- Promotion/discount strategies
- Insurance pricing strategy
- Multiple pickup/drop-off locations
- Event publishing with Kafka
- Distributed locking for concurrent reservations
- Redis for high-volume availability queries

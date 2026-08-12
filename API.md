# API Contract

## GET /api/options

Query parameters:

- `startDate` — ISO date, required
- `endDate` — ISO date, required
- `dailyMileage` — decimal, default 0
- `licenseYears` — integer, default 0

Example response:

```json
[
  {"category":"SEDAN","durationDays":3,"totalAmount":60.00},
  {"category":"VAN","durationDays":3,"totalAmount":72.60},
  {"category":"PICKUP_TRUCK","durationDays":3,"totalAmount":90.00},
  {"category":"SUV","durationDays":3,"totalAmount":195.00}
]
```

## POST /api/reservations

```json
{
  "customerName": "John Doe",
  "category": "SUV",
  "startDate": "2026-08-20",
  "endDate": "2026-08-23",
  "dailyMileage": 100,
  "licenseYears": 5
}
```

## PUT /api/reservations/{id}

Same JSON shape as POST.

## DELETE /api/reservations/{id}

Returns the cancelled reservation.
